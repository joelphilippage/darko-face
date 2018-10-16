package com.turndapage.wear.watchface.watchfacedarko.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wear.widget.SwipeDismissFrameLayout;
import android.support.wearable.complications.ComplicationHelperActivity;
import android.support.wearable.complications.ComplicationProviderInfo;
import android.support.wearable.complications.ProviderChooserIntent;
import android.support.wearable.complications.ProviderInfoRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.turndapage.wear.watchface.watchfacedarko.R;
import com.turndapage.wear.watchface.watchfacedarko.model.WatchFaceComplication;
import com.turndapage.wear.watchface.watchfacedarko.service.AbstractKotlinWatchFace;

import net.vrallev.android.cat.Cat;

import java.util.concurrent.Executors;

import static android.app.Activity.RESULT_OK;

public class ComplicationSelectionFragment extends Fragment implements View.OnClickListener {
    private ComplicationSelectionFragment complicationSelectionFragment;
    private SwipeDismissFrameLayout swipeDismissFrameLayout;

    private ImageButton complication1Icon;
    private ImageButton complication2Icon;
    private ImageButton complication3Icon;

    private int complication1Id;
    private int complication2Id;
    private int complication3Id;

    // Selected complication id by user.
    private int mSelectedComplicationId;

    // ComponentName associated with watch face service (service that renders watch face). Used
    // to retrieve complication information.
    private ComponentName mWatchFaceComponentName;
    private AbstractKotlinWatchFace abstractKotlinWatchFace;

    public static final int COMPLICATION_CONFIG_REQUEST_CODE = 1001;

    // Required to retrieve complication data from watch face for preview.
    private ProviderInfoRetriever mProviderInfoRetriever;

    public enum ComplicationLocation {
        COMPLICATION1,
        COMPLICATION2,
        COMPLICATION3
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.complication_selection, container, false);

        complication1Id = 101;
        complication2Id = 102;
        complication3Id = 103;

        swipeDismissFrameLayout = view.findViewById(R.id.swipe_dismiss);
        complicationSelectionFragment = this;
        swipeDismissFrameLayout.addCallback(new SwipeDismissFrameLayout.Callback() {
            @Override
            public void onDismissed(SwipeDismissFrameLayout layout) {
                dismissThis();
            }
        });

        complication1Icon = view.findViewById(R.id.complication1Icon);
        complication2Icon = view.findViewById(R.id.complication2Icon);
        complication3Icon = view.findViewById(R.id.complication3Icon);

        complication1Icon.setOnClickListener(this);
        complication2Icon.setOnClickListener(this);
        complication3Icon.setOnClickListener(this);

        final ImageView complication1 = view.findViewById(R.id.complication1);
        final ImageView complication2 = view.findViewById(R.id.complication2);
        final ImageView complication3 = view.findViewById(R.id.complication3);
        final ImageView background = view.findViewById(R.id.preview);

        background.post(new Runnable() {
            @Override
            public void run() {
                float width = background.getMeasuredWidth();
                updateComplicationPositions(complication1, complication1Icon, width, abstractKotlinWatchFace.getWatchFaceStyle().getWatchFaceComplication1());
                updateComplicationPositions(complication2, complication2Icon, width, abstractKotlinWatchFace.getWatchFaceStyle().getWatchFaceComplication2());
                updateComplicationPositions(complication3, complication3Icon, width, abstractKotlinWatchFace.getWatchFaceStyle().getWatchFaceComplication3());
            }
        });

        background.setImageResource(abstractKotlinWatchFace.getWatchFaceStyle().getWatchFaceBackgroundImage().getBackgroundImageResource());

        mProviderInfoRetriever =
                new ProviderInfoRetriever(getContext(), Executors.newCachedThreadPool());
        mProviderInfoRetriever.init();

        final int[] complicationIds = {
                complication1Id,
                complication2Id,
                complication3Id
        };

        mProviderInfoRetriever.retrieveProviderInfo(new ProviderInfoRetriever.OnProviderInfoReceivedCallback() {
                    @Override
                    public void onProviderInfoReceived(int watchFaceComplicationId, @Nullable ComplicationProviderInfo info) {
                        updateComplicationViews(watchFaceComplicationId, info);
                    }
                },mWatchFaceComponentName,
                complicationIds
        );

        return view;
    }

    private void updateComplicationPositions(ImageView imageView, ImageButton imageButton, float width, WatchFaceComplication watchFaceComplication) {
        float complicationRadius = (imageView.getMeasuredWidth() / 2);
        float leftMargin1 = (watchFaceComplication.getXPos() * width) - complicationRadius;
        float topMargin1 = (watchFaceComplication.getYPos() * width) - complicationRadius;

        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams((int)(complicationRadius * 2), (int)(complicationRadius * 2));
        params1.leftMargin = (int)leftMargin1;
        params1.topMargin = (int)topMargin1;
        Cat.d("Width: " + width + " Radius: " + complicationRadius + " leftMargin: " + leftMargin1 + " topMargin: " + topMargin1);
        imageView.setLayoutParams(params1);
        imageButton.setLayoutParams(params1);
    }

    public void setComponentName(ComponentName componentName) {
        mWatchFaceComponentName = componentName;
        try {
            abstractKotlinWatchFace = (AbstractKotlinWatchFace)Class.forName(componentName.getClassName()).newInstance();
        }catch (ClassNotFoundException | IllegalAccessException | java.lang.InstantiationException ex) {
            ex.printStackTrace();
            dismissThis();
        }
    }

    @Override
    public void onClick(View view) {
        //Cat.d("Clicked Complication");
        if(view.equals(complication1Icon)) {
            launchComplicationHelperActivity(getActivity(), ComplicationLocation.COMPLICATION1);
        } else if(view.equals(complication2Icon)) {
            launchComplicationHelperActivity(getActivity(), ComplicationLocation.COMPLICATION2);
        } else if(view.equals(complication3Icon)) {
            launchComplicationHelperActivity(getActivity(), ComplicationLocation.COMPLICATION3);
        }
    }

    // Verifies the watch face supports the complication location, then launches the helper
    // class, so user can choose their complication data provider.
    private void launchComplicationHelperActivity(
            Activity currentActivity, ComplicationLocation complicationLocation) {

        int[] supportedTypes = new int[]{};
        switch (complicationLocation) {
            case COMPLICATION1:
                mSelectedComplicationId = 101;
                supportedTypes = abstractKotlinWatchFace.getWatchFaceStyle().getWatchFaceComplication1().getSupportedTypes();
                break;
            case COMPLICATION2:
                mSelectedComplicationId = 102;
                supportedTypes = abstractKotlinWatchFace.getWatchFaceStyle().getWatchFaceComplication2().getSupportedTypes();
                break;
            case COMPLICATION3:
                mSelectedComplicationId = 103;
                supportedTypes = abstractKotlinWatchFace.getWatchFaceStyle().getWatchFaceComplication3().getSupportedTypes();
                break;
        }

        startActivityForResult(
                ComplicationHelperActivity.createProviderChooserHelperIntent(
                        currentActivity,
                        mWatchFaceComponentName,
                        mSelectedComplicationId,
                        supportedTypes),
                COMPLICATION_CONFIG_REQUEST_CODE);
        Cat.d("Launched Activity");
    }

    public void updateComplicationViews(
            int watchFaceComplicationId, ComplicationProviderInfo complicationProviderInfo) {
        Cat.d("updateComplicationViews(): id: " + watchFaceComplicationId);
        Cat.d("\tinfo: " + complicationProviderInfo);

        if (watchFaceComplicationId == complication1Id) {
            if (complicationProviderInfo != null) {
                complication1Icon.setImageIcon(complicationProviderInfo.providerIcon);
            } else {
                complication1Icon.setImageResource(R.drawable.add);
            }
        } else if (watchFaceComplicationId == complication2Id) {
            if (complicationProviderInfo != null) {
                complication2Icon.setImageIcon(complicationProviderInfo.providerIcon);
            } else {
                complication2Icon.setImageResource(R.drawable.add);
            }
        } else if (watchFaceComplicationId == complication3Id) {
            if(complicationProviderInfo != null) {
                complication3Icon.setImageIcon(complicationProviderInfo.providerIcon);
            } else {
                complication3Icon.setImageResource(R.drawable.add);
            }
        }
    }

    public void updateSelectedComplication(ComplicationProviderInfo complicationProviderInfo) {
        // Checks if id is valid.
        if (mSelectedComplicationId >= 0) {
            updateComplicationViews(mSelectedComplicationId, complicationProviderInfo);
        }
    }

    public void dismissThis() {
        swipeDismissFrameLayout.setVisibility(View.GONE);
        getActivity().getFragmentManager().beginTransaction().remove(complicationSelectionFragment)
                .commitAllowingStateLoss();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Cat.d("Got result");
        if (requestCode == COMPLICATION_CONFIG_REQUEST_CODE && resultCode == RESULT_OK) {
            ComplicationProviderInfo complicationProviderInfo =
                    data.getParcelableExtra(ProviderChooserIntent.EXTRA_PROVIDER_INFO);
            Cat.d("Provider: " + complicationProviderInfo);

            updateSelectedComplication(complicationProviderInfo);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mProviderInfoRetriever.release();
    }
}

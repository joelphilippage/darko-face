package com.turndapage.wear.watchface.watchfacedarko.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.wear.widget.SwipeDismissFrameLayout;
import android.support.wear.widget.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.turndapage.wear.watchface.watchfacedarko.R;
import com.turndapage.wear.watchface.watchfacedarko.adapter.BackgroundAdapter;
import com.turndapage.wear.watchface.watchfacedarko.adapter.ConfigAdapter;

public class BackgroundSelectionFragment extends Fragment {

    private BackgroundSelectionFragment backgroundSelectionFragment;
    private SwipeDismissFrameLayout swipeDismissFrameLayout;
    private ConfigAdapter configAdapter;

    public void setConfigAdapter(ConfigAdapter configAdapter) {
        this.configAdapter = configAdapter;
    }

    public ConfigAdapter getConfigAdapter() {
        return configAdapter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dismissable_config, container, false);

        swipeDismissFrameLayout = view.findViewById(R.id.swipe_dismiss);
        backgroundSelectionFragment = this;
        swipeDismissFrameLayout.addCallback(new SwipeDismissFrameLayout.Callback() {
            @Override
            public void onDismissed(SwipeDismissFrameLayout layout) {
                dismissThis();
            }
        });

        BackgroundAdapter backgroundAdapter = new BackgroundAdapter(this);
        WearableRecyclerView recyclerView = view.findViewById(R.id.recycler);

        backgroundAdapter.setToRecycler(recyclerView);

        return view;
    }

    public void dismissThis() {
        swipeDismissFrameLayout.setVisibility(View.GONE);
        getActivity().getFragmentManager().beginTransaction().remove(backgroundSelectionFragment)
                .commitAllowingStateLoss();
    }
}

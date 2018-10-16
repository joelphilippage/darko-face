package com.turndapage.wear.watchface.watchfacedarko.adapter;

import android.content.ComponentName;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.wear.widget.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.turndapage.wear.watchface.watchfacedarko.fragment.ComplicationSelectionFragment;
import com.turndapage.wear.watchface.watchfacedarko.R;
import com.turndapage.wear.watchface.watchfacedarko.SettingsUtil;
import com.turndapage.wear.watchface.watchfacedarko.TransactionHelper;

public class ConfigAdapter extends WearableRecyclerView.Adapter<ConfigAdapter.ConfigViewHolder> {

    private Context context;
    private ConfigAdapter configAdapter;
    private ComponentName componentName;

    private static final int VIEW_TYPE_COMPLICATIONS = 0;
    //private static final int VIEW_TYPE_BACKGROUND = 1;
    private static final int VIEW_TYPE_HIDE_COMPLICATIONS = 1;

    public ConfigAdapter(Context context, ComponentName componentName) {
        this.context = context;
        configAdapter = this;
        this.componentName = componentName;
    }

    public void setToRecycler(WearableRecyclerView recycler) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setAdapter(this);
    }

    @Override
    public void onBindViewHolder(@NonNull ConfigViewHolder holder, int position) {
        View view = holder.itemView;

        ImageView imageView = view.findViewById(R.id.icon);
        TextView textView = view.findViewById(R.id.text);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_COMPLICATIONS:
                imageView.setImageResource(R.drawable.complication);
                textView.setText("Complications");
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ComplicationSelectionFragment complicationSelectionFragment = new ComplicationSelectionFragment();
                        complicationSelectionFragment.setComponentName(componentName);
                        TransactionHelper.SlideIn(context, complicationSelectionFragment);
                    }
                });
                break;
            /*case VIEW_TYPE_BACKGROUND:
                imageView.setImageResource(R.drawable.background);
                textView.setText("Background");
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BackgroundSelectionFragment backgroundSelectionFragment = new BackgroundSelectionFragment();
                        backgroundSelectionFragment.setConfigAdapter(configAdapter);
                        TransactionHelper.SlideIn(context, backgroundSelectionFragment);
                    }
                });
                break;*/
            case VIEW_TYPE_HIDE_COMPLICATIONS:
                imageView.setImageResource(R.drawable.hide_complications);
                final Switch toggleSwitch = view.findViewById(R.id.toggle);
                textView.setText("Hide Complications in Ambient");
                toggleSwitch.setVisibility(View.VISIBLE);
                toggleSwitch.setChecked(SettingsUtil.GetHideComplications(context));
                toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                        SettingsUtil.SetHideComplications(context, b);
                    }
                });
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toggleSwitch.toggle();
                    }
                });
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return 2;
    }

    @NonNull
    @Override
    public ConfigViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_view, parent, false);

        return new ConfigViewHolder(view);
    }

    static class ConfigViewHolder extends RecyclerView.ViewHolder {
        ConfigViewHolder(View configView) {
            super(configView);
        }
    }
}

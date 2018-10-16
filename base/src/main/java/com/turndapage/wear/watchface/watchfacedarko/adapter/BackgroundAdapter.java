package com.turndapage.wear.watchface.watchfacedarko.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.wear.widget.WearableRecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.turndapage.wear.watchface.watchfacedarko.fragment.BackgroundSelectionFragment;
import com.turndapage.wear.watchface.watchfacedarko.R;
import com.turndapage.wear.watchface.watchfacedarko.ResourceHelper;
import com.turndapage.wear.watchface.watchfacedarko.SettingsUtil;

public class BackgroundAdapter extends WearableRecyclerView.Adapter<ConfigAdapter.ConfigViewHolder> {

    private Context context;
    private BackgroundSelectionFragment backgroundSelectionFragment;
    private int[] resourceIds;

    public BackgroundAdapter(BackgroundSelectionFragment backgroundSelectionFragment) {
        this.context = backgroundSelectionFragment.getContext();
        this.backgroundSelectionFragment = backgroundSelectionFragment;
        String[] backgroundResources = context.getResources().getStringArray(R.array.backgrounds);

        resourceIds = new int[backgroundResources.length];

        for(int i = 0; i < backgroundResources.length; i++) {
            resourceIds[i] = ResourceHelper.getResourceId(context, backgroundResources[i],
                    "drawable", context.getPackageName());
        }
    }

    public void setToRecycler(WearableRecyclerView recycler) {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(linearLayoutManager);
        recycler.setAdapter(this);
        recycler.addItemDecoration(new PagerIndicator());

        recycler.scrollToPosition(SettingsUtil.GetCurrentBackground(context));
    }

    @Override
    public void onBindViewHolder(@NonNull final ConfigAdapter.ConfigViewHolder holder, int position) {
        View view = holder.itemView;

        ImageView imageView = view.findViewById(R.id.preview);

        imageView.setImageResource(resourceIds[position]);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsUtil.SetCurrentBackground(context, resourceIds[holder.getAdapterPosition()]);
                backgroundSelectionFragment.dismissThis();
            }
        });
    }

    @Override
    public int getItemCount() {
        return resourceIds.length;
    }

    @NonNull
    @Override
    public ConfigAdapter.ConfigViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.watch_preview_view, parent, false);

        return new ConfigAdapter.ConfigViewHolder(view);
    }
}

package com.turndapage.wear.watchface.watchfacedarko.activity

import android.os.Bundle
import android.support.wear.widget.WearableRecyclerView
import android.support.wearable.activity.WearableActivity
import android.content.ComponentName
import android.support.wearable.companion.WatchFaceCompanion
import com.turndapage.wear.watchface.watchfacedarko.adapter.ConfigAdapter
import com.turndapage.wear.watchface.watchfacedarko.R


class ConfigActivity : WearableActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_config)

        val wearableRecyclerView: WearableRecyclerView = findViewById(R.id.recycler)

        try {
            val componentName = intent.getParcelableExtra(WatchFaceCompanion.EXTRA_WATCH_FACE_COMPONENT)
                    as ComponentName

            val configAdapter = ConfigAdapter(this, componentName)

            configAdapter.setToRecycler(wearableRecyclerView)
        }catch (exception:TypeCastException) {
            exception.printStackTrace()
            finish()
        }

        // Enables Always-on
        setAmbientEnabled()
    }
}

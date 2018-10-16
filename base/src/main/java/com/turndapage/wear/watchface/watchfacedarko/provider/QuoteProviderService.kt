package com.turndapage.wear.watchface.watchfacedarko.provider

import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.ComplicationManager
import android.support.wearable.complications.ComplicationProviderService
import android.support.wearable.complications.ComplicationText
import com.turndapage.wear.watchface.watchfacedarko.R

class QuoteProviderService : ComplicationProviderService() {
    override fun onComplicationUpdate(complicationId: Int, type: Int, manager: ComplicationManager?) {
        val quotesArray = resources.getStringArray(R.array.quotes)
        val quote = quotesArray[(Math.random() * quotesArray.size).toInt()]
        val complicationData: ComplicationData = ComplicationData.Builder(ComplicationData.TYPE_LONG_TEXT)
                .setLongText(ComplicationText.plainText(quote))
                .build()
        manager?.updateComplicationData(complicationId, complicationData)
    }


}
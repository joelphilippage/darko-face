/*
 * Copyright 2018 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.turndapage.wear.watchface.watchfacedarko.faces

import android.graphics.Color
import android.support.wearable.complications.ComplicationData
import android.view.Gravity
import com.turndapage.wear.watchface.watchfacedarko.R
import com.turndapage.wear.watchface.watchfacedarko.model.AnalogWatchFaceStyle
import com.turndapage.wear.watchface.watchfacedarko.provider.DaysToEndLongProviderService
import com.turndapage.wear.watchface.watchfacedarko.service.AbstractKotlinWatchFace
import com.turndapage.wear.watchface.watchfacedarko.provider.DaysToEndProviderService
import com.turndapage.wear.watchface.watchfacedarko.provider.QuoteProviderService
import com.turndapage.wear.watchface.watchfacedarko.service.analogWatchFaceStyle

/**
 * Renders watch face via data object created by DSL.
 */
class FrankClassic : AbstractKotlinWatchFace() {

    override fun getWatchFaceStyle(): AnalogWatchFaceStyle {

        /**
         * Initializes colors and dimensions of watch face. Review [AnalogWatchFaceStyle] for
         * detailed explanation of each field.
         */
        return analogWatchFaceStyle {
            watchFaceColors {
                main = Color.WHITE
                highlight = Color.parseColor("#b1b2cb")
                background = Color.BLACK
                complication = Color.parseColor("#3f000000")
                shadow = Color.BLACK
                tickPaint = Color.WHITE
            }
            watchFaceDimensions {
                hourHandRadiusRatio = 0.2f
                minuteHandRadiusRatio = 0.25f
                secondHandRadiusRatio = 0.9f
                innerCircleRadius = 10f
                shadowRadius = 10f
            }
            watchFaceBackgroundImage {
                backgroundImageResource = R.drawable.classic
            }
            watchFaceHourHand {
                drawable = R.drawable.gothic_hour_hand
                offset = .27f
            }
            watchFaceMinuteHand {
                drawable = R.drawable.gothic_minute_hand
                offset = .27f
            }
            watchFaceComplication1 {
                widthRatio = 0.25f
                heightRatio = 0.25f
                xPos = 0.75f
                yPos = 0.5f
                supportedTypes = intArrayOf(ComplicationData.TYPE_RANGED_VALUE,
                        ComplicationData.TYPE_ICON,
                        ComplicationData.TYPE_SMALL_IMAGE,
                        ComplicationData.TYPE_SHORT_TEXT,
                        ComplicationData.TYPE_LONG_TEXT)
                defaultProviderService = DaysToEndLongProviderService::class.java
                titleTypeface = R.font.donnie_darko
            }
            watchFaceComplication2 {
                widthRatio = 0.80f
                heightRatio = 0.25f
                xPos = 0.5f
                yPos = 0.75f
                supportedTypes = intArrayOf(ComplicationData.TYPE_RANGED_VALUE,
                        ComplicationData.TYPE_ICON,
                        ComplicationData.TYPE_SMALL_IMAGE,
                        ComplicationData.TYPE_SHORT_TEXT,
                        ComplicationData.TYPE_LONG_TEXT)
                defaultProviderService = QuoteProviderService::class.java
                titleTypeface = R.font.donnie_darko
            }
            watchFaceStyle {
                topNumber = true
                bottomNumber = true
                leftNumber = true
                statusGravity = Gravity.CENTER
            }
        }
    }
}

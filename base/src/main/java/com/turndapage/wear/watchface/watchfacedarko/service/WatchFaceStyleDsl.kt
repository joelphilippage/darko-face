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

package com.turndapage.wear.watchface.watchfacedarko.service

import android.graphics.Color
import android.support.wearable.complications.ComplicationData
import android.view.Gravity
import com.turndapage.wear.watchface.watchfacedarko.model.*

/**
 * Creates watch face style DSL so developers can easily customize watch faces without learning the
 * underlying complex implementation.
 */
@DslMarker
annotation class WatchFaceStyleDSL

@WatchFaceStyleDSL
class WatchFaceComplicationBuilder {

    // Initializes defaults for fields. Check [AnalogWatchFaceStyle] for detailed explanation of
    // each field.
    private val attributesMap: MutableMap<String, Any?> = mutableMapOf(
            "widthRatio" to 0.0,
            "heightRatio" to 0.0,
            "xPos" to -100.0,
            "yPos" to -100.0,
            "supportedTypes" to intArrayOf(ComplicationData.TYPE_SHORT_TEXT,
                    ComplicationData.TYPE_SMALL_IMAGE, ComplicationData.TYPE_ICON,
                    ComplicationData.TYPE_RANGED_VALUE),
            "defaultProviderService" to null,
            "titleTypeface" to EMPTY_IMAGE_RESOURCE
    )

    var widthRatio:Float by attributesMap
    var heightRatio:Float by attributesMap
    var xPos:Float by attributesMap
    var yPos:Float by attributesMap
    var supportedTypes:IntArray by attributesMap
    var defaultProviderService:Class<*>? by attributesMap
    var titleTypeface:Int by attributesMap

    fun build(): WatchFaceComplication {
        return WatchFaceComplication(
                widthRatio, heightRatio, xPos, yPos, supportedTypes,
                defaultProviderService, titleTypeface
        )
    }
}

@WatchFaceStyleDSL
class WatchFaceStyleBuilder {
    private val attributesMap: MutableMap<String, Any?> = mutableMapOf(
            "topNumber" to false,
            "rightNumber" to false,
            "bottomNumber" to false,
            "leftNumber" to false,
            "statusGravity" to Gravity.TOP
    )
    var topNumber:Boolean by attributesMap
    var rightNumber:Boolean by attributesMap
    var bottomNumber:Boolean by attributesMap
    var leftNumber:Boolean by attributesMap
    var statusGravity:Int by attributesMap

    fun build(): WatchFaceStyle {
        return WatchFaceStyle(topNumber, rightNumber, bottomNumber, leftNumber, statusGravity)
    }
}

@WatchFaceStyleDSL
class WatchFaceHourHandBuilder {
    private val attributesMap: MutableMap<String, Any?> = mutableMapOf(
            "drawable" to EMPTY_IMAGE_RESOURCE,
            "offset" to 0
    )
    var drawable:Int by attributesMap
    var offset:Float by attributesMap

    fun build(): WatchFaceHourHand {
        return WatchFaceHourHand(drawable, offset)
    }
}

@WatchFaceStyleDSL
class WatchFaceMinuteHandBuilder {
    private val attributesMap: MutableMap<String, Any?> = mutableMapOf(
            "drawable" to EMPTY_IMAGE_RESOURCE,
            "offset" to 0
    )
    var drawable:Int by attributesMap
    var offset:Float by attributesMap

    fun build(): WatchFaceMinuteHand {
        return WatchFaceMinuteHand(drawable, offset)
    }
}

@WatchFaceStyleDSL
class WatchFaceColorsBuilder {

    // Initializes defaults for fields. Check [AnalogWatchFaceStyle] for detailed explanation of
    // each field.
    private val attributesMap: MutableMap<String, Any?> = mutableMapOf(
            "main" to Color.WHITE,
            "highlight" to Color.RED,
            "background" to Color.DKGRAY,
            "shadow" to Color.BLACK,
            "complication" to Color.BLACK,
            "tickPaint" to Color.WHITE
    )

    var main:Int by attributesMap
    var highlight:Int by attributesMap
    var background:Int by attributesMap
    var shadow:Int by attributesMap
    var complication:Int by attributesMap
    var tickPaint:Int by attributesMap

    fun build(): WatchFaceColors {
        return WatchFaceColors(
                main, highlight, background, shadow, complication, tickPaint
        )
    }
}

@WatchFaceStyleDSL
class WatchFaceDimensionsBuilder {

    // Initializes defaults for fields. Non-ratio fields represent pixels, as watch faces are
    // painted from a Canvas object. Check [AnalogWatchFaceStyle] for detailed explanation of
    // each field.
    private val attributesMap: MutableMap<String, Any?> = mutableMapOf(
            "hourHandRadiusRatio" to 0.5f,
            "minuteHandRadiusRatio" to 0.75f,
            "secondHandRadiusRatio" to 0.875f,
            "hourHandWidth" to 5f,
            "minuteHandWidth" to 3f,
            "secondHandWidth" to 2f,
            "shadowRadius" to 2f,
            "innerCircleRadius" to 4f,
            "innerCircleToArmsDistance" to 5f
    )

    var hourHandRadiusRatio:Float by attributesMap
    var minuteHandRadiusRatio:Float by attributesMap
    var secondHandRadiusRatio:Float by attributesMap
    var hourHandWidth:Float by attributesMap
    var minuteHandWidth:Float by attributesMap
    var secondHandWidth:Float by attributesMap
    var shadowRadius:Float by attributesMap
    var innerCircleRadius:Float by attributesMap
    var innerCircleToArmsDistance:Float by attributesMap

    fun build(): WatchFaceDimensions {
        return WatchFaceDimensions(
                hourHandRadiusRatio,
                minuteHandRadiusRatio,
                secondHandRadiusRatio,
                hourHandWidth,
                minuteHandWidth,
                secondHandWidth,
                shadowRadius,
                innerCircleRadius,
                innerCircleToArmsDistance
        )
    }
}

@WatchFaceStyleDSL
class WatchFaceBackgroundImageBuilder {

    // A background image isn't required for a watch face, so if it isn't defined in the DSL,
    // it gets an empty image resource value which means it won't be rendered.
    private val attributesMap: MutableMap<String, Any?> = mutableMapOf(
            "backgroundImageResource" to EMPTY_IMAGE_RESOURCE
    )

    var backgroundImageResource:Int by attributesMap

    fun build(): WatchFaceBackgroundImage {
        return WatchFaceBackgroundImage(backgroundImageResource)
    }
}

@WatchFaceStyleDSL
class AnalogWatchFaceStyleBuilder {
    private var watchFaceColors: WatchFaceColors? = null
    private var watchFaceDimensions: WatchFaceDimensions? = null
    private var watchFaceBackgroundImage: WatchFaceBackgroundImage =
        WatchFaceBackgroundImageBuilder().build()
    private var watchFaceHourHand: WatchFaceHourHand = WatchFaceHourHandBuilder().build()
    private var watchFaceMinuteHand: WatchFaceMinuteHand = WatchFaceMinuteHandBuilder().build()
    private var watchFaceComplication1: WatchFaceComplication = WatchFaceComplicationBuilder().build()
    private var watchFaceComplication2: WatchFaceComplication = WatchFaceComplicationBuilder().build()
    private var watchFaceComplication3: WatchFaceComplication = WatchFaceComplicationBuilder().build()
    private var watchFaceStyle: WatchFaceStyle = WatchFaceStyleBuilder().build()

    fun watchFaceComplication1(setup: WatchFaceComplicationBuilder.() -> Unit) {
        val watchFaceComplicationsBuilder = WatchFaceComplicationBuilder()
        watchFaceComplicationsBuilder.setup()
        watchFaceComplication1 = watchFaceComplicationsBuilder.build()
    }
    fun watchFaceComplication2(setup: WatchFaceComplicationBuilder.() -> Unit) {
        val watchFaceComplicationsBuilder = WatchFaceComplicationBuilder()
        watchFaceComplicationsBuilder.setup()
        watchFaceComplication2 = watchFaceComplicationsBuilder.build()
    }
    fun watchFaceComplication3(setup: WatchFaceComplicationBuilder.() -> Unit) {
        val watchFaceComplicationsBuilder = WatchFaceComplicationBuilder()
        watchFaceComplicationsBuilder.setup()
        watchFaceComplication3 = watchFaceComplicationsBuilder.build()
    }

    fun watchFaceMinuteHand(setup: WatchFaceMinuteHandBuilder.() -> Unit) {
        val watchFaceMinuteHandBuilder = WatchFaceMinuteHandBuilder()
        watchFaceMinuteHandBuilder.setup()
        watchFaceMinuteHand = watchFaceMinuteHandBuilder.build()
    }

    fun watchFaceHourHand(setup: WatchFaceHourHandBuilder.() -> Unit) {
        val watchFaceHourHandBuilder = WatchFaceHourHandBuilder()
        watchFaceHourHandBuilder.setup()
        watchFaceHourHand = watchFaceHourHandBuilder.build()
    }

    fun watchFaceColors(setup: WatchFaceColorsBuilder.() -> Unit) {
        val watchFaceColorsBuilder = WatchFaceColorsBuilder()
        watchFaceColorsBuilder.setup()
        watchFaceColors = watchFaceColorsBuilder.build()
    }

    fun watchFaceDimensions(setup: WatchFaceDimensionsBuilder.() -> Unit) {
        val analogWatchFaceDimensionsBuilder = WatchFaceDimensionsBuilder()
        analogWatchFaceDimensionsBuilder.setup()
        watchFaceDimensions = analogWatchFaceDimensionsBuilder.build()
    }

    fun watchFaceBackgroundImage(setup: WatchFaceBackgroundImageBuilder.() -> Unit) {
        val analogWatchFaceBackgroundImageBuilder = WatchFaceBackgroundImageBuilder()
        analogWatchFaceBackgroundImageBuilder.setup()
        watchFaceBackgroundImage = analogWatchFaceBackgroundImageBuilder.build()
    }

    fun watchFaceStyle(setup: WatchFaceStyleBuilder.() -> Unit) {
        val analogWatchFaceStyleBuilder = WatchFaceStyleBuilder()
        analogWatchFaceStyleBuilder.setup()
        watchFaceStyle = analogWatchFaceStyleBuilder.build()
    }


    fun build(): AnalogWatchFaceStyle {

        val watchFaceColorsArgument = watchFaceColors ?:
            throw IllegalStateException("Must define watch face styles in DSL.")

        val watchFaceDimensionsArgument = watchFaceDimensions ?:
            throw IllegalStateException("Must define watch face dimensions in DSL.")

        return AnalogWatchFaceStyle(
                watchFaceColorsArgument,
                watchFaceDimensionsArgument,
                watchFaceBackgroundImage,
                watchFaceHourHand,
                watchFaceMinuteHand,
                watchFaceComplication1,
                watchFaceComplication2,
                watchFaceComplication3,
                watchFaceStyle
        )
    }

    /**
     * This method shadows the [analogWatchFaceStyle] method when inside the scope
     * of a [AnalogWatchFaceStyleBuilder], so that watch faces can't be nested.
     */
    @Suppress("UNUSED_PARAMETER")
    @Deprecated(level = DeprecationLevel.ERROR, message = "WatchFaceStyles can't be nested.")
    fun analogWatchFaceStyle(param: () -> Unit = {}) {
    }
}

@WatchFaceStyleDSL
fun analogWatchFaceStyle (setup: AnalogWatchFaceStyleBuilder.() -> Unit): AnalogWatchFaceStyle {
    val analogWatchFaceStyleBuilder = AnalogWatchFaceStyleBuilder()
    analogWatchFaceStyleBuilder.setup()
    return analogWatchFaceStyleBuilder.build()
}

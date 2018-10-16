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
package com.turndapage.wear.watchface.watchfacedarko.model

import android.support.annotation.DrawableRes

/**
 * Used when no background image is available for watch face (renders color instead).
 */
const val EMPTY_IMAGE_RESOURCE = 0

/**
 * Represents all data required to style an analog watch face.
 */
data class AnalogWatchFaceStyle(
    val watchFaceColors: WatchFaceColors,
    val watchFaceDimensions: WatchFaceDimensions,
    val watchFaceBackgroundImage: WatchFaceBackgroundImage,
    val watchFaceHourHand: WatchFaceHourHand,
    val watchFaceMinuteHand: WatchFaceMinuteHand,
    val watchFaceComplication1: WatchFaceComplication,
    val watchFaceComplication2: WatchFaceComplication,
    val watchFaceComplication3: WatchFaceComplication,
    val watchFaceStyle: WatchFaceStyle)

/**
 * Represents all colors associated with watch face:
 * - widthRatio - the width in percentage relative to the screen of the complication (range from
 *   0.0 to 1.0).
 * - heightRatio - the height in percentage relative to the screen of the complication (range from
 *   0.0 to 1.0).
 * - xPos - the X position on the screen from 0.0 to 1.0
 * - yPos - the Y position on the screen from 0.0 to 1.0
 */
data class WatchFaceComplication(
        val widthRatio:Float,
        val heightRatio:Float,
        val xPos:Float,
        val yPos:Float,
        val supportedTypes: IntArray,
        val defaultProvider:Class<*>?,
        val titleTypeface:Int)

/**
 * Represents all colors associated with watch face:
 * - main - hour hand, minute hand, and mark colors
 * - highlight - second hand color
 * - background - background color
 * - shadow - shadow color beneath all hands and ticks.
 */
data class WatchFaceColors(
    val main:Int,
    val highlight:Int,
    val background:Int,
    val shadow:Int,
    val complication:Int,
    val tickPaint:Int)

/**
 * Represents dimensions of objects within the watch face:
 * - hourHandRadiusRatio - Hour hand length as a ratio of the device's radius (range from
 *   0.0 to 1.0).
 * - minuteHandRadiusRatio - Minute hand length as a ratio of the device's radius (range from
 *   0.0 to 1.0).
 * - secondHandRadiusRatio - Second hand length as a ratio of the device's radius (range from
 *   0.0 to 1.0).
 * - hourHandWidth - Width of hour hand in pixels.
 * - minuteHandWidth - Width of minute hand in pixels.
 * - secondHandWidth - Width of second hand in pixels.
 * - shadowRadius - Length in pixels of the shadow radius around all hands and marks.
 * - innerCircleRadius - Radius in pixels for inner circle that all hands connect with.
 *   IMPORTANT: Should never be zero, as you don't want to burn in the center of the screen.
 * - innerCircleToArmsDistance - Distance in pixels from inner circle to each watch arm.
 */
data class WatchFaceDimensions(
    val hourHandRadiusRatio:Float,
    val minuteHandRadiusRatio:Float,
    val secondHandRadiusRatio:Float,
    val hourHandWidth:Float,
    val minuteHandWidth:Float,
    val secondHandWidth:Float,
    val shadowRadius:Float,
    val innerCircleRadius:Float,
    val innerCircleToArmsDistance:Float
)

/**
 * Represents the background image resource id for a watch face, or 0 if there isn't a
 * background image drawable.
 *
 * Image is scaled to fit the device screen by width but will maintain its aspect ratio, and
 * centered to the top of the screen.
 */
data class WatchFaceBackgroundImage(@DrawableRes val backgroundImageResource:Int)

data class WatchFaceHourHand(
        @DrawableRes val drawable:Int,
        val offset:Float
)
data class WatchFaceMinuteHand(
        @DrawableRes val drawable: Int,
        val offset: Float
)

data class WatchFaceStyle(
        val topNumber:Boolean,
        val rightNumber:Boolean,
        val bottomNumber:Boolean,
        val leftNumber:Boolean,
        val statusGravity:Int
)
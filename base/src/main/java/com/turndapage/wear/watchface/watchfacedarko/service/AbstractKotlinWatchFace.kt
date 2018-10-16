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

import android.content.*
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.res.ResourcesCompat
import android.support.wearable.complications.ComplicationData
import android.support.wearable.complications.rendering.ComplicationDrawable
import android.support.wearable.watchface.CanvasWatchFaceService
import android.support.wearable.watchface.WatchFaceService
import android.support.wearable.watchface.WatchFaceStyle
import android.util.Log
import android.util.SparseArray
import android.view.SurfaceHolder
import com.turndapage.wear.watchface.watchfacedarko.R
import com.turndapage.wear.watchface.watchfacedarko.SettingsUtil
import com.turndapage.wear.watchface.watchfacedarko.TextRect
import com.turndapage.wear.watchface.watchfacedarko.model.AnalogWatchFaceStyle
import com.turndapage.wear.watchface.watchfacedarko.model.EMPTY_IMAGE_RESOURCE
import org.w3c.dom.Text

import java.lang.ref.WeakReference
import java.util.Calendar
import java.util.TimeZone
import kotlin.math.roundToInt

/**
 * Updates rate in milliseconds for interactive mode. We update once a second to advance the
 * second hand.
 */
private const val INTERACTIVE_UPDATE_RATE_MS = 1000

/**
 * Handler message id for updating the time periodically in interactive mode.
 */
private const val MSG_UPDATE_TIME = 0

/**
 * This is a helper class which renders an analog watch face based on a data object passed in
 * representing the style. The class implements all best practices for watch faces, so the
 * developer can just focus on designing the watch face they want.
 *
 * Analog watch face with a ticking second hand. In ambient mode, the second hand isn't
 * shown. On devices with low-bit ambient mode, the hands are drawn without anti-aliasing in ambient
 * mode. The watch face is drawn with less contrast in mute mode.
 *
 *
 * Important Note: Because watch face apps do not have a default Activity in
 * their project, you will need to set your Configurations to
 * "Do not launch Activity" for both the Wear and/or Application modules. If you
 * are unsure how to do this, please review the "Run Starter project" section
 * in the
 * [Watch Face Code Lab](https://codelabs.developers.google.com/codelabs/watchface/index.html#0)
 */
abstract class AbstractKotlinWatchFace : CanvasWatchFaceService() {

    private lateinit var analogWatchFaceStyle: AnalogWatchFaceStyle

    private val complicationId1: Int = 101
    private val complicationId2: Int = 102
    private val complicationId3: Int = 103

    private val complicationIds: IntArray = intArrayOf(complicationId1, complicationId2, complicationId3)

    abstract fun getWatchFaceStyle():AnalogWatchFaceStyle

    override fun onCreateEngine(): Engine {
        return Engine()
    }

    private class EngineHandler(reference: AbstractKotlinWatchFace.Engine) : Handler() {
        private val weakReference: WeakReference<AbstractKotlinWatchFace.Engine> =
            WeakReference(reference)

        override fun handleMessage(msg: Message) {
            val engine = weakReference.get()
            if (engine != null) {
                when (msg.what) {
                    MSG_UPDATE_TIME -> engine.handleUpdateTimeMessage()
                }
            }
        }
    }

    inner class Engine : CanvasWatchFaceService.Engine() {

        private lateinit var calendar: Calendar

        private var registeredTimeZoneReceiver = false
        private var muteMode: Boolean = false
        private var centerX: Float = 0F
        private var centerY: Float = 0F

        private var secondHandLengthRatio: Float = 0F
        private var minuteHandLengthRatio: Float = 0F
        private var hourHandLengthRatio: Float = 0F

        private var hourHandWidth: Float = 0F
        private var hourHandHeight: Float = 0F
        private var hourOffset: Float = 0F

        private var minuteHandWidth: Float = 0F
        private var minuteHandHeight: Float = 0F
        private var minuteOffset: Float = 0F

        private var hourHandDrawable: Drawable? = null
        private var minuteHandDrawable: Drawable? = null

        private lateinit var hourPaint: Paint
        private lateinit var minutePaint: Paint
        private lateinit var secondPaint: Paint
        private lateinit var tickAndCirclePaint: Paint
        private lateinit var circlePaint: Paint

        private lateinit var datePaint: Paint
        private lateinit var textPaint: Paint

        private lateinit var backgroundPaint: Paint
        // Best practice is to always use black for watch face in ambient mode (saves battery
        // and prevents burn-in.
        private val backgroundAmbientPaint:Paint = Paint().apply { color = Color.BLACK }

        private var backgroundImageEnabled:Boolean = false
        private lateinit var backgroundBitmap: Bitmap
        private lateinit var grayBackgroundBitmap: Bitmap

        private var ambient: Boolean = false
        private var lowBitAmbient: Boolean = false
        private var burnInProtection: Boolean = false

        /* Handler to update the time once a second in interactive mode. */
        private val updateTimeHandler = EngineHandler(this)

        private var complicationWidthRatio1: Float = 0F
        private var complicationHeightRatio1: Float = 0F
        private var complicationXPos1: Float = 0F
        private var complicationYPos1: Float = 0F

        private var complicationWidthRatio2: Float = 0F
        private var complicationHeightRatio2: Float = 0F
        private var complicationXPos2: Float = 0F
        private var complicationYPos2: Float = 0F

        private var complicationWidthRatio3: Float = 0F
        private var complicationHeightRatio3: Float = 0F
        private var complicationXPos3: Float = 0F
        private var complicationYPos3: Float = 0F

        private lateinit var complicationDataSparseArray: SparseArray<ComplicationData?>
        private lateinit var complicationDrawableSparseArray: SparseArray<ComplicationDrawable?>

        private val timeZoneReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                calendar.timeZone = TimeZone.getDefault()
                invalidate()
            }
        }

        override fun onCreate(holder: SurfaceHolder) {
            super.onCreate(holder)

            analogWatchFaceStyle = getWatchFaceStyle()

            setWatchFaceStyle(
                    WatchFaceStyle.Builder(this@AbstractKotlinWatchFace)
                        .setAcceptsTapEvents(true)
                            .setStatusBarGravity(analogWatchFaceStyle.watchFaceStyle.statusGravity)
                        .build()
            )

            calendar = Calendar.getInstance()

            initializeBackground()
            initializeWatchFace()
            initializeComplications()
        }

        private fun initializeBackground() {

            backgroundImageEnabled =
                    analogWatchFaceStyle.watchFaceBackgroundImage.backgroundImageResource !=
                    EMPTY_IMAGE_RESOURCE

            if (backgroundImageEnabled) {
                backgroundBitmap = BitmapFactory.decodeResource(
                        resources,
                        analogWatchFaceStyle.watchFaceBackgroundImage.backgroundImageResource
                )
            }
        }

        private fun initializeComplications() {
            datePaint = Paint()
            datePaint.color = analogWatchFaceStyle.watchFaceColors.highlight
            datePaint.isAntiAlias = true
            val typeface = ResourcesCompat.getFont(applicationContext, R.font.frank_knows)
            datePaint.typeface = typeface
            datePaint.textSize = 40f

            textPaint = Paint()
            textPaint.color = Color.WHITE
            if(analogWatchFaceStyle.watchFaceComplication2.titleTypeface != EMPTY_IMAGE_RESOURCE)
                textPaint.typeface = ResourcesCompat.getFont(applicationContext,
                        analogWatchFaceStyle.watchFaceComplication2.titleTypeface)
            textPaint.isAntiAlias = true
            textPaint.textSize = 18f
            textPaint.textAlign = Paint.Align.CENTER

            complicationWidthRatio1 = analogWatchFaceStyle.watchFaceComplication1.widthRatio
            complicationHeightRatio1 = analogWatchFaceStyle.watchFaceComplication1.heightRatio
            complicationXPos1 = analogWatchFaceStyle.watchFaceComplication1.xPos
            complicationYPos1 = analogWatchFaceStyle.watchFaceComplication1.yPos

            complicationWidthRatio2 = analogWatchFaceStyle.watchFaceComplication2.widthRatio
            complicationHeightRatio2 = analogWatchFaceStyle.watchFaceComplication2.heightRatio
            complicationXPos2 = analogWatchFaceStyle.watchFaceComplication2.xPos
            complicationYPos2 = analogWatchFaceStyle.watchFaceComplication2.yPos

            complicationWidthRatio3 = analogWatchFaceStyle.watchFaceComplication3.widthRatio
            complicationHeightRatio3 = analogWatchFaceStyle.watchFaceComplication3.heightRatio
            complicationXPos3 = analogWatchFaceStyle.watchFaceComplication3.xPos
            complicationYPos3 = analogWatchFaceStyle.watchFaceComplication3.yPos

            complicationDataSparseArray = SparseArray(complicationIds.size)

            val complicationDrawable1 = ComplicationDrawable(applicationContext)
            val complicationDrawable2 = ComplicationDrawable(applicationContext)
            val complicationDrawable3 = ComplicationDrawable(applicationContext)

            complicationDrawable1.setBackgroundColorActive(analogWatchFaceStyle.watchFaceColors.complication)
            complicationDrawable1.setBorderColorActive(Color.TRANSPARENT)
            complicationDrawable1.setHighlightColorActive(analogWatchFaceStyle.watchFaceColors.highlight)
            complicationDrawable1.setTextColorActive(analogWatchFaceStyle.watchFaceColors.main)
            complicationDrawable1.setTextColorAmbient(Color.WHITE)
            complicationDrawable1.setTitleTypefaceActive(textPaint.typeface)
            complicationDrawable1.setTitleTypefaceAmbient(textPaint.typeface)
            complicationDrawable1.setIconColorAmbient(Color.WHITE)
            complicationDrawable1.setImageColorFilterAmbient(getBlackAndWhiteColorFilter())

            complicationDrawable2.setBackgroundColorActive(analogWatchFaceStyle.watchFaceColors.complication)
            complicationDrawable2.setBorderColorActive(Color.TRANSPARENT)
            complicationDrawable2.setHighlightColorActive(analogWatchFaceStyle.watchFaceColors.highlight)
            complicationDrawable2.setTextColorActive(analogWatchFaceStyle.watchFaceColors.main)
            complicationDrawable2.setTextColorAmbient(Color.WHITE)
            complicationDrawable2.setTitleTypefaceActive(textPaint.typeface)
            complicationDrawable2.setTitleTypefaceAmbient(textPaint.typeface)
            complicationDrawable2.setIconColorAmbient(Color.WHITE)
            complicationDrawable2.setImageColorFilterAmbient(getBlackAndWhiteColorFilter())

            complicationDrawable3.setBackgroundColorActive(analogWatchFaceStyle.watchFaceColors.complication)
            complicationDrawable3.setBorderColorActive(Color.TRANSPARENT)
            complicationDrawable3.setHighlightColorActive(analogWatchFaceStyle.watchFaceColors.highlight)
            complicationDrawable3.setTextColorActive(analogWatchFaceStyle.watchFaceColors.main)
            complicationDrawable3.setTextColorAmbient(Color.WHITE)
            complicationDrawable3.setTitleTypefaceActive(textPaint.typeface)
            complicationDrawable3.setTitleTypefaceAmbient(textPaint.typeface)
            complicationDrawable3.setIconColorAmbient(Color.WHITE)
            complicationDrawable3.setImageColorFilterAmbient(getBlackAndWhiteColorFilter())

            complicationDrawableSparseArray = SparseArray(complicationIds.size)

            complicationDrawableSparseArray.put(complicationId1, complicationDrawable1)
            complicationDrawableSparseArray.put(complicationId2, complicationDrawable2)
            complicationDrawableSparseArray.put(complicationId3, complicationDrawable3)

            setActiveComplications(complicationId1, complicationId2, complicationId3)


            if(analogWatchFaceStyle.watchFaceComplication1.defaultProvider != null) {
                val defaultProviderService = ComponentName(applicationContext, analogWatchFaceStyle.watchFaceComplication1.defaultProvider!!)
                if(analogWatchFaceStyle.watchFaceComplication1.supportedTypes.contains(ComplicationData.TYPE_LONG_TEXT)) {
                    setDefaultComplicationProvider(complicationId1, defaultProviderService, ComplicationData.TYPE_LONG_TEXT)
                } else {
                    setDefaultComplicationProvider(complicationId1, defaultProviderService, ComplicationData.TYPE_SHORT_TEXT)
                }
            }
            if(analogWatchFaceStyle.watchFaceComplication2.defaultProvider != null) {
                val defaultProviderService = ComponentName(applicationContext, analogWatchFaceStyle.watchFaceComplication2.defaultProvider!!)
                setDefaultComplicationProvider(complicationId2, defaultProviderService, ComplicationData.TYPE_LONG_TEXT)
            }
            if(analogWatchFaceStyle.watchFaceComplication3.defaultProvider != null) {
                val defaultProviderService = ComponentName(applicationContext, analogWatchFaceStyle.watchFaceComplication3.defaultProvider!!)
                if(analogWatchFaceStyle.watchFaceComplication3.supportedTypes.contains(ComplicationData.TYPE_LONG_TEXT)) {
                    setDefaultComplicationProvider(complicationId3, defaultProviderService, ComplicationData.TYPE_LONG_TEXT)
                } else {
                    setDefaultComplicationProvider(complicationId3, defaultProviderService, ComplicationData.TYPE_SHORT_TEXT)
                }
            }
        }

        private fun getBlackAndWhiteColorFilter(): ColorFilter {
            val colorMatrix: FloatArray = floatArrayOf(
                    0.33f, 0.33f, 0.33f, 0f, 50f,
                    0.33f, 0.33f, 0.33f, 0f, 50f,
                    0.33f, 0.33f, 0.33f, 0f, 50f,
                    0f, 0f, 0f, 1f, 0f
            )
            return  ColorMatrixColorFilter(colorMatrix)
        }

        override fun onUnreadCountChanged(count: Int) {
            invalidate()
        }

        private fun initializeWatchFace() {

            hourPaint = Paint().apply {
                color = analogWatchFaceStyle.watchFaceColors.main
                strokeWidth = analogWatchFaceStyle.watchFaceDimensions.hourHandWidth
                isAntiAlias = true
                strokeCap = Paint.Cap.ROUND
                style = Paint.Style.FILL
                setShadowLayer(
                        analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                        0f,
                        0f,
                        analogWatchFaceStyle.watchFaceColors.shadow
                )
            }

            minutePaint = Paint().apply {
                color = analogWatchFaceStyle.watchFaceColors.main
                strokeWidth = analogWatchFaceStyle.watchFaceDimensions.minuteHandWidth
                isAntiAlias = true
                strokeCap = Paint.Cap.ROUND
                setShadowLayer(
                        analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                        0f,
                        0f,
                        analogWatchFaceStyle.watchFaceColors.shadow
                )
            }

            secondPaint = Paint().apply {
                color = analogWatchFaceStyle.watchFaceColors.highlight
                strokeWidth = analogWatchFaceStyle.watchFaceDimensions.secondHandWidth
                isAntiAlias = true
                strokeCap = Paint.Cap.ROUND
                setShadowLayer(
                        analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                        0f,
                        0f,
                        analogWatchFaceStyle.watchFaceColors.shadow
                )
            }

            tickAndCirclePaint = Paint().apply {
                color = analogWatchFaceStyle.watchFaceColors.tickPaint
                strokeWidth = analogWatchFaceStyle.watchFaceDimensions.secondHandWidth
                isAntiAlias = true
                style = Paint.Style.STROKE
                setShadowLayer(
                        analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                        0f,
                        0f,
                        analogWatchFaceStyle.watchFaceColors.shadow
                )
            }

            circlePaint = Paint().apply {
                color = analogWatchFaceStyle.watchFaceColors.main
                strokeWidth = analogWatchFaceStyle.watchFaceDimensions.secondHandWidth
                isAntiAlias = true
                style = Paint.Style.FILL
                setShadowLayer(
                        analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                        0f,
                        0f,
                        analogWatchFaceStyle.watchFaceColors.shadow
                )
            }

            backgroundPaint = Paint().apply {
                color = analogWatchFaceStyle.watchFaceColors.background
            }
        }

        override fun onDestroy() {
            updateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            super.onDestroy()
        }

        override fun onPropertiesChanged(properties: Bundle) {
            super.onPropertiesChanged(properties)
            lowBitAmbient = properties.getBoolean(
                    WatchFaceService.PROPERTY_LOW_BIT_AMBIENT, false
            )
            burnInProtection = properties.getBoolean(
                    WatchFaceService.PROPERTY_BURN_IN_PROTECTION, false
            )
        }

        override fun onComplicationDataUpdate(watchFaceComplicationId: Int, data: ComplicationData?) {
            complicationDataSparseArray.put(watchFaceComplicationId, data)

            val complicationDrawable: ComplicationDrawable? =
                    complicationDrawableSparseArray.get(watchFaceComplicationId)
            complicationDrawable?.setComplicationData(data)

            invalidate()
        }

        override fun onTapCommand(tapType: Int, x: Int, y: Int, eventTime: Long) {
            if(WatchFaceService.TAP_TYPE_TAP == tapType) {
                for(i in 0..(complicationIds.size - 1)) {
                    val complicationId = complicationIds[i]
                    val complicationDrawable: ComplicationDrawable? = complicationDrawableSparseArray.get(complicationId)
                    val successfulTap: Boolean? = complicationDrawable?.onTap(x, y)
                    if(successfulTap!!) {
                        return
                    }
                }
            }
        }

        override fun onTimeTick() {
            super.onTimeTick()
            invalidate()
        }

        override fun onAmbientModeChanged(inAmbientMode: Boolean) {
            super.onAmbientModeChanged(inAmbientMode)
            ambient = inAmbientMode

            updateWatchHandStyle()
            updateComplicationStyle()

            // Check and trigger whether or not timer should be running (only
            // in active mode).
            updateTimer()
        }

        private fun updateComplicationStyle() {
            for(i in 0..(complicationIds.size - 1)) {
                val complicationDrawable = complicationDrawableSparseArray.get(complicationIds[i])
                complicationDrawable?.setInAmbientMode(ambient)
                Log.d("Tag","updating complication ambient with id ${complicationIds[i]}")
            }
        }

        private fun updateWatchHandStyle() {
            if (ambient) {
                hourPaint.color = Color.WHITE
                minutePaint.color = Color.WHITE
                secondPaint.color = Color.WHITE
                tickAndCirclePaint.color = analogWatchFaceStyle.watchFaceColors.tickPaint
                circlePaint.color = Color.WHITE

                hourPaint.isAntiAlias = false
                minutePaint.isAntiAlias = false
                secondPaint.isAntiAlias = false
                tickAndCirclePaint.isAntiAlias = false
                circlePaint.isAntiAlias = false

                hourPaint.clearShadowLayer()
                minutePaint.clearShadowLayer()
                secondPaint.clearShadowLayer()
                tickAndCirclePaint.clearShadowLayer()
                circlePaint.clearShadowLayer()

            } else {
                hourPaint.color = analogWatchFaceStyle.watchFaceColors.main
                minutePaint.color = analogWatchFaceStyle.watchFaceColors.main
                secondPaint.color = analogWatchFaceStyle.watchFaceColors.highlight
                tickAndCirclePaint.color = analogWatchFaceStyle.watchFaceColors.tickPaint
                circlePaint.color = analogWatchFaceStyle.watchFaceColors.main

                hourPaint.isAntiAlias = true
                minutePaint.isAntiAlias = true
                secondPaint.isAntiAlias = true
                tickAndCirclePaint.isAntiAlias = true
                circlePaint.isAntiAlias = true

                hourPaint.setShadowLayer(
                        analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                        0f,
                        0f,
                        analogWatchFaceStyle.watchFaceColors.shadow
                )
                minutePaint.setShadowLayer(
                        analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                        0f,
                        0f,
                        analogWatchFaceStyle.watchFaceColors.shadow
                )
                secondPaint.setShadowLayer(
                        analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                        0f,
                        0f,
                        analogWatchFaceStyle.watchFaceColors.shadow
                )
                tickAndCirclePaint.setShadowLayer(
                        analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                        0f,
                        0f,
                        analogWatchFaceStyle.watchFaceColors.shadow
                )
                circlePaint.setShadowLayer(
                        analogWatchFaceStyle.watchFaceDimensions.shadowRadius,
                        0f,
                        0f,
                        analogWatchFaceStyle.watchFaceColors.shadow
                )
            }
        }

        override fun onInterruptionFilterChanged(interruptionFilter: Int) {
            super.onInterruptionFilterChanged(interruptionFilter)
            val inMuteMode = interruptionFilter == WatchFaceService.INTERRUPTION_FILTER_NONE

            /* Dim display in mute mode. */
            if (muteMode != inMuteMode) {
                muteMode = inMuteMode
                hourPaint.alpha = if (inMuteMode) 100 else 255
                minutePaint.alpha = if (inMuteMode) 100 else 255
                secondPaint.alpha = if (inMuteMode) 80 else 255
                invalidate()
            }
        }

        override fun onSurfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
            super.onSurfaceChanged(holder, format, width, height)

            /*
             * Find the coordinates of the center point on the screen, and ignore the window
             * insets, so that, on round watches with a "chin", the watch face is centered on the
             * entire screen, not just the usable portion.
             */
            centerX = width / 2f
            centerY = height / 2f

            /*
             * Calculate lengths of different hands based on watch screen size.
             */
            secondHandLengthRatio =
                    (centerX * analogWatchFaceStyle.watchFaceDimensions.secondHandRadiusRatio)
            minuteHandLengthRatio =
                    (centerX * analogWatchFaceStyle.watchFaceDimensions.minuteHandRadiusRatio)
            hourHandLengthRatio =
                    (centerX * analogWatchFaceStyle.watchFaceDimensions.hourHandRadiusRatio)

            /*
             * Calculate the size of the complications
             */
            complicationWidthRatio1 =
                    (width * analogWatchFaceStyle.watchFaceComplication1.widthRatio)
            complicationHeightRatio1 =
                    (height * analogWatchFaceStyle.watchFaceComplication1.heightRatio)
            complicationXPos1 =
                    ((width * analogWatchFaceStyle.watchFaceComplication1.xPos) -
                            (complicationWidthRatio1 / 2))
            complicationYPos1 =
                    ((height * analogWatchFaceStyle.watchFaceComplication1.yPos) -
                            (complicationHeightRatio1 / 2))

            complicationWidthRatio2 =
                    (width * analogWatchFaceStyle.watchFaceComplication2.widthRatio)
            complicationHeightRatio2 =
                    (height * analogWatchFaceStyle.watchFaceComplication2.heightRatio)
            complicationXPos2 =
                    ((width * analogWatchFaceStyle.watchFaceComplication2.xPos) -
                            (complicationWidthRatio2 / 2))
            complicationYPos2 =
                    ((height * analogWatchFaceStyle.watchFaceComplication2.yPos) -
                            (complicationHeightRatio2 / 2))

            complicationWidthRatio3 =
                    (width * analogWatchFaceStyle.watchFaceComplication3.widthRatio)
            complicationHeightRatio3 =
                    (height * analogWatchFaceStyle.watchFaceComplication3.heightRatio)
            complicationXPos3 =
                    ((width * analogWatchFaceStyle.watchFaceComplication3.xPos) -
                            (complicationWidthRatio3 / 2))
            complicationYPos3 =
                    ((height * analogWatchFaceStyle.watchFaceComplication3.yPos) -
                            (complicationHeightRatio3 / 2))

            val bounds1 = Rect(
                    complicationXPos1.roundToInt(), // Left
                    complicationYPos1.roundToInt(), // Top
                    (complicationXPos1 + complicationWidthRatio1).roundToInt(), // Right
                    (complicationYPos1 + complicationHeightRatio1).roundToInt()) // Bottom

            complicationDrawableSparseArray.get(complicationIds[0])?.bounds = bounds1

            val bounds2 = Rect(
                    complicationXPos2.roundToInt(), // Left
                    complicationYPos2.roundToInt(), // Top
                    (complicationXPos2 + complicationWidthRatio2).roundToInt(), // Right
                    (complicationYPos2 + complicationHeightRatio2).roundToInt()) // Bottom

            complicationDrawableSparseArray.get(complicationIds[1])?.bounds = bounds2

            val bounds3 = Rect(
                    complicationXPos3.roundToInt(), // Left
                    complicationYPos3.roundToInt(), // Top
                    (complicationXPos3 + complicationWidthRatio3).roundToInt(), // Right
                    (complicationYPos3 + complicationHeightRatio3).roundToInt()) // Bottom

            complicationDrawableSparseArray.get(complicationIds[2])?.bounds = bounds3

            Log.d("Tag", "Set Bounds")

            if (backgroundImageEnabled) {
                // Scale loaded background image (more efficient) if surface dimensions change.
                val scale = width.toFloat() / backgroundBitmap.width.toFloat()

                backgroundBitmap = Bitmap.createScaledBitmap(
                        backgroundBitmap,
                        (backgroundBitmap.width * scale).toInt(),
                        (backgroundBitmap.height * scale).toInt(), true
                )

                /*
                 * Create a gray version of the image only if it will look nice on the device in
                 * ambient mode. That means we don't want devices that support burn-in
                 * protection (slight movements in pixels, not great for images going all the way
                 * to edges) and low ambient mode (degrades image quality).
                 *
                 * Also, if your watch face will know about all images ahead of time (users aren't
                 * selecting their own photos for the watch face), it will be more
                 * efficient to create a black/white version (png, etc.) and load that when
                 * you need it.
                 */
                if (!burnInProtection && !lowBitAmbient) {
                    initGrayBackgroundBitmap()
                }
            }

            if(analogWatchFaceStyle.watchFaceHourHand.drawable != 0) {
                val offset = analogWatchFaceStyle.watchFaceHourHand.offset
                hourHandDrawable = getDrawable(analogWatchFaceStyle.watchFaceHourHand.drawable)
                hourHandDrawable?.colorFilter =
                        PorterDuffColorFilter(analogWatchFaceStyle.watchFaceColors.main,
                                PorterDuff.Mode.MULTIPLY)
                val dWidth = hourHandDrawable?.intrinsicWidth
                val dHeight = hourHandDrawable?.intrinsicHeight

                val outHeight = dHeight?.minus(dHeight.times(offset))
                val scale = outHeight?.div(width * analogWatchFaceStyle.watchFaceDimensions.hourHandRadiusRatio)

                Log.d("Tag", "Out Height: $outHeight Width: $width Hour Scale: $scale")

                hourHandWidth = dWidth!!.times(scale!!)
                hourHandHeight = dHeight.times(scale)
                hourOffset = hourHandHeight * offset
            }

            if(analogWatchFaceStyle.watchFaceMinuteHand.drawable != 0) {
                val offset = analogWatchFaceStyle.watchFaceMinuteHand.offset
                minuteHandDrawable = getDrawable(analogWatchFaceStyle.watchFaceMinuteHand.drawable)
                minuteHandDrawable?.colorFilter =
                        PorterDuffColorFilter(analogWatchFaceStyle.watchFaceColors.main,
                                PorterDuff.Mode.MULTIPLY)
                val dWidth = minuteHandDrawable?.intrinsicWidth
                val dHeight = minuteHandDrawable?.intrinsicHeight

                val outHeight = dHeight?.minus(dHeight.times(offset))
                val scale = outHeight?.div(width * analogWatchFaceStyle.watchFaceDimensions.minuteHandRadiusRatio)
                Log.d("Tag", "Out Height: $outHeight Width: $width Minute Scale: $scale")

                minuteHandWidth = dWidth!!.times(scale!!)
                minuteHandHeight = dHeight.times(scale)
                minuteOffset = minuteHandHeight * offset
            }
        }

        private fun initGrayBackgroundBitmap() {
            grayBackgroundBitmap = Bitmap.createBitmap(
                    backgroundBitmap.width,
                    backgroundBitmap.height,
                    Bitmap.Config.ARGB_8888
            )
            val canvas = Canvas(grayBackgroundBitmap)
            val grayPaint = Paint()
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0f)
            val filter = ColorMatrixColorFilter(colorMatrix)
            grayPaint.colorFilter = filter
            canvas.drawBitmap(backgroundBitmap, 0f, 0f, grayPaint)
        }

        override fun onDraw(canvas: Canvas, bounds: Rect) {
            val now = System.currentTimeMillis()
            calendar.timeInMillis = now

            drawBackground(canvas)
            drawComplications(canvas, now)
            drawWatchFace(canvas)
        }

        private fun drawBackground(canvas: Canvas) {

            if (ambient && (lowBitAmbient || burnInProtection)) {
                canvas.drawColor(backgroundAmbientPaint.color)

            } else if (ambient && backgroundImageEnabled) {
                canvas.drawBitmap(grayBackgroundBitmap, 0f, 0f, backgroundAmbientPaint)

            } else if (backgroundImageEnabled) {
                canvas.drawBitmap(backgroundBitmap, 0f, 0f, backgroundPaint)

            } else {
                canvas.drawColor(backgroundPaint.color)
            }
        }

        private fun drawWatchFace(canvas: Canvas) {
            /*
             * Draw ticks. Usually you will want to bake this directly into the photo, but in
             * cases where you want to allow users to select their own photos, this dynamically
             * creates them on top of the photo.
             */
            val innerTickRadius = centerX - 10
            val outerTickRadius = centerX
            for (tickIndex in 0..11) {
                val tickRot = (tickIndex.toDouble() * Math.PI * 2.0 / 12).toFloat()
                val innerX = Math.sin(tickRot.toDouble()).toFloat() * innerTickRadius
                val innerY = (-Math.cos(tickRot.toDouble())).toFloat() * innerTickRadius
                val outerX = Math.sin(tickRot.toDouble()).toFloat() * outerTickRadius
                val outerY = (-Math.cos(tickRot.toDouble())).toFloat() * outerTickRadius
                canvas.drawLine(
                        centerX + innerX, centerY + innerY,
                        centerX + outerX, centerY + outerY, tickAndCirclePaint
                )
            }

            val screenWidth = centerX * 2
            val screenHeight = centerY * 2
            val textBounds = Rect()
            datePaint.getTextBounds("3", 0, 1, textBounds)
            val textWidth = textBounds.width().toFloat()
            val textHeight = textBounds.height().toFloat()
            datePaint.getTextBounds("12", 0, 2, textBounds)
            val topOffset = (textBounds.width() / 2).toFloat()
            val offset = 12f

            if(isInAmbientMode)
                datePaint.color = Color.WHITE
            else
                datePaint.color = analogWatchFaceStyle.watchFaceColors.highlight

            // draw out the number ticks
            if(analogWatchFaceStyle.watchFaceStyle.topNumber)
                canvas.drawText("12", centerX - topOffset,
                        textHeight + offset, datePaint)
            if(analogWatchFaceStyle.watchFaceStyle.rightNumber)
                canvas.drawText("3", screenWidth - textWidth - offset,
                        centerY + textHeight / 2, datePaint)
            if(analogWatchFaceStyle.watchFaceStyle.bottomNumber)
                canvas.drawText("6", centerX - textWidth / 2,
                        screenHeight - offset, datePaint)
            if(analogWatchFaceStyle.watchFaceStyle.leftNumber)
                canvas.drawText("9", offset, centerY + textHeight / 2, datePaint)

            /*
             * These calculations reflect the rotation in degrees per unit of time, e.g.,
             * 360 / 60 = 6 and 360 / 12 = 30.
             */
            val seconds =
                calendar.get(Calendar.SECOND) + calendar.get(Calendar.MILLISECOND) / 1000f
            val secondsRotation = seconds * 6f

            val minutesRotation = calendar.get(Calendar.MINUTE) * 6f

            val hourHandOffset = calendar.get(Calendar.MINUTE) / 2f
            val hoursRotation = calendar.get(Calendar.HOUR) * 30 + hourHandOffset

            /*
             * Save the canvas state before we can begin to rotate it.
             */
            canvas.save()

            val distanceFromCenterToArms =
                analogWatchFaceStyle.watchFaceDimensions.innerCircleRadius +
                        analogWatchFaceStyle.watchFaceDimensions.innerCircleToArmsDistance

            canvas.rotate(hoursRotation, centerX, centerY)
            if(hourHandDrawable == null) {
                canvas.drawLine(
                        centerX,
                        centerY - distanceFromCenterToArms,
                        centerX,
                        centerY - hourHandLengthRatio,
                        hourPaint
                )
            } else {
                hourHandDrawable?.bounds?.set(
                        (centerX - (hourHandWidth/2)).toInt(),
                        (centerY - hourHandHeight + hourOffset).toInt(),
                        (centerX + (hourHandWidth/2)).toInt(),
                        (centerY + hourOffset).toInt())
                hourHandDrawable?.draw(canvas)
            }

            canvas.rotate(minutesRotation - hoursRotation, centerX, centerY)
            if(minuteHandDrawable == null) {
                canvas.drawLine(
                        centerX,
                        centerY - distanceFromCenterToArms,
                        centerX,
                        centerY - minuteHandLengthRatio,
                        minutePaint
                )
            } else {
                minuteHandDrawable?.bounds?.set(
                        (centerX - (minuteHandWidth/2)).toInt(),
                        (centerY - minuteHandHeight + minuteOffset).toInt(),
                        (centerX + (minuteHandWidth/2)).toInt(),
                        (centerY + minuteOffset).toInt())
                minuteHandDrawable?.draw(canvas)
            }

            /*
             * Ensure the "seconds" hand is drawn only when we are in interactive mode.
             * Otherwise, we only update the watch face once a minute.
             */
            if (!ambient) {
                canvas.rotate(secondsRotation - minutesRotation, centerX, centerY)
                canvas.drawLine(
                        centerX,
                        centerY - distanceFromCenterToArms,
                        centerX,
                        centerY - secondHandLengthRatio,
                        secondPaint
                )
            }
            canvas.drawCircle(
                    centerX,
                    centerY,
                    analogWatchFaceStyle.watchFaceDimensions.innerCircleRadius,
                    circlePaint
            )

            /* Restore the canvas' original orientation. */
            canvas.restore()
        }

        private fun drawComplications(canvas: Canvas, currentTimeMillis: Long) {
            if(!SettingsUtil.GetHideComplications(applicationContext) || !isInAmbientMode) {
                var complicationId: Int
                var complicationDrawable: ComplicationDrawable?
                for (i in 0..(complicationIds.size - 1)) {
                    complicationId = complicationIds[i]
                    val complicationData: ComplicationData? = complicationDataSparseArray.get(complicationId)

                    if (complicationData != null) {
                        complicationDrawable = complicationDrawableSparseArray.get(complicationId)

                        if (complicationId == complicationId1 &&
                                complicationData.type == ComplicationData.TYPE_LONG_TEXT)
                                /*complicationData.longTitle != null &&
                                complicationData.longTitle.getText(baseContext, currentTimeMillis)
                                == "That is when\nthe world will end")*/
                            drawDarkoLongText(complicationDrawable, complicationData, currentTimeMillis, canvas)
                        else
                            complicationDrawable?.draw(canvas, currentTimeMillis)
                    }
                }
            }
        }

        private fun drawDarkoLongText(complicationDrawable: ComplicationDrawable?,
                                      complicationData: ComplicationData,
                                      currentTimeMillis: Long,
                                      canvas: Canvas) {
            if(complicationDrawable?.bounds != null) {
                val textRect = TextRect(datePaint)

                var titleText: String? = null
                if(complicationData.longTitle != null) {
                    titleText = complicationData.longTitle.getText(applicationContext, currentTimeMillis).toString()
                } else if (complicationData.shortTitle != null) {
                    titleText = complicationData.shortTitle.getText(applicationContext, currentTimeMillis).toString()
                }

                val height = (complicationDrawable.bounds.height().toFloat()/2).toInt()

                /*if(!complicationData.longTitle.getText(baseContext, currentTimeMillis).toString().isEmpty()
                || !complicationData.shortTitle.getText(baseContext, currentTimeMillis).toString().isEmpty())
                    height = height.toFloat().div(2).toInt()*/
                val width = (complicationDrawable.bounds.width().toFloat() * 1.6f).toInt()

                textRect.prepare(complicationData.longText.getText(applicationContext, currentTimeMillis).toString(),
                        width, // width
                         height) // height

                val left = (complicationDrawable.bounds.left - (complicationDrawable.bounds.width().toFloat() / 4)).toInt()

                textRect.draw(canvas,
                        left, // left
                        complicationDrawable.bounds.top) // top

                if(titleText != null) {
                    val titleRect = TextRect(textPaint)
                    titleRect.prepare(titleText,
                            width,
                            height)

                    titleRect.draw(canvas,
                            (left * 1.25f).toInt(),
                            complicationDrawable.bounds.top + height)

                }
            }
        }

        private fun drawMultiLineText(str: String, x: Float, y: Float, paint: Paint, canvas: Canvas) {
            val lines = str.split("\n".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            var txtSize = -paint.ascent() + paint.descent()

            if (paint.style == Paint.Style.FILL_AND_STROKE || paint.style == Paint.Style.STROKE) {
                txtSize += paint.strokeWidth //add stroke width to the text size
            }
            val lineSpace = txtSize * 0.2f  //default line spacing

            for (i in lines.indices) {
                canvas.drawText(lines[i], x, y + (txtSize + lineSpace) * i, paint)
            }
        }

        override fun onVisibilityChanged(visible: Boolean) {
            super.onVisibilityChanged(visible)

            if (visible) {
                registerReceiver()
                /* Update time zone in case it changed while we weren't visible. */
                calendar.timeZone = TimeZone.getDefault()
                invalidate()
            } else {
                unregisterReceiver()
            }

            /* Check and trigger whether or not timer should be running (only in active mode). */
            updateTimer()
        }

        private fun registerReceiver() {
            if (registeredTimeZoneReceiver) {
                return
            }
            registeredTimeZoneReceiver = true
            val filter = IntentFilter(Intent.ACTION_TIMEZONE_CHANGED)
            this@AbstractKotlinWatchFace.registerReceiver(timeZoneReceiver, filter)
        }

        private fun unregisterReceiver() {
            if (!registeredTimeZoneReceiver) {
                return
            }
            registeredTimeZoneReceiver = false
            this@AbstractKotlinWatchFace.unregisterReceiver(timeZoneReceiver)
        }

        /**
         * Starts/stops the [.updateTimeHandler] timer based on the state of the watch face.
         */
        private fun updateTimer() {
            updateTimeHandler.removeMessages(MSG_UPDATE_TIME)
            if (shouldTimerBeRunning()) {
                updateTimeHandler.sendEmptyMessage(MSG_UPDATE_TIME)
            }
        }

        /**
         * Returns whether the [.updateTimeHandler] timer should be running. The timer
         * should only run in active mode.
         */
        private fun shouldTimerBeRunning(): Boolean {
            return isVisible && !ambient
        }

        /**
         * Handle updating the time periodically in interactive mode.
         */
        fun handleUpdateTimeMessage() {
            invalidate()
            if (shouldTimerBeRunning()) {
                val timeMs = System.currentTimeMillis()
                val delayMs = INTERACTIVE_UPDATE_RATE_MS - timeMs % INTERACTIVE_UPDATE_RATE_MS
                updateTimeHandler.sendEmptyMessageDelayed(MSG_UPDATE_TIME, delayMs)
            }
        }

        private fun getBitmapFromVectorDrawable(drawable: Drawable?, width: Float, height: Float, highlight: Boolean): Bitmap {
            val widthInt = (width/1.25f).toInt()
            val heightInt = (height/1.25f).toInt()
            val bitmap = Bitmap.createBitmap(widthInt, heightInt, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)

            if (drawable != null) {
                drawable.setBounds(0, 0, widthInt, heightInt)
                drawable.colorFilter = PorterDuffColorFilter(hourPaint.color, PorterDuff.Mode.SRC_IN)
                drawable.draw(canvas)
            }
            return if (highlight)
                doHighlightImage(bitmap)
            else
                bitmap
        }

        private fun doHighlightImage(src: Bitmap): Bitmap {
            val bmOut = Bitmap.createBitmap(src.width + 96,
                    src.height + 96, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bmOut)
            canvas.drawColor(0, PorterDuff.Mode.CLEAR)
            val ptBlur = Paint()
            ptBlur.maskFilter = BlurMaskFilter(8f, BlurMaskFilter.Blur.NORMAL)
            val offsetXY = IntArray(2)
            val bmAlpha = src.extractAlpha(ptBlur, offsetXY)
            val ptAlphaColor = Paint()
            ptAlphaColor.color = Color.BLACK
            canvas.drawBitmap(bmAlpha, (offsetXY[0] + 43).toFloat(), (offsetXY[1] + 43).toFloat(), ptAlphaColor)
            bmAlpha.recycle()
            canvas.drawBitmap(src, 43f, 43f, null)
            return bmOut
        }
    }
}

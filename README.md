Darko Face
============
A Darko-themed watch face for Wear OS written with the Kotlin DSL

Screenshots
------------
![alt text](https://lh3.googleusercontent.com/loRFVTJ-FSgz5pPW8_RYhRnygJMqiYhwGPDxzogfXjqkCNz2G-MJJXWQcTz72ntALiJ0=w1920-h969-rw)
![alt text](https://lh3.googleusercontent.com/r0GGZDG2TtLl6dn8RLqVgU0gDtsq1bCTUPgx3cns4IQ61Ws4M8Tmg92p95GuXX8Qag=w1920-h969-rw)
![alt text](https://lh3.googleusercontent.com/Sm181grPxvXju1W-q1E-jSINfEjKI8SGN5nsnAlHr2XGSBxvUcMSznriJQuc_RJX_9Tf=w1920-h969-rw)
![alt text](https://lh3.googleusercontent.com/Nv2y9SrvGsJT5YEI1gInHn2FDNZY463QCYH7X9CSkoZ_m9307oYh04fz8jCpctoI7Q=w1920-h969-rw)

Introduction
------------
This is an expansion of the codelab [This codelab](http://github.com/googlesamples/watchface-kotlin) 
This example shows how to use the Kotlin DSL for watchfaces to add complications and other features. Feel free to modify to create your own watch face


How To Use
------------
Create a new Kotlin class that extends AbstractKotlinWatchFace. Here is an example
```
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
```

License
-------

Licensed to the Apache Software Foundation (ASF) under one or more contributor
license agreements.  See the NOTICE file distributed with this work for
additional information regarding copyright ownership.  The ASF licenses this
file to you under the Apache License, Version 2.0 (the "License"); you may not
use this file except in compliance with the License.  You may obtain a copy of
the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
License for the specific language governing permissions and limitations under
the License.
#darko-face

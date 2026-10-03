/*
The MIT License

Copyright (c) 2026 DigitalWorldPure

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
 */

package com.goldenankh.purewave.ui.screens

import android.media.MediaPlayer
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.goldenankh.purewave.R
import com.goldenankh.purewave.ui.components.animatedLogo.AnimatedLogo
import com.goldenankh.purewave.ui.components.animatedwave.AnimatedWave
import com.goldenankh.purewave.ui.components.crystal.Crystal
import com.goldenankh.purewave.ui.theme.PureWaveTheme
import kotlinx.coroutines.delay
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    windowSizeClass: WindowSizeClass
) {
    val offsetX = remember { Animatable(-1000f) }
    val animatedScale = remember { Animatable(1f) }
    val context = LocalContext.current

    val logoWeightPadding = when (windowSizeClass.widthSizeClass) {
        WindowWidthSizeClass.Expanded -> 0.2f
        WindowWidthSizeClass.Medium -> 0.1f
        else -> 0.05f
    }

    LaunchedEffect(Unit) {
        // Play sound
        val mediaPlayer = MediaPlayer.create(
            context,
            R.raw.crystal_sound
        )

        mediaPlayer.start()

        mediaPlayer.setOnCompletionListener {
            it.release()
        }

        //Animating the movement of the label from the invisible area on the left side of the screen
        offsetX.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 600,
                easing = FastOutSlowInEasing
            )
        )

        // Animating crystal size
        animatedScale.animateTo(
            targetValue = 0f,
            animationSpec = tween(
                durationMillis = 200,
                easing = FastOutSlowInEasing
            )
        )

        animatedScale.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = 200,
                easing = FastOutSlowInEasing
            )
        )

        delay(120.milliseconds)
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PureWaveTheme.colorScheme.primary)
    ) {
        Crystal(
            modifier = Modifier
                .align(Alignment.Center)
                .graphicsLayer {
                    scaleX = animatedScale.value
                    scaleY = animatedScale.value
                }
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp),
                horizontalArrangement = Arrangement.Center

            ) {
                Spacer(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(logoWeightPadding)
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f - logoWeightPadding)
                ) {
                    AnimatedLogo(
                        modifier = Modifier
                            .width(280.dp)
                            .height(160.dp)
                            .offset {
                                IntOffset(
                                    x = offsetX.value.roundToInt(),
                                    y = 0
                                )
                            },
                        startColor = PureWaveTheme.colorScheme.onPrimary,
                        gradientTopColor = PureWaveTheme.additionalColors.logoGradientTopColor,
                        gradientBottomColor = PureWaveTheme.additionalColors.logoGradientBottomColor,
                        fadeInDuration = 500,
                        gradientDuration = 500
                    )
                }
            }
            AnimatedWave(
                modifier = Modifier
                    .padding(top = 50.dp)
                    .fillMaxWidth()
                    .height(200.dp),
                durationMillis = 900,
                shineDurationMillis = 300,
                color = PureWaveTheme.colorScheme.onPrimary,
                strokeWidth = 3f
            )
        }
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    PureWaveTheme {
        SplashScreen(
            {},
            WindowSizeClass.calculateFromSize(DpSize(320.dp, 500.dp))
        )
    }
}


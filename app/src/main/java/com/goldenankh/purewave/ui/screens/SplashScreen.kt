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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.goldenankh.purewave.R
import com.goldenankh.purewave.ui.components.animatedLogo.AnimatedLogo
import com.goldenankh.purewave.ui.components.animatedwave.AnimatedWave
import com.goldenankh.purewave.ui.components.crystal.Crystal
import kotlin.math.roundToInt

@Composable
fun SplashScreen(
    onFinished: () -> Unit
) {
    val offsetX = remember { Animatable(-1000f) }
    val animatedScale = remember { Animatable(1f) }
    val context = LocalContext.current

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
        onFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1E42DE))
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
                startColor = Color.White,
                gradientTopColor = Color(0xFF7B61FF),
                gradientBottomColor = Color(0xFFFF4D8D),
                fadeInDuration = 500,
                gradientDuration = 500
            )
            AnimatedWave(
                modifier = Modifier
                    .padding(top = 50.dp)
                    .fillMaxWidth()
                    .height(200.dp),
                durationMillis = 900,
                shineDurationMillis = 300,
                color = Color.White,
                strokeWidth = 3f
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SplashScreenPreview() {
    SplashScreen({})
}


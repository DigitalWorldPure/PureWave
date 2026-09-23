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

package com.goldenankh.purewave.ui.components.animatedwave

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.min

@Composable
fun AnimatedWave(
    modifier: Modifier = Modifier,
    // Wave animation duration
    durationMillis: Int = 2000,
    // Shine animation duration
    shineDurationMillis: Int = 500,
    color: Color = Color.Black,
    strokeWidth: Float = 3f
) {

    // =========================================================
    // 1. Wave animation progress
    // =========================================================

    val progress = remember {
        Animatable(0f)
    }


    // =========================================================
    // 2. Shine animation progress
    // =========================================================

    val shineProgress = remember {
        Animatable(0f)
    }


    // =========================================================
    // 3. Sequential execution:
    //
    // First, draw the wave completely.
    // Then, begin the appearance of the shine.
    // =========================================================

    LaunchedEffect(
        durationMillis,
        shineDurationMillis
    ) {

        // ─────────────────────────────────────────
        // First, the wave
        // ─────────────────────────────────────────

        progress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = durationMillis,
                easing = LinearEasing
            )
        )


        // ─────────────────────────────────────────
        // We begin the appearance of the shine only
        // after the wave has fully completed
        // ─────────────────────────────────────────

        shineProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = shineDurationMillis,
                easing = LinearEasing
            )
        )
    }


    // =========================================================
    // 4. Canvas
    // =========================================================

    Canvas(
        modifier = modifier
    ) {

        // Preserving the proportions of the original SVG
        val scaleX =
            size.width / SVG_WIDTH

        val scaleY =
            size.height / SVG_HEIGHT

        val scale =
            min(scaleX, scaleY)


        // Centering the SVG
        val offsetX =
            (size.width - SVG_WIDTH * scale) / 2f

        val offsetY =
            (size.height - SVG_HEIGHT * scale) / 2f


        // =====================================================
        // 5. Creating the wave path
        // =====================================================

        val path = createWavePath(
            scale = scale,
            offsetX = offsetX,
            offsetY = offsetY
        )


        // =====================================================
        // 6. Measuring the Path length
        // =====================================================

        val pathMeasure = PathMeasure()

        pathMeasure.setPath(
            path = path,
            forceClosed = false
        )

        val totalLength =
            pathMeasure.length


        // =====================================================
        // 7. Current wave length
        // =====================================================

        val currentLength =
            totalLength * progress.value


        // =====================================================
        // 8. Get the drawn wave segment
        // =====================================================

        val animatedPath = Path()

        pathMeasure.getSegment(
            startDistance = 0f,
            stopDistance = currentLength,
            destination = animatedPath,
            startWithMoveTo = true
        )


        // =====================================================
        // 9. Drawing a wave
        // =====================================================

        drawPath(
            path = animatedPath,
            color = color,
            style = Stroke(
                width = strokeWidth * scale,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )


        // =====================================================
        // 10. Creating a shine
        // =====================================================

        val shinePath = createShinePath(
            scale = scale,

            centerX =
                offsetX + WAVE_END_X * scale,

            centerY =
                offsetY + WAVE_END_Y * scale
        )


        // =====================================================
        // 11. Draw a shine over the wave
        // =====================================================

        if (shineProgress.value > 0f) {

            drawPath(
                path = shinePath,
                color = color.copy(
                    alpha = shineProgress.value
                ),
                style = Fill
            )
        }
    }
}

@Preview
@Composable
fun AnimatedWavePreview() {
    AnimatedWave()
}


// Wave SVG size
private const val SVG_WIDTH = 161.9f
private const val SVG_HEIGHT = 111.3f

// Shine SVG size
private const val SHINE_WIDTH = 18.2f
private const val SHINE_HEIGHT = 19.6f

// The endpoint of the wave where we will place the shine SVG
private const val WAVE_END_X = 158.5f
private const val WAVE_END_Y = 55.6f


/**
 * Draw a wave from left to right
 */
private fun createWavePath(
    scale: Float,
    offsetX: Float,
    offsetY: Float
): Path {

    fun x(value: Float) =
        offsetX + value * scale

    fun y(value: Float) =
        offsetY + value * scale

    return Path().apply {

        moveTo(
            x(5.3f),
            y(55.6f)
        )

        cubicTo(
            x(5.3f), y(82.9f),
            x(13.7f), y(105.0f),
            x(24.3f), y(105.0f)
        )

        cubicTo(
            x(34.9f), y(105.0f),
            x(43.4f), y(82.9f),
            x(43.4f), y(55.6f)
        )

        cubicTo(
            x(43.4f), y(28.3f),
            x(51.9f), y(6.3f),
            x(62.4f), y(6.3f)
        )

        cubicTo(
            x(72.9f), y(6.3f),
            x(81.5f), y(28.3f),
            x(81.5f), y(55.6f)
        )

        cubicTo(
            x(81.5f), y(82.9f),
            x(90.0f), y(105.0f),
            x(100.6f), y(105.0f)
        )

        cubicTo(
            x(111.1f), y(105.0f),
            x(119.7f), y(82.9f),
            x(119.7f), y(55.6f)
        )

        cubicTo(
            x(119.7f), y(28.3f),
            x(128.2f), y(6.3f),
            x(138.7f), y(6.3f)
        )

        cubicTo(
            x(149.3f), y(6.3f),
            x(157.8f), y(28.3f),
            x(157.8f), y(55.6f)
        )
    }
}

private fun createShinePath(
    scale: Float,
    centerX: Float,
    centerY: Float
): Path {

    val offsetX =
        centerX - (SHINE_WIDTH / 2f) * scale

    val offsetY =
        centerY - (SHINE_HEIGHT / 2f) * scale


    fun x(value: Float): Float =
        offsetX + value * scale

    fun y(value: Float): Float =
        offsetY + value * scale


    return Path().apply {

        moveTo(
            x(7.0f),
            y(7.0f)
        )

        lineTo(
            x(8.1f),
            y(0.4f)
        )

        lineTo(
            x(8.7f),
            y(7.0f)
        )

        lineTo(
            x(11.3f),
            y(2.7f)
        )

        lineTo(
            x(10.0f),
            y(7.0f)
        )

        lineTo(
            x(12.7f),
            y(5.5f)
        )

        lineTo(
            x(10.9f),
            y(7.9f)
        )

        lineTo(
            x(17.6f),
            y(7.0f)
        )

        lineTo(
            x(11.0f),
            y(9.0f)
        )

        lineTo(
            x(13.9f),
            y(9.9f)
        )

        lineTo(
            x(10.6f),
            y(10.4f)
        )

        lineTo(
            x(11.5f),
            y(13.5f)
        )

        lineTo(
            x(8.7f),
            y(11.0f)
        )

        lineTo(
            x(9.6f),
            y(18.8f)
        )

        lineTo(
            x(7.0f),
            y(11.3f)
        )

        lineTo(
            x(6.4f),
            y(14.0f)
        )

        lineTo(
            x(5.8f),
            y(11.0f)
        )

        lineTo(
            x(3.0f),
            y(13.6f)
        )

        lineTo(
            x(5.2f),
            y(9.6f)
        )

        lineTo(
            x(1.1f),
            y(8.8f)
        )

        lineTo(
            x(4.9f),
            y(8.8f)
        )

        lineTo(
            x(0.5f),
            y(4.8f)
        )

        lineTo(
            x(5.8f),
            y(8.1f)
        )

        lineTo(
            x(3.3f),
            y(2.6f)
        )

        close()
    }
}

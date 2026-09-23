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

package com.goldenankh.purewave.ui.components.animatedLogo

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.vector.PathParser
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun AnimatedLogo(
    modifier: Modifier = Modifier,
    startColor: Color = Color.Black,
    gradientTopColor: Color,
    gradientBottomColor: Color,
    fadeInDuration: Int = 700,
    gradientDuration: Int = 1200
) {
    val alpha = remember {
        Animatable(0f)
    }

    val gradientProgress = remember {
        Animatable(0f)
    }

    LaunchedEffect(Unit) {
        alpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = fadeInDuration,
                easing = LinearEasing
            )
        )

        gradientProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(
                durationMillis = gradientDuration,
                easing = LinearEasing
            )
        )
    }

    val topColor = lerp(
        start = startColor,
        stop = gradientTopColor,
        fraction = gradientProgress.value
    )

    val bottomColor = lerp(
        start = startColor,
        stop = gradientBottomColor,
        fraction = gradientProgress.value
    )

    val paths = remember {
        logoPathData.map { data ->
            PathParser()
                .parsePathString(data)
                .toPath()
        }
    }

    Canvas(
        modifier = modifier
    ) {
        drawLogo(
            paths = paths,
            alpha = alpha.value,
            topColor = topColor,
            bottomColor = bottomColor
        )
    }
}

private fun DrawScope.drawLogo(
    paths: List<Path>,
    alpha: Float,
    topColor: Color,
    bottomColor: Color
) {
    val scaleX = size.width / VIEWBOX_WIDTH
    val scaleY = size.height / VIEWBOX_HEIGHT

    val scale = minOf(scaleX, scaleY)

    val offsetX = (size.width - VIEWBOX_WIDTH * scale) / 2f
    val offsetY = (size.height - VIEWBOX_HEIGHT * scale) / 2f

    val brush = Brush.linearGradient(
        colors = listOf(
            topColor,
            bottomColor
        ),
        start = Offset(
            x = 0f,
            y = offsetY
        ),
        end = Offset(
            x = 0f,
            y = offsetY + VIEWBOX_HEIGHT * scale
        )
    )

    paths.forEach { originalPath ->
        val path = Path().apply {
            addPath(
                originalPath,
                Offset(
                    x = offsetX,
                    y = offsetY
                )
            )

            transform(
                Matrix().apply {
                    scale(scale, scale)
                }
            )
        }

        drawPath(
            path = path,
            brush = brush,
            alpha = alpha,
            style = Fill
        )
    }
}

@Preview
@Composable
fun AnimatedLogoPreview() {
    AnimatedLogo(
        gradientTopColor = Color(0xFF7B61FF),
        gradientBottomColor = Color(0xFFFF4D8D)
    )
}

private const val VIEWBOX_WIDTH = 92.9f
private const val VIEWBOX_HEIGHT = 52.7f

private val logoPathData = listOf(
    """
    M11.7,5
    C14.5,5 16.7,5.5 18.2,6.5
    C20,7.7 21,9.4 21,11.9
    C21,17.1 16.6,19.3 12.7,19.4
    C12.3,19.4 11.9,19.4 11.6,19.3
    L9.5,18.8
    L9.5,23.7
    C9.5,26.7 9.8,26.9 12.9,27.2
    L12.9,28.5
    L1.4,28.5
    L1.4,27.2
    C4.1,26.9 4.5,26.8 4.5,23.7
    L4.5,9.9
    C4.5,6.9 4.1,6.6 1.4,6.4
    L1.4,5
    L11.7,5
    Z

    M9.5,17.2
    C9.9,17.4 10.7,17.6 11.4,17.6
    C13.1,17.6 15.8,16.6 15.8,11.9
    C15.8,7.9 13.6,6.5 11.3,6.5
    C10.5,6.5 10.1,6.7 9.9,6.9
    C9.6,7.1 9.5,7.4 9.5,8
    L9.5,17.2
    Z
    """,

    """
    M41.3,27.5
    C39.1,27.8 36.8,28.3 34.5,28.8
    C34.5,28 34.5,27.1 34.5,26.4
    C33,27.6 31.5,28.8 29.5,28.8
    C26.5,28.8 24.8,26.9 24.8,23.7
    L24.8,16.2
    C24.8,14.5 24.6,14.4 23.6,14.1
    L22.8,13.9
    L22.8,12.7
    C24.6,12.6 27.5,12.4 29.5,12.1
    C29.4,13.6 29.4,15.5 29.4,17.5
    L29.4,22.8
    C29.4,25.1 30.6,25.9 31.8,25.9
    C32.8,25.9 33.6,25.7 34.4,24.9
    L34.4,16.2
    C34.4,14.5 34.2,14.3 33.2,14.1
    L32.1,13.9
    L32.1,12.7
    C34.2,12.6 37.2,12.4 39,12.1
    L39,23.8
    C39,25.7 39.2,26.1 40.4,26.2
    L41.2,26.3
    L41.3,27.5
    Z
    """,

    """
    M52.5,28.4
    L43,28.4
    L43,27.2
    C44.9,27 45.1,26.8 45.1,24.6
    L45.1,16.7
    C45.1,14.9 45,14.7 43.3,14.4
    L43.3,13.3
    C45.5,13 47.5,12.6 49.8,11.9
    C49.8,13.1 49.8,14.6 49.8,15.8
    C51.4,13.3 52.7,12 54.2,12
    C55.5,12 56.5,12.9 56.5,14.2
    C56.5,16 55.2,17 54.7,17.3
    C54.2,17.5 53.8,17.3 53.6,17.1
    C53,16.6 52.5,16 51.9,16
    C51.4,16 50.5,16.5 49.8,17.8
    L49.8,24.6
    C49.8,26.8 50.1,26.9 52.5,27.2
    L52.5,28.4
    Z
    """,

    """
    M71.8,24.9
    C69.6,28.1 66.7,28.9 65.2,28.9
    C60.4,28.9 57.6,25.3 57.6,21.1
    C57.6,18.6 58.8,16.1 60.3,14.6
    C62,12.9 64,12 66,12
    C69.5,12 72,14.9 71.9,17.9
    C71.9,18.5 71.9,19.1 71.4,19.2
    C70.7,19.4 65.6,19.6 62.1,19.8
    C62.2,23.6 64.4,25.6 67,25.6
    C68.4,25.6 69.8,25.1 71.1,24
    L71.8,24.9
    Z

    M65.3,13.5
    C63.9,13.5 62.6,15.2 62.4,18
    C63.9,18 65.2,17.9 66.8,17.9
    C67.3,17.9 67.5,17.8 67.5,17.1
    C67.5,15.4 66.7,13.5 65.3,13.5
    Z
    """,

    """
    M64.2,35.6
    C62.7,35.8 62.4,36.1 61.8,37.8
    C61.3,39.4 59.7,44.4 58.4,48.7
    L57.2,48.7
    C56.1,45.7 54.9,42.6 53.7,39.4
    C52.6,42.6 51.6,45.6 50.6,48.7
    L49.4,48.7
    C48.6,45.7 47.2,41.4 46,37.7
    C45.5,36.1 45.2,35.7 43.9,35.6
    L43.9,34.8
    L50.2,34.8
    L50.2,35.6
    C48.6,35.7 48.6,36.1 48.9,37.2
    C49.6,39.6 50.3,42.1 51,44.4
    C52.1,41.4 53.1,38.3 54,35
    L55.1,35
    C56.3,38.2 57.5,41.3 58.6,44.4
    C59.4,42.1 60.4,38.6 60.7,37.4
    C61,36.2 60.8,35.8 59,35.6
    L59,34.8
    L64.2,34.8
    L64.2,35.6
    Z
    """,

    """
    M71.2,48.7
    C70.6,48.7 70.2,48.5 69.9,48.3
    C69.6,48 69.5,47.8 69.4,47.5
    C68.6,48 67.6,48.7 67.2,48.7
    C65.4,48.7 64.3,47.3 64.3,46
    C64.3,44.9 64.8,44.4 65.9,43.9
    C67.1,43.4 68.8,42.9 69.3,42.5
    L69.3,41.6
    C69.3,40.5 68.9,39.9 68,39.9
    C67.6,39.9 67.3,40.1 67.2,40.3
    C66.9,40.6 66.9,41.1 66.7,41.7
    C66.6,42.3 66.2,42.4 65.8,42.4
    C65.3,42.4 64.6,41.9 64.6,41.3
    C64.6,40.9 64.9,40.6 65.3,40.3
    C66.2,39.7 67.5,39.1 68.7,38.8
    C69.5,38.8 70.3,39 70.9,39.6
    C71.8,40.2 72.1,41.1 72.1,42.2
    L72.1,45.8
    C72.1,46.8 72.4,47.1 72.8,47.1
    C73,47.1 73.2,47 73.5,46.9
    L73.7,47.6
    L71.2,48.7
    Z

    M69.3,43.4
    C68.9,43.6 68.5,43.8 68.1,44.1
    C67.4,44.4 67.1,44.8 67.1,45.5
    C67.1,46.7 67.9,47.1 68.4,47.1
    C68.7,47.1 69,47.1 69.3,46.8
    C69.3,45.8 69.3,44.4 69.3,43.4
    Z
    """,

    """
    M83.8,39.8
    C82.7,40 82.5,40.2 82.1,41.2
    C81.4,42.9 80.3,45.5 79,48.6
    L78,48.6
    C77,46 76,43.5 75,41.1
    C74.6,40.2 74.4,39.9 73.3,39.8
    L73.3,39.1
    L78.5,39.1
    L78.5,39.8
    C77.6,40 77.4,40.2 77.7,40.8
    C78.2,42.2 79,44.3 79.3,45.1
    C79.7,44.2 80.4,42.3 80.9,40.9
    C81.1,40.3 81.1,39.9 79.8,39.7
    L79.8,39
    L83.8,39
    L83.8,39.8
    Z
    """,

    """
    M92.2,46.4
    C90.9,48.3 89.2,48.7 88.4,48.7
    C85.6,48.7 84,46.6 84,44.1
    C84,42.6 84.7,41.2 85.6,40.3
    C86.5,39.4 87.7,38.8 88.9,38.8
    C91,38.8 92.4,40.5 92.4,42.2
    C92.4,42.6 92.4,42.9 92.1,43
    C91.7,43.1 88.7,43.3 86.7,43.3
    C86.8,45.5 88,46.7 89.5,46.7
    C90.3,46.7 91.1,46.4 91.9,45.8
    L92.2,46.4
    Z

    M88.4,39.7
    C87.6,39.7 86.8,40.7 86.7,42.3
    C87.6,42.3 88.4,42.3 89.2,42.2
    C89.5,42.2 89.6,42.1 89.6,41.8
    C89.7,40.8 89.2,39.7 88.4,39.7
    Z
    """
)
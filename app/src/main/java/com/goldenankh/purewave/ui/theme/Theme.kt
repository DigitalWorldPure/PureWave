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

package com.goldenankh.purewave.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLightColor,
    onSurfaceVariant = Color.Black,
    surfaceContainerHighest = PanelContainerLightColor,
    onPrimary = Color.White,
    tertiaryContainer = TertiaryLightColor,
    background = Color.White,
    onBackground = Color.Black,
    onSurface = OnSurfaceColor
)

private val LightColorScheme = lightColorScheme(
    primary = PrimaryLightColor,
    onSurfaceVariant = Color.Black,
    surfaceContainerHighest = PanelContainerLightColor,
    onPrimary = Color.White,
    tertiaryContainer = TertiaryLightColor,
    background = Color.White,
    onBackground = Color.Black,
    onSurface = OnSurfaceColor
)

val LocalExtendedColors = staticCompositionLocalOf { defaultAdditionalColors() }

@Composable
fun PureWaveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val pureWaveAdditionalColors = PureWaveAdditionalColors(
        logoGradientTopColor = LogoGradientTopColor,
        logoGradientBottomColor = LogoGradientBottomColor,
        trackHeaderBorderColor = TrackHeaderBorderColor,
        trackHeaderBackgroundColor = TrackHeaderBackgroundColor,
        playbackIndicatorColor = PlaybackIndicatorColor,
        playbackDotColor = PlaybackDotColor,
        dropIndicatorColor = DropIndicatorColor,
        dropIndicatorBorderColor = DropIndicatorBorderColor,
        scaleBorderColor = Color.Black,
        drawerDividerColor = DrawerDividerColor
    )
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    CompositionLocalProvider(LocalExtendedColors provides pureWaveAdditionalColors) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

object PureWaveTheme {
    val additionalColors: PureWaveAdditionalColors
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedColors.current
    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme
}
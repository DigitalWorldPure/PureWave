package com.goldenankh.purewave.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Immutable
data class PureWaveAdditionalColors (
    val trackHeaderBorderColor: Color,
    val trackHeaderBackgroundColor: Color,
    val playbackIndicatorColor: Color,
    val playbackDotColor: Color,
    val logoGradientTopColor: Color,
    val logoGradientBottomColor: Color,
    val dropIndicatorColor: Color,
    val dropIndicatorBorderColor: Color,
    val scaleBorderColor: Color,
    val drawerDividerColor: Color
)

fun defaultAdditionalColors() = PureWaveAdditionalColors(
    trackHeaderBorderColor = Color.Black,
    logoGradientTopColor = LogoGradientTopColor,
    logoGradientBottomColor = LogoGradientBottomColor,
    trackHeaderBackgroundColor = Color.Unspecified,
    playbackIndicatorColor = Color.Unspecified,
    playbackDotColor = Color.Unspecified,
    dropIndicatorColor = Color.Unspecified,
    dropIndicatorBorderColor = Color.Unspecified,
    scaleBorderColor = Color.Unspecified,
    drawerDividerColor = Color.White
)
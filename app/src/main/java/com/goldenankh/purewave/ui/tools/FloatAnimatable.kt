package com.goldenankh.purewave.ui.tools

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.runtime.saveable.Saver

val saver
    get() =
        Saver<Animatable<Float, AnimationVector1D>, Float>(
            save = { it.value },
            restore = { Animatable(it) }
        )
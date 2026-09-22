package com.goldenankh.purewave.ui.components.tracklist.lazy.scrollstate

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
fun rememberLazyTracksState(): LazyTracksState = remember { LazyTracksState() }
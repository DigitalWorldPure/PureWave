package com.goldenankh.purewave.ui.components.tracklist.lazy.pointerdetection

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerInputScope
import androidx.compose.ui.input.pointer.changedToUpIgnoreConsumed
import kotlin.math.sqrt

// ============================================================================
// REMOVE TAP GESTURE
// ============================================================================

suspend fun PointerInputScope.detectRemoveTap(
    onTap: (Offset) -> Unit
) {
    awaitEachGesture {

        val down = awaitFirstDown(
            requireUnconsumed = false, pass = PointerEventPass.Initial
        )

        val startPosition = down.position
        var moved = false

        while (true) {

            val event = awaitPointerEvent(
                PointerEventPass.Final
            )

            val change = event.changes.firstOrNull {
                it.id == down.id
            }

            if (change == null) {
                break
            }

            val dx = change.position.x - startPosition.x

            val dy = change.position.y - startPosition.y

            val distance = sqrt(dx * dx + dy * dy)

            if (distance > viewConfiguration.touchSlop) {
                moved = true
            }

            if (change.changedToUpIgnoreConsumed()) {
                if (!moved) {
                    onTap(
                        change.position
                    )
                }

                break
            }
        }
    }
}

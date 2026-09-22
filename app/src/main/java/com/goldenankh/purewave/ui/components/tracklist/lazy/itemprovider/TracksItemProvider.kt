package com.goldenankh.purewave.ui.components.tracklist.lazy.itemprovider

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.layout.LazyLayoutItemProvider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import com.goldenankh.domain.model.DragSession
import com.goldenankh.domain.model.TracksItem

class TracksItemProvider(
    private val session: DragSession?,
    private val flatTracks: List<TracksItem>,
    private val content: @Composable (TracksItem) -> Unit
) : LazyLayoutItemProvider {

    override val itemCount: Int
        get() = flatTracks.size

    @Composable
    override fun Item(index: Int, key: Any) {

        val item = flatTracks.getOrNull(index) ?: return
        val draggedId = session?.block?.id

        Box(
            modifier = Modifier.alpha(
                if (item.id == draggedId) {
                    0.5f
                } else {
                    1f
                }
            )
        ) {
            content(item)
        }
    }

    override fun getKey(index: Int): Any =
        flatTracks.getOrNull(index)?.id ?: "tracks-item-$index"

    override fun getIndex(key: Any): Int {

        if (key !is String) return -1

        for (index in flatTracks.indices) {
            if (flatTracks.getOrNull(index)?.id == key) {
                return index
            }
        }

        return -1
    }

    override fun getContentType(index: Int): Any? =
        when (flatTracks.getOrNull(index)) {
            is TracksItem.Block -> "block"
            is TracksItem.Gap -> "gap"
            null -> null
        }


}
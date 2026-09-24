package com.goldenankh.domain

import com.goldenankh.domain.config.TracksSettings
import com.goldenankh.domain.helpers.TrackItemIdGenerator
import com.goldenankh.domain.model.AddSelection
import com.goldenankh.domain.model.ItemPosition
import com.goldenankh.domain.model.TrackPosition
import com.goldenankh.domain.model.TrackRow
import com.goldenankh.domain.model.TracksData
import com.goldenankh.domain.model.TracksItem
import com.goldenankh.domain.model.common.RGBColor
import com.goldenankh.domain.usecases.EditTracksUseCase
import com.goldenankh.domain.usecases.EditTracksUseCaseImpl
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class EditTracksUseCaseImplTest {

    private lateinit var useCase: EditTracksUseCase

    private val itemIdGenerator = mockk<TrackItemIdGenerator>()

    private var generatedId = 0

    @Before
    fun setUp() {
        generatedId = 0

        every {
            itemIdGenerator.generateItemId()
        } answers {
            "generated-${++generatedId}"
        }

        useCase = EditTracksUseCaseImpl(itemIdGenerator)
    }

    // -------------------------------------------------------------------------
    // calculateTracksData
    // -------------------------------------------------------------------------

    @Test
    fun `calculateTracksData returns empty data for empty tracks`() {
        val result = useCase.calculateTracksData(emptyList())

        assertTrue(result.tracks.isEmpty())
        assertTrue(result.positions.isEmpty())
        assertEquals(0, result.contentWidth)
        assertEquals(0, result.contentHeight)
    }

    @Test
    fun `calculateTracksData normalizes tracks to the same width`() {
        val tracks = listOf(
            row(
                "row-1",
                gap("gap-1", 100f)
            ),
            row(
                "row-2",
                gap("gap-2", 200f)
            )
        )

        val result = useCase.calculateTracksData(tracks)

        val width1 = result.tracks[0].items.sumOf { it.width.toDouble() }.toFloat()
        val width2 = result.tracks[1].items.sumOf { it.width.toDouble() }.toFloat()

        assertEquals(width1, width2, 0.001f)
    }

    @Test
    fun `calculateTracksData preserves existing ending gap and changes its width`() {
        val tracks = listOf(
            row(
                "row-1",
                block("block-1", 50f),
                gap("gap-1", 30f)
            )
        )

        val result = useCase.calculateTracksData(tracks)

        val resultRow = result.tracks.single()
        val lastItem = resultRow.items.last()

        assertTrue(lastItem is TracksItem.Gap)
        assertEquals(
            "gap-1",
            lastItem.id
        )

        assertTrue(lastItem.width >= TracksSettings.END_TRACK_MINIMUM_SPACE)
    }

    @Test
    fun `calculateTracksData adds ending gap when track does not have one`() {
        val tracks = listOf(
            row(
                "row-1",
                block("block-1", 50f)
            )
        )

        val result = useCase.calculateTracksData(tracks)

        val lastItem = result.tracks.single().items.last()

        assertTrue(lastItem is TracksItem.Gap)
        assertTrue(lastItem.width >= TracksSettings.END_TRACK_MINIMUM_SPACE)

        verify(exactly = 1) {
            itemIdGenerator.generateItemId()
        }
    }

    @Test
    fun `calculateTracksData calculates item positions`() {
        val tracks = listOf(
            row(
                "row-1",
                block("block-1", 30f),
                gap("gap-1", 20f)
            )
        )

        val result = useCase.calculateTracksData(tracks)

        val position = result.positions["row-1"]

        assertNotNull(position)

        assertEquals(
            0f,
            position!!.itemPositions["block-1"]!!.startX,
            0.001f
        )

        assertEquals(
            30f,
            position.itemPositions["block-1"]!!.endX,
            0.001f
        )

        assertEquals(
            30f,
            position.itemPositions["gap-1"]!!.startX,
            0.001f
        )
    }

    @Test
    fun `calculateTracksData calculates track vertical positions`() {
        val tracks = listOf(
            row(
                "row-1",
                gap("gap-1", 100f)
            ),
            row(
                "row-2",
                gap("gap-2", 100f)
            )
        )

        val result = useCase.calculateTracksData(tracks)

        val first = result.positions["row-1"]!!
        val second = result.positions["row-2"]!!

        assertEquals(0, first.top)
        assertEquals(
            TracksSettings.TRACK_HEIGHT,
            first.bottom
        )

        assertEquals(
            TracksSettings.TRACK_HEIGHT,
            second.top
        )

        assertEquals(
            TracksSettings.TRACK_HEIGHT * 2,
            second.bottom
        )
    }

    @Test
    fun `calculateTracksData calculates maximum content width`() {
        val tracks = listOf(
            row(
                "row-1",
                block("block-1", 40f),
                gap("gap-1", 60f)
            ),
            row(
                "row-2",
                block("block-2", 100f),
                gap("gap-2", 20f)
            )
        )

        val result = useCase.calculateTracksData(tracks)

        val maximum = TracksSettings.END_TRACK_MINIMUM_SPACE.toInt() + 100

        assertEquals(
            maximum,
            result.contentWidth
        )
    }

    @Test
    fun `calculateTracksData calculates content height`() {
        val tracks = listOf(
            row("row-1", gap("gap-1", 100f)),
            row("row-2", gap("gap-2", 100f)),
            row("row-3", gap("gap-3", 100f))
        )

        val result = useCase.calculateTracksData(tracks)

        assertEquals(
            3 * TracksSettings.TRACK_HEIGHT,
            result.contentHeight
        )
    }

    // -------------------------------------------------------------------------
    // getDragAndDropTarget
    // -------------------------------------------------------------------------

    @Test
    fun `getDragAndDropTarget returns null when position is outside track`() {
        val block = block("block-1", 20f)

        val data = tracksData(
            row("row-1", block, gap("gap-1", 100f))
        )

        val result = useCase.getDragAndDropTarget(
            tracks = data,
            block = block,
            positionX = 10f,
            positionY = 1000f,
            currentTouchOffsetX = 0f,
            scrollX = 0f,
            scrollY = 0f
        )

        assertNull(result)
    }

    @Test
    fun `getDragAndDropTarget returns null when target is a block or targetIsSourceBlockGap`() {
        val block = block("block-1", 20f)
        val block2 = block("block-2", 200f)

        val data = tracksData(
            row(
                "row-1",
                block,
                gap("gap-1", 100f),
                block2
            )
        )

        val result = useCase.getDragAndDropTarget(
            tracks = data,
            block = block,
            positionX = 10f,
            positionY = 10f,
            currentTouchOffsetX = 0f,
            scrollX = 0f,
            scrollY = 0f
        )

        assert(result == null || result.targetIsSourceBlockGap)

        val result2 = useCase.getDragAndDropTarget(
            tracks = data,
            block = block,
            positionX = 125f,
            positionY = 0f,
            currentTouchOffsetX = 0f,
            scrollX = 0f,
            scrollY = 0f
        )
        assert(result2 == null || result2.targetIsSourceBlockGap)
    }

    @Test
    fun `getDragAndDropTarget returns target for gap`() {
        val block = block("block-1", 20f)

        val data = tracksData(
            row(
                "row-1",
                gap("gap-1", 100f)
            )
        )

        val result = useCase.getDragAndDropTarget(
            tracks = data,
            block = block,
            positionX = 30f,
            positionY = 10f,
            currentTouchOffsetX = 5f,
            scrollX = 0f,
            scrollY = 0f
        )

        assertNotNull(result)

        assertEquals(0, result!!.rowIndex)
        assertEquals(0, result.gapIndex)
        assertEquals(30f - 5f, result.offset, 0.001f)
        assertEquals(0f, result.gapStartX, 0.001f)
        assertEquals(100f, result.gapWidth, 0.001f)
        assertEquals(false, result.targetIsSourceBlockGap)
    }

    @Test
    fun `getDragAndDropTarget returns null when block is wider than gap`() {
        val block = block("block-1", 120f)

        val data = tracksData(
            row(
                "row-1",
                gap("gap-1", 100f)
            )
        )

        val result = useCase.getDragAndDropTarget(
            tracks = data,
            block = block,
            positionX = 20f,
            positionY = 10f,
            currentTouchOffsetX = 0f,
            scrollX = 0f,
            scrollY = 0f
        )

        assertNull(result)
    }

    @Test
    fun `getDragAndDropTarget detects source block intersection`() {
        val block = block("block-1", 20f)

        val data = tracksData(
            row(
                "row-1",
                gap("left-gap", 20f),
                block,
                gap("right-gap", 60f)
            )
        )

        val result = useCase.getDragAndDropTarget(
            tracks = data,
            block = block,
            positionX = 45f,
            positionY = 10f,
            currentTouchOffsetX = 5f,
            scrollX = 0f,
            scrollY = 0f
        )

        assertNotNull(result)

        assertEquals(
            true,
            result!!.targetIsSourceBlockGap
        )

        assertEquals(
            0,
            result.gapIndex
        )

        assertEquals(
            0f,
            result.gapStartX,
            0.001f
        )

        assertEquals(
            100f,
            result.gapWidth,
            0.001f
        )
    }

    // -------------------------------------------------------------------------
    // addBlockAt
    // -------------------------------------------------------------------------

    @Test
    fun `addBlockAt returns null when position is outside tracks`() {
        val data = tracksData(
            row(
                "row-1",
                gap("gap-1", 100f)
            )
        )

        val result = useCase.addBlockAt(
            tracksData = data,
            positionX = 10f,
            positionY = 1000f,
            scrollX = 0f,
            scrollY = 0f,
            blockWidth = 20f
        )

        assertNull(result)
    }

    @Test
    fun `addBlockAt returns null when position is inside block`() {
        val data = tracksData(
            row(
                "row-1",
                block("block-1", 50f),
                gap("gap-1", 100f)
            )
        )

        val result = useCase.addBlockAt(
            tracksData = data,
            positionX = 20f,
            positionY = 10f,
            scrollX = 0f,
            scrollY = 0f,
            blockWidth = 20f
        )

        assertNull(result)
    }

    @Test
    fun `addBlockAt inserts block into gap`() {
        val data = tracksData(
            row(
                "row-1",
                gap("gap-1", 100f)
            )
        )

        val result = useCase.addBlockAt(
            tracksData = data,
            positionX = 30f,
            positionY = 10f,
            scrollX = 0f,
            scrollY = 0f,
            blockWidth = 20f
        )

        assertNotNull(result)

        val items = result!![0].items

        assertEquals(3, items.size)

        assertTrue(items[0] is TracksItem.Gap)
        assertTrue(items[1] is TracksItem.Block)
        assertTrue(items[2] is TracksItem.Gap)

        assertEquals(30f, items[0].width, 0.001f)
        assertEquals(20f, items[1].width, 0.001f)
        assertEquals(50f, items[2].width, 0.001f)

        val insertedBlock = items[1] as TracksItem.Block

        assertEquals("New block", insertedBlock.text)
        assertEquals(
            RGBColor(103, 80, 164),
            insertedBlock.color
        )
    }

    @Test
    fun `addBlockAt returns null when gap is too small`() {
        val data = tracksData(
            row(
                "row-1",
                gap("gap-1", 10f)
            )
        )

        val result = useCase.addBlockAt(
            tracksData = data,
            positionX = 5f,
            positionY = 10f,
            scrollX = 0f,
            scrollY = 0f,
            blockWidth = 20f
        )

        assertNull(result)
    }

    @Test
    fun `addBlockAt creates only block when it exactly fills gap`() {
        val data = tracksData(
            row(
                "row-1",
                gap("gap-1", 50f)
            )
        )

        val result = useCase.addBlockAt(
            tracksData = data,
            positionX = 0f,
            positionY = 10f,
            scrollX = 0f,
            scrollY = 0f,
            blockWidth = 50f
        )

        assertNotNull(result)

        val items = result!![0].items

        assertEquals(1, items.size)
        assertTrue(items.single() is TracksItem.Block)
        assertEquals(50f, items.single().width, 0.001f)
    }

    // -------------------------------------------------------------------------
    // addBlocksInSelection
    // -------------------------------------------------------------------------

    @Test
    fun `addBlocksInSelection returns same list for invalid row`() {
        val tracks = listOf(
            row(
                "row-1",
                gap("gap-1", 100f)
            )
        )

        val result = useCase.addBlocksInSelection(
            tracks = tracks,
            rowIndex = 1,
            startX = 0f,
            endX = 50f,
            blockWidth = 10f
        )

        assertSame(tracks, result)
    }

    @Test
    fun `addBlocksInSelection returns same list for non-positive block width`() {
        val tracks = listOf(
            row(
                "row-1",
                gap("gap-1", 100f)
            )
        )

        val result = useCase.addBlocksInSelection(
            tracks = tracks,
            rowIndex = 0,
            startX = 0f,
            endX = 50f,
            blockWidth = 0f
        )

        assertSame(tracks, result)
    }

    @Test
    fun `addBlocksInSelection returns same list for zero selection`() {
        val tracks = listOf(
            row(
                "row-1",
                gap("gap-1", 100f)
            )
        )

        val result = useCase.addBlocksInSelection(
            tracks = tracks,
            rowIndex = 0,
            startX = 20f,
            endX = 20f,
            blockWidth = 10f
        )

        assertSame(tracks, result)
    }

    @Test
    fun `addBlocksInSelection adds multiple blocks forward`() {
        val tracks = listOf(
            row(
                "row-1",
                gap("gap-1", 100f)
            )
        )

        val result = useCase.addBlocksInSelection(
            tracks = tracks,
            rowIndex = 0,
            startX = 10f,
            endX = 70f,
            blockWidth = 20f
        )

        assertEquals(1, result.size)

        val items = result[0].items

        assertEquals(5, items.size)

        assertTrue(items[0] is TracksItem.Gap)
        assertTrue(items[1] is TracksItem.Block)
        assertTrue(items[2] is TracksItem.Block)
        assertTrue(items[3] is TracksItem.Block)
        assertTrue(items[4] is TracksItem.Gap)

        assertEquals(10f, items[0].width, 0.001f)
        assertEquals(20f, items[1].width, 0.001f)
        assertEquals(20f, items[2].width, 0.001f)
        assertEquals(20f, items[3].width, 0.001f)
        assertEquals(30f, items[4].width, 0.001f)
    }

    @Test
    fun `addBlocksInSelection adds blocks backward`() {
        val tracks = listOf(
            row(
                "row-1",
                gap("gap-1", 100f)
            )
        )

        val result = useCase.addBlocksInSelection(
            tracks = tracks,
            rowIndex = 0,
            startX = 70f,
            endX = 10f,
            blockWidth = 20f
        )

        val items = result[0].items

        assertEquals(5, items.size)

        assertEquals(10f, items[0].width, 0.001f)
        assertEquals(20f, items[1].width, 0.001f)
        assertEquals(20f, items[2].width, 0.001f)
        assertEquals(20f, items[3].width, 0.001f)
        assertEquals(30f, items[4].width, 0.001f)
    }

    @Test
    fun `addBlocksInSelection does not cross existing block`() {
        val tracks = listOf(
            row(
                "row-1",
                gap("gap-1", 30f),
                block("block-1", 20f),
                gap("gap-2", 50f)
            )
        )

        val result = useCase.addBlocksInSelection(
            tracks = tracks,
            rowIndex = 0,
            startX = 0f,
            endX = 100f,
            blockWidth = 10f
        )

        val items = result[0].items

        assertTrue(items[0] is TracksItem.Block)
        assertTrue(items[1] is TracksItem.Block)
        assertTrue(items[2] is TracksItem.Block)
        assertTrue(items[3] is TracksItem.Block && items[3].id == "block-1")
        assertTrue(items[4] is TracksItem.Gap)
    }

    @Test
    fun `addBlocksInSelection returns same list when selection starts on block`() {
        val tracks = listOf(
            row(
                "row-1",
                gap("gap-1", 20f),
                block("block-1", 30f),
                gap("gap-2", 50f)
            )
        )

        val result = useCase.addBlocksInSelection(
            tracks = tracks,
            rowIndex = 0,
            startX = 30f,
            endX = 70f,
            blockWidth = 10f
        )

        assertSame(tracks, result)
    }

    @Test
    fun `addBlocksInSelection returns same list when available space is insufficient`() {
        val tracks = listOf(
            row(
                "row-1",
                gap("gap-1", 15f)
            )
        )

        val result = useCase.addBlocksInSelection(
            tracks = tracks,
            rowIndex = 0,
            startX = 0f,
            endX = 15f,
            blockWidth = 20f
        )

        assertSame(tracks, result)
    }

    // -------------------------------------------------------------------------
    // removeBlockAt
    // -------------------------------------------------------------------------

    @Test
    fun `removeBlockAt returns null when outside track`() {
        val data = tracksData(
            row(
                "row-1",
                block("block-1", 30f),
                gap("gap-1", 50f)
            )
        )

        val result = useCase.removeBlockAt(
            tracksData = data,
            positionX = 10f,
            positionY = 1000f,
            scrollX = 0f,
            scrollY = 0f
        )

        assertNull(result)
    }

    @Test
    fun `removeBlockAt returns null when position is inside gap`() {
        val data = tracksData(
            row(
                "row-1",
                block("block-1", 30f),
                gap("gap-1", 50f)
            )
        )

        val result = useCase.removeBlockAt(
            tracksData = data,
            positionX = 50f,
            positionY = 10f,
            scrollX = 0f,
            scrollY = 0f
        )

        assertNull(result)
    }

    @Test
    fun `removeBlockAt replaces block with gap`() {
        val data = tracksData(
            row(
                "row-1",
                block("block-1", 30f),
                gap("gap-1", 50f)
            )
        )

        val result = useCase.removeBlockAt(
            tracksData = data,
            positionX = 10f,
            positionY = 10f,
            scrollX = 0f,
            scrollY = 0f
        )

        assertNotNull(result)

        val items = result!![0].items

        assertEquals(1, items.size)
        assertTrue(items.single() is TracksItem.Gap)
        assertEquals(80f, items.single().width, 0.001f)

        verify(exactly = 1) {
            itemIdGenerator.generateItemId()
        }
    }

    // -------------------------------------------------------------------------
    // moveBlock
    // -------------------------------------------------------------------------

    @Test
    fun `moveBlock returns null for invalid source row`() {
        val tracks = listOf(
            row(
                "row-1",
                block("block-1", 20f),
                gap("gap-1", 50f)
            )
        )

        val result = useCase.moveBlock(
            tracks = tracks,
            blockId = "block-1",
            sourceRowIndex = 1,
            targetRowIndex = 0,
            targetGapIndex = 1,
            targetIsSourceBlockGap = false,
            dropOffset = 0f
        )

        assertNull(result)
    }

    @Test
    fun `moveBlock returns null when block does not exist`() {
        val tracks = listOf(
            row(
                "row-1",
                block("block-1", 20f),
                gap("gap-1", 50f)
            )
        )

        val result = useCase.moveBlock(
            tracks = tracks,
            blockId = "missing",
            sourceRowIndex = 0,
            targetRowIndex = 0,
            targetGapIndex = 1,
            targetIsSourceBlockGap = false,
            dropOffset = 0f
        )

        assertNull(result)
    }

    @Test
    fun `moveBlock returns null when target gap is too small`() {
        val tracks = listOf(
            row(
                "row-1",
                block("block-1", 50f),
                gap("gap-1", 100f)
            ),
            row(
                "row-2",
                gap("gap-2", 20f)
            )
        )

        val result = useCase.moveBlock(
            tracks = tracks,
            blockId = "block-1",
            sourceRowIndex = 0,
            targetRowIndex = 1,
            targetGapIndex = 0,
            targetIsSourceBlockGap = false,
            dropOffset = 0f
        )

        assertEquals(tracks, result)
    }

    @Test
    fun `moveBlock moves block between rows`() {
        val tracks = listOf(
            row(
                "row-1",
                block("block-1", 20f),
                gap("gap-1", 50f)
            ),
            row(
                "row-2",
                gap("gap-2", 100f)
            )
        )

        val result = useCase.moveBlock(
            tracks = tracks,
            blockId = "block-1",
            sourceRowIndex = 0,
            targetRowIndex = 1,
            targetGapIndex = 0,
            targetIsSourceBlockGap = false,
            dropOffset = 20f
        )

        assertNotNull(result)

        val sourceItems = result!![0].items
        val targetItems = result[1].items

        assertTrue(sourceItems.single() is TracksItem.Gap)
        assertEquals(70f, sourceItems.single().width, 0.001f)

        assertTrue(targetItems[0] is TracksItem.Gap)
        assertTrue(targetItems[1] is TracksItem.Block)
        assertTrue(targetItems[2] is TracksItem.Gap)

        assertEquals(20f, targetItems[0].width, 0.001f)
        assertEquals(20f, targetItems[1].width, 0.001f)
        assertEquals(60f, targetItems[2].width, 0.001f)

        assertEquals(
            "block-1",
            (targetItems[1] as TracksItem.Block).id
        )
    }

    @Test
    fun `moveBlock preserves total width when moving between rows`() {
        val tracks = listOf(
            row(
                "row-1",
                block("block-1", 20f),
                gap("gap-1", 80f)
            ),
            row(
                "row-2",
                gap("gap-2", 100f)
            )
        )

        val result = useCase.moveBlock(
            tracks = tracks,
            blockId = "block-1",
            sourceRowIndex = 0,
            targetRowIndex = 1,
            targetGapIndex = 0,
            targetIsSourceBlockGap = false,
            dropOffset = 40f
        )

        assertNotNull(result)

        val sourceWidth =
            result!![0].items.sumOf { it.width.toDouble() }.toFloat()

        val targetWidth =
            result[1].items.sumOf { it.width.toDouble() }.toFloat()

        assertEquals(100f, sourceWidth, 0.001f)
        assertEquals(100f, targetWidth, 0.001f)
    }

    @Test
    fun `moveBlock moves block inside same row`() {
        val tracks = listOf(
            row(
                "row-1",
                block("block-1", 20f),
                gap("gap-1", 80f)
            )
        )

        val result = useCase.moveBlock(
            tracks = tracks,
            blockId = "block-1",
            sourceRowIndex = 0,
            targetRowIndex = 0,
            targetGapIndex = 1,
            targetIsSourceBlockGap = false,
            dropOffset = 10f
        )

        assertNotNull(result)

        val items = result!![0].items

        assertTrue(items[0] is TracksItem.Gap)
        assertTrue(items[1] is TracksItem.Block)
        assertTrue(items[2] is TracksItem.Gap)

        assertEquals(30f, items[0].width, 0.001f)
        assertEquals(20f, items[1].width, 0.001f)
        assertEquals(50f, items[2].width, 0.001f)

        assertEquals(
            "block-1",
            (items[1] as TracksItem.Block).id
        )
    }

    @Test
    fun `moveBlock returns null when target gap index is invalid`() {
        val tracks = listOf(
            row(
                "row-1",
                block("block-1", 20f),
                gap("gap-1", 80f)
            )
        )

        val result = useCase.moveBlock(
            tracks = tracks,
            blockId = "block-1",
            sourceRowIndex = 0,
            targetRowIndex = 0,
            targetGapIndex = 99,
            targetIsSourceBlockGap = false,
            dropOffset = 0f
        )

        assertNull(result)
    }

    // -------------------------------------------------------------------------
    // calculateInitialDragSession
    // -------------------------------------------------------------------------

    @Test
    fun `calculateInitialDragSession returns null outside track`() {
        val block = block("block-1", 30f)

        val data = tracksData(
            row("row-1", block)
        )

        val result = useCase.calculateInitialDragSession(
            tracksData = data,
            startPositionX = 10f,
            startPositionY = 1000f,
            scrollX = 0f,
            scrollY = 0f
        )

        assertNull(result)
    }

    @Test
    fun `calculateInitialDragSession returns null on gap`() {
        val data = tracksData(
            row(
                "row-1",
                gap("gap-1", 50f)
            )
        )

        val result = useCase.calculateInitialDragSession(
            tracksData = data,
            startPositionX = 10f,
            startPositionY = 10f,
            scrollX = 0f,
            scrollY = 0f
        )

        assertNull(result)
    }

    @Test
    fun `calculateInitialDragSession creates session for block`() {
        val block = block("block-1", 30f)

        val data = tracksData(
            row(
                "row-1",
                gap("gap-1", 20f),
                block,
                gap("gap-2", 50f)
            )
        )

        val result = useCase.calculateInitialDragSession(
            tracksData = data,
            startPositionX = 30f,
            startPositionY = 10f,
            scrollX = 0f,
            scrollY = 0f
        )

        assertNotNull(result)

        assertEquals(
            block,
            result!!.block
        )

        assertEquals(0, result.sourceRowIndex)
        assertEquals(1, result.sourceItemIndex)

        assertEquals(
            10f,
            result.touchOffsetX,
            0.001f
        )

        assertEquals(
            10f,
            result.touchOffsetY,
            0.001f
        )

        assertEquals(
            30f,
            result.pointerPositionX,
            0.001f
        )

        assertEquals(
            10f,
            result.pointerPositionY,
            0.001f
        )
    }

    @Test
    fun `calculateInitialDragSession accounts for scroll`() {
        val block = block("block-1", 30f)

        val data = tracksData(
            row(
                "row-1",
                gap("gap-1", 20f),
                block
            )
        )

        val result = useCase.calculateInitialDragSession(
            tracksData = data,
            startPositionX = 25f,
            startPositionY = 15f,
            scrollX = 10f,
            scrollY = 5f
        )

        assertNotNull(result)

        assertEquals(
            15f,
            result!!.touchOffsetX,
            0.001f
        )

        assertEquals(
            20f,
            result.touchOffsetY,
            0.001f
        )
    }

    // -------------------------------------------------------------------------
    // startAddItemsBySelection
    // -------------------------------------------------------------------------

    @Test
    fun `startAddItemsBySelection returns null outside track`() {
        val data = tracksData(
            row(
                "row-1",
                gap("gap-1", 100f)
            )
        )

        val result = useCase.startAddItemsBySelection(
            tracksData = data,
            positionX = 10f,
            positionY = 1000f,
            scrollX = 0f,
            scrollY = 0f
        )

        assertNull(result)
    }

    @Test
    fun `startAddItemsBySelection returns null on block`() {
        val data = tracksData(
            row(
                "row-1",
                block("block-1", 30f),
                gap("gap-1", 70f)
            )
        )

        val result = useCase.startAddItemsBySelection(
            tracksData = data,
            positionX = 10f,
            positionY = 10f,
            scrollX = 0f,
            scrollY = 0f
        )

        assertNull(result)
    }

    @Test
    fun `startAddItemsBySelection creates selection in gap`() {
        val data = tracksData(
            row(
                "row-1",
                gap("gap-1", 100f)
            )
        )

        val result = useCase.startAddItemsBySelection(
            tracksData = data,
            positionX = 30f,
            positionY = 10f,
            scrollX = 0f,
            scrollY = 0f
        )

        assertNotNull(result)

        assertEquals(
            AddSelection(
                rowIndex = 0,
                startX = 30f,
                currentX = 30f,
                minX = 0f,
                maxX = 100f
            ),
            result
        )
    }

    // -------------------------------------------------------------------------
    // updateAddSelectionArea
    // -------------------------------------------------------------------------

    @Test
    fun `updateAddSelectionArea updates current position`() {
        val selection = AddSelection(
            rowIndex = 0,
            startX = 20f,
            currentX = 20f,
            minX = 0f,
            maxX = 100f
        )

        val result = useCase.updateAddSelectionArea(
            selection = selection,
            positionX = 60f,
            scrollX = 0f
        )

        assertEquals(60f, result.currentX, 0.001f)
        assertEquals(20f, result.startX, 0.001f)
    }

    @Test
    fun `updateAddSelectionArea clamps position below minimum`() {
        val selection = AddSelection(
            rowIndex = 0,
            startX = 50f,
            currentX = 50f,
            minX = 20f,
            maxX = 100f
        )

        val result = useCase.updateAddSelectionArea(
            selection = selection,
            positionX = 0f,
            scrollX = 0f
        )

        assertEquals(20f, result.currentX, 0.001f)
    }

    @Test
    fun `updateAddSelectionArea clamps position above maximum`() {
        val selection = AddSelection(
            rowIndex = 0,
            startX = 50f,
            currentX = 50f,
            minX = 20f,
            maxX = 100f
        )

        val result = useCase.updateAddSelectionArea(
            selection = selection,
            positionX = 200f,
            scrollX = 0f
        )

        assertEquals(100f, result.currentX, 0.001f)
    }

    @Test
    fun `updateAddSelectionArea accounts for scroll`() {
        val selection = AddSelection(
            rowIndex = 0,
            startX = 20f,
            currentX = 20f,
            minX = 0f,
            maxX = 100f
        )

        val result = useCase.updateAddSelectionArea(
            selection = selection,
            positionX = 40f,
            scrollX = 20f
        )

        assertEquals(60f, result.currentX, 0.001f)
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private fun row(
        id: String,
        vararg items: TracksItem
    ): TrackRow {
        return TrackRow(
            id = id,
            items = items.toList()
        )
    }

    private fun block(
        id: String,
        width: Float,
        text: String = "Block"
    ): TracksItem.Block {
        return TracksItem.Block(
            id = id,
            text = text,
            width = width,
            color = RGBColor(1, 2, 3)
        )
    }

    private fun gap(
        id: String,
        width: Float
    ): TracksItem.Gap {
        return TracksItem.Gap(
            id = id,
            width = width
        )
    }

    /**
     * Creates TracksData directly without normalizeTracksWidth().
     *
     * This is useful for tests where we need exact item coordinates
     * independently of TracksSettings.END_TRACK_MINIMUM_SPACE.
     */
    private fun tracksData(
        vararg rows: TrackRow
    ): TracksData {
        val tracks = rows.toList()

        val positions = HashMap<String, TrackPosition>()

        var top = 0

        tracks.forEach { row ->
            val itemPositions = HashMap<String, ItemPosition>()

            var currentX = 0f

            row.items.forEach { item ->
                itemPositions[item.id] = ItemPosition(
                    startX = currentX,
                    endX = currentX + item.width
                )

                currentX += item.width
            }

            positions[row.id] = TrackPosition(
                top = top,
                bottom = top + TracksSettings.TRACK_HEIGHT,
                itemPositions = itemPositions
            )

            top += TracksSettings.TRACK_HEIGHT
        }

        val contentWidth = tracks
            .maxOfOrNull { track ->
                track.items.sumOf { it.width.toDouble() }.toInt()
            }
            ?: 0

        return TracksData(
            tracks = tracks,
            positions = positions,
            contentWidth = contentWidth,
            contentHeight = tracks.size * TracksSettings.TRACK_HEIGHT
        )
    }
}
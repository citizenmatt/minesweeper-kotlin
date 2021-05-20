package minesweeper

import junit.framework.TestCase
import minesweeper.exception.FailureException
import minesweeper.model.CellState
import minesweeper.model.CellType
import kotlin.random.Random
import minesweeper.model.Field

class FieldTest: TestCase() {

    fun `test initialise board to empty`() {
        val field = Field(7, 8)
        assertEquals(7, field.board.size)
        field.board.forEach {
            assertEquals(8, it.size)
            it.forEach {
                assertEquals(CellType.NONE, it.type)
                assertEquals(CellState.UNMARKED, it.state)
            }
        }
    }

    fun `test game not over before first move`() {
        val field = Field()
        assertFalse(field.isTheGameEnd())
    }

    fun `test mark cell as mine updates state`() {
        val field = Field()
        field.mark(5, 5)
        assertEquals(CellType.NONE, field.board[5][5].type)
        assertEquals(CellState.MARKED, field.board[5][5].state)
    }

    fun `test first free move is always safe`() {
        val field = Field(9, 9, 10, ReproducibleRandom)
        field.free(5, 5)
        assertFalse(field.isTheGameEnd())
        assertEquals(CellType.NONE, field.board[5][5].type)
        assertEquals(CellState.FREE, field.board[5][5].state)
    }

    fun `test mines initialised after first free move`() {
        val expectedMineCount = 10
        val field = Field(9, 9, expectedMineCount, ReproducibleRandom)
        field.free(5, 5)
        val mineCount = field.board.sumBy { it.count { cell -> cell.type == CellType.MINE } }
        assertEquals(expectedMineCount, mineCount)
    }

    fun `test game ends if free a mine`() {
        val field = Field(9, 9, 10, ReproducibleRandom)
        field.free(5, 5)    // Initialise the board
        try {
            field.free(1, 1)
        } catch (e: FailureException) {
            assertEquals("You stepped on a mine and failed!", e.message)
            return
        }

        fail("Expected FailureException")
    }

    companion object {

        // This seed gives us a reproducible random stream so we can set up nicely for tests
        //  |123456789|
        // -|---------|
        // 1|.........|
        // 2|.X..XX..X|
        // 3|1113X....|
        // 4|   2X....|
        // 5|   1111..|
        // 6|111   1X.|
        // 7|.X11111.X|
        // 8|....X....|
        // 9|.........|
        // -|---------|
        //
        private val ReproducibleRandom
            get() = Random(42)
    }
}
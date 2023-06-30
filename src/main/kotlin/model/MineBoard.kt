package model

import model.minemark.Mine
import model.minemark.MineMark
import model.minemark.Safety

class MineBoard(elements: Map<Position, MineMark>) {

    init {
        require(isAllFilled(elements)) {
            "elements must be filled with all positions. but provided `$elements`"
        }
    }

    val elements: Map<Position, MineMark> = elements.toMap()
    val size = elements.size
    val maxXPosition: Int by lazy { elements.keys.maxOf { it.x } }
    val maxYPosition: Int by lazy { elements.keys.maxOf { it.y } }

    fun contains(position: Position): Boolean {
        return elements.containsKey(position)
    }

    fun isEqualMarkInPosition(position: Position, mark: MineMark): Boolean {
        validateContainsPosition(position)
        return elements[position] == mark
    }

    fun replacedMark(position: Position, mark: MineMark): MineBoard {
        validateContainsPosition(position)
        return MineBoard(elements.toMutableMap().apply { this[position] = mark })
    }

    fun doesNotContainsMark(mark: MineMark): Boolean {
        return elements.values.contains(mark).not()
    }

    fun replacedOnlySafetyMarks(replaceMarkMapper: (Position) -> (MineMark)): MineBoard {
        var current = this
        elements.forEach {
            current = replacedOnlySafetyMark(current, it, replaceMarkMapper)
        }
        return current
    }

    fun mineCount(positions: Collection<Position>): Int {
        return positions.count {
            validateContainsPosition(it)
            elements[it] == Mine
        }
    }

    private fun replacedOnlySafetyMark(
        mineBoard: MineBoard,
        element: Map.Entry<Position, MineMark>,
        replaceMarkMapper: (Position) -> MineMark,
    ): MineBoard {
        if (element.value == Safety) {
            return mineBoard.replacedMark(element.key, replaceMarkMapper(element.key))
        }
        return mineBoard
    }

    private fun isAllFilled(elements: Map<Position, MineMark>): Boolean {
        return zeroToRange(elements.keys.maxOf { it.x })
            .flatMap { x -> positions(x, elements.keys.maxOf { it.y }) }
            .all { elements.containsKey(it) }
    }

    private fun positions(
        x: Int,
        maxY: Int,
    ): Collection<Position> {
        return zeroToRange(maxY)
            .map { y -> Position(x, y) }
    }

    private fun zeroToRange(count: Int): IntRange = (0..count)

    private fun validateContainsPosition(position: Position) {
        if (contains(position).not()) {
            throw IllegalArgumentException("position must be in elements. elements($this), position(`$position`)")
        }
    }

    override fun toString(): String {
        return "MineBoard(elements=$elements, size=$size)"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MineBoard

        return elements == other.elements
    }

    override fun hashCode(): Int {
        return elements.hashCode()
    }
}

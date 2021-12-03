package wtf.log.xmas2021.day.day03

import wtf.log.xmas2021.Day
import java.io.BufferedReader

object Day03 : Day<BitTable, Int, Int> {

    override fun parseInput(reader: BufferedReader): BitTable = BitTable.parse(reader)

    override fun part1(input: BitTable): Int {
        val mostCommonBits = input.foldMostCommon()
        val width = mostCommonBits.size
        val gammaRate = mostCommonBits.toInt()
        val epsilonRate = gammaRate.inv() and ((1 shl width) - 1)
        return gammaRate * epsilonRate
    }

    override fun part2(input: BitTable): Int {
        return computeRating(input, false) * computeRating(input, true)
    }

    private fun computeRating(input: BitTable, invert: Boolean): Int {
        val candidateRows = input.rows.toCollection(mutableSetOf())
        for (columnIndex in 0 until input.width) {
            val subTable = BitTable(candidateRows.toList())
            val column = subTable.columns[columnIndex]
            val mostCommon = column.findMostCommon()
            val target = mostCommon xor invert
            val rowIterator = candidateRows.iterator()
            while (rowIterator.hasNext()) {
                val candidateRow = rowIterator.next()
                if (candidateRow[columnIndex] != target) {
                    rowIterator.remove()
                }
            }
            if (candidateRows.size == 1) {
                break
            }
        }
        return candidateRows.single().toInt()
    }
}

data class BitTable(
    val rows: List<List<Boolean>>,
) {

    init {
        require(rows.isNotEmpty())
    }

    val height: Int = rows.size
    val width: Int = rows.first().size
    val columns: List<List<Boolean>> = (0 until width).map { ColumnView(it) }

    init {
        require(rows.all { it.size == width })
    }

    override fun toString(): String = buildString {
        for (row in rows) {
            for (value in row) {
                append(
                    when (value) {
                        true -> '1'
                        false -> '0'
                    }
                )
            }
            append('\n')
        }
    }

    private inner class ColumnView(private val columnIndex: Int) : AbstractList<Boolean>() {

        override val size: Int
            get() = height

        override fun get(index: Int): Boolean = rows[index][columnIndex]
    }

    companion object {

        fun parse(reader: BufferedReader): BitTable {
            val rows = mutableListOf<List<Boolean>>()
            for (line in reader.lineSequence()) {
                val row = line.map { char ->
                    when (char) {
                        '0' -> false
                        '1' -> true
                        else -> error("Invalid bit character: $char")
                    }
                }
                rows += row
            }
            return BitTable(rows)
        }
    }
}


private fun BitTable.foldMostCommon(): List<Boolean> {
    return columns.map { it.findMostCommon() }
}

private fun List<Boolean>.findMostCommon(): Boolean {
    val count = count { it }
    val half = size / 2
    return size % 2 == 0 && count == half || count > half
}

private fun List<Boolean>.toInt(): Int {
    require(size <= 32) {
        "Too large for an Int: $size"
    }
    var result = 0
    for (bit in this) {
        result = (result shl 1) or bit.toInt()
    }
    return result
}

private fun Boolean.toInt(): Int = when (this) {
    true -> 1
    else -> 0
}

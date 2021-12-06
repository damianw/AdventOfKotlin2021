package wtf.log.xmas2021.day.day04

import wtf.log.xmas2021.Day
import wtf.log.xmas2021.util.collect.Grid
import java.io.BufferedReader

object Day04 : Day<Day04.Input, Int, Int> {

    override fun parseInput(reader: BufferedReader): Input = Input.parse(reader)

    override fun part1(input: Input): Int {
        val cards = input.hands.map(::Card)
        var winningValue: Int? = null
        var winningCard: Card? = null
        outer@ for (value in input.drawnValues) {
            for (card in cards) {
                if (card.mark(value)) {
                    winningValue = value
                    winningCard = card
                    break@outer
                }
            }
        }
        return winningValue!! * winningCard!!.sumUnmarked()
    }

    override fun part2(input: Input): Int {
        val cards = input.hands.map(::Card)
        val winningHands = mutableSetOf<Grid<Int>>()
        var winningValue: Int? = null
        var winningCard: Card? = null
        outer@ for (value in input.drawnValues) {
            for (card in cards) {
                val win = card.mark(value)
                if (win && card.hand !in winningHands) {
                    winningHands += card.hand
                    winningValue = value
                    winningCard = card
                    if (winningHands.size == input.hands.size) {
                        break@outer
                    }
                }
            }
        }

        return winningValue!! * winningCard!!.sumUnmarked()
    }

    data class Input(
        val drawnValues: List<Int>,
        val hands: List<Grid<Int>>,
    ) {

        companion object {

            fun parse(reader: BufferedReader): Input {
                val drawnValues = reader.readLine().split(',').map { it.toInt() }
                check(reader.readLine().isEmpty())
                val hands = reader
                    .readLines()
                    .chunked(Card.SIZE + 1)
                    .map { chunk ->
                        val rows = chunk.take(Card.SIZE).map { line ->
                            line.trim().split(Regex("\\s+")).map { it.toInt() }
                        }
                        Grid(rows)
                    }
                    .toList()
                return Input(drawnValues, hands)
            }
        }
    }
}

class Card(
    val hand: Grid<Int>,
) {

    private val cells = hand.map { Cell(it) }

    /**
     * Marks the value on this card and returns true iff this was a winning call.
     */
    fun mark(value: Int): Boolean {
        for (cell in cells.values) {
            if (cell.value == value) {
                cell.mark()
            }
        }
        return checkMarks()
    }

    fun sumUnmarked(): Int = cells.values.sumOf { if (it.isMarked) 0 else it.value }

    private fun checkMarks(): Boolean {
        return cells.rows.any { row -> row.all { it.isMarked } } ||
                cells.columns.any { column -> column.all { it.isMarked } }
    }

    private class Cell(
        val value: Int,
    ) {

        var isMarked: Boolean = false
            private set

        fun mark() {
            isMarked = true
        }

        override fun toString(): String = if (isMarked) "[$value]" else value.toString()
    }

    override fun toString(): String = cells.toString()

    companion object {

        const val SIZE = 5
    }
}

package wtf.log.xmas2021.day.day13

import com.google.common.collect.HashBasedTable
import com.google.common.collect.Table
import wtf.log.xmas2021.Day
import wtf.log.xmas2021.util.collect.set
import wtf.log.xmas2021.util.math.Point
import java.io.BufferedReader

object Day13 : Day<Input, Int, String> {

    override fun parseInput(reader: BufferedReader): Input {
        val points = mutableSetOf<Point>()
        val iterator = reader.lineSequence().iterator()
        while (iterator.hasNext()) {
            val line = iterator.next()
            if (line.isEmpty()) break

            val split = line.split(',')
            check(split.size == 2)
            points += Point(split[0].toInt(), split[1].toInt())
        }

        val folds = mutableListOf<Fold>()
        while (iterator.hasNext()) {
            val line = iterator.next()
            check(line.startsWith("fold along "))
            val split = line.substring(11).split('=')
            check(split.size == 2)
            folds += Fold(
                axis = when (val axis = split[0]) {
                    "x" -> Fold.Axis.X
                    "y" -> Fold.Axis.Y
                    else -> error("Unknown fold axis: $axis")
                },
                value = split[1].toInt(),
            )
        }

        return Input(points, folds)
    }

    override fun part1(input: Input): Int {
        return solve(input.dotPositions, input.folds.take(1)).size()
    }

    override fun part2(input: Input): String {
        return solve(input.dotPositions, input.folds).dump()
    }

    private fun solve(dotPositions: Set<Point>, folds: List<Fold>): Table<Int, Int, Unit> {
        @Suppress("UNCHECKED_CAST")
        val table = HashBasedTable.create<Int, Int, Unit>() as Table<Int, Int, Unit>
        for (point in dotPositions) {
            table[point.x, point.y] = Unit
        }

        for (fold in folds) {
            for (point in table.cellSet().toSet()) {
                when (fold.axis) {
                    Fold.Axis.X -> if (point.rowKey >= fold.value) {
                        table.remove(point.rowKey, point.columnKey)
                        table[(2 * fold.value) - point.rowKey, point.columnKey] = Unit
                    }
                    Fold.Axis.Y -> if (point.columnKey >= fold.value) {
                        table.remove(point.rowKey, point.columnKey)
                        table[point.rowKey, (2 * fold.value) - point.columnKey] = Unit
                    }
                }
            }
        }
        return table
    }

    private fun Table<Int, Int, Unit>.dump(): String = buildString {
        val maxX = rowKeySet().maxOf { it }
        val maxY = columnKeySet().maxOf { it }
        append('\n')
        repeat(maxY + 1) { y ->
            repeat(maxX + 1) { x ->
                // Kotlin and Guava's annotations really don't get along
                @Suppress("SENSELESS_COMPARISON")
                append(if (get(x, y) != null) '#' else '.')
            }
            append('\n')
        }
    }
}

data class Input(
    val dotPositions: Set<Point>,
    val folds: List<Fold>,
)

data class Fold(
    val axis: Axis,
    val value: Int,
) {

    enum class Axis {
        X,
        Y,
    }
}

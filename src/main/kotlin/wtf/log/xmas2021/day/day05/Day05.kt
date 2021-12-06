package wtf.log.xmas2021.day.day05

import com.google.common.collect.HashMultiset
import com.google.common.collect.Multiset
import wtf.log.xmas2021.Day
import wtf.log.xmas2021.util.math.Line
import wtf.log.xmas2021.util.math.Point
import wtf.log.xmas2021.util.math.points
import java.io.BufferedReader

object Day05 : Day<List<Line>, Int, Int> {

    private val REGEX = Regex("""(\d+),(\d+) -> (\d+),(\d+)""")

    override fun parseInput(reader: BufferedReader): List<Line> = reader
        .lineSequence()
        .map { line ->
            val (x1, y1, x2, y2) = requireNotNull(REGEX.matchEntire(line)).destructured
            Line(
                start = Point(x1.toInt(), y1.toInt()),
                end = Point(x2.toInt(), y2.toInt()),
            )
        }
        .toList()

    override fun part1(input: List<Line>): Int {
        return solve(input) { it.isVertical || it.isHorizontal }
    }

    override fun part2(input: List<Line>): Int {
        return solve(input) { true }
    }

    private inline fun solve(input: List<Line>, predicate: (Line) -> Boolean): Int {
        val pointCounts: Multiset<Point> = HashMultiset.create()
        for (line in input) {
            if (predicate(line)) {
                pointCounts.addAll(line.points.asIterable())
            }
        }
        val dangerousPoints = pointCounts
            .entrySet()
            .filter { it.count >= 2 }
            .map { it.element }
        return dangerousPoints.size
    }
}

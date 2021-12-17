package wtf.log.xmas2021.day.day17

import wtf.log.xmas2021.Day
import wtf.log.xmas2021.util.math.Point
import wtf.log.xmas2021.util.math.Rect
import java.io.BufferedReader
import kotlin.math.max
import kotlin.math.sign

object Day17 : Day<Rect, Int, Int> {

    private val REGEX = Regex("""target area: x=(-?\d+)..(-?\d+), y=(-?\d+)..(-?\d+)""")

    override fun parseInput(reader: BufferedReader): Rect {
        val match = requireNotNull(REGEX.matchEntire(reader.readLine())) {
            "Input string did not match pattern $REGEX"
        }
        val (x1, x2, y1, y2) = match.destructured
        return Rect(
            x = x1.toInt()..x2.toInt(),
            y = y1.toInt()..y2.toInt(),
        )
    }

    override fun part1(input: Rect): Int = findInitialVelocities(input).values.maxOf { it }

    override fun part2(input: Rect): Int = findInitialVelocities(input).size

    private fun findInitialVelocities(target: Rect): Map<Point, Int> = (1..target.x.last)
        .asSequence()
        .flatMap { x ->
            (target.y.first..target.x.last).map { y -> Point(x, y) }
        }
        .map { velocity ->
            val (hit, maxY) = simulate(target, velocity)
            Triple(hit, maxY, velocity)
        }
        .filter { (hit) -> hit }
        .associate { (_, maxY, velocity) -> velocity to maxY }

    private fun simulate(target: Rect, initialVelocity: Point): Pair<Boolean, Int> {
        var position = Point()
        var velocity = initialVelocity
        var maxY = 0
        while (position !in target && position.x <= target.x.last && position.y >= target.y.first) {
            position += velocity
            velocity += Point(x = -velocity.x.sign, y = -1)
            maxY = max(maxY, position.y)
        }
        return Pair(position in target, maxY)
    }
}

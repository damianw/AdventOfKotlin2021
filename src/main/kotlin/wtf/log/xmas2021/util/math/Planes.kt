package wtf.log.xmas2021.util.math

import org.apache.commons.math3.fraction.Fraction

data class Point(val x: Int = 0, val y: Int = 0)

data class Line(val start: Point, val end: Point) {

    val deltaX: Int
        get() = end.x - start.x

    val deltaY: Int
        get() = end.y - start.y

    val isVertical: Boolean
        get() = deltaX == 0

    val isHorizontal: Boolean
        get() = deltaY == 0
}

val Line.points: Sequence<Point>
    get() = when (val deltaX = end.x - start.x) {
        0 -> {
            val range = if (end.y > start.y) start.y..end.y else end.y..start.y
            range
                .asSequence()
                .map { start.copy(y = it) }
        }
        else -> {
            val slope = Fraction(end.y - start.y, deltaX)
            val startingPoint: Point
            val endingPoint: Point
            if (deltaX > 0) {
                startingPoint = start
                endingPoint = end
            } else {
                startingPoint = end
                endingPoint = start
            }
            sequence {
                var currentPoint = startingPoint
                yield(currentPoint)
                while (currentPoint != endingPoint) {
                    currentPoint = currentPoint.copy(
                        x = currentPoint.x + slope.denominator,
                        y = currentPoint.y + slope.numerator
                    )
                    yield(currentPoint)
                }
            }
        }
    }

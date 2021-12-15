package wtf.log.xmas2021.day.day15

import wtf.log.xmas2021.Day
import wtf.log.xmas2021.util.collect.Grid
import wtf.log.xmas2021.util.collect.Grid.Coordinate
import wtf.log.xmas2021.util.collect.Grid.Entry
import wtf.log.xmas2021.util.collect.toGrid
import java.io.BufferedReader
import java.util.*

object Day15 : Day<Grid<Int>, Int, Int> {

    override fun parseInput(reader: BufferedReader): Grid<Int> = reader
        .lineSequence()
        .map { line ->
            line.map { it.digitToInt() }
        }
        .toList()
        .toGrid()

    override fun part1(input: Grid<Int>): Int {
        val path = input.findShortestPath(
            from = Coordinate(0, 0),
            to = Coordinate(input.height - 1, input.width - 1),
        )
        return path.drop(1).sumOf { it.value }
    }

    override fun part2(input: Grid<Int>): Int {
        val tiled = input.tile(5) { i, value ->
            val incremented = value + i
            (incremented % 10) + (incremented / 10)
        }
        val path = tiled.findShortestPath(
            from = Coordinate(0, 0),
            to = Coordinate(tiled.height - 1, tiled.width - 1),
        )
        return path.drop(1).sumOf { it.value }
    }

    /**
     * https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm#Pseudocode
     */
    private fun Grid<Int>.findShortestPath(from: Coordinate, to: Coordinate): List<Entry<Int>> {
        val fromEntry = Entry(from, get(from))
        val toEntry = Entry(to, get(to))
        val queue = PriorityQueue<Distance>().apply {
            add(Distance(fromEntry, 0))
        }
        val dist = mutableMapOf(fromEntry to 0)
        val prev = mutableMapOf<Entry<Int>, Entry<Int>>()

        var current = fromEntry
        while (current != toEntry && queue.isNotEmpty()) {
            current = queue.remove().to

            for (neighbor in getCardinallyAdjacent(current.coordinate)) {
                val alt = dist[current]?.let { it + neighbor.value } ?: Int.MAX_VALUE
                if (alt < (dist[neighbor] ?: Int.MAX_VALUE)) {
                    dist[neighbor] = alt
                    prev[neighbor] = current
                    queue.add(Distance(neighbor, alt))
                }
            }
        }

        return generateSequence(current, prev::get).toList().asReversed()
    }

    private fun <T> Grid<T>.tile(times: Int, transform: (Int, T) -> T): Grid<T> {
        val horizontal = rows.map { row ->
            (0 until times).flatMap { i ->
                row.map { transform(i, it) }
            }
        }
        return (0 until times)
            .flatMap { i ->
                horizontal.map { row ->
                    row.map { transform(i, it) }
                }
            }
            .toGrid()
    }

    private data class Distance(
        val to: Entry<Int>,
        val length: Int,
    ) : Comparable<Distance> {

        override fun compareTo(other: Distance): Int = this.length.compareTo(other.length)
    }
}

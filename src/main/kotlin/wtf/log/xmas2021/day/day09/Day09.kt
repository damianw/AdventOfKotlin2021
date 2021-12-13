package wtf.log.xmas2021.day.day09

import wtf.log.xmas2021.Day
import wtf.log.xmas2021.util.collect.Grid
import wtf.log.xmas2021.util.collect.toGrid
import java.io.BufferedReader

object Day09 : Day<Grid<Int>, Int, Int> {

    override fun parseInput(reader: BufferedReader): Grid<Int> = reader
        .lineSequence()
        .map { line ->
            line.map { it.digitToInt() }
        }
        .toList()
        .toGrid()

    override fun part1(input: Grid<Int>): Int = findLowPoints(input).sumOf { it.value + 1 }

    override fun part2(input: Grid<Int>): Int {
        val lowPoints = findLowPoints(input)
        val basins = mutableMapOf<Grid.Entry<Int>, Set<Grid.Entry<Int>>>()
        val visited = mutableSetOf<Grid.Entry<Int>>()
        for (lowPoint in lowPoints) {
            val basin = mutableSetOf<Grid.Entry<Int>>()
            val deque = ArrayDeque<Grid.Entry<Int>>()
            deque.addFirst(lowPoint)
            while (deque.isNotEmpty()) {
                val point = deque.removeFirst()
                if (point.value != 9) {
                    basin += point
                    if (point !in visited) {
                        for (adjacent in input.getCardinallyAdjacent(point.coordinate)) {
                            if (adjacent !in lowPoints) {
                                deque.addFirst(adjacent)
                            }
                        }
                    }
                    visited += point
                }
            }
            basins[lowPoint] = basin
        }

        return basins.values.map { it.size }.sortedDescending().take(3).fold(1, Int::times)
    }

    private fun findLowPoints(input: Grid<Int>): Set<Grid.Entry<Int>> {
        return input.filterTo(mutableSetOf()) { entry ->
            input.getCardinallyAdjacent(entry.coordinate)
                .all { it.value > entry.value }
        }
    }
}

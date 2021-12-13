package wtf.log.xmas2021.day.day11

import wtf.log.xmas2021.Day
import wtf.log.xmas2021.util.collect.Grid
import wtf.log.xmas2021.util.collect.toGrid
import wtf.log.xmas2021.util.collect.toMutableGrid
import java.io.BufferedReader

object Day11 : Day<Grid<Int>, Int, Int> {

    override fun parseInput(reader: BufferedReader): Grid<Int> = reader
        .lineSequence()
        .map { it.map(Char::digitToInt) }
        .toList()
        .toGrid()

    override fun part1(input: Grid<Int>): Int {
        var flashCount = 0
        var grid = input
        repeat(100) { i ->
            val (nextFlashes, nextGrid) = step(grid)
            flashCount += nextFlashes
            grid = nextGrid
        }
        return flashCount
    }

    override fun part2(input: Grid<Int>): Int {
        var grid = input
        var count = 0
        while (true) {
            count++
            val (nextFlashes, nextGrid) = step(grid)
            grid = nextGrid
            if (nextFlashes == input.size) {
                return count
            }
        }
    }

    private fun step(input: Grid<Int>): Pair<Int, Grid<Int>> {
        val workGrid = input.toMutableGrid()
        for ((coordinate) in workGrid) {
            workGrid[coordinate]++
        }

        var flashed = true
        val flashes = mutableSetOf<Grid.Coordinate>()
        while (flashed) {
            flashed = false
            for ((coordinate, value) in workGrid) {
                if (value > 9 && coordinate !in flashes) {
                    for (adjacent in workGrid.getAllAdjacent(coordinate)) {
                        workGrid[adjacent.coordinate]++
                    }
                    flashes += coordinate
                    flashed = true
                }
            }
        }

        for (coordinate in flashes) {
            workGrid[coordinate] = 0
        }

        return flashes.size to workGrid
    }
}

package wtf.log.xmas2021.day.day01

import wtf.log.xmas2021.Day
import java.io.BufferedReader

object Day01 : Day<List<Int>, Int, Int> {

    override fun parseInput(reader: BufferedReader): List<Int> = reader.useLines { lines ->
        lines
            .map { it.toInt() }
            .toList()
    }

    override fun part1(input: List<Int>): Int = input.windowed(2).count { it[1] > it[0] }

    override fun part2(input: List<Int>): Int = input
        .windowed(3)
        .map { it.sum() }
        .windowed(2)
        .count { it[1] > it[0] }
}

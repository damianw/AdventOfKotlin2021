package wtf.log.xmas2021.day.day06

import wtf.log.xmas2021.Day
import java.io.BufferedReader

@Suppress("SameParameterValue")
object Day06 : Day<List<Int>, Long, Long> {

    override fun parseInput(reader: BufferedReader): List<Int> {
        return reader.readLine().split(',').map { it.toInt() }
    }

    override fun part1(input: List<Int>): Long {
        return solveNaively(input, dayCount = 80)
    }

    private fun solveNaively(input: List<Int>, dayCount: Int): Long {
        var currentTimers = input.map { it.toLong() }
        repeat(dayCount) {
            val nextTimers = mutableListOf<Long>()
            var newFishCount = 0
            for (timer in currentTimers) {
                if (timer == 0L) {
                    nextTimers += 6L
                    newFishCount += 1
                } else {
                    nextTimers += timer - 1L
                }
            }
            nextTimers += Array(newFishCount) { 8 }
            currentTimers = nextTimers
        }
        return currentTimers.size.toLong()
    }

    override fun part2(input: List<Int>): Long {
        return solveQuickly(input, dayCount = 256)
    }

    private fun solveQuickly(input: List<Int>, dayCount: Int): Long {
        val newByDay = LongArray(dayCount + 1)
        repeat(dayCount) { previousDay ->
            val today = previousDay + 1
            val newFromSeedToday = input.count { startingValue ->
                (previousDay - startingValue) % 7 == 0
            }
            var extraNewToday = 0L
            var lookBack = today - 9
            while (lookBack >= 0) {
                extraNewToday += newByDay[lookBack]
                lookBack -= 7
            }
            val totalNewToday = newFromSeedToday + extraNewToday
            newByDay[today] = totalNewToday
        }
        return input.size + newByDay.sum()
    }
}

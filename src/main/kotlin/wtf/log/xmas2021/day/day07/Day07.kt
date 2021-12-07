package wtf.log.xmas2021.day.day07

import com.google.common.collect.ImmutableSortedMultiset
import wtf.log.xmas2021.Day
import java.io.BufferedReader
import kotlin.math.absoluteValue

object Day07 : Day<ImmutableSortedMultiset<Int>, Int, Int> {

    override fun parseInput(reader: BufferedReader): ImmutableSortedMultiset<Int> {
        val values = reader.readLine().split(',').map { it.toInt() }
        return ImmutableSortedMultiset.copyOf(values)
    }

    override fun part1(input: ImmutableSortedMultiset<Int>): Int {
        @Suppress("UNNECESSARY_NOT_NULL_ASSERTION") // IDE confused by Guava's annotations
        val range = input.firstEntry()!!.element!!..input.lastEntry()!!.element!!
        val fuelCosts = range.associateWith { position ->
            input.sumOf { (it!! - position).absoluteValue }
        }
        return fuelCosts.values.minOrNull()!!
    }

    override fun part2(input: ImmutableSortedMultiset<Int>): Int? {
        @Suppress("UNNECESSARY_NOT_NULL_ASSERTION") // IDE confused by Guava's annotations
        val range = input.firstEntry()!!.element!!..input.lastEntry()!!.element!!
        val fuelCosts = range.associateWith { position ->
            input.sumOf { (0..(it!! - position).absoluteValue).sum() }
        }
        return fuelCosts.values.minOrNull()!!
    }
}

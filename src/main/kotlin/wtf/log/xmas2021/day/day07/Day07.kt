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
        return range.minOf { position ->
            input.sumOf { (it!! - position).absoluteValue }
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    override fun part2(input: ImmutableSortedMultiset<Int>): Int {
        @Suppress("UNNECESSARY_NOT_NULL_ASSERTION") // IDE confused by Guava's annotations
        val range = input.firstEntry()!!.element!!..input.lastEntry()!!.element!!
        return range.minOf { position ->
            input.sumOf { value ->
                val delta = ((value!! - position).absoluteValue)
                delta * (delta + 1) / 2
            }
        }
    }
}

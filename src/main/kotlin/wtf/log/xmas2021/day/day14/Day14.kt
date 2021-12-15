package wtf.log.xmas2021.day.day14

import com.google.common.collect.HashMultiset
import wtf.log.xmas2021.Day
import java.io.BufferedReader

object Day14 : Day<Input, Int, Long> {

    override fun parseInput(reader: BufferedReader): Input {
        val template = reader.readLine()
        require(reader.readLine().isEmpty())
        val rules = reader
            .lineSequence()
            .map { line ->
                val split = line.split(" -> ")
                require(split.size == 2)
                val first = split.first()
                require(first.length == 2)
                val second = split.last().single()
                Rule(
                    pattern = first[0] to first[1],
                    insertedChar = second,
                )
            }
            .toList()
        return Input(template, rules)
    }

    override fun part1(input: Input): Int {
        var polymer = input.template
        val rules = input.ruleMap
        repeat(10) {
            val windows = polymer.windowed(2)
            polymer = windows
                .mapIndexed { index, window ->
                    check(window.length == 2)
                    val pair = window[0] to window[1]
                    val replacement = rules[pair]?.replacement ?: window
                    if (index == windows.lastIndex) replacement else replacement.dropLast(1)
                }
                .joinToString("")
        }

        val chars = HashMultiset.create(polymer.asIterable())
        val mostCommonChar = checkNotNull(chars.elementSet().maxByOrNull(chars::count))
        val leastCommonChar = checkNotNull(chars.elementSet().minByOrNull(chars::count))
        return chars.count(mostCommonChar) - chars.count(leastCommonChar)
    }

    override fun part2(input: Input): Long {
        val rules = input.ruleMap
        val startingPatterns = input.template.windowed(2).map { window ->
            check(window.length == 2)
            window[0] to window[1]
        }
        val patterns = mutableMapOf<Pair<Char, Char>, Long>()
        for (pattern in startingPatterns) {
            patterns.merge(pattern, 1, Long::plus)
        }
        val newPatterns = mutableMapOf<Pair<Char, Char>, Long>()
        repeat(40) {
            for ((pattern, count) in patterns.entries) {
                val rule = rules[pattern]
                if (rule == null) {
                    newPatterns.merge(pattern, count, Long::plus)
                } else {
                    for (replacement in rule.replacementPatterns) {
                        newPatterns.merge(replacement, count, Long::plus)
                    }
                }
            }
            patterns.clear()
            patterns.putAll(newPatterns)
            newPatterns.clear()
        }

        val chars = mutableMapOf<Char, Long>()
        for ((pattern, count) in patterns) {
            chars.merge(pattern.first, count, Long::plus)
            chars.merge(pattern.second, count, Long::plus)
        }
        // Every character is counted twice, except for the starting and ending characters. Add one
        // for each of those so that all entries are exactly doubled.
        chars[input.template.first()] = chars.getValue(input.template.first()) + 1
        chars[input.template.last()] = chars.getValue(input.template.last()) + 1

        val mostCommonCount = chars.entries.maxOf { it.value }
        val leastCommonCount = chars.entries.minOf { it.value }
        val difference = mostCommonCount - leastCommonCount
        return difference / 2L
    }
}

data class Rule(
    val pattern: Pair<Char, Char>,
    val insertedChar: Char,
) {

    val replacement: String = "${pattern.first}$insertedChar${pattern.second}"

    val replacementPatterns: List<Pair<Char, Char>> = listOf(
        pattern.first to insertedChar,
        insertedChar to pattern.second,
    )
}

data class Input(
    val template: String,
    val ruleList: List<Rule>,
) {

    val ruleMap: Map<Pair<Char, Char>, Rule> = ruleList.associateBy { it.pattern }
}

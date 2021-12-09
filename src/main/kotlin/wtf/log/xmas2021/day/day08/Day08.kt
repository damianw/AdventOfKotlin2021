package wtf.log.xmas2021.day.day08

import com.google.common.collect.Multimap
import wtf.log.xmas2021.Day
import wtf.log.xmas2021.util.collect.copy
import wtf.log.xmas2021.util.collect.multimapOf
import java.io.BufferedReader

object Day08 : Day<List<Entry>, Int, Int> {

    override fun parseInput(reader: BufferedReader): List<Entry> = reader
        .lineSequence()
        .map(Entry::parse)
        .toList()

    override fun part1(input: List<Entry>): Int {
        val sizes = setOf(2, 3, 4, 7)
        return input.sumOf { entry ->
            entry.outputs.count { signal ->
                signal.size in sizes
            }
        }
    }

    override fun part2(input: List<Entry>): Int = input.sumOf { entry ->
        val mapping = computeMapping(entry)
        entry.outputs
            .map { checkNotNull(Digit.fromSignal(it.mapTo(mutableSetOf(), mapping::getValue))) }
            .toInt()
    }

    private fun computeMapping(entry: Entry): Map<Segment, Segment> {
        val digitsBySize = mapOf(
            2 to Digit.ONE,
            3 to Digit.SEVEN,
            4 to Digit.FOUR,
            7 to Digit.EIGHT,
        )

        val allSegments = Segment.values()
        val candidates = multimapOf<Segment, Segment>()
        for (segment in allSegments) {
            candidates.putAll(segment, allSegments.asIterable())
        }

        for (signal in entry.patterns) {
            val digit = digitsBySize[signal.size]
            if (digit != null) {
                for (segment in signal) {
                    candidates[segment].retainAll(digit.signal)
                }
            }
        }

        return checkNotNull(search(candidates, entry.patterns, emptySet()))
    }

    private fun search(
        candidates: Multimap<Segment, Segment>,
        testSignals: Set<Set<Segment>>,
        currentSearch: Set<Segment>,
    ): Map<Segment, Segment>? {
        val keyCount = candidates.keySet().size
        val segmentCount = Segment.values().size
        if (keyCount == segmentCount && candidates.size() == segmentCount) {
            val mapping = candidates.asMap().mapValues { (_, segments) -> segments.single() }
            for (signal in testSignals) {
                val mappedSignal = signal.mapTo(mutableSetOf(), mapping::getValue)
                if (Digit.fromSignal(mappedSignal) == null) {
                    return null
                }
            }
            return mapping
        } else if (keyCount < segmentCount) {
            return null
        }

        val segment = candidates
            .keySet()
            .filter { it !in currentSearch && candidates[it].size > 1 }
            .minByOrNull { candidates[it].size }
            ?: return null
        for (choice in candidates[segment]) {
            val snapshot = candidates.copy()
            snapshot.commitChoice(segment, choice)
            val nextSearch = search(snapshot, testSignals, currentSearch + segment)
            if (nextSearch != null) {
                return nextSearch
            }
        }

        return null
    }

    private fun Multimap<Segment, Segment>.commitChoice(key: Segment, value: Segment) {
        for (otherSegment in Segment.values()) {
            if (otherSegment != key && remove(otherSegment, value)) {
                val otherMapping = get(otherSegment)
                if (otherMapping.size == 1) {
                    commitChoice(otherSegment, otherMapping.single())
                }
            }
        }
        removeAll(key)
        put(key, value)
    }

    private fun Iterable<Digit>.toInt(): Int {
        var result = 0
        for (digit in this) {
            result *= 10
            result += digit.ordinal
        }
        return result
    }
}

enum class Digit(val signal: Set<Segment>) {
    ZERO(Segment.A, Segment.B, Segment.C, Segment.E, Segment.F, Segment.G),
    ONE(Segment.C, Segment.F),
    TWO(Segment.A, Segment.C, Segment.D, Segment.E, Segment.G),
    THREE(Segment.A, Segment.C, Segment.D, Segment.F, Segment.G),
    FOUR(Segment.B, Segment.C, Segment.D, Segment.F),
    FIVE(Segment.A, Segment.B, Segment.D, Segment.F, Segment.G),
    SIX(Segment.A, Segment.B, Segment.D, Segment.E, Segment.F, Segment.G),
    SEVEN(Segment.A, Segment.C, Segment.F),
    EIGHT(Segment.A, Segment.B, Segment.C, Segment.D, Segment.E, Segment.F, Segment.G),
    NINE(Segment.A, Segment.B, Segment.C, Segment.D, Segment.F, Segment.G),
    ;

    constructor(vararg segments: Segment) : this(setOf(*segments))

    companion object {

        private val mapping: Map<Set<Segment>, Digit> = values().associateBy { it.signal }

        fun fromSignal(signal: Set<Segment>): Digit? = mapping[signal]
    }
}

data class Entry(
    val patterns: Set<Set<Segment>>,
    val outputs: List<Set<Segment>>,
) {

    companion object {

        fun parse(line: String): Entry {
            val split = line.split('|')
            check(split.size == 2)
            return Entry(
                patterns = split[0].trim().split(' ').mapTo(mutableSetOf(), Segment::parse),
                outputs = split[1].trim().split(' ').map(Segment::parse),
            )
        }
    }
}

enum class Segment {
    A,
    B,
    C,
    D,
    E,
    F,
    G,
    ;

    companion object {

        private val mapping = values().associateBy { it.name.lowercase().single() }

        fun parse(text: String): Set<Segment> = text.mapTo(mutableSetOf(), mapping::getValue)
    }
}

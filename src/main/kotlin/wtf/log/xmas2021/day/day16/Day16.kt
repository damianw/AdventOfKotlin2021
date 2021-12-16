package wtf.log.xmas2021.day.day16

import wtf.log.xmas2021.Day
import wtf.log.xmas2021.util.collect.removeFirst
import wtf.log.xmas2021.util.math.toBits
import wtf.log.xmas2021.util.math.toInt
import wtf.log.xmas2021.util.math.toLong
import java.io.BufferedReader
import java.util.*

/**
 * It would be better to use a BitSet or BigInteger but collections are so much easier to deal with.
 */
object Day16 : Day<Packet, Int, Long> {

    override fun parseInput(reader: BufferedReader): Packet {
        val bits = reader.readLine().flatMap { it.digitToInt(16).toBits().takeLast(4) }
        return parsePacket(ArrayDeque(bits))
    }

    private fun parsePacket(queue: Deque<Boolean>): Packet {
        val version = queue.removeFirst(3).toInt()
        when (val typeId = queue.removeFirst(3).toInt()) {
            4 -> {
                val valueBits = mutableListOf<Boolean>()
                do {
                    val shouldContinue = queue.removeFirst()
                    valueBits += queue.removeFirst(4)
                } while (shouldContinue)
                return Literal(
                    version = version,
                    value = valueBits.toLong(),
                )
            }
            else -> {
                val isCountType = queue.removeFirst()
                if (isCountType) {
                    val packetCount = queue.removeFirst(11).toInt()
                    val subPackets = (0 until packetCount).map { parsePacket(queue) }
                    return Operator(
                        version = version,
                        type = Operator.Type.fromId(typeId),
                        subPackets = subPackets,
                    )
                } else {
                    val packetLength = queue.removeFirst(15).toInt()
                    val startingSize = queue.size
                    val endingSize = startingSize - packetLength
                    val subPackets = mutableListOf<Packet>()
                    while (queue.size > endingSize) {
                        subPackets += parsePacket(queue)
                    }
                    return Operator(
                        version = version,
                        type = Operator.Type.fromId(typeId),
                        subPackets = subPackets,
                    )
                }
            }
        }
    }

    override fun part1(input: Packet): Int = input.versionSum()

    override fun part2(input: Packet): Long = input.evaluate()
}

sealed class Packet {

    abstract val version: Int

    abstract fun versionSum(): Int
    abstract fun evaluate(): Long
}

data class Literal(
    override val version: Int,
    val value: Long,
) : Packet() {

    override fun versionSum(): Int = version

    override fun evaluate(): Long = value
}

data class Operator(
    override val version: Int,
    val type: Type,
    val subPackets: List<Packet>,
) : Packet() {

    override fun versionSum(): Int = version + subPackets.sumOf { it.versionSum() }

    override fun evaluate(): Long = type.evaluate(subPackets.map { it.evaluate() })

    enum class Type(val id: Int) {
        SUM(0) {
            override fun evaluate(values: List<Long>): Long = values.reduce(Long::plus)
        },
        PRODUCT(1) {
            override fun evaluate(values: List<Long>): Long = values.reduce(Long::times)
        },
        MINIMUM(2) {
            override fun evaluate(values: List<Long>): Long = values.minOf { it }
        },
        MAXIMUM(3) {
            override fun evaluate(values: List<Long>): Long = values.maxOf { it }
        },
        GREATER_THAN(5) {
            override fun evaluate(values: List<Long>): Long {
                require(values.size == 2)
                return if (values[0] > values[1]) 1L else 0L
            }
        },
        LESS_THAN(6) {
            override fun evaluate(values: List<Long>): Long {
                require(values.size == 2)
                return if (values[0] < values[1]) 1L else 0L
            }
        },
        EQUAL_TO(7) {
            override fun evaluate(values: List<Long>): Long {
                require(values.size == 2)
                return if (values[0] == values[1]) 1L else 0L
            }
        },
        ;

        abstract fun evaluate(values: List<Long>): Long

        companion object {

            private val mapping: Map<Int, Type> = values().associateBy { it.id }

            fun fromId(id: Int): Type = mapping.getValue(id)
        }
    }
}

package wtf.log.xmas2021.day.day02

import wtf.log.xmas2021.Day
import java.io.BufferedReader

object Day02 : Day<List<Instruction>, Int, Int> {

    override fun parseInput(reader: BufferedReader): List<Instruction> = reader
        .lineSequence()
        .map(Instruction::parse)
        .toList()

    override fun part1(input: List<Instruction>): Int {
        val finalPosition = input.fold(Position(), Position::followingSimple)
        return finalPosition.horizontal * finalPosition.depth
    }

    override fun part2(input: List<Instruction>): Int {
        val finalPosition = input.fold(Position(), Position::followingWithAim)
        return finalPosition.horizontal * finalPosition.depth
    }
}

data class Position(
    val horizontal: Int = 0,
    val depth: Int = 0,
    val aim: Int = 0,
) {

    fun followingSimple(instruction: Instruction): Position = when (instruction.axis) {
        Axis.HORIZONTAL -> copy(horizontal = horizontal + instruction.amount)
        Axis.DEPTH -> copy(depth = depth + instruction.amount)
    }

    fun followingWithAim(instruction: Instruction): Position = when (instruction.axis) {
        Axis.HORIZONTAL -> copy(
            horizontal = horizontal + instruction.amount,
            depth = depth + (aim * instruction.amount),
        )
        Axis.DEPTH -> copy(aim = aim + instruction.amount)
    }
}

data class Instruction(
    val axis: Axis,
    val amount: Int,
) {

    companion object {

        fun parse(input: String): Instruction {
            val parts = input.split(' ')
            require(parts.size == 2)
            val axis: Axis
            val multiplier: Int
            when (val direction = parts.first()) {
                "forward" -> {
                    axis = Axis.HORIZONTAL
                    multiplier = 1
                }
                "down" -> {
                    axis = Axis.DEPTH
                    multiplier = 1
                }
                "up" -> {
                    axis = Axis.DEPTH
                    multiplier = -1
                }
                else -> throw IllegalArgumentException("Unknown direction: $direction")
            }
            return Instruction(axis, multiplier * parts[1].toInt())
        }
    }
}

enum class Axis {
    HORIZONTAL,
    DEPTH,
}

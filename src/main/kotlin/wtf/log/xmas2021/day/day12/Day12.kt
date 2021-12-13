package wtf.log.xmas2021.day.day12

import wtf.log.xmas2021.Day
import java.io.BufferedReader

object Day12 : Day<CaveSystem, Int, Int> {

    override fun parseInput(reader: BufferedReader): CaveSystem {
        return CaveSystem.parse(reader.lineSequence().asIterable())
    }

    override fun part1(input: CaveSystem): Int {
        fun findAllPaths(
            input: CaveSystem,
            output: MutableSet<List<Cave>>,
            path: List<Cave>,
            currentCave: Cave,
        ) {
            if (currentCave == Cave.END) {
                output += path
                return
            }

            for (adjacentCave in input[currentCave]) {
                if (adjacentCave.type == Cave.Type.LARGE || adjacentCave !in path) {
                    findAllPaths(input, output, path + adjacentCave, adjacentCave)
                }
            }
        }

        val output = mutableSetOf<List<Cave>>()
        findAllPaths(input, output, listOf(Cave.START), Cave.START)
        return output.size
    }

    override fun part2(input: CaveSystem): Int {
        fun findAllPaths(
            input: CaveSystem,
            output: MutableSet<List<Cave>>,
            path: List<Cave>,
            currentCave: Cave,
            haveSelectedLuckyCave: Boolean,
        ) {
            if (currentCave == Cave.END) {
                output += path
                return
            }

            for (adjacentCave in input[currentCave]) {
                if (adjacentCave.type == Cave.Type.LARGE || adjacentCave !in path) {
                    findAllPaths(
                        input = input,
                        output = output,
                        path = path + adjacentCave,
                        currentCave = adjacentCave,
                        haveSelectedLuckyCave = haveSelectedLuckyCave,
                    )
                } else if (!haveSelectedLuckyCave && adjacentCave != Cave.START && adjacentCave != Cave.END) {
                    findAllPaths(
                        input = input,
                        output = output,
                        path = path + adjacentCave,
                        currentCave = adjacentCave,
                        haveSelectedLuckyCave = true,
                    )
                }
            }
        }

        val output = mutableSetOf<List<Cave>>()
        findAllPaths(input, output, listOf(Cave.START), Cave.START, false)
        return output.size
    }
}

class CaveSystem private constructor(
    private val edges: Map<Cave, Set<Cave>>,
) {

    operator fun get(cave: Cave): Set<Cave> = edges[cave].orEmpty()

    companion object {

        fun parse(lines: Iterable<String>): CaveSystem {
            val edges = mutableMapOf<Cave, MutableSet<Cave>>()
            for (line in lines) {
                val split = line.split('-')
                require(split.size == 2)
                val left = Cave.parse(split[0])
                val right = Cave.parse(split[1])
                val leftEdges = edges.getOrPut(left, ::mutableSetOf)
                val rightEdges = edges.getOrPut(right, ::mutableSetOf)
                leftEdges += right
                rightEdges += left
            }
            return CaveSystem(edges)
        }
    }

}

data class Cave(
    val type: Type,
    val symbol: String,
) {

    enum class Type {
        SMALL, LARGE,
    }

    companion object {

        val START = Cave(Type.SMALL, "start")
        val END = Cave(Type.SMALL, "end")

        fun parse(text: String): Cave = Cave(
            type = when {
                text.isEmpty() -> error("Cave name must not be empty")
                text.all { it.isUpperCase() } -> Type.LARGE
                text.all { it.isLowerCase() } -> Type.SMALL
                else -> error("Unsupported cave name: $text")
            },
            symbol = text,
        )
    }
}

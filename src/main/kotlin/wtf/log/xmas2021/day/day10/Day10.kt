package wtf.log.xmas2021.day.day10

import wtf.log.xmas2021.Day
import java.io.BufferedReader
import java.util.*

object Day10 : Day<List<List<Token>>, Long, Long> {

    override fun parseInput(reader: BufferedReader): List<List<Token>> = reader
        .lineSequence()
        .map { line ->
            line.map(Token::match)
        }
        .toList()

    override fun part1(input: List<List<Token>>): Long = input.sumOf { line ->
        (parseTokens(line) as? ParseResult.Invalid)
            ?.firstIllegalTokenKind
            ?.syntaxErrorScore
            ?.toLong()
            ?: 0L
    }

    override fun part2(input: List<List<Token>>): Long = input
        .map(::parseTokens)
        .filterIsInstance<ParseResult.Incomplete>()
        .map { (completion) ->
            completion.fold(0L) { acc, token ->
                (acc * 5) + token.kind.completionScore
            }
        }
        .sorted().let { it[it.size / 2] }

    private fun parseTokens(tokens: List<Token>): ParseResult {
        val stack = ArrayDeque<Token.Kind>()
        for (token in tokens) {
            when (token.type) {
                Token.Type.OPEN -> stack.addFirst(token.kind)
                Token.Type.CLOSE -> {
                    val firstKind = stack.removeFirst()
                    if (firstKind != token.kind) {
                        return ParseResult.Invalid(token.kind)
                    }
                }
            }
        }
        if (stack.isEmpty()) {
            return ParseResult.Complete
        }
        val completion = mutableListOf<Token>()
        while (stack.isNotEmpty()) {
            val first = stack.removeFirst()
            completion += Token(Token.Type.CLOSE, first)
        }
        return ParseResult.Incomplete(completion)
    }

    private sealed class ParseResult {

        data class Invalid(val firstIllegalTokenKind: Token.Kind) : ParseResult()

        data class Incomplete(val completion: List<Token>) : ParseResult() {

            init {
                require(completion.isNotEmpty())
            }
        }

        object Complete : ParseResult()
    }
}

data class Token(
    val type: Type,
    val kind: Kind,
) {

    enum class Type {
        OPEN,
        CLOSE,
        ;
    }

    enum class Kind(
        val openingChar: Char,
        val closingChar: Char,
        val syntaxErrorScore: Int,
    ) {
        PARENTHESES('(', ')', syntaxErrorScore = 3),
        SQUARE_BRACKETS('[', ']', syntaxErrorScore = 57),
        CURLY_BRACES('{', '}', syntaxErrorScore = 1197),
        ANGLE_BRACKETS('<', '>', syntaxErrorScore = 25137),
        ;

        val completionScore: Int
            get() = ordinal + 1
    }

    companion object {

        private val mapping: Map<Char, Token> = Kind
            .values()
            .flatMap { kind ->
                listOf(
                    kind.openingChar to Token(Type.OPEN, kind),
                    kind.closingChar to Token(Type.CLOSE, kind),
                )
            }
            .toMap()

        fun match(char: Char): Token = mapping.getValue(char)
    }
}

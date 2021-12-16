package wtf.log.xmas2021.util.math

/**
 * Returns digits of the receiver (using [radix]) in little-endian (ascending order).
 */
fun Long.digits(radix: Long = 10): Sequence<Long> = sequence {
    var value = this@digits
    do {
        yield(value % radix)
        value /= radix
    } while (value != 0L)
}

fun Collection<Boolean>.toInt(): Int {
    val startIndex = indexOfFirst { it }
    if (startIndex == -1) return 0
    val bitCount = size - startIndex
    require(bitCount <= 32) {
        "Too large ($size) for an Int: ${joinToString("") { if (it) "1" else "0" }}"
    }

    val iterator = iterator()
    repeat(startIndex) {
        iterator.next()
    }

    var result = 0
    while (iterator.hasNext()) {
        result = (result shl 1) or iterator.next().toInt()
    }
    return result
}

fun Collection<Boolean>.toLong(): Long {
    val startIndex = indexOfFirst { it }
    if (startIndex == -1) return 0
    val bitCount = size - startIndex
    require(bitCount <= 64) {
        "Too large ($size) for a Long: ${joinToString("") { if (it) "1" else "0" }}"
    }

    val iterator = iterator()
    repeat(startIndex) {
        iterator.next()
    }

    var result = 0L
    while (iterator.hasNext()) {
        result = (result shl 1) or iterator.next().toLong()
    }
    return result
}

fun Boolean.toInt(): Int = when (this) {
    true -> 1
    else -> 0
}

fun Boolean.toLong(): Long = when (this) {
    true -> 1L
    else -> 0L
}

fun Int.toBits(): List<Boolean> = List(32) { index ->
    ((this shr (31 - index)) and 0x1) == 1
}

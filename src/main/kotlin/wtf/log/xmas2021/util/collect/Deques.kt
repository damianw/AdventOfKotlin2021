package wtf.log.xmas2021.util.collect

import java.util.*

fun <E> Queue<E>.removeFirst(n: Int): List<E> = (0 until n).map { remove() }

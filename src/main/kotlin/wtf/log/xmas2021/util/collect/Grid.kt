package wtf.log.xmas2021.util.collect

data class Grid<T>(
    val rows: List<List<T>>,
) {

    init {
        require(rows.isNotEmpty())
    }

    val height: Int = rows.size
    val width: Int = rows.first().size
    val columns: List<List<T>> = (0 until width).map { ColumnView(it) }
    val values: Sequence<T> = rows.asSequence().flatMap { it.asSequence() }

    init {
        require(rows.all { it.size == width })
    }

    operator fun get(rowIndex: Int, columnIndex: Int): T = rows[rowIndex][columnIndex]

    inline fun <R> map(transform: (T) -> R): Grid<R> = Grid(rows.map { row ->
        row.map(transform)
    })

    override fun toString(): String = buildString {
        append("\n┌")
        repeat(width * 8 - 1) {
            append('─')
        }
        append("┐\n")
        repeat(height) { y ->
            append('│')
            repeat(width) { x ->
                append(get(y, x))
                append('\t')
            }
            append("│\n")
        }
        append('└')
        repeat(width * 8 - 1) {
            append('─')
        }
        append("┘\n")
    }

    private inner class ColumnView(private val columnIndex: Int) : AbstractList<T>() {

        override val size: Int
            get() = height

        override fun get(index: Int): T = rows[index][columnIndex]
    }
}

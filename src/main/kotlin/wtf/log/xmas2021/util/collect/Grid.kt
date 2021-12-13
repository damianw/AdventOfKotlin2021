package wtf.log.xmas2021.util.collect

data class Grid<T>(
    val rows: List<List<T>>,
) : Iterable<Grid.Entry<T>> {

    init {
        require(rows.isNotEmpty())
    }

    val height: Int = rows.size
    val width: Int = rows.first().size
    val columns: List<List<T>> = (0 until width).map { ColumnView(it) }
    val values: Sequence<T> = rows.asSequence().flatMap { it.asSequence() }
    val entries: Sequence<Entry<T>> = sequence {
        for (rowIndex in 0 until height) {
            for (columnIndex in 0 until width) {
                yield(Entry(rowIndex, columnIndex, get(rowIndex, columnIndex)))
            }
        }
    }

    init {
        require(rows.all { it.size == width })
    }

    operator fun get(rowIndex: Int, columnIndex: Int): T = rows[rowIndex][columnIndex]

    override fun iterator(): Iterator<Entry<T>> = entries.iterator()

    fun getAdjacent(rowIndex: Int, columnIndex: Int): Sequence<Entry<T>> = sequence {
        val lowerRow = rowIndex - 1
        if (lowerRow >= 0) {
            yield(Entry(lowerRow, columnIndex, get(lowerRow, columnIndex)))
        }
        val upperRow = rowIndex + 1
        if (upperRow < height) {
            yield(Entry(upperRow, columnIndex, get(upperRow, columnIndex)))
        }

        val lowerColumn = columnIndex - 1
        if (lowerColumn >= 0) {
            yield(Entry(rowIndex, lowerColumn, get(rowIndex, lowerColumn)))
        }
        val upperColumn = columnIndex + 1
        if (upperColumn < width) {
            yield(Entry(rowIndex, upperColumn, get(rowIndex, upperColumn)))
        }
    }

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

    data class Entry<T>(
        val rowIndex: Int,
        val columnIndex: Int,
        val value: T,
    )

    private inner class ColumnView(private val columnIndex: Int) : AbstractList<T>() {

        override val size: Int
            get() = height

        override fun get(index: Int): T = rows[index][columnIndex]
    }
}

package wtf.log.xmas2021.util.collect

interface Grid<T> : Iterable<Grid.Entry<T>> {

    val rows: List<List<T>>
    val columns: List<List<T>>

    val height: Int
    val width: Int

    val values: Sequence<T>
    val entries: Sequence<Entry<T>>

    val size: Int
        get() = width * height

    operator fun get(rowIndex: Int, columnIndex: Int): T = rows[rowIndex][columnIndex]

    operator fun get(coordinate: Coordinate): T = get(coordinate.rowIndex, coordinate.columnIndex)

    override fun iterator(): Iterator<Entry<T>> = entries.iterator()

    fun getAllAdjacent(rowIndex: Int, columnIndex: Int): Sequence<Entry<T>> = sequence {
        for (r in (rowIndex - 1).coerceAtLeast(0)..(rowIndex + 1).coerceAtMost(height - 1)) {
            for (c in (columnIndex - 1).coerceAtLeast(0)..(columnIndex + 1).coerceAtMost(width - 1)) {
                if (r != rowIndex || c != columnIndex) {
                    yield(Entry(r, c, get(r, c)))
                }
            }
        }
    }

    fun getAllAdjacent(coordinate: Coordinate): Sequence<Entry<T>> = getAllAdjacent(
        rowIndex = coordinate.rowIndex,
        columnIndex = coordinate.columnIndex,
    )

    fun getCardinallyAdjacent(rowIndex: Int, columnIndex: Int): Sequence<Entry<T>> = sequence {
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

    fun getCardinallyAdjacent(coordinate: Coordinate): Sequence<Entry<T>> = getCardinallyAdjacent(
        rowIndex = coordinate.rowIndex,
        columnIndex = coordinate.columnIndex,
    )

    data class Coordinate(
        val rowIndex: Int,
        val columnIndex: Int,
    )

    data class Entry<T>(
        val coordinate: Coordinate,
        val value: T,
    ) {

        constructor(rowIndex: Int, columnIndex: Int, value: T)
                : this(Coordinate(rowIndex, columnIndex), value)
    }
}

interface MutableGrid<T> : Grid<T> {

    operator fun set(rowIndex: Int, columnIndex: Int, value: T)

    operator fun set(coordinate: Grid.Coordinate, value: T) {
        set(coordinate.rowIndex, coordinate.columnIndex, value)
    }
}

internal data class RealGrid<T>(
    override val rows: List<MutableList<T>>,
) : MutableGrid<T> {

    init {
        require(rows.isNotEmpty())
    }

    override val height: Int = rows.size
    override val width: Int = rows.first().size
    override val columns: List<List<T>> = (0 until width).map { ColumnView(it) }
    override val values: Sequence<T> = rows.asSequence().flatMap { it.asSequence() }
    override val entries: Sequence<Grid.Entry<T>> = sequence {
        for (rowIndex in 0 until height) {
            for (columnIndex in 0 until width) {
                yield(Grid.Entry(rowIndex, columnIndex, get(rowIndex, columnIndex)))
            }
        }
    }

    init {
        require(rows.all { it.size == width })
    }

    override fun set(rowIndex: Int, columnIndex: Int, value: T) {
        rows[rowIndex][columnIndex] = value
    }

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

fun <T> List<List<T>>.toGrid(): Grid<T> = RealGrid(map { it.toMutableList() })

fun <T> List<List<T>>.toMutableGrid(): MutableGrid<T> = RealGrid(map { it.toMutableList() })

fun <T> Grid<T>.toMutableGrid(): MutableGrid<T> = rows.toMutableGrid()

inline fun <T, R> Grid<T>.mapCells(transform: (T) -> R): Grid<R> = rows
    .map { row ->
        row.map(transform)
    }
    .toGrid()

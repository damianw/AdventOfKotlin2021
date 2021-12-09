package wtf.log.xmas2021.util.collect

import com.google.common.collect.HashMultimap
import com.google.common.collect.Multimap

@Suppress("UNCHECKED_CAST")
fun <K, V> multimapOf(): Multimap<K, V> = HashMultimap.create<K, V>() as Multimap<K, V>

@Suppress("UNCHECKED_CAST")
fun <K, V> Multimap<K, V>.copy(): Multimap<K, V> = HashMultimap.create(this) as Multimap<K, V>

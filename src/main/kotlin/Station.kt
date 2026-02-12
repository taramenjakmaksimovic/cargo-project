package org.example

data class Station(
    val id: Int,
    val unload: Int,
    val load: Int
) {
    fun transformationCargo(input: Set<Int>) : Set<Int> {
        val result = input.toMutableSet()
        result.remove(unload)
        result.add(load)
        return result
    }
}

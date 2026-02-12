package org.example

import java.util.Scanner

fun main() {
    val sc = Scanner(System.`in`)
    val line = sc.nextLine().trim()
    val parts = line.split(Regex("\\s+"))

    if (parts.size != 2) {
        println()
        System.err.println("Argument size is not 2")
        return
    }

    val S = parts[0].toIntOrNull() ?: run {
        println()
        System.err.println("S is not a number")
        return
    }

    val T = parts[1].toIntOrNull() ?: run {
        println()
        System.err.println("T is not a number")
        return
    }

    if (S <= 0) {
        println()
        System.err.println("S is not positive")
        return
    }

    if (T < 0) {
        println()
        System.err.println("T shouldn't be negative")
        return
    }

    val stations = mutableMapOf<Int, Station>()
    repeat(S) {
        if (!sc.hasNextInt()) {
            println()
            System.err.println("Missing station id")
            return
        }
        val s = sc.nextInt()

        if (!sc.hasNextInt()) {
            println()
            System.err.println("Missing station unload for station $s")
            return
        }
        val c_unload = sc.nextInt()

        if (!sc.hasNextInt()) {
            println()
            System.err.println("Missing station load for station $s")
            return
        }
        val c_load = sc.nextInt()

        stations[s] = Station(s, c_unload, c_load)
    }

    val adj = mutableMapOf<Int, MutableList<Int>>()
    repeat(T) { i->
        if (!sc.hasNextInt()) {
            println()
            System.err.println("Missing s_from for track ${ i + 1}")
            return
        }
        val s_from = sc.nextInt()

        if (!sc.hasNextInt()) {
            println()
            System.err.println("Missing s_to for track ${ i + 1}")
            return
        }

        val s_to = sc.nextInt()

        if(s_from !in stations || s_to !in stations){
            println()
            System.err.println("Station $s_from or $s_to is not in stations")
            return
        }

        adj.computeIfAbsent(s_from) { mutableListOf() }.add(s_to)
    }

    if (!sc.hasNextInt()) {
        println()
        System.err.println("Missing starting station s_0")
        return
    }
    val s_0 = sc.nextInt()

    if(s_0 !in stations){
        println()
        System.err.println("Starting station s_0 is not in stations")
        return
    }

    val arrivalCargo = mutableMapOf<Int, MutableSet<Int>>()
    val departureCargo = mutableMapOf<Int, MutableSet<Int>>()

    for(s in stations.keys) {
        arrivalCargo[s] = mutableSetOf()
        departureCargo[s] = mutableSetOf()
    }

    val startStation= stations[s_0]!!
    departureCargo[s_0]!!.add(startStation.load)

    val queue = ArrayDeque<Int>()
    queue.add(s_0)
    while (queue.isNotEmpty()) {
        val u = queue.first()
        queue.remove(u)

        val departSet = departureCargo[u]!!
        if(departSet.isEmpty()) continue

       for(v in adj[u] ?: emptyList()) {
           var arrivalChanged = false
           for (c in departSet) {
               if (arrivalCargo[v]!!.add(c)) {
                   arrivalChanged = true
               }
           }

           if (!arrivalChanged) continue
           val transformed = stations[v]!!.transformationCargo(arrivalCargo[v]!!)
           var departureChanged = false

           for (c in transformed) {
               if (departureCargo[v]!!.add(c)) {
                   departureChanged = true
               }
           }
           if(departureChanged){
               queue.add(v)
           }
       }
    }

    for (s in stations.keys.sorted()) {
        val cargos = arrivalCargo[s]!!
        if (cargos.isEmpty()){
            println("Station $s: none")
        } else {
            println("Station $s: ${cargos.sorted().joinToString(" ")}")
        }
    }

    sc.close()
}



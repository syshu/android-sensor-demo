package com.example.sensordemo

fun <T: Comparable<T>> findMedian(x: T, y: T, z: T) =
        when {
            y in z..x || y in x..z -> y
            x in y..z || x in z..y -> x
            else -> z
        }
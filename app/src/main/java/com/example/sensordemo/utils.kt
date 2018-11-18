package com.example.sensordemo

fun findMedian(x: Double, y: Double, z: Double) =
        when {
            y in z..x || y in x..z -> y
            x in y..z || x in z..y -> x
            else -> z
        }

fun findMedian(x: Float, y: Float, z: Float) =
        when {
            y in z..x || y in x..z -> y
            x in y..z || x in z..y -> x
            else -> z
        }
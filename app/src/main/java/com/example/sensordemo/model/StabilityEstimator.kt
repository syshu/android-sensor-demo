package com.example.sensordemo.model

import android.hardware.SensorEvent
import io.reactivex.Observable

interface StabilityEstimator {
    fun estimate(event: SensorEvent)
    fun getStability(): Observable<String>

    companion object {
        val STEADY = "steady";
        val SHAKY = "shaky";
    }
}
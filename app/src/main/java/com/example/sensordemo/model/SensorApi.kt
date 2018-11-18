package com.example.sensordemo.model

import android.hardware.Sensor
import android.hardware.SensorEvent
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit

class SensorApi {
    private val estimator = CombinedEstimator()
    private val result = PublishSubject.create<String>()

    init {
        var status = "initial"
        var timeout = false
        estimator.getStability().doOnNext{
            if (status != "okayed" && it == StabilityEstimator.STEADY) {
                status = "okayed"
                result.onNext(SensorApi.OK)
            }
            Observable.just(true).delay(1500, TimeUnit.MILLISECONDS).subscribe{
                timeout = true
            }
        }.buffer(2, 1).subscribe{
            if (it[1] == StabilityEstimator.SHAKY && it[0] == StabilityEstimator.SHAKY && status == "okayed" && timeout == false) {
                result.onNext(SensorApi.RECALL)
                status = "recalled"
            }
        }
    }

    fun estimate(event: SensorEvent) {
        estimator.estimate(event)
    }

    fun getStability(): Observable<String> {
        return result
    }

    companion object {
        val OK = "ok"
        val RECALL = "recall"
    }
}
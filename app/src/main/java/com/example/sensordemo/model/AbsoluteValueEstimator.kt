package com.example.sensordemo.model

import android.hardware.SensorEvent
import android.hardware.SensorManager
import com.example.sensordemo.findMedian
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class AbsoluteValueEstimator: StabilityEstimator {
    private val mSubject: Subject<SensorEvent> = PublishSubject.create()
    private val mObservable: Observable<String> by lazy {
        var lastResult = StabilityEstimator.SHAKY
        mSubject
                .map{
                    it.values.clone()
                }.map {
                    Math.sqrt(Math.pow(it[0].toDouble(), 2.0) +
                            Math.pow(it[1].toDouble(), 2.0) +
                            Math.pow(it[2].toDouble(), 2.0))
                }.buffer(3, 1)
                .map {
                    findMedian(it[0], it[1], it[2])
                }.map {
                    val linearAcc = Math.abs(it - SensorManager.GRAVITY_EARTH)
                    when {
                        linearAcc <= 0.3 -> StabilityEstimator.STEADY
                        linearAcc >= 0.5 -> StabilityEstimator.SHAKY
                        else -> "ellaSolo"
                    }
                }
                .map{
                    if (it != "ellaSolo") {
                        lastResult = it
                    }
                    lastResult
                }
    }

    override fun estimate(event: SensorEvent) {
        mSubject.onNext(event)
    }

    override fun getStability(): Observable<String> {
        return mObservable
    }
}
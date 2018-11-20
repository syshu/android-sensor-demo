package com.example.sensordemo.model

import android.hardware.SensorEvent
import com.example.sensordemo.findMedian
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class DeltaVectorEstimator: StabilityEstimator {
    private val mSubject: Subject<SensorEvent> = PublishSubject.create()
    private val mObservable: Observable<String> by lazy {
        var last = Array(3) {0f}
        val lastResult = Array(3) {StabilityEstimator.SHAKY}
        mSubject
                .map{
                    it.values.clone()
                }.buffer(3, 1)
                .map {
                    Array(3) {i ->
                        findMedian(it[2][i], it[1][i], it[0][i])
                    }
                }.map {
                    val delta = it.mapIndexed {i, each ->
                        Math.abs(each - last[i])
                    }
                    last = it.clone()
                    delta
                }.map {
                    it.map {each ->
                        when {
                            each <= 0.3 -> StabilityEstimator.STEADY
                            each >= 0.6 -> StabilityEstimator.SHAKY
                            else -> "ellaSolo"
                        }
                    }
                }
                .map {
                    it.mapIndexed {i, each ->
                        if (each != "ellaSolo") {
                            lastResult[i] = each
                        }
                        lastResult[i]
                    }
                }.map {
                    if (it.all {each ->
                        each == StabilityEstimator.STEADY
                    }) {
                        StabilityEstimator.STEADY
                    } else {
                        StabilityEstimator.SHAKY
                    }
                }
    }

    override fun estimate(event: SensorEvent) {
        mSubject.onNext(event)
    }

    override fun getStability(): Observable<String> {
        return mObservable
    }
}
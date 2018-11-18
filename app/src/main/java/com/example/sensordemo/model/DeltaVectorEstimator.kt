package com.example.sensordemo.model

import android.hardware.SensorEvent
import com.example.sensordemo.findMedian
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

class DeltaVectorEstimator: StabilityEstimator {
    private val mSubject: Subject<SensorEvent> = PublishSubject.create()
    private val mObservable: Observable<String> by lazy {
        var lastX = 0f
        var lastY = 0f
        var lastZ = 0f
        val lastResult = arrayOf(
                StabilityEstimator.SHAKY,
                StabilityEstimator.SHAKY,
                StabilityEstimator.SHAKY
                )
        mSubject
                .map{
                    it.values.clone()
                }.buffer(3, 1)
                .map {
                    arrayOf(
                            findMedian(it[2][0], it[1][0], it[0][0]),
                            findMedian(it[2][1], it[1][1], it[0][1]),
                            findMedian(it[2][2], it[1][2], it[0][2])
                            )
                }.map {
                    val deltaX = Math.abs(it[0] - lastX)
                    val deltaY = Math.abs(it[1] - lastY)
                    val deltaZ = Math.abs(it[2] - lastZ)
                    lastX = it[0]
                    lastY = it[1]
                    lastZ = it[2]
                    arrayOf(deltaX, deltaY, deltaZ)
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
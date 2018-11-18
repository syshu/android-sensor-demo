package com.example.sensordemo.model

import android.hardware.SensorEvent
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

class CombinedEstimator: StabilityEstimator {
    private val absoluteValueEstimator = AbsoluteValueEstimator()
    private val deltaVectorEstimator = DeltaVectorEstimator()
    private val observable: Observable<String> by lazy {
        Observable.zip(
                absoluteValueEstimator.getStability(),
                deltaVectorEstimator.getStability(),
                BiFunction<String, String, String> { t1, t2 ->
                    if (t1 == StabilityEstimator.STEADY &&
                            t2 == StabilityEstimator.STEADY) {
                        StabilityEstimator.STEADY
                    } else {
                        StabilityEstimator.SHAKY
                    }
                })
    }

    override fun estimate(event: SensorEvent) {
        absoluteValueEstimator.estimate(event)
        deltaVectorEstimator.estimate(event)
    }

    override fun getStability(): Observable<String> {
        return observable
    }
}
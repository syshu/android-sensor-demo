package com.example.sensordemo

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.example.sensordemo.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.PublishSubject
import io.reactivex.subjects.Subject

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {
    private lateinit var sensorManager: SensorManager
    private val absoluteValueEstimator = AbsoluteValueEstimator()
    private val deltaVectorEstimator = DeltaVectorEstimator()
    private val combinedEstimator = CombinedEstimator()
    private val sensorApi = SensorApi()
    private lateinit var disposable1: Disposable
    private lateinit var disposable2: Disposable
    private lateinit var disposable3: Disposable
    private lateinit var disposable4: Disposable

    private val sensorListener = object : SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        }

        override fun onSensorChanged(event: SensorEvent) {
            // sensorSubject.onNext(event!!.values)
            absoluteValueEstimator.estimate(event)
            deltaVectorEstimator.estimate(event)
            combinedEstimator.estimate(event)
            sensorApi.estimate(event)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(
                sensorListener,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_GAME
        )
        disposable1 = absoluteValueEstimator.getStability().observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    gravityScalar.setBackgroundColor(
                            resources.getColor(
                                    when (it) {
                                        StabilityEstimator.SHAKY -> android.R.color.holo_red_light
                                        StabilityEstimator.STEADY -> android.R.color.holo_green_light
                                        else -> android.R.color.black
                                    },
                                    theme)
                    )
                }
        disposable2 = deltaVectorEstimator.getStability().observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    yText.setBackgroundColor(
                            resources.getColor(
                                    when (it) {
                                        StabilityEstimator.SHAKY -> android.R.color.holo_red_light
                                        StabilityEstimator.STEADY -> android.R.color.holo_green_light
                                        else -> android.R.color.black
                                    },
                                    theme)
                    )
                }
        disposable3 = combinedEstimator.getStability().observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    combinedText.setBackgroundColor(
                            resources.getColor(
                                    when (it) {
                                        StabilityEstimator.SHAKY -> android.R.color.holo_red_light
                                        StabilityEstimator.STEADY -> android.R.color.holo_green_light
                                        else -> android.R.color.black
                                    },
                                    theme)
                    )
                }
        disposable4 = sensorApi.getStability().observeOn(AndroidSchedulers.mainThread())
                .subscribe{
                    finalText.setBackgroundColor(
                            resources.getColor(
                                    when (it) {
                                        SensorApi.OK -> android.R.color.holo_green_light
                                        SensorApi.RECALL -> android.R.color.holo_red_light
                                        else -> android.R.color.black
                                    },
                                    theme)
                    )
                }
    }

    override fun onPause() {
        disposable1.dispose()
        disposable2.dispose()
        disposable3.dispose()
        disposable4.dispose()
        sensorManager.unregisterListener(sensorListener)
        super.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    fun onFabClick(view: View) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }
}

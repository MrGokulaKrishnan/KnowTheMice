package com.knowthemice.app.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.abs

class AirMouseEngine(
    context: Context,
    private val onDelta: (dx: Float, dy: Float) -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    var isEnabled: Boolean = false
        private set

    var sensitivity: Float = 1.8f
    var deadZone: Float = 0.04f

    private var biasX = 0f
    private var biasY = 0f

    fun start() {
        if (isEnabled) return
        isEnabled = true
        gyroscope?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_GAME)
        }
    }

    fun stop() {
        isEnabled = false
        sensorManager.unregisterListener(this)
    }

    fun calibrate() {
        // Zeros the current drift bias
        biasX = 0f
        biasY = 0f
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (!isEnabled || event == null) return

        if (event.sensor.type == Sensor.TYPE_GYROSCOPE) {
            // Gyro rotation around Z axis = horizontal cursor movement
            // Gyro rotation around X axis = vertical cursor movement
            val rawDx = -event.values[2] - biasX
            val rawDy = -event.values[0] - biasY

            val dx = if (abs(rawDx) > deadZone) rawDx * sensitivity * 20f else 0f
            val dy = if (abs(rawDy) > deadZone) rawDy * sensitivity * 20f else 0f

            if (dx != 0f || dy != 0f) {
                onDelta(dx, dy)
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
}

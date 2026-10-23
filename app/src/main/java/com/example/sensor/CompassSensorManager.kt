package com.example.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.abs

class CompassSensorManager(context: Context) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val vibrator: Vibrator? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
        vibratorManager?.defaultVibrator
    } else {
        @Suppress("DEPRECATION")
        context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
    }

    private var rotationVectorSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
    private var accelerometerSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
    private var magnetometerSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

    private val _azimuthDegrees = MutableStateFlow(0f)
    val azimuthDegrees: StateFlow<Float> = _azimuthDegrees.asStateFlow()

    private val _accuracy = MutableStateFlow(SensorManager.SENSOR_STATUS_ACCURACY_HIGH)
    val accuracy: StateFlow<Int> = _accuracy.asStateFlow()

    private val _isSensorAvailable = MutableStateFlow(
        rotationVectorSensor != null || (accelerometerSensor != null && magnetometerSensor != null)
    )
    val isSensorAvailable: StateFlow<Boolean> = _isSensorAvailable.asStateFlow()

    // Filter buffers
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)
    private val lastAccelerometer = FloatArray(3)
    private val lastMagnetometer = FloatArray(3)
    private var lastAccelerometerSet = false
    private var lastMagnetometerSet = false

    private var lastVibratedBearing = -1f
    private var hasVibratedForCurrentAlignment = false

    fun startListening() {
        if (rotationVectorSensor != null) {
            sensorManager.registerListener(this, rotationVectorSensor, SensorManager.SENSOR_DELAY_UI)
        } else {
            accelerometerSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
            magnetometerSensor?.let { sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI) }
        }
    }

    fun stopListening() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)
            var azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
            azimuth = (azimuth + 360f) % 360f
            updateSmoothAzimuth(azimuth)
        } else if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, lastAccelerometer, 0, event.values.size)
            lastAccelerometerSet = true
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, lastMagnetometer, 0, event.values.size)
            lastMagnetometerSet = true
        }

        if (rotationVectorSensor == null && lastAccelerometerSet && lastMagnetometerSet) {
            val success = SensorManager.getRotationMatrix(rotationMatrix, null, lastAccelerometer, lastMagnetometer)
            if (success) {
                SensorManager.getOrientation(rotationMatrix, orientationAngles)
                var azimuth = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
                azimuth = (azimuth + 360f) % 360f
                updateSmoothAzimuth(azimuth)
            }
        }
    }

    private fun updateSmoothAzimuth(newAzimuth: Float) {
        val current = _azimuthDegrees.value
        // Handle 360 degree boundary smoothing
        var diff = newAzimuth - current
        while (diff < -180f) diff += 360f
        while (diff > 180f) diff -= 360f

        val smoothed = (current + diff * 0.25f + 360f) % 360f
        _azimuthDegrees.value = smoothed
    }

    /**
     * Checks if current heading matches Qibla bearing within tolerance (e.g. ±3°)
     * and performs subtle haptic feedback once per alignment.
     */
    fun checkAndVibrateOnAlignment(qiblaBearing: Float, toleranceDegrees: Float = 3.5f) {
        val currentHeading = _azimuthDegrees.value
        var diff = abs(currentHeading - qiblaBearing)
        if (diff > 180f) diff = 360f - diff

        val isAligned = diff <= toleranceDegrees
        if (isAligned) {
            if (!hasVibratedForCurrentAlignment) {
                hasVibratedForCurrentAlignment = true
                performHapticTick()
            }
        } else {
            hasVibratedForCurrentAlignment = false
        }
    }

    private fun performHapticTick() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(35L, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(35L)
            }
        } catch (e: Exception) {
            // Ignore if vibration permissions or hardware unavailable
        }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        _accuracy.value = accuracy
    }
}

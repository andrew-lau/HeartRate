package mhci.uk.ac.gla.heartrate;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;

/**
 * Created by Velizar on 6.3.2015 г..
 */
public class VibrationController implements SensorEventListener {

    private Vibrator vibrator;

    public VibrationController(Activity parentActivity) {
        SensorManager heartRateSm = (SensorManager) parentActivity.getSystemService(Context.SENSOR_SERVICE);
        Sensor heartRateSensor = heartRateSm.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        heartRateSm.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);

        vibrator = (Vibrator) parentActivity.getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // do stuff here...
        float[] values = event.values; // get the values for heart rate
        vibrator.vibrate(500);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

}

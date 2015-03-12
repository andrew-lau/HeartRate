package mhci.uk.ac.gla.heartrate;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.util.Log;

/**
 * Created by Velizar on 6.3.2015 г..
 */
public class VibrationController implements SensorEventListener {

    private Vibrator vibrator;
    private MainActivity parent;
    private float start;
    private float end;
    private boolean running;
    private boolean vibrating;

    public VibrationController(MainActivity parentActivity) {
        parent = parentActivity;

        SensorManager heartRateSm = (SensorManager) parentActivity.getSystemService(Context.SENSOR_SERVICE);
        Sensor heartRateSensor = heartRateSm.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        heartRateSm.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_FASTEST);

        vibrator = (Vibrator) parentActivity.getSystemService(Context.VIBRATOR_SERVICE);

        start = end = 0.0f;
        running = false;
    }

    public void setRange(float start, float end) {
        this.start = start;
        this.end = end;
    }

    public void start() {
        running = true;
    }

    public void stop() {
        running = false;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(running) {
            float[] values = event.values; // get the values for heart rate
            float current = values[0];
            Log.e("Heart Beat", "New heart beat data - " + values[0] + ". <<------------------------------------");
            parent.update(current);
            if(start != 0 && end != 0 && current < start && !vibrating) {
                vibrating = true;
                vibrator.vibrate(new long[] {0, 500, 1000, 500, 1000, 500, 1000, 500, 30000}, 0);
            } else if(start != 0 && end != 0 && current > end && !vibrating) {
                vibrating = true;
                vibrator.vibrate(new long[] {0, 500, 100, 500, 100, 500, 100, 500, 30000}, 0);
            } else if(current >= start && current <= end) {
                vibrating = false;
                vibrator.cancel();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(accuracy == SensorManager.SENSOR_STATUS_NO_CONTACT) {
            Log.e("Heart Beat", "No contact to heart sensor. <<------------------------------------");
            parent.update(-1.0f);
        } else {
            Log.e("Heart Beat", "Connected to heart sensor. <<------------------------------------");
        }
    }

}

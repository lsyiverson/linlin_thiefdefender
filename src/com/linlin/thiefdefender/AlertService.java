package com.linlin.thiefdefender;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

public class AlertService extends Service implements SensorEventListener {
    private static final String TAG = "AlertService";

    private SensorManager mSensorManager;
    private Sensor mLightSensor;
    private Sensor mProximitySensor;
    private float mLightValue = -1;
    private float mProximityValue = -1;

    private Intent mAlertIntent;

    @Override
    public void onCreate() {
        super.onCreate();
        mAlertIntent = new Intent(AlertService.this, AlertActivity.class);
        mAlertIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mProximitySensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "service onStartCommand");
        if (null != mLightSensor) {
            mSensorManager.registerListener(this, mLightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (null != mProximitySensor) {
            mSensorManager.registerListener(this, mProximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "service onDestroy");
        if (null != mLightSensor) {
            mSensorManager.unregisterListener(this, mLightSensor);
        }
        if (null != mProximitySensor) {
            mSensorManager.unregisterListener(this, mProximitySensor);
        }
        try {
            this.finalize();
        } catch (Throwable e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // TODO Auto-generated method stub
        switch (event.sensor.getType()) {
        case Sensor.TYPE_LIGHT:
            if (null != mLightSensor) {
                Log.d(TAG + ",light", String.valueOf(event.values[0]));
                if (-1 != mLightValue) {
                    if (event.values[0] / mLightValue >= 20) {
                        Log.i(TAG, "Light Alert!!!");
                        startActivity(mAlertIntent);
                    }
                }
                mLightValue = event.values[0];
            }
            break;
        case Sensor.TYPE_PROXIMITY:
            if (null != mProximitySensor) {
                Log.d(TAG + ",proximity", String.valueOf(event.values[0]));
                if (-1 != mProximityValue) {
                    if (event.values[0] > mProximityValue) {
                        Log.i(TAG, "Proximity Alert!!!");
                        startActivity(mAlertIntent);
                    }
                }
                mProximityValue = event.values[0];
            }
            break;
        default:
            break;
        }
    }

}

package com.linlin.thiefdefender;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CtrlService extends Service implements SensorEventListener {
    private static final String TAG = "CtrlService";

    /**
     * Intent used for start AlertActivity
     */
    private Intent mAlertIntent;
    private Context mContext;
    private IntentFilter mFilter;

    private SensorManager mSensorManager;
    private TelephonyManager mTelephonyManager;
    private Sensor mLightSensor;
    private Sensor mProximitySensor;
    private float mLightValue = -1;
    private float mProximityValue = -1;
    private boolean mScreenON = true;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        mAlertIntent = new Intent(CtrlService.this, AlertActivity.class);
        mAlertIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_SCREEN_ON);
        mFilter.addAction(Intent.ACTION_SCREEN_OFF);

        mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        mTelephonyManager.listen(new TelListener(),
                PhoneStateListener.LISTEN_CALL_STATE);

        // Get the light sensor and proximity sensor
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        mLightSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mProximitySensor = mSensorManager
                .getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            // unregister the listener and receiver
            unregisterSensorListener();
            unregisterReceiver(mScreenStateReceiver);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }
        try {
            this.finalize();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                mScreenON = true;
                unregisterSensorListener();
            }
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                mScreenON = false;
                if (TelephonyManager.CALL_STATE_OFFHOOK != mTelephonyManager
                        .getCallState()) {
                    registerSensorListener();
                }
            }
        }
    };

    private class TelListener extends PhoneStateListener {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            super.onCallStateChanged(state, incomingNumber);
            switch (state) {
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.d(TAG, "offhook");
                try {
                    mContext.unregisterReceiver(mScreenStateReceiver);
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                Log.d(TAG, "idle");
                mContext.registerReceiver(mScreenStateReceiver, mFilter);
                break;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
        case Sensor.TYPE_LIGHT:
            if (null != mLightSensor) {
                if (-1 != mLightValue) {
                    if (!mScreenON && event.values[0] / mLightValue >= 20) {
                        Log.i(TAG, "Light Alert!!!");
                        startActivity(mAlertIntent);
                    }
                }
                mLightValue = event.values[0];
            }
            break;
        case Sensor.TYPE_PROXIMITY:
            if (null != mProximitySensor) {
                if (-1 != mProximityValue) {
                    if (!mScreenON && event.values[0] > mProximityValue) {
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

    /**
     * register the light and proximity sensor
     */
    private void registerSensorListener() {
        Log.d(TAG, "registerSensorListener");
        if (null != mLightSensor) {
            mSensorManager.registerListener(this, mLightSensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
        if (null != mProximitySensor) {
            mSensorManager.registerListener(this, mProximitySensor,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    /**
     * unregister the light and proximity sensor
     */
    private void unregisterSensorListener() {
        Log.d(TAG, "unregisterSensorListener");
        if (null != mLightSensor) {
            mSensorManager.unregisterListener(this, mLightSensor);
        }
        if (null != mProximitySensor) {
            mSensorManager.unregisterListener(this, mProximitySensor);
        }
    }
}

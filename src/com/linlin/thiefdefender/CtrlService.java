package com.linlin.thiefdefender;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class CtrlService extends Service {
    private static final String TAG = "CtrlService";

    private Intent mIntent;
    private Context mContext;
    private IntentFilter mFilter;

    @Override
    public void onCreate() {
        super.onCreate();

        mContext = this;

        mIntent = new Intent(CtrlService.this, AlertService.class);
        mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_SCREEN_ON);
        mFilter.addAction(Intent.ACTION_SCREEN_OFF);

        TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(new TelListener(), PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopService(mIntent);
        try {
            unregisterReceiver(mScreenStateReceiver);
        } catch (IllegalArgumentException e) {
            // TODO: handle exception
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

    private BroadcastReceiver mScreenStateReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
                stopService(mIntent);
            }
            if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
                startService(mIntent);
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
                    // TODO: handle exception
                }
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                Log.d(TAG, "idle");
                mContext.registerReceiver(mScreenStateReceiver, mFilter);
                break;
            }
        }
    }
}

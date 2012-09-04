package com.linlin.thiefdefender;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlertEventReceiver extends BroadcastReceiver {
    
	private static final String LOG_TAG = AlertEventReceiver.class.getSimpleName();
    
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.d(LOG_TAG, "Receive alert action: " + action);
		if(action != null && action.equals(context.getString(R.string.alert_started_broadcast_action))) {
			String trigger = intent.getStringExtra(context.getString(R.string.alert_trigger_broadcast_extra));
			long alertId = insertAlert(context, trigger);
			if(alertId > 0) {
				launchAlertActivity(context, alertId);
			}
			return ;
		}
		if(action != null && action.equals(context.getString(R.string.alert_stopped_broadcast_action))) {
			long alertId = intent.getLongExtra(context.getString(R.string.alert_id_broadcast_extra), -1);
			if(alertId > 0) {
			    updateAlert(context, alertId);
			}
			return ;
		}
	}
	
	private void launchAlertActivity(Context context, long alertId) {
		Intent intent = new Intent(context, AlertActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(context.getString(R.string.alert_id_broadcast_extra), alertId);
		context.startActivity(intent);
	}
	
    /**
     * @param event The event, which causes the alert.
     */
    private long insertAlert(Context context, String trigger) {
    	long retId = -1;
        ThiefDefenderStore store = ThiefDefenderStore.getDefaultStore(context.getApplicationContext());
        ContentValues alertValues = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        alertValues.put(ThiefDefenderAlert.START_TIME, dateFormat.format(new Date()));
        alertValues.put(ThiefDefenderAlert.TRIGGER, trigger);
        retId = store.insertOrIgnore(alertValues);
        return retId;
    }

    private boolean updateAlert(Context context, long alertId) {
    	ThiefDefenderStore store = ThiefDefenderStore.getDefaultStore(context.getApplicationContext());
        ContentValues alertValues = new ContentValues();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date startDate = null;
        try {
	        startDate = dateFormat.parse(store.getAlertStartDate(alertId));
		} catch (ParseException e) {
			e.printStackTrace();
			return false;
		}
        Date endDate = new Date();
        alertValues.put(ThiefDefenderAlert.END_TIME, dateFormat.format(new Date()));
        alertValues.put(ThiefDefenderAlert.DURATION, (endDate.getTime() - startDate.getTime())/1000);
        return store.update(alertValues, alertId);
    }
}

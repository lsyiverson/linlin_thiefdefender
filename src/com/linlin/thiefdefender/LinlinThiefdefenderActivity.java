
package com.linlin.thiefdefender;

import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class LinlinThiefdefenderActivity extends Activity {
    private static final String TAG = "LinlinThiefdefenderActivity";

    private static final String CTRLSERVICE_CLASSNAME = "com.linlin.thiefdefender.CtrlService";

    private Button mStartEndButton;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        mStartEndButton = (Button)findViewById(R.id.StartEndButton);

        // Get the password from the SharedPreferences
        AppData appData = (AppData)getApplicationContext();

        SharedPreferences sharedPfs = getSharedPreferences(appData.getSharedPfsName(),
                Context.MODE_PRIVATE);
        String password = sharedPfs.getString(appData.getSharedPfsKey(), null);

        // If the password is not set, pop up a dialog to set the password.
        if (null == password) {
            AlertDialog.Builder dialogBuilder = (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) ? new AlertDialog.Builder(
                    LinlinThiefdefenderActivity.this) : new AlertDialog.Builder(
                    LinlinThiefdefenderActivity.this, AlertDialog.THEME_HOLO_DARK);
            Dialog dialog = dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(R.string.set_passwd).setMessage(R.string.set_passwd_msg)
                    .setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(LinlinThiefdefenderActivity.this,
                                    SettingPswActivity.class);
                            startActivity(intent);
                        }
                    }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            LinlinThiefdefenderActivity.this.finish();
                        }
                    }).setCancelable(false).create();
            dialog.show();
        }
    }

    @Override
    protected void onResume() {
        initDisplayStates();

        mStartEndButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LinlinThiefdefenderActivity.this, CtrlService.class);

                // Decide what to do when the mStartEndButton press down.
                if (getResources().getString(R.string.start_defender).equals(
                        mStartEndButton.getText().toString())
                        && !isServiceRunning()) {
                    mStartEndButton.setText(R.string.end_defender);
                    Log.d(TAG, "Start CtrlService");
                    startService(intent);
                } else if (getResources().getString(R.string.end_defender).equals(
                        mStartEndButton.getText().toString())
                        && isServiceRunning()) {
                    mStartEndButton.setText(R.string.start_defender);
                    Log.d(TAG, "End CtrlService");
                    stopService(intent);
                } else {
                    initDisplayStates();
                }
            }
        });
        super.onResume();
    }

    /**
     * Get the state of the CtrlService.
     * 
     * @return true if the CtrlService is running, false if there is no
     *         CtrlService running
     */
    private boolean isServiceRunning() {
        ActivityManager am = (ActivityManager)this.getSystemService(ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningServiceInfos = (ArrayList<RunningServiceInfo>)am
                .getRunningServices(30);
        for (int i = 0; i < runningServiceInfos.size(); i++) {
            if (runningServiceInfos.get(i).service.getClassName().equals(CTRLSERVICE_CLASSNAME)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Initialize the UI of the Button and etc.
     */
    private void initDisplayStates() {
        if (isServiceRunning()) {
            mStartEndButton.setText(R.string.end_defender);
        } else {
            mStartEndButton.setText(R.string.start_defender);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();

        switch (item_id) {
            case R.id.mod_psw:
                Intent intent = new Intent(LinlinThiefdefenderActivity.this,
                        SettingPswActivity.class);
                startActivity(intent);
                break;
            case R.id.dump_db:
                try {
                    ThiefDefenderUtils.dumpDatabase(getPackageName(),
                            ThiefDefenderConstants.DB_NAME);
                } catch (Exception e) {
                }
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        this.finish();
        super.onBackPressed();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Kill the process if the CtrlService is not running.
        if (!isServiceRunning()) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
    }
}

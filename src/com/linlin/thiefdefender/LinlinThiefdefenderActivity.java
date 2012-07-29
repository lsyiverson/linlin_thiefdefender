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
        mStartEndButton = (Button) findViewById(R.id.StartEndButton);
        if (isServiceRunning()) {
            mStartEndButton.setText(R.string.end_defender);
        } else {
            mStartEndButton.setText(R.string.start_defender);
        }

        mStartEndButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(LinlinThiefdefenderActivity.this,
                        CtrlService.class);
                if (getResources().getString(R.string.start_defender).equals(
                        mStartEndButton.getText().toString())) {
                    mStartEndButton.setText(R.string.end_defender);
                    Log.d(TAG, "Start CtrlService");
                    startService(intent);
                    return;
                }
                if (getResources().getString(R.string.end_defender).equals(
                        mStartEndButton.getText().toString())) {
                    mStartEndButton.setText(R.string.start_defender);
                    Log.d(TAG, "End CtrlService");
                    stopService(intent);
                    return;
                }
            }
        });

        AppData appData = (AppData) getApplicationContext();

        SharedPreferences sharedPfs = getSharedPreferences(
                appData.getSharedPfsName(), Context.MODE_PRIVATE);
        String password = sharedPfs.getString(appData.getSharedPfsKey(), null);
        if (null == password) {
            Dialog dialog = new AlertDialog.Builder(
                    LinlinThiefdefenderActivity.this)
                    .setTitle(R.string.set_passwd)
                    .setMessage(R.string.set_passwd_msg)
                    .setPositiveButton(R.string.setting,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    Intent intent = new Intent(
                                            LinlinThiefdefenderActivity.this,
                                            SettingPswActivity.class);
                                    startActivity(intent);
                                }
                            })
                    .setNegativeButton(R.string.cancel,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog,
                                        int which) {
                                    System.exit(0);
                                }
                            }).setCancelable(false).create();
            dialog.show();
        }
    }

    private boolean isServiceRunning() {
        ActivityManager am = (ActivityManager) this
                .getSystemService(ACTIVITY_SERVICE);
        ArrayList<RunningServiceInfo> runningServiceInfos = (ArrayList<RunningServiceInfo>) am
                .getRunningServices(30);
        for (int i = 0; i < runningServiceInfos.size(); i++) {
            if (runningServiceInfos.get(i).service.getClassName().equals(
                    CTRLSERVICE_CLASSNAME)) {
                return true;
            }
        }
        return false;
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
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.finish();
    }
}
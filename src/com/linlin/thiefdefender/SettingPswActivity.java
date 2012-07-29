package com.linlin.thiefdefender;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SettingPswActivity extends Activity {
    private static final String TAG = "SettingPswActivity";

    private EditText mOldPasswdEditText;
    private EditText mNewPasswdEditText;
    private EditText mConfirmPasswdEditText;

    private String mOldPasswd;

    private boolean mIsSet;

    private SharedPreferences mSharedPfs;
    private AppData mAppData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_psw);

        mOldPasswdEditText = (EditText) findViewById(R.id.old_psw);
        mNewPasswdEditText = (EditText) findViewById(R.id.new_psw);
        mConfirmPasswdEditText = (EditText) findViewById(R.id.confirm_psw);
        Button setPasswdButton = (Button) findViewById(R.id.set_psw_button);

        mAppData = (AppData) getApplicationContext();
        mSharedPfs = getSharedPreferences(mAppData.getSharedPfsName(),
                Context.MODE_PRIVATE);
        mOldPasswd = mSharedPfs.getString(mAppData.getSharedPfsKey(), null);

        mIsSet = (null != mOldPasswd) ? true : false;

        if (!mIsSet) {
            mOldPasswdEditText.setEnabled(false);
            mNewPasswdEditText.setSelected(true);
        }

        setPasswdButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mIsSet) {
                    if (!mOldPasswdEditText.getText().toString()
                            .equals(mOldPasswd)) {
                        Dialog oldPswWrong = new AlertDialog.Builder(
                                SettingPswActivity.this)
                                .setTitle(R.string.error)
                                .setMessage(R.string.old_psw_err).create();
                        oldPswWrong.show();
                        return;
                    }
                }
                if (mNewPasswdEditText.getText().toString().equals("")
                        || mConfirmPasswdEditText.getText().toString()
                                .equals("")) {
                    Dialog emptyPswWrong = new AlertDialog.Builder(
                            SettingPswActivity.this).setTitle(R.string.error)
                            .setMessage(R.string.empty_psw_err).create();
                    emptyPswWrong.show();
                    return;
                }
                if (!mNewPasswdEditText.getText().toString()
                        .equals(mConfirmPasswdEditText.getText().toString())) {
                    Dialog mismatchPswWrong = new AlertDialog.Builder(
                            SettingPswActivity.this).setTitle(R.string.error)
                            .setMessage(R.string.mismatch_psw_err).create();
                    mismatchPswWrong.show();
                    return;
                }
                Editor editor = mSharedPfs.edit();
                editor.putString(mAppData.getSharedPfsKey(), mNewPasswdEditText
                        .getText().toString());
                editor.commit();
                Dialog success = new AlertDialog.Builder(
                        SettingPswActivity.this)
                        .setTitle(R.string.success)
                        .setMessage(R.string.success_msg)
                        .setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,
                                            int which) {
                                        System.exit(0);
                                    }
                                }).setCancelable(false).create();
                success.show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.finish();
    }
}

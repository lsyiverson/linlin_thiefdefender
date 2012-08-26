
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
import android.widget.TextView;

public class SettingPswActivity extends Activity {
    private static final String TAG = "SettingPswActivity";

    private EditText mOldPasswdEditText;

    private EditText mNewPasswdEditText;

    private EditText mConfirmPasswdEditText;

    private String mOldPasswd;

    private boolean mPasswdIsSet;

    private SharedPreferences mSharedPfs;

    private AppData mAppData;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_psw);

        mOldPasswdEditText = (EditText)findViewById(R.id.old_psw);
        mNewPasswdEditText = (EditText)findViewById(R.id.new_psw);
        mConfirmPasswdEditText = (EditText)findViewById(R.id.confirm_psw);
        Button setPasswdButton = (Button)findViewById(R.id.set_psw_button);

        mAppData = (AppData)getApplicationContext();
        mSharedPfs = getSharedPreferences(mAppData.getSharedPfsName(), Context.MODE_PRIVATE);
        mOldPasswd = mSharedPfs.getString(mAppData.getSharedPfsKey(), null);

        mPasswdIsSet = (null != mOldPasswd) ? true : false;

        if (!mPasswdIsSet) {
            TextView oldPasswdText = (TextView)findViewById(R.id.old_psw_text);
            oldPasswdText.setVisibility(View.GONE);
            mOldPasswdEditText.setVisibility(View.GONE);
        }

        setPasswdButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mPasswdIsSet) {

                    // Enter the wrong old password
                    if (!mOldPasswdEditText.getText().toString().equals(mOldPasswd)) {
                        Dialog oldPswWrong = new AlertDialog.Builder(SettingPswActivity.this)
                                .setTitle(R.string.error).setMessage(R.string.old_psw_err).create();
                        oldPswWrong.show();
                        return;
                    }
                }

                /*
                 * The new password EditView is empty or the confirm password
                 * EditView is empty
                 */
                if (mNewPasswdEditText.getText().toString().equals("")
                        || mConfirmPasswdEditText.getText().toString().equals("")) {
                    Dialog emptyPswError = new AlertDialog.Builder(SettingPswActivity.this)
                            .setTitle(R.string.error).setMessage(R.string.empty_psw_err).create();
                    emptyPswError.show();
                    return;
                }

                // The new password and the confirm password is mismatch
                if (!mNewPasswdEditText.getText().toString()
                        .equals(mConfirmPasswdEditText.getText().toString())) {
                    Dialog mismatchPswError = new AlertDialog.Builder(SettingPswActivity.this)
                            .setTitle(R.string.error).setMessage(R.string.mismatch_psw_err)
                            .create();
                    mismatchPswError.show();
                    return;
                }

                // Save the new password
                Editor editor = mSharedPfs.edit();
                editor.putString(mAppData.getSharedPfsKey(), mNewPasswdEditText.getText()
                        .toString());
                editor.commit();
                Dialog success = new AlertDialog.Builder(SettingPswActivity.this)
                        .setTitle(R.string.success).setMessage(R.string.success_msg)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SettingPswActivity.this.finish();
                            }
                        }).setCancelable(false).create();
                success.show();
            }
        });
    }
}

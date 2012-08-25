
package com.linlin.thiefdefender;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class AlertActivity extends Activity {
    private MediaPlayer mPlayer = new MediaPlayer();

    private AudioManager am;

    private int mCurrentVolume;

    private Vibrator mVibrator;

    private EditText mPasswdText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alert);

        Button enterPasswdButton = (Button)findViewById(R.id.passwd_button);
        mPasswdText = (EditText)findViewById(R.id.passwd);

        // Set the stream volume to max
        am = (AudioManager)getSystemService(AUDIO_SERVICE);
        mCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        am.setStreamVolume(AudioManager.STREAM_MUSIC,
                am.getStreamMaxVolume(AudioManager.STREAM_MUSIC),
                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

        mPlayer = MediaPlayer.create(AlertActivity.this, R.raw.alert);
        mPlayer.setLooping(true);
        mPlayer.start();

        mPlayer.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                mPlayer.release();
            }
        });

        // Use vibrator
        mVibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
        long[] pattern = {
                500, 3500
        };
        if (null != mVibrator) {
            mVibrator.vibrate(pattern, 0);
        }

        enterPasswdButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                AppData appData = (AppData)getApplicationContext();
                SharedPreferences sharedPfs = getSharedPreferences(appData.getSharedPfsName(),
                        Context.MODE_PRIVATE);
                String password = sharedPfs.getString(appData.getSharedPfsKey(), null);

                if (null != password) {
                    if (mPasswdText.getText().toString().equals(password)) {
                        mPlayer.stop();
                        mPlayer.release();

                        // Recover the stream volume
                        am.setStreamVolume(AudioManager.STREAM_MUSIC, mCurrentVolume,
                                AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);

                        if (null != mVibrator) {
                            mVibrator.cancel();
                        }
                        AlertActivity.this.finish();
                    } else {
                        Dialog errPasswd = new AlertDialog.Builder(AlertActivity.this)
                        .setTitle(R.string.error)
                        .setMessage(R.string.error_passwd)
                        .setPositiveButton(R.string.ok,
                                new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mPasswdText.setText("");
                            }
                        }).setCancelable(false).create();
                        errPasswd.show();
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Disable to exit the activity by press BACK key.
    }

}

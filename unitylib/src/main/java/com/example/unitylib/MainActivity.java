package com.example.unitylib;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import com.unity3d.player.UnityPlayer;
import com.unity3d.player.UnityPlayerActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


public class MainActivity extends UnityPlayerActivity {

    private Toast mToast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
    }

    public void showToast(final String text) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {

            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(MainActivity.this, text, Toast.LENGTH_SHORT);
                } else {
                    mToast.setText(text);
                }
                mToast.show();
                UnityPlayer.UnitySendMessage("Canvas","OnAndroidResult",text);
            }
        });
    }

    public String getNowTime() {
        long time = System.currentTimeMillis();
        return new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.CHINESE).format(new Date(time));
    }
}

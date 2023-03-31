package com.android.bhuiyans.serviceapp;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MyService extends Service {
    private static final String TAG = "SERVICE::" + "MyService";
    private static final int GET_RESULT = 0;
    private int result;

    private class RequestHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case GET_RESULT:
                    Message message = Message.obtain(null, GET_RESULT);
                    message.arg1 = getResult();
                    try {
                        msg.replyTo.send(message);
                    } catch (Exception e) {

                    }
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private Messenger messenger = new Messenger(new RequestHandler());

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind():: Service has bounded.");
        return messenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand():: Service has started.");
        new Thread(() -> {
            result = calculateResult(4, 9);
            Log.i(TAG, "result is " + result + " thread id = " + Thread.currentThread().getId());
        }).start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy():: Service has destroyed.");
        messenger = null;
        result = 0;
    }

    private int calculateResult(int a, int b) {
        return a + b;
    }

    private int getResult() {
        return result;
    }
}

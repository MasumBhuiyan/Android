package com.android.bhuiyans.serviceapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String TAG = "SERVICE::" + "ServiceApp::MainActivity";
    private static final String SERVICE_PACKAGE_NAME = "com.android.bhuiyans.serviceapp";
    private static final String SERVICE_CLASS_NAME = SERVICE_PACKAGE_NAME + ".MyService";
    private static final ComponentName SERVICE_COMPONENT_NAME = new ComponentName(SERVICE_PACKAGE_NAME, SERVICE_CLASS_NAME);
    private static final int GET_RESULT = 0;

    private Button startServiceButton;
    private Button stopServiceButton;
    private Button bindServiceButton;
    private Button unbindServiceButton;
    private Button getResultButton;
    private Intent serviceIntent;
    protected Context context;
    private int result = 0;
    private Messenger requestSenderMessenger;
    private Messenger responseReceiverMessenger;
    private boolean isBound;
    private class ResponseHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case GET_RESULT:
                    result = msg.arg1;
                    Log.i(TAG, "Result received:: result = " + result);
                    break;
                default:
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            requestSenderMessenger = new Messenger(service);
            responseReceiverMessenger = new Messenger(new ResponseHandler());
            isBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            requestSenderMessenger = null;
            responseReceiverMessenger = null;
            isBound = false;
            result = 0;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isBound = false;
        context = getApplicationContext();
        startServiceButton = findViewById(R.id.startService);
        bindServiceButton = findViewById(R.id.bindService);
        unbindServiceButton = findViewById(R.id.unbindService);
        stopServiceButton = findViewById(R.id.stopService);
        getResultButton = findViewById(R.id.getResult);

        startServiceButton.setOnClickListener(this);
        bindServiceButton.setOnClickListener(this);
        unbindServiceButton.setOnClickListener(this);
        stopServiceButton.setOnClickListener(this);
        getResultButton.setOnClickListener(this);

        serviceIntent = new Intent();
        serviceIntent.setComponent(SERVICE_COMPONENT_NAME);

        Log.i(TAG, "onCreate():: Thread id = " + Thread.currentThread().getId());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startService:
                startService(serviceIntent);
                Log.i(TAG, "onClick(start Button)");
                break;
            case R.id.bindService:
                bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
                Log.i(TAG, "onClick(bind service Button)");
                break;
            case R.id.unbindService:
                unbindService(serviceConnection);
                Log.i(TAG, "onClick(unbind service Button)");
                break;
            case R.id.getResult:
                Log.i(TAG, "onClick(get result)");
                if (isBound) {
                    Message requestMessage = Message.obtain(null, GET_RESULT);
                    requestMessage.replyTo = responseReceiverMessenger;

                    try {
                        requestSenderMessenger.send(requestMessage);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.i(TAG, "Could not get result as Service is not bounded.");
                }
                break;
            case R.id.stopService:
                stopService(serviceIntent);
                Log.i(TAG, "onClick(stop Button)");
                break;
        }
    }
}
package com.obgz.lockdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.onbright.oblink.cloud.ObInit;
import com.onbright.oblink.cloud.handler.SmartLockHotelHandler;
import com.onbright.oblink.smartconfig.SmartConnectDeviceHandler;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}

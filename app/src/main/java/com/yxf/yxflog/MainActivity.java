package com.yxf.yxflog;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.yxf.log.YxfLog;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        YxfLog.d("interesting");
        YxfLog.sw("it is a simple warming");
        YxfLog.SubLog log = YxfLog.Builder(this).create();
        log.d("it does not interesting");
        log.se("it is a simple error message of sub log");
    }
}

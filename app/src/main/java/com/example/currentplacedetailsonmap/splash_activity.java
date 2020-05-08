package com.example.currentplacedetailsonmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by AbhiAndroid
 */

public class splash_activity extends Activity   {

    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_lyt);

        handler=new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(splash_activity.this,MapsActivityCurrentPlace.class);
                startActivity(intent);
                finish();
            }
        },1000);

    }
}
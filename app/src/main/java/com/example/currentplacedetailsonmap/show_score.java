package com.example.currentplacedetailsonmap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.util.Date;

public class show_score extends Activity {
    private static final String TAG = "Score activity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.score_layout);
        Intent intent = getIntent();
        Bundle b = intent.getExtras();
        Double score = b.getDouble("score");
        Integer place_ct = b.getInt("place_ct");
        Integer dev_ct = b.getInt("dev_ct");
        score = Double.parseDouble(String.format("%.2f", score));
        String sc = score.toString();
        Log.i(TAG,"s"+score);
        TextView t =findViewById(R.id.score_id);
        t.setText(sc);

        TextView p =findViewById(R.id.place_ct_id);
        p.setText(place_ct.toString());

        TextView bt_ct =findViewById(R.id.dev_ct_id);
        bt_ct.setText(dev_ct.toString());

        TextView dat=findViewById(R.id.date);
        Date today = new Date();
        dat.setText(today.toString());

        TextView quote=findViewById(R.id.quote);

    }
}

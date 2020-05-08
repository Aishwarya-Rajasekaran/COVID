package com.example.currentplacedetailsonmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static android.content.ContentValues.TAG;

public class Activity_places_list extends Activity {

    private RecyclerView rv;
    private RecyclerView.Adapter rv_adapter;
    private ArrayList<Details> fetched_list=new ArrayList<Details>();
    static Integer bt_count=0;
    static Integer place_count=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aishu_test);

        this.rv = (RecyclerView) findViewById(R.id.det_data);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        this.rv.setLayoutManager(mLayoutManager);

            Log.i(TAG,"inside Activity places list class");
           // fetched_data =fetch_db();
            //Log.i(TAG,"fetch-data"+fetched_data.size());
            DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
            DatabaseReference tripsRef = rootRef.child("user");
            tripsRef.addValueEventListener(new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds : dataSnapshot.getChildren()) {

                        Details d  = ds.getValue(Details.class);
                        String lat = ds.child("lat").getValue().toString();
                        String longi = ds.child("lon").getValue().toString();
                        String placee = (String) ds.child("place").getValue();
                        String no_dev = ds.child("no_devices").getValue().toString();
                        String time =ds.child("time").getValue().toString();
                        Log.i("TAG", d.getPlace()+"/"+d.getLat() + " / " + d.getLon()  + " / " + d.getNo_devices()+"/"+d.getTime());
                        //Log.i("TAG", ds.getKey()+"/"+lat + " / " + longi  + " / " + placee);
                        fetched_list.add(d);
                        Log.i("TAG", "added success"+fetched_list.size());

                    }
                    rv_adapter = new details_adapter(fetched_list);
                    rv.addItemDecoration(new DividerItemDecoration(Activity_places_list.this, LinearLayoutManager.VERTICAL));

                    rv.setAdapter(rv_adapter);
                    Log.i("TAG","lets check the size inside query"+fetched_list.size());
                 //score_computation(fetched_list);
                 //fetch_db();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }

            //tripsRef.addListenerForSingleValueEvent(valueEventListener);
        });
    }//end of oncreate


    public static ArrayList<Details> fetch_db() {
        final ArrayList<Details> fetched_data=new ArrayList<Details>();
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference tripsRef = rootRef.child("user");
        tripsRef.addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {

                    Details d = ds.getValue(Details.class);
                    String lat = ds.child("lat").getValue().toString();
                    String longi = ds.child("lon").getValue().toString();
                    String placee = (String) ds.child("place").getValue();
                    String no_dev = ds.child("no_devices").getValue().toString();
                    String time = ds.child("time").getValue().toString();
                    Log.i(TAG,"Important--------------"+time);
                    Log.i("TAG", d.getPlace() + "/" + d.getLat() + " / " + d.getLon() + " / " + d.getNo_devices() + "/" + d.getTime());
                    //Log.i("TAG", ds.getKey()+"/"+lat + " / " + longi  + " / " + placee);
                    fetched_data.add(d);
                    Log.i("TAG", "added success" + fetched_data.size());

                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    return fetched_data;
    }



//    private ArrayList<Details> initDetails() {
//        ArrayList<Details> list = new ArrayList<>();
//
//        list.add(new Details("amherst", 1.0, 1.3,2));
//        list.add(new Details("bangalore", 1.0, 1.3,2));
//        list.add(new Details("chennai", 1.0, 1.3,2));
//        list.add(new Details("bikaner", 1.0, 1.3,2));
//        list.add(new Details("bangalore", 1.0, 1.3,2));
//        list.add(new Details("chennai", 1.0, 1.3,2));
//        list.add(new Details("bikaner", 1.0, 1.3,2));
//
//
//        return list;
//    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Intent i = new Intent(this, MapsActivityCurrentPlace.class);
        startActivity(i);

    }


    public double score_computation(ArrayList<Details>fetched_list)  {
        //note that the gettime has data and time
        Log.i(TAG,"Inside score computation");
        Integer size = fetched_list.size();
        double day_score=0.0;
        Map<String, Map<String, Integer>> map = new HashMap<>();

        for (Details d : fetched_list) {
//            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy", Locale.ENGLISH);
//            LocalDate dateTime = LocalDate.parse(d.getTime(), formatter);


            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date da = formatter.parse(d.getTime());
                String s_date = new SimpleDateFormat("dd-MM-yyyy").format(da);
                Log.i(TAG, "msg" + s_date);
                String s_time = new SimpleDateFormat("HH:mm:ss").format(da);


                // in_map.put(d.getPlace(), d.getNo_devices());
                if (map.containsKey(s_date)) {
                    Log.i(TAG, "------bro---- ");
                    if (map.get(s_date).containsKey(d.getPlace())) {

                        Integer ct = map.get(s_date).get(d.getPlace());
                        //Log.i(TAG, map.get(s_date).toString());
                        map.get(s_date).put(d.getPlace(), (ct + d.getNo_devices()));
                    } else {
                        Map<String, Integer> in_map = new HashMap<>();
                        in_map.put(d.getPlace(), d.getNo_devices());
                        //Log.i(TAG, "else "+in_map.get(s_date).toString());
                        map.put(s_date, in_map);
                    }

                } else {
                    Map<String, Integer> in_map = new HashMap<>();
                    in_map.put(d.getPlace(), d.getNo_devices());
                    map.put(s_date, in_map);
                    //Log.i(TAG, "---------- "+map.get(s_date).toString());
                }
            } catch (Exception e) {

            }
        }



            //print all values
            Log.i(TAG, map.toString());

            //get current date
            //go to that key in the map and for all its values count the values

            DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
            String today_date = formatter.format(new Date());
            Log.i(TAG, "todays date" + today_date);

            //today_date = "05-05-2020";
            bt_count = 0;

            for (String name : map.get(today_date).keySet()) {
                bt_count += map.get(today_date).get(name);
            }
            Log.i(TAG, "bt_count" + bt_count);
            place_count = map.get(today_date).size();
            Log.i(TAG, "place_count" + place_count);

            day_score = (0.6 * place_count + 0.4 * bt_count);

            Log.i(TAG, "Score" + day_score);




        return day_score;
    }

}

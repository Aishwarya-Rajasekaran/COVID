package com.example.currentplacedetailsonmap;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class Database_helper {

    private DatabaseReference mDatabase;
    private DatabaseReference mref;
    private List<Details> det =new ArrayList<>();

    public Database_helper(){
        mDatabase = FirebaseDatabase.getInstance().getReference("user");
    }

    public interface DataStatus{
        void DataisLoaded(List<Details> det, List<String> keys);
    }
    public void readDetails(final DataStatus dataStatus)
    {
        //Asynchronous process
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                det.clear();
                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode:dataSnapshot.getChildren())
                {
                    keys.add((keyNode.getKey()));
                    Details d = keyNode.getValue(Details.class);
                    det.add(d);
                }
                dataStatus.DataisLoaded(det,keys);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




}

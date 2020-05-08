package com.example.currentplacedetailsonmap;
//
//import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class details_adapter extends RecyclerView.Adapter<details_adapter.ViewHolder> {

    private ArrayList<Details> d_obj;

    public details_adapter(ArrayList<Details> d_obj) {
        this.d_obj = d_obj;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.detail_item, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Details d = d_obj.get(position);
        holder.hold_place.setText(d.getPlace());
        holder.hold_no_dev.setText(d.getNo_devices().toString());
        holder.hold_time.setText(d.getTime());
    }

    @Override
    public int getItemCount() {
        if (d_obj != null) {
            return d_obj.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView hold_place;
       public final TextView hold_no_dev;
        public final TextView hold_time;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            hold_place= view.findViewById(R.id.place_id);
            hold_no_dev= view.findViewById(R.id.dev_no);
            hold_time=view.findViewById(R.id.time_stamp);
        }
    }
}

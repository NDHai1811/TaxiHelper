package com.google.mlkit.vision.demo.realtime_data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.mlkit.vision.demo.R;

import java.util.ArrayList;

public class DataInfo_Adapter extends RecyclerView.Adapter<DataInfo_Adapter.ViewHolder>{

    private ArrayList<DataInfo> dates;
    private Context context;

    public DataInfo_Adapter(ArrayList<DataInfo> dates, Context context) {
        this.dates = dates;
        this.context = context;
    }
    @NonNull
    @Override
    public DataInfo_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = (View) LayoutInflater.from(parent.getContext()).inflate(R.layout.realtime_info, parent, false);

        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull DataInfo_Adapter.ViewHolder holder, int position) {
        DataInfo data = dates.get(position);
        holder.location.setText(data.getLocation());
        holder.realtime.setText(data.getrTime());
        holder.counter.setText(data.getCounter());
    }

    @Override
    public int getItemCount() {
        if (dates != null) {
            return dates.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final View view;
        public final TextView location;
        public final TextView realtime;
        public final TextView counter;

        public ViewHolder(View view) {
            super(view);
            this.view = view;
            location = view.findViewById(R.id.locateTV);
            realtime = view.findViewById(R.id.timeTV);
            counter = view.findViewById(R.id.sleepCountTV);
        }
    }
}

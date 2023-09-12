package com.sample.gs_gram.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.gs_gram.Data.SimulationUserLectureData;
import com.sample.gs_gram.R;

import java.util.List;

public class SimulatorAdapter extends RecyclerView.Adapter<SimulatorAdapter.ViewHolder> {
    private List<SimulationUserLectureData> mDataset;


    public class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView textview_divition, textview_field, textview_credit, textview_subject;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textview_subject = itemView.findViewById(R.id.textview_subject);
            textview_divition = itemView.findViewById(R.id.textview_divition);
            textview_field = itemView.findViewById(R.id.textview_field);
            textview_credit = itemView.findViewById(R.id.textview_credit);
            rootView =  itemView;
        }
    }


    public SimulatorAdapter(List<SimulationUserLectureData> myDataset) {
        mDataset = myDataset;

    }

    @NonNull
    @Override
    public SimulatorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v;
        v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_divition_renew, parent, false);
        SimulatorAdapter.ViewHolder vh = new SimulatorAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SimulatorAdapter.ViewHolder holder, int position) {
            SimulationUserLectureData  simulData = mDataset.get(position);
            holder.textview_subject.setText(simulData.getSimulation_subject());
            holder.textview_divition.setText(simulData.getSimulation_divition());
            holder.textview_field.setText(simulData.getSimulation_culture_area());
            holder.textview_credit.setText(simulData.getSimulation_credit());
    }

    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
    }



}

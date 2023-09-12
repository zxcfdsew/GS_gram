package com.sample.gs_gram.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sample.gs_gram.Data.SimulationCultureData;
import com.sample.gs_gram.Data.SimulationMajorData;
import com.sample.gs_gram.R;

import java.util.ArrayList;
import java.util.List;

public class SimulatorCultureAdapter extends RecyclerView.Adapter<SimulatorCultureAdapter.ViewHolder> {
    private List<SimulationCultureData> mDataset;
    private List<String> listA= new ArrayList<String>();

    public class ViewHolder extends RecyclerView.ViewHolder {
        public View rootView;
        public TextView textview_subject, textview_divition, textview_grade, textview_term, textview_credit, textview_confirm;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textview_subject = itemView.findViewById(R.id.textview_subject);
            textview_divition = itemView.findViewById(R.id.textview_divition);
            textview_grade = itemView.findViewById(R.id.textview_grade);
            textview_term = itemView.findViewById(R.id.textview_term);
            textview_credit = itemView.findViewById(R.id.textview_credit);
            textview_confirm = itemView.findViewById(R.id.textview_confirm);
            rootView = itemView;
        }
    }


    public SimulatorCultureAdapter(List<SimulationCultureData> myDataset, Context context, List<String> listA) {
        mDataset = myDataset;
        this.listA = listA;
    }

    @NonNull
    @Override
    public SimulatorCultureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v;
        v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_divition_culture, parent, false);
        SimulatorCultureAdapter.ViewHolder vh = new SimulatorCultureAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SimulatorCultureAdapter.ViewHolder holder, int position) {

        SimulationCultureData simulData = mDataset.get(position);

        holder.textview_confirm.setText("미이수");
        holder.textview_confirm.setTextColor(Color.RED);
        if (listA.contains(simulData.getCode())) {
            holder.textview_confirm.setText("이수");
            holder.textview_confirm.setTextColor(Color.BLACK);
        }

        holder.textview_subject.setText(simulData.getSubject());
        holder.textview_divition.setText(simulData.getDivition());
        holder.textview_grade.setText(simulData.getGrade());
        holder.textview_term.setText(simulData.getTerm());
        holder.textview_credit.setText(simulData.getCredit());
    }

    @Override
    public int getItemCount() {
        return mDataset == null ? 0 : mDataset.size();
    }


}

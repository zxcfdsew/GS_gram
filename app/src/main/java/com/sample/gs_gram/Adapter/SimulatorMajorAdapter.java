package com.sample.gs_gram.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sample.gs_gram.Data.SimulationMajorData;
import com.sample.gs_gram.Data.SimulationUserLectureData;
import com.sample.gs_gram.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SimulatorMajorAdapter extends RecyclerView.Adapter<SimulatorMajorAdapter.ViewHolder> {
    private List<SimulationMajorData> mDataset;

    private FirebaseFirestore mStore;
    private FirebaseAuth mAuth;
    private String currentUid;
    private List<String> listA = new ArrayList<String>();

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


    public SimulatorMajorAdapter(List<SimulationMajorData> myDataset, Context context, List<String> listA) {
        mDataset = myDataset;
        this.listA = listA;
    }

    @NonNull
    @Override
    public SimulatorMajorAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout v;
        v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_divition_major, parent, false);
        SimulatorMajorAdapter.ViewHolder vh = new SimulatorMajorAdapter.ViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull SimulatorMajorAdapter.ViewHolder holder, int position) {

        mStore = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUid = mAuth.getCurrentUser().getUid();
        SimulationMajorData simulData = mDataset.get(position);

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

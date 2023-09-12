package com.sample.gs_gram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.gs_gram.Data.SubjectData;
import com.sample.gs_gram.R;

import java.util.ArrayList;
import java.util.List;

public class SaveAdapter extends RecyclerView.Adapter<SaveAdapter.ViewHolder> {

    private ArrayList<SubjectData> originData;
    private ArrayList<SubjectData> filteredData;
    private OnItemClickListener listener;

    public SaveAdapter(ArrayList<SubjectData> data){
        this.originData = data;
        this.filteredData = new ArrayList<>(data);
    }

    @NonNull
    @Override
    public SaveAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_subject_view, parent, false);
        SaveAdapter.ViewHolder viewHolder = new SaveAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull SaveAdapter.ViewHolder holder, int position) {
        SubjectData item = filteredData.get(position);

        holder.textview_subject.setText(item.getSubject());
        holder.textview_divition.setText(item.getDivition());
        holder.textview_credit.setText(item.getCredit());

        if (item.getDepartment().equals("전학과")){
            holder.major_layout.setVisibility(View.GONE);
        }else {
            holder.textview_major.setText(item.getMajor());
        }

        holder.button_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickedPosition = holder.getAdapterPosition(); // 클릭한 아이템의 위치 저장
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    listener.onItemClick(filteredData.get(clickedPosition));
                    remove(clickedPosition); // 선택된 항목 삭제
                }
            }
        });

    }
    public void remove(int position){
        if (position >= 0 && position < filteredData.size()){
            filteredData.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public int getItemCount() {return filteredData.size();}

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textview_divition, textview_subject, textview_credit ,textview_major ,button_choose;
        private LinearLayout major_layout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textview_divition = itemView.findViewById(R.id.textview_divition);
            textview_subject = itemView.findViewById(R.id.textview_subject);
            textview_credit = itemView.findViewById(R.id.textview_credit);
            textview_major = itemView.findViewById(R.id.textview_major);
            button_choose = itemView.findViewById(R.id.button_choose);
            major_layout = itemView.findViewById(R.id.major_layout);
        }
    }
    public interface OnItemClickListener {
        void onItemClick(SubjectData clickedData);
    }
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void filterData(String searchText){
        filteredData.clear();

        if (searchText.isEmpty()){
            filteredData.addAll(originData); //비어있으면 전체데이터
        }else {
            for (SubjectData subject : originData){
                if (subject.getSubject().toLowerCase().contains(searchText.toLowerCase())){
                    filteredData.add(subject);
                }
            }
        }
        notifyDataSetChanged();
    }
    public void setItems(List<SubjectData> items){
        filteredData.clear();
        filteredData.addAll(items);
        notifyDataSetChanged();
    }
}

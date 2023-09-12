package com.sample.gs_gram.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sample.gs_gram.Data.SubjectData;
import com.sample.gs_gram.R;

import java.util.ArrayList;

public class InquireAdapter extends RecyclerView.Adapter<InquireAdapter.ViewHolder>{
    private ArrayList<SubjectData> data;
    private CartAdapter.OnItemClickListener listener;

    public InquireAdapter(ArrayList<SubjectData> data){this.data = data;}

    @NonNull
    @Override
    public InquireAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_inquire, parent, false);
        InquireAdapter.ViewHolder viewHolder = new InquireAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull InquireAdapter.ViewHolder holder, int position) {
        SubjectData item = data.get(position);

        holder.textview_divition.setText(item.getDivition());
        holder.textview_subject.setText(item.getSubject());
        holder.textview_credit.setText(item.getCredit());
        holder.textview_field.setText(item.getField());

        holder.button_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int clickedPosition = holder.getAdapterPosition();
                if (clickedPosition!=RecyclerView.NO_POSITION){
                    if (listener != null) {
                        listener.onItemClick(data.get(clickedPosition));
                    }
                }
            }
        });
    }

    @Override
    public int getItemCount() {return (data != null ? data.size() : 0);}
    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView textview_divition, textview_subject, textview_credit, textview_field, button_delete;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textview_divition = itemView.findViewById(R.id.textview_divition);
            textview_subject = itemView.findViewById(R.id.textview_subject);
            textview_credit = itemView.findViewById(R.id.textview_credit);
            textview_field = itemView.findViewById(R.id.textview_field);
            button_delete = itemView.findViewById(R.id.button_delete);
        }
    }
    public interface OnItemClickListener{
        void onItemClick(SubjectData clickedData);
    }
    public void setOnItemClickListener(CartAdapter.OnItemClickListener listener){this.listener = listener;}
}

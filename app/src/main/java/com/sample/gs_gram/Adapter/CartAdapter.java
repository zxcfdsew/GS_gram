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

import java.lang.reflect.Array;
import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {
    private ArrayList<SubjectData> data;
    private CartAdapter.OnItemClickListener listener;

    public CartAdapter(ArrayList<SubjectData> data){this.data = data;}

    @NonNull
    @Override
    public CartAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_cart, parent, false);
        CartAdapter.ViewHolder viewHolder = new CartAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CartAdapter.ViewHolder holder, int position) {
        SubjectData item = data.get(position);

        holder.textview_subject.setText(item.getSubject());
        holder.textview_divition.setText(item.getDivition());
        holder.textview_major.setText(item.getMajor());
        if (item.getDivition().equals("교양")){
            holder.textview_term.setVisibility(View.INVISIBLE);
        }else {
            holder.textview_term.setText(item.getTerm());
        }

        holder.textview_delete.setOnClickListener(new View.OnClickListener() {
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
        private TextView textview_divition, textview_subject, textview_delete, textview_term, textview_major;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            textview_divition = itemView.findViewById(R.id.textview_divition);
            textview_subject = itemView.findViewById(R.id.textview_subject);
            textview_delete = itemView.findViewById(R.id.textview_delete);
            textview_term = itemView.findViewById(R.id.textview_term);
            textview_major = itemView.findViewById(R.id.textview_major);
        }
    }
    public interface OnItemClickListener{
        void onItemClick(SubjectData clickedData);
    }
    public void setOnItemClickListener(OnItemClickListener listener){this.listener = listener;}
}

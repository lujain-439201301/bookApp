package com.example.bookapp.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.example.bookapp.R;
import com.example.bookapp.buyer.interestActivity;
import com.example.bookapp.models.Interest;

import java.util.List;

public class InterestAdapter extends RecyclerView.Adapter<InterestAdapter.MyViewHolder> {

    List<Interest> list;
    Context context;
    boolean isEnabled = false;

    public InterestAdapter(List<Interest> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_view_interest, parent, false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.name.setText(list.get(position).getName());

        if (list.get(position).isSelected())
        {
            holder.name.setTextColor(Color.RED);
        }else
        {
            holder.name.setTextColor(Color.BLACK);
        }

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Interest i = list.get(position);
                if(i.getName().equals("All"))
                {
                    list.get(0).setSelected(!list.get(0).isSelected());
                    for(int loop = 1; loop < list.size(); loop++)
                    {
                        list.get(loop).setSelected(list.get(0).isSelected());
                    }
                }else{
                    list.get(0).setSelected(false);
                    list.get(position).setSelected(!list.get(position).isSelected());
                    holder.name.setTextColor(Color.RED);
                }
                notifyDataSetChanged();


            }
        });
    }


    private void CLickItem(MyViewHolder holder) {
        if (!isEnabled) {
            interestActivity.listInterest.add(list.get(holder.getAdapterPosition()));
            holder.name.setTextColor(Color.RED);
            isEnabled = true;
        } else {
            isEnabled = false;
            holder.name.setTextColor(Color.BLACK);
            interestActivity.listInterest.remove(list.get(holder.getAdapterPosition()));
        }

    }


    @Override
    public int getItemCount() {
        return list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView name;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            name = itemView.findViewById(R.id.name);

        }
    }
}

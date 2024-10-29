package com.tbi.chatapplication.dummyui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.tbi.chatapplication.R;
import com.tbi.chatapplication.databinding.RvWeekBinding;
import com.tbi.chatapplication.dummyui.mode.WeekModel;

import java.util.List;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.MyViewHolder> {

    List<WeekModel> modelList;
    boolean isFirstClick = true;


    public WeekAdapter(List<WeekModel> modelList) {
        this.modelList = modelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RvWeekBinding binding=RvWeekBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        WeekModel model=modelList.get(position);

        holder.binding.tvDate.setText(model.getDate());
        holder.binding.tvDay.setText(model.getDay());

        if (model.isSelected()) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.green_500));
            //selectedIds.add(model.getId());
        } else {
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            //unselectedIds.add(model.getId());
            holder.binding.tvDate.setAlpha(0.3f);
            holder.binding.tvDay.setAlpha(0.3f);
        }

        holder.itemView.setOnClickListener(v -> {
            int id=model.getId();
            if (isFirstClick) {
                isFirstClick = false;
                if (id % 2 == 0) {
                    disableOddDays();
                } else {
                    disableEvenDays();
                }
            }
        });
    }

    private void disableOddDays() {
        int size = modelList.size();
        for (int i = 0; i < size; i++) {
            if (i % 2 != 0) {
                modelList.get(i).setSelected(false);
                notifyItemChanged(i);
            }
        }
    }

    private void disableEvenDays() {
        int size = modelList.size();
        for (int i = 0; i < size; i++) {
            if (i % 2 == 0) {
                modelList.get(i).setSelected(false);
                notifyItemChanged(i);
            }
        }

    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        RvWeekBinding binding;
        public MyViewHolder(RvWeekBinding binding) {
            super(binding.getRoot());
            this.binding=binding;
        }
    }
}

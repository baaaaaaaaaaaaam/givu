package com.login.donation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.login.donation.Object.response_mypage_donate_volunteer;
import com.login.donation.Object.volunteer_recruitment;
import com.login.donation.R;
import com.login.donation.info.info;

import java.util.ArrayList;

public class mypage_foundation_volunteer_recruitmentAdapter extends RecyclerView.Adapter<mypage_foundation_volunteer_recruitmentAdapter.ViewHolder> {

    private ArrayList<volunteer_recruitment> mData;


    public mypage_foundation_volunteer_recruitmentAdapter(ArrayList<volunteer_recruitment> mData) {
        this.mData = mData;

    }

    private mypage_foundation_volunteer_recruitmentAdapter.MyrecyclerViewClickListener mListener;


    public interface MyrecyclerViewClickListener {
        void onItemClicked(int position,String adapter_name);
    }

    public void setOnclickListener(mypage_foundation_volunteer_recruitmentAdapter.MyrecyclerViewClickListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public mypage_foundation_volunteer_recruitmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_mypage_foundation_volunteer_recruitment_list, null);

        return new mypage_foundation_volunteer_recruitmentAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(mypage_foundation_volunteer_recruitmentAdapter.ViewHolder holder, int position) {

        volunteer_recruitment object = mData.get(position);


        Glide.with(holder.imageview.getContext())
                .load(info.upload_ip + object.getImagePath()).circleCrop()
                .into(holder.imageview);
        holder.writer.setText(object.getId());
        holder.startDate.setText(object.getStartDate());
        holder.endDate.setText(object.getEndDate());
        holder.startTime.setText(object.getStartTime());
        holder.endTime.setText(object.getEndTime());
        holder.location.setText(object.getLocation());



        if (mListener != null) {
            final int pos = position;
            holder.touch_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClicked(pos,"volunteer");
                }
            });
        }

    }

    @Override
    public int getItemCount() {

        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageview;
        TextView writer, startDate, endDate,startTime,endTime,location;
        LinearLayout touch_ll;

        public ViewHolder(View view) {
            super(view);
            imageview = view.findViewById(R.id.profile);
            writer = view.findViewById(R.id.id);
            startDate = view.findViewById(R.id.startDate);
            endDate = view.findViewById(R.id.endDate);
            startTime = view.findViewById(R.id.startTime);
            endTime = view.findViewById(R.id.endTime);
            location = view.findViewById(R.id.location);
            touch_ll = view.findViewById(R.id.touch_ll);
        }
    }
}

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
import com.login.donation.Object.response_mypage_donate;
import com.login.donation.Object.response_mypage_donate_volunteer;
import com.login.donation.R;
import com.login.donation.info.info;

import java.util.ArrayList;

public class mypage_donate_volunteer_Adapter  extends RecyclerView.Adapter<mypage_donate_volunteer_Adapter.ViewHolder> {

    private ArrayList<response_mypage_donate_volunteer> mData;


    public mypage_donate_volunteer_Adapter(ArrayList<response_mypage_donate_volunteer> mData) {
        this.mData = mData;

    }

    private mypage_donate_volunteer_Adapter.MyrecyclerViewClickListener mListener;


    public interface MyrecyclerViewClickListener {
        void onItemClicked(int position,String adapter_name);
    }

    public void setOnclickListener(mypage_donate_volunteer_Adapter.MyrecyclerViewClickListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public mypage_donate_volunteer_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_mypage_donate_volunteer_list, null);

        return new mypage_donate_volunteer_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(mypage_donate_volunteer_Adapter.ViewHolder holder, int position) {

        response_mypage_donate_volunteer object = mData.get(position);


        Glide.with(holder.imageview.getContext())
                .load(info.upload_ip + object.getVolunteer().getImagePath()).circleCrop()
                .into(holder.imageview);
        holder.writer.setText(object.getVolunteer().getId());
        holder.startDate.setText(object.getVolunteer().getStartDate());
        holder.endDate.setText(object.getVolunteer().getEndDate());
        holder.startTime.setText(object.getVolunteer().getStartTime());
        holder.endTime.setText(object.getVolunteer().getEndTime());
        holder.location.setText(object.getVolunteer().getLocation());
        holder.time.setText(object.getTime()+"분");


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
        TextView writer, startDate, endDate,startTime,endTime,location,time;
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
            time = view.findViewById(R.id.time);
            touch_ll = view.findViewById(R.id.touch_ll);
        }
    }
}
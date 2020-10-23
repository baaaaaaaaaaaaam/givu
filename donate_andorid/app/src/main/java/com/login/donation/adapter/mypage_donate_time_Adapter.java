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
import com.login.donation.Object.time_campaign;
import com.login.donation.R;
import com.login.donation.info.info;

import java.util.ArrayList;

public class mypage_donate_time_Adapter extends RecyclerView.Adapter<mypage_donate_time_Adapter.ViewHolder>{

    private ArrayList<time_campaign> mData ;


    public mypage_donate_time_Adapter(ArrayList<time_campaign> mData){
        this.mData=mData;

    }

    private mypage_donate_time_Adapter.MyrecyclerViewClickListener mListener;


    public interface MyrecyclerViewClickListener{
        void onItemClicked(int position,String adapter_name);
    }

    public void setOnclickListener(mypage_donate_time_Adapter.MyrecyclerViewClickListener listener){
        mListener=listener;
    }




    @NonNull
    @Override
    public mypage_donate_time_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_mypage_donate_list, null);

        return new mypage_donate_time_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder( mypage_donate_time_Adapter.ViewHolder holder, int position) {

        time_campaign object=mData.get(position);
        Glide.with(holder.imageview.getContext())
                .load(info.upload_ip+object.getImagePath()).circleCrop()
                .into(holder.imageview);
        holder.writer.setText(object.getId());
        holder.startDate.setText(object.getStartDate());
        holder.endDate.setText(object.getEndDate());
        holder.subject.setText(object.getSubject());
        holder.donate.setText(object.getTime()+" 분");

        if(mListener!=null){
            final int pos=position;
            holder.touch_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClicked(pos,"donate_time");
                }
            });
        }

    }

    @Override
    public int getItemCount() {

        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageview;
        TextView writer,startDate,endDate,subject,donate;
        LinearLayout touch_ll;
        public ViewHolder(View view) {
            super(view);
            imageview = view.findViewById(R.id.profile);
            writer = view.findViewById(R.id.writer);
            startDate = view.findViewById(R.id.startDate);
            endDate = view.findViewById(R.id.endDate);
            subject = view.findViewById(R.id.subject);
            donate = view.findViewById(R.id.donate);
            touch_ll=view.findViewById(R.id.touch_ll);
        }
    }




}

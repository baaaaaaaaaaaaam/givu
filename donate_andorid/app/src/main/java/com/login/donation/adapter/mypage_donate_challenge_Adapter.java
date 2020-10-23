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
import com.login.donation.Object.time_campaign;
import com.login.donation.R;
import com.login.donation.info.info;

import java.util.ArrayList;

public class mypage_donate_challenge_Adapter extends RecyclerView.Adapter<mypage_donate_challenge_Adapter.ViewHolder>{

private ArrayList<time_campaign> mData;


public mypage_donate_challenge_Adapter(ArrayList<time_campaign> mData){
        this.mData=mData;

        }

private MyrecyclerViewClickListener mListener;


public interface MyrecyclerViewClickListener {
    void onItemClicked(int position, String adapter_name);
}

    public void setOnclickListener(mypage_donate_challenge_Adapter.MyrecyclerViewClickListener listener) {
        mListener = listener;
    }


    @NonNull
    @Override
    public mypage_donate_challenge_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_mypage_doante_challenge, null);

        return new mypage_donate_challenge_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(mypage_donate_challenge_Adapter.ViewHolder holder, int position) {

        time_campaign object = mData.get(position);
        Glide.with(holder.imageview.getContext())
                .load(info.upload_ip + object.getImagePath()).circleCrop()
                .into(holder.imageview);
        holder.writer.setText(object.getId());
        holder.startDate.setText(object.getStartDate());
        holder.endDate.setText(object.getEndDate());
        if(object.getPermission().equals("reject")){
            holder.mission.setText("거절");
        }else if(object.getDoing().equals("true")){
            holder.mission.setText("진행중");
        }else if(object.getMission().equals("success")){
            holder.mission.setText("성공");
        }else if(object.getMission().equals("fail")){
            holder.mission.setText("실패");
        }
        holder.donate.setText(object.getMoney()+"원");


        if (mListener != null) {
            final int pos = position;
            holder.touch_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClicked(pos, "challege");
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
    TextView writer, startDate, endDate, mission, donate;
    LinearLayout touch_ll;

    public ViewHolder(View view) {
        super(view);
        imageview = view.findViewById(R.id.profile);
        writer = view.findViewById(R.id.writer);
        startDate = view.findViewById(R.id.startDate);
        endDate = view.findViewById(R.id.endDate);
        mission = view.findViewById(R.id.mission);
        donate = view.findViewById(R.id.donate);
        touch_ll = view.findViewById(R.id.touch_ll);
    }
}

}

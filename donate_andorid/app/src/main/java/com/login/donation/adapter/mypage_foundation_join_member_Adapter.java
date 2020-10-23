package com.login.donation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.login.donation.Object.campaign_add_list_member;
import com.login.donation.Object.response_mypage_foundation_join_member;
import com.login.donation.R;
import com.login.donation.info.info;

import java.util.ArrayList;

public class mypage_foundation_join_member_Adapter extends RecyclerView.Adapter<mypage_foundation_join_member_Adapter.HorizontalViewHolder> {


    private ArrayList<response_mypage_foundation_join_member> dataList;

    public mypage_foundation_join_member_Adapter(ArrayList<response_mypage_foundation_join_member> data )
    {
        this.dataList = data;

    }

    @NonNull
    @Override
    public mypage_foundation_join_member_Adapter.HorizontalViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.item_foundation_join_member, null);

        return new mypage_foundation_join_member_Adapter.HorizontalViewHolder(v);
    }
    @Override
    public void onBindViewHolder(mypage_foundation_join_member_Adapter.HorizontalViewHolder horizontalViewHolder, int position) {

        response_mypage_foundation_join_member m = dataList.get(position) ;
        Glide.with(horizontalViewHolder.itemView.getContext())
                .load(info.upload_ip+m.getImagepath()).circleCrop()
                .into(horizontalViewHolder.imageview);
        horizontalViewHolder
                .id
                .setText(m.getMemberid());



    }


    public class HorizontalViewHolder extends RecyclerView.ViewHolder{
        protected ImageView imageview;
        protected TextView id;

        public HorizontalViewHolder(View view) {
            super(view);
            imageview = view.findViewById(R.id.profile);
            id = view.findViewById(R.id.id);


        }
    }




    @Override
    public int getItemCount() {
        return dataList.size();
    }


}

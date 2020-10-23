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
import com.login.donation.Object.Image_and_id_object;
import com.login.donation.R;
import com.login.donation.info.info;

import java.util.ArrayList;

public class campaign_detail_pageAdapter extends RecyclerView.Adapter<campaign_detail_pageAdapter.HorizontalViewHolder> {



    Context context;
    private ArrayList<Image_and_id_object> dataList;

    public campaign_detail_pageAdapter(ArrayList<Image_and_id_object> data, Context context)
    {
        this.dataList = data;
        this.context=context;
    }



    @NonNull
    @Override
    public campaign_detail_pageAdapter.HorizontalViewHolder onCreateViewHolder(ViewGroup
    viewGroup, int i)
    {
        View v = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.sub_item_main, null);

        return new campaign_detail_pageAdapter.HorizontalViewHolder(v);
    }
    @Override
    public void onBindViewHolder(campaign_detail_pageAdapter.HorizontalViewHolder horizontalViewHolder, int position) {

        Image_and_id_object m = dataList.get(position) ;
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
        protected CheckBox check;
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

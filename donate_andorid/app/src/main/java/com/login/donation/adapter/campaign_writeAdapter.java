package com.login.donation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.login.donation.Object.campaign_write;
import com.login.donation.R;

import java.util.ArrayList;

public class campaign_writeAdapter extends RecyclerView.Adapter<campaign_writeAdapter.HorizontalViewHolder> {
    private ArrayList<campaign_write> dataList;

    public campaign_writeAdapter(ArrayList<campaign_write> data)
    {
        this.dataList = data;
    }

    public class HorizontalViewHolder extends RecyclerView.ViewHolder{
        protected ImageView imageview;
        protected TextView id;

        public HorizontalViewHolder(View view)
        {
            super(view);
            imageview = view.findViewById(R.id.profile);
            id = view.findViewById(R.id.id);
        }
    }

    @NonNull
    @Override
    public campaign_writeAdapter.HorizontalViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.item_write_campaign, null);

        return new campaign_writeAdapter.HorizontalViewHolder(v);
    }

    @Override
    public void onBindViewHolder(campaign_writeAdapter.HorizontalViewHolder horizontalViewHolder, int position)
    {
        campaign_write m = dataList.get(position) ;
        Glide.with(horizontalViewHolder.itemView.getContext())
                .load(m.getAddImagePath()).circleCrop()
                .into(horizontalViewHolder.imageview);
        horizontalViewHolder
                .id
                .setText(m.getAddId());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}

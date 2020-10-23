package com.login.donation.adapter;

import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.login.donation.Object.Image_and_id_object;
import com.login.donation.R;

import java.util.ArrayList;

public class sub_mainAdapter extends RecyclerView.Adapter<sub_mainAdapter.HorizontalViewHolder> {

    private ArrayList<Image_and_id_object> dataList;

    public sub_mainAdapter(ArrayList<Image_and_id_object> data)
    {
        this.dataList = data;
    }


    @NonNull
    @Override
    public HorizontalViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.sub_item_main, null);

        return new sub_mainAdapter.HorizontalViewHolder(v);
    }





    @Override
    public void onBindViewHolder(HorizontalViewHolder horizontalViewHolder, int position)
    {




        Image_and_id_object m = dataList.get(position) ;
        Glide.with(horizontalViewHolder.itemView.getContext())
                .load(m.getImagepath()).circleCrop()
                .into(horizontalViewHolder.imageview);
        horizontalViewHolder
                .id
                .setText(m.getMemberid());
    }

    @Override
    public int getItemCount() {
        return dataList.size();
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
}

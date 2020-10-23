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
import com.login.donation.Object.campaign;
import com.login.donation.Object.response_mypage_beneficiary_notify;
import com.login.donation.R;
import com.login.donation.info.info;

import java.util.ArrayList;

public class mypage_beneficiary_notify_Adapter extends RecyclerView.Adapter<mypage_beneficiary_notify_Adapter.ViewHolder>{

    private ArrayList<response_mypage_beneficiary_notify> mData ;


    public mypage_beneficiary_notify_Adapter(ArrayList<response_mypage_beneficiary_notify> mData){
        this.mData=mData;

    }

    private mypage_beneficiary_notify_Adapter.MyrecyclerViewClickListener mListener;


    public interface MyrecyclerViewClickListener{
        void onItemClicked(int position);
    }

    public void setOnclickListener(mypage_beneficiary_notify_Adapter.MyrecyclerViewClickListener listener){
        mListener=listener;
    }




    @NonNull
    @Override
    public mypage_beneficiary_notify_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_mypage_beneficiary_notify, null);

        return new mypage_beneficiary_notify_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder( mypage_beneficiary_notify_Adapter.ViewHolder holder, int position) {

        response_mypage_beneficiary_notify object=mData.get(position);

        holder.notify.setText(object.toString());

        if(mListener!=null){
            final int pos=position;
            holder.notify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClicked(pos);
                }
            });
        }

    }

    @Override
    public int getItemCount() {

        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView notify;

        public ViewHolder(View view) {
            super(view);
          notify=view.findViewById(R.id.message);


        }
    }
}

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
import com.login.donation.R;
import com.login.donation.info.info;

import java.util.ArrayList;

public class mypage_beneficiary_campaign_Adatpter  extends RecyclerView.Adapter<mypage_beneficiary_campaign_Adatpter.ViewHolder>{

    String Adapt_name;
    private ArrayList<campaign> mData ;


    public mypage_beneficiary_campaign_Adatpter(ArrayList<campaign> mData,String Adapt_name){
        this.mData=mData;
        this.Adapt_name=Adapt_name;
    }

    private mypage_beneficiary_campaign_Adatpter.MyrecyclerViewClickListener mListener;


    public interface MyrecyclerViewClickListener{
        void onItemClicked(int position,String Adapt_name);
    }

    public void setOnclickListener(mypage_beneficiary_campaign_Adatpter.MyrecyclerViewClickListener listener){
        mListener=listener;
    }




    @NonNull
    @Override
    public mypage_beneficiary_campaign_Adatpter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_mypage_campaign_info, null);

        return new mypage_beneficiary_campaign_Adatpter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder( mypage_beneficiary_campaign_Adatpter.ViewHolder holder, int position) {

        campaign object=mData.get(position);
        Glide.with(holder.imageview.getContext())
                .load(info.upload_ip+object.getImagePath()).circleCrop()
                .into(holder.imageview);
        holder.writer.setText(object.getWriter());
        holder.startDate.setText(object.getStartDate());
        holder.endDate.setText(object.getEndDate());
        holder.subject.setText(object.getSubject());
        holder.total_sum.setText(object.getCollection() + " 원");
        if(object.getDoing().equals("true")){
            holder.doing.setText("진행중");
        }else if(object.getDoing().equals("false")){
            holder.doing.setText("종료");
        }

        if(mListener!=null){
            final int pos=position;
            holder.touch_ll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClicked(pos,Adapt_name);
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
        TextView writer,startDate,endDate,subject,total_sum,doing;
        LinearLayout touch_ll;
        public ViewHolder(View view) {
            super(view);
            touch_ll=view.findViewById(R.id.touch_ll);
            imageview = view.findViewById(R.id.profile);
            writer = view.findViewById(R.id.writer);
            startDate = view.findViewById(R.id.startDate);
            endDate = view.findViewById(R.id.endDate);
            subject = view.findViewById(R.id.subject);
            total_sum = view.findViewById(R.id.total_sum);
            touch_ll=view.findViewById(R.id.touch_ll);
            doing=view.findViewById(R.id.doing);
        }
    }
}

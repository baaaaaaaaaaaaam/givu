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

public class mypage_foundation_campaign_Adapter  extends RecyclerView.Adapter<mypage_foundation_campaign_Adapter.ViewHolder>{


    //mypage_foundationActivity에서 doing_campaign과 not_doing_campaign 두가지 종류가 잇다
    // 때문에 하나의 mypage_foundation_campaign_Adapter 를 두가지 이름으로 사용한다.
    // 만약 진행 중 캠페인의 0번 아이템을 터치하였다면 어뎁터 이름과 포지션 번호를 mypage_foundationActivity에서 구분하여 onItemClicked에서 받을수있게
    //position값과 string 값을 전달한다.
    // 캠페인 이름을 정해줌으로써 해당 액티비티에서 앱을 터치할 때 어떤 어뎁터에서
    String adapter_name;
    private ArrayList<campaign> mData ;


    public mypage_foundation_campaign_Adapter(ArrayList<campaign> mData,String adapter_name){
        this.mData=mData;
        this.adapter_name=adapter_name;
    }

    private mypage_foundation_campaign_Adapter.MyrecyclerViewClickListener mListener;


    public interface MyrecyclerViewClickListener{
        void onItemClicked(int position,String adapter_name);
    }

    public void setOnclickListener(mypage_foundation_campaign_Adapter.MyrecyclerViewClickListener listener){
        mListener=listener;
    }




    @NonNull
    @Override
    public mypage_foundation_campaign_Adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_mypage_campaign_info, null);

        return new mypage_foundation_campaign_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder( mypage_foundation_campaign_Adapter.ViewHolder holder, int position) {

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
                    mListener.onItemClicked(pos,adapter_name);
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
            doing=view.findViewById(R.id.doing);

        }
    }




}

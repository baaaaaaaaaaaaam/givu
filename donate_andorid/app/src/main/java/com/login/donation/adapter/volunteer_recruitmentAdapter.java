package com.login.donation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.login.donation.Object.volunteer_recruitment;
import com.login.donation.R;
import com.login.donation.info.info;

import java.util.ArrayList;

public class volunteer_recruitmentAdapter extends RecyclerView.Adapter<volunteer_recruitmentAdapter.ViewHolder> {

    private ArrayList<volunteer_recruitment> mData = null ;
    private Context context;
    // 생성자에서 데이터 리스트 객체를 전달받음.
    public volunteer_recruitmentAdapter(ArrayList<volunteer_recruitment> list, Context context) {
        mData = list ;
        this.context=context;
    }


    //클릭리스너
    private volunteer_recruitmentAdapter.MyrecyclerViewClickListener mListener;

    public interface MyrecyclerViewClickListener{
        void onItemClicked(int position);
    }

    public void setOnclickListener(volunteer_recruitmentAdapter.MyrecyclerViewClickListener listener){
        mListener=listener;
    }




    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public volunteer_recruitmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        //위에 껄로 하면 화면에 꽉차게 리사이클러뷰가 동작하지만 아래걸로 하면 match_parent를 해도 화면의 2/3밖에 안됨
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_volunteer_recruitment, parent, false) ;
        volunteer_recruitmentAdapter.ViewHolder vh = new volunteer_recruitmentAdapter .ViewHolder(view) ;

        return vh ;

//
//        View v = LayoutInflater
//                .from(parent.getContext())
//                .inflate(R.layout.item_volunteer_recruitment, null);
//
//        return new volunteer_recruitmentAdapter.ViewHolder(v);
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView item_cardview;

        TextView subject ;
        ImageView imageview ;
        TextView startDate ;
        TextView endDate ;
        TextView startTime;
        TextView endTime;
        TextView location;

        TextView doing;
        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)

            item_cardview=itemView.findViewById(R.id.item_cardview);
            subject = itemView.findViewById(R.id.subject) ;
            imageview = itemView.findViewById(R.id.profile) ;
            startDate = itemView.findViewById(R.id.startDate) ;
            endDate = itemView.findViewById(R.id.endDate) ;
            startTime=itemView.findViewById(R.id.startTime) ;
            endTime=itemView.findViewById(R.id.endTime) ;
            location=itemView.findViewById(R.id.location) ;
            doing=itemView.findViewById(R.id.doing);


        }
    }
    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(volunteer_recruitmentAdapter.ViewHolder holder, int position) {
        volunteer_recruitment c = mData.get(position) ;
        holder.subject.setText(c.getSubject()) ;
        Glide.with(holder.itemView.getContext())
                .load(info.upload_ip+c.getImagePath())
                .into(holder.imageview);
        holder.startDate.setText(c.getStartDate()) ;
        holder.endDate.setText(c.getEndDate()) ;
        holder.startTime.setText(c.getStartTime()) ;
        holder.endTime.setText(c.getEndTime()) ;
        holder.location.setText(c.getLocation()) ;
        SpannableString s = new SpannableString("진행중 / 종료");
        if(c.getDoing().equals("true")){
            s.setSpan(new ForegroundColorSpan(Color.BLUE), 0, 3, 0);
            s.setSpan(new ForegroundColorSpan(Color.GRAY), 6, 8, 0);
            holder.doing.setText(s) ;
        }else if(c.getDoing().equals("false")){
            s.setSpan(new ForegroundColorSpan(Color.GRAY), 0, 3, 0);
            s.setSpan(new ForegroundColorSpan(Color.RED), 6, 8, 0);
            holder.doing.setText(s) ;
        }




        if(mListener!=null){
            final int pos=position;
            holder.item_cardview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClicked(pos);
                }
            });



        }
    }

    // getItemCount() - 전체 데이터 갯수 리턴.
    @Override
    public int getItemCount() {
        return mData.size() ;
    }
}

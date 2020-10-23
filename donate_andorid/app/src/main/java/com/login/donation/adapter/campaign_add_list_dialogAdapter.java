package com.login.donation.adapter;

import android.content.Context;
import android.util.Log;
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
import com.login.donation.R;

import java.util.ArrayList;



/*
add_share_listDialog에 생성된 리사이클러뷰에 관한 설정파일이다
이 어뎁터를 통해 서버에서받아온 그룹멤버 아이디와 이미지를 add_share_listDialog에 리스트 형태로 보여준다
리스트에서 체크박스를 선택할 경우 해당 객체 dataList에서 찾아 객체의 check 값이 true이면 false로 변경하고 false이면 true로 변경시킨다.
 */





public class campaign_add_list_dialogAdapter extends RecyclerView.Adapter<campaign_add_list_dialogAdapter.HorizontalViewHolder> {



    Context context;
    private ArrayList<campaign_add_list_member> dataList;

    public campaign_add_list_dialogAdapter(ArrayList<campaign_add_list_member> data,Context context)
    {
        this.dataList = data;
        this.context=context;
    }


    private MyrecyclerViewClickListener mListener;

    public interface MyrecyclerViewClickListener{
        void onItemClicked(int position);
    }

    public void setOnclickListener(MyrecyclerViewClickListener listener){
        mListener=listener;
    }

    @NonNull
    @Override
    public campaign_add_list_dialogAdapter.HorizontalViewHolder onCreateViewHolder(ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater
                .from(viewGroup.getContext())
                .inflate(R.layout.item_add_share, null);

        return new campaign_add_list_dialogAdapter.HorizontalViewHolder(v);
    }
    @Override
    public void onBindViewHolder(campaign_add_list_dialogAdapter.HorizontalViewHolder horizontalViewHolder, int position) {

        campaign_add_list_member m = dataList.get(position) ;
        Glide.with(horizontalViewHolder.itemView.getContext())
                .load(m.getImagepath()).circleCrop()
                .into(horizontalViewHolder.imageview);
        horizontalViewHolder
                .id
                .setText(m.getMemberid());

        if(mListener!=null){
            final int pos=position;
            horizontalViewHolder.check.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    mListener.onItemClicked(pos);
                    if(dataList.get(pos).isCheck()){
                        dataList.get(pos).setCheck(false);
                    }else{
                        dataList.get(pos).setCheck(true);
                    }
                }
            });

        }

    }



    public class HorizontalViewHolder extends RecyclerView.ViewHolder{
        protected ImageView imageview;
        protected TextView id;
        protected CheckBox check;
        public HorizontalViewHolder(View view) {
            super(view);
            imageview = view.findViewById(R.id.profile);
            id = view.findViewById(R.id.id);
            check=view.findViewById(R.id.checked);

        }
    }




    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public void addItem(campaign_add_list_member item,int pos) {
        dataList.add(pos, item);
        notifyItemInserted(pos);
        notifyItemRangeChanged(0, dataList.size());
    }

}

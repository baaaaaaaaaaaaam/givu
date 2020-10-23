package com.login.donation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.login.donation.Object.Image_and_id_object;
import com.login.donation.R;
import com.login.donation.info.info;

import java.util.ArrayList;

public class time_campaign_donation_signup_Adapter extends RecyclerView.Adapter<time_campaign_donation_signup_Adapter.ViewHolder> implements Filterable {

    ArrayList<Image_and_id_object> mData;
    ArrayList<Image_and_id_object> unfilter_mData;
    ArrayList<Image_and_id_object> empty_mData = new ArrayList<>();
    private boolean isSelected;
    Context context;
    public time_campaign_donation_signup_Adapter(Context context, ArrayList mData){
        super();
        this.unfilter_mData=mData;
        this.mData=mData;
        this.context=context;

    }

    private time_campaign_donation_signup_Adapter.MyrecyclerViewClickListener mListener;


    public interface MyrecyclerViewClickListener{
        void onItemClicked(String memberid);
    }

    public void setOnclickListener(time_campaign_donation_signup_Adapter.MyrecyclerViewClickListener listener){
        mListener=listener;
    }



    @NonNull
    @Override
    public time_campaign_donation_signup_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.item_mypage_beneficiary_signup, null);

        return new time_campaign_donation_signup_Adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull time_campaign_donation_signup_Adapter.ViewHolder holder, int position) {
        Image_and_id_object object= mData.get(position);
        Glide.with(holder.group_image.getContext())
                .load(info.upload_ip+object.getImagepath()).circleCrop()
                .into(holder.group_image);
        holder.foundation_id.setText(object.getMemberid());


        if(mListener!=null){
            final int pos=position;
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onItemClicked(mData.get(pos).getMemberid());

                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView group_image;
        TextView foundation_id;
        LinearLayout linearLayout;
        public ViewHolder(View view) {
            super(view);
            foundation_id = view.findViewById(R.id.foundation_id);
            group_image = view.findViewById(R.id.group_image);
            linearLayout = view.findViewById(R.id.Linearlayout);

        }
    }





    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if(charString.isEmpty()) {
                    //입력한값이 없을떄
                    mData = empty_mData;
                } else {
                    ArrayList<Image_and_id_object> filtering_mData = new ArrayList<>();
                    for(Image_and_id_object object : unfilter_mData) {
                        if(object.getMemberid().toLowerCase().contains(charString.toLowerCase())) {
                            filtering_mData.add(object);
                        }
                    }
                    mData = filtering_mData;
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = mData;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mData = (ArrayList<Image_and_id_object>) results.values;
                notifyDataSetChanged();
            }
        };
    }


}
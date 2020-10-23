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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.login.donation.Object.campaign;
import com.login.donation.R;

import java.util.ArrayList;



/*
메인 액티비티에서 보여주는 카드뷰 리스트 형태로 보여준다
카드뷰안에 기부자 정보가 리스트형태로 포함되어있어 어뎁터 안에 추가되어있다.
 */



public class mainAdapter extends RecyclerView.Adapter<mainAdapter.ViewHolder> {

    private ArrayList<campaign> mData = null ;
    private Context context;
    // 생성자에서 데이터 리스트 객체를 전달받음.
    public mainAdapter(ArrayList<campaign> list,Context context) {
        mData = list ;
        this.context=context;
    }


    //클릭리스너
    private MyrecyclerViewClickListener mListener;

    public interface MyrecyclerViewClickListener{
        void onItemClicked(int position);
    }

    public void setOnclickListener(MyrecyclerViewClickListener listener){
        mListener=listener;
    }




    // onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴.
    @Override
    public mainAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext() ;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

        View view = inflater.inflate(R.layout.item_main, parent, false) ;
        mainAdapter.ViewHolder vh = new mainAdapter .ViewHolder(view) ;

        return vh ;
    }


    // 아이템 뷰를 저장하는 뷰홀더 클래스.
    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView item_cardview;
        TextView writer ;
        TextView subject ;
        ImageView imageview ;
        TextView startDate ;
        TextView endDate ;
        TextView collection;
        protected RecyclerView recyclerView;
        TextView doing;
        ViewHolder(View itemView) {
            super(itemView) ;
            // 뷰 객체에 대한 참조. (hold strong reference)

            item_cardview=itemView.findViewById(R.id.item_cardview);
            writer = itemView.findViewById(R.id.writer) ;
            subject = itemView.findViewById(R.id.subject) ;
            imageview = itemView.findViewById(R.id.profile) ;
            startDate = itemView.findViewById(R.id.startDate) ;
            endDate = itemView.findViewById(R.id.endDate) ;
            collection=itemView.findViewById(R.id.collection) ;
            doing=itemView.findViewById(R.id.doing);
            this.recyclerView = (RecyclerView)itemView.findViewById(R.id.recyclerViewVertical);

        }
    }
    // onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템뷰에 표시.
    @Override
    public void onBindViewHolder(mainAdapter.ViewHolder holder, int position) {
        campaign c = mData.get(position) ;
        holder.writer.setText(c.getWriter()) ;
        holder.subject.setText(c.getSubject()) ;
        Glide.with(holder.itemView.getContext())
                .load(c.getImagePath())
                .into(holder.imageview);
        holder.startDate.setText(c.getStartDate()) ;
        holder.endDate.setText(c.getEndDate()) ;
        holder.collection.setText(c.getCollection()) ;
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
        c.getImage_and_id_object();

        sub_mainAdapter adapter = new sub_mainAdapter(c.getImage_and_id_object());

        holder.recyclerView.setHasFixedSize(true);
        holder.recyclerView.setLayoutManager(new LinearLayoutManager(context
                , LinearLayoutManager.HORIZONTAL
                ,false));
        holder.recyclerView.setAdapter(adapter);



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
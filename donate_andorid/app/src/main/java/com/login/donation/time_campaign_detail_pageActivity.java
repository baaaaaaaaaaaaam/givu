package com.login.donation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.login.donation.Object.Image_and_id_object;
import com.login.donation.Object.campaign;
import com.login.donation.Object.responseResult;
import com.login.donation.Object.response_campaign_detail_page;
import com.login.donation.Object.response_time_campaign_detail_page;
import com.login.donation.Object.time_campaign;
import com.login.donation.adapter.campaign_detail_pageAdapter;
import com.login.donation.info.info;
import com.login.donation.retrofit.MyRetrofit2;
import com.login.donation.retrofit.UploadService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class time_campaign_detail_pageActivity extends AppCompatActivity {


    TextView writer, subject,mission_money,currunt_time,mission_time,startDate,endDate,donation,content;
    ImageView profile;
    ProgressBar process,mprogressbar;
    Button permission,reject,donate,back,end;

    // 쉐어드에서 캠페인 번호 및 접속한 유저 상태
    SharedPreferences pref;
    String id,mode,seq;

    campaign_detail_pageAdapter share_adapter,joiner_adapter;
    RecyclerView recyclerview_share_list,recyclerview_joiner;
    ArrayList<Image_and_id_object> joiner_array,beneficiary_array;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_campaign_detail_page);


        //객체 초기화
        initiate_object();
        //쉐어드 초기화
        getShared();
        //버튼 초기화
        initiate_touch();
        //서버에서 데이터 받아오기
        getServerDate();
    }

    void initiate_object(){
        writer=findViewById(R.id.writer);
        subject=findViewById(R.id.subject);
        mission_money=findViewById(R.id.mission_money);
        currunt_time=findViewById(R.id.currunt_time);
        mission_time=findViewById(R.id.mission_time);
        startDate=findViewById(R.id.startDate);
        endDate=findViewById(R.id.endDate);
        donation=findViewById(R.id.donation);
        content=findViewById(R.id.content);
        process=findViewById(R.id.process);
        end=findViewById(R.id.end);
        profile=findViewById(R.id.profile);
        permission=findViewById(R.id.permission);
        reject=findViewById(R.id.reject);
        donate=findViewById(R.id.donate);
        back=findViewById(R.id.back);
        mprogressbar=findViewById(R.id.progress);



        beneficiary_array=new ArrayList<>();
        recyclerview_share_list=findViewById(R.id.recyclerview_share_list);
        recyclerview_share_list.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL
                ,false));
        recyclerview_share_list.setHasFixedSize(true);
        share_adapter = new campaign_detail_pageAdapter(beneficiary_array,getApplicationContext());
        recyclerview_share_list.setAdapter(share_adapter);



        joiner_array=new ArrayList<>();
        recyclerview_joiner=findViewById(R.id.recyclerview_joiner);
        recyclerview_joiner.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL
                ,false));
        recyclerview_joiner.setHasFixedSize(true);
        joiner_adapter = new campaign_detail_pageAdapter(joiner_array,getApplicationContext());
        recyclerview_joiner.setAdapter(joiner_adapter);



        pref=getSharedPreferences("auto_login",MODE_PRIVATE);

    }
    void getShared(){
        id=pref.getString("id","");
        mode=pref.getString("mode","");
        pref = getSharedPreferences("campaign_detail_page_info", MODE_PRIVATE);
        seq=pref.getString("seq","");
    }

    void initiate_touch(){
        permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_permission("agree");
            }
        });
        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upload_permission("reject");
            }
        });
        donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                donate_time();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                end_signal();
            }
        });
    }

    void getServerDate(){
        Map select = new HashMap();
        select.put("request","detail_time_campaignActivity");
        select.put("seq",seq);

        UploadService retrofit = MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<response_time_campaign_detail_page>call =retrofit.get_time_campaign_detail(select);
        call.enqueue(new Callback<response_time_campaign_detail_page>() {
            @Override
            public void onResponse(Call<response_time_campaign_detail_page> call, Response<response_time_campaign_detail_page> response) {
                response_time_campaign_detail_page repo=response.body();

                if(repo.getResult().equals("ok")){
                    parsing_time_campaign(repo.getTime_campaign());
                    parsing_joiner(repo.getJoiner());
                    parsing_share_list(repo.getShare_list());
                }else {
                    Toast.makeText(getApplicationContext(),repo.getResult(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<response_time_campaign_detail_page> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    void parsing_time_campaign(String s){

        if(s.length()==0){
            Log.d("test111","parsing_joiner에러");
        }else{
            Gson gson = new Gson();
            time_campaign result = gson.fromJson(s, time_campaign.class);

            writer.setText(result.getId());
            subject.setText(result.getSubject());
            Glide.with(profile.getContext())
                    .load(info.upload_ip+result.getImagePath())
                    .into(profile);
            mission_money.setText(result.getMoney());
            currunt_time.setText(result.getCurrent_time()+"");
            mission_time.setText(result.getTime());
            process.setMax(Integer.parseInt(result.getTime()));
            process.setProgress(result.getCurrent_time());
            startDate.setText(result.getStartDate());
            endDate.setText(result.getEndDate());
            donation.setText(result.getDonation());
            content.setText(result.getContent());


            //아이디 비교해서 버튼 지우기
            if (result.getPermission().equals("false")){
                //기부자가 아직 승락하기전 기부자와 로그인한 유저가 같은 경우에만 승락,거절 버튼 보이고
                //나머지의 경우 뒤로가기 버튼만 보임
                if(result.getDonation().equals(id)){

                    check_button_state(View.VISIBLE,View.VISIBLE,View.GONE,View.GONE);
                }else{
                    check_button_state(View.GONE,View.GONE,View.GONE,View.VISIBLE);
                }
            }else if(result.getPermission().equals("agree")){
                //기부자가 승락햇고 ,  진행이 false된 캠페인은 뒤로가기버튼만
                // 기부자가 승락하고 , 진행 중인 캠페인에 기부자만 기부할 수 있다.
                //캠페인이 승락되고 , 로그인한사람과 글쓴이 (기부단체) 가같은 아이디일경우 종료하기 버튼 보여줌
                if(result.getDoing().equals("true")){
                    if(mode.equals("mode:1")){
                        check_button_state(View.GONE,View.GONE,View.VISIBLE,View.VISIBLE);
                    }else if(id.equals(result.getId())){
                        end.setVisibility(View.VISIBLE);
                    } else{
                        check_button_state(View.GONE,View.GONE,View.GONE,View.VISIBLE);
                    }
                }else if(result.getDoing().equals("false")){
                    check_button_state(View.GONE,View.GONE,View.GONE,View.VISIBLE);
                }
            }else if(result.getPermission().equals("reject")){
                check_button_state(View.GONE,View.GONE,View.GONE,View.VISIBLE);
            }

        }


    }
    void parsing_joiner(String s){
        joiner_array.clear();
        if(s.length()==2){
            //나눔 대상자 없음
        }else{
            Gson gson = new Gson();
            Image_and_id_object[] array = gson.fromJson(s, Image_and_id_object[].class);
            for(Image_and_id_object temp : array){

                joiner_array.add(temp);
            }
           joiner_adapter.notifyDataSetChanged();
        }

    }
    void parsing_share_list(String s){
        beneficiary_array.clear();
        if(s.length()==2){
            //나눔 대상자 없음
        }else{
            Gson gson = new Gson();
            Image_and_id_object[] array = gson.fromJson(s, Image_and_id_object[].class);
            for(Image_and_id_object temp : array){

                beneficiary_array.add(temp);
            }
            share_adapter.notifyDataSetChanged();
        }
    }

    //상태에따라 다른 버튼 보이게함
    void check_button_state(int a,int b,int c, int d){
        permission.setVisibility(a);
        reject.setVisibility(b);
        donate.setVisibility(c);
        back.setVisibility(d);
    }
    void button_enable(boolean b){
        permission.setEnabled(b);
        reject.setEnabled(b);
        donate.setEnabled(b);
        back.setEnabled(b);
    }
    void  upload_permission(String s){
        mprogressbar.setVisibility(View.VISIBLE);
        button_enable(false);
        Map update = new HashMap();
        update.put("request","time_campaign_upload_permission");
        update.put("seq",seq);
        update.put("id",id);
        update.put("permission",s);

        UploadService retrofit = MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<responseResult>call =retrofit.simple_result(update);


        call.enqueue(new Callback<responseResult>() {
            @Override
            public void onResponse(Call<responseResult> call, Response<responseResult> response) {
                mprogressbar.setVisibility(View.GONE);
                button_enable(true);
                responseResult repo= response.body();

                if(repo.getResult().equals("ok")){
                    check_button_state(View.GONE,View.GONE,View.VISIBLE,View.VISIBLE);
                }else if(repo.getResult().equals("no:7")){
                    check_button_state(View.VISIBLE,View.VISIBLE,View.GONE,View.GONE);
                    Toast.makeText(getApplicationContext(),"충전이 필요합니다",Toast.LENGTH_LONG).show();
                }
                else{
                    check_button_state(View.GONE,View.GONE,View.GONE,View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<responseResult> call, Throwable t) {
                mprogressbar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),"네트워크 통신 불안",Toast.LENGTH_LONG).show();
            }
        });
    }

    void donate_time(){
        donateDialog customDialog = new donateDialog(time_campaign_detail_pageActivity.this);
        customDialog.callFunction(id,seq,"point");
    }


    void end_signal(){
        mprogressbar.setVisibility(View.VISIBLE);
        Map update = new HashMap();
        update.put("request","time_campaign_end");
        update.put("seq",seq);
        UploadService retrofit = MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<responseResult>call =retrofit.simple_result(update);

        call.enqueue(new Callback<responseResult>() {
            @Override
            public void onResponse(Call<responseResult> call, Response<responseResult> response) {
                mprogressbar.setVisibility(View.GONE);
                responseResult repo=response.body();
                Log.d("test111",repo.getResult());
                if(repo.getResult().equals("ok")){
                    Toast.makeText(getApplicationContext(),repo.getResult(),Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(),repo.getResult(),Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<responseResult> call, Throwable t) {
                mprogressbar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}

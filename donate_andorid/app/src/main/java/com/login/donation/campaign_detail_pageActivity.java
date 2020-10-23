package com.login.donation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.login.donation.Object.Image_and_id_object;
import com.login.donation.Object.campaign;
import com.login.donation.Object.responseResult;
import com.login.donation.Object.response_campaign_detail_page;
import com.login.donation.adapter.campaign_detail_pageAdapter;
import com.login.donation.info.info;
import com.login.donation.retrofit.MyRetrofit2;
import com.login.donation.retrofit.UploadService;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;



/*

메인 페이지에서 선택한 캠페인의 켐페인 번호만 저장한 후 상세 페이지로 이동한다
상세 페이지에서는 작성자 , 제목 ,이미지, 기간 , 기부자 , 수혜자 ,내용이 표시된다
기부자나 수혜자의 경우 하단에 기부하기 버튼과 뒤로가기 버튼이있고
기부단체는 캠페인 종료하기 버튼이있다. 캠페인 종료하기버튼은 기간이 끝난날부터 종료할수있다.
캠페인 종료하기 버튼을 누르면 자동으로 모금액이 나눔된다.

서


메소드

        initiate(); 사용할 객체 초기화
        getSharedPrefence();  이전페이지에서 받아온 시퀀스 정보 꺼내기
        check_mode();  로그인 한 유저가 일반회원이거나 수혜자일경우 기부하기버튼 활성화 , 기부 단체일 경우 종료하기활성화
        getShare_list_from_server(); 서버에 캠페인 번호를 전달하여 캠페인 정보  , 나눔 대상 ,기부자 리스트를 받아온다
            getCampaign()  : 캠패인 정보 파싱  , 서버에서 받아온 캠페인 정보 중 종료 날짜와 현재 기부 진행중 상태를비교하여 기부하기 버튼을 활성화 하거나 비활성화 한다.
            getShare_list() : 나눔 대상자 아이디 , 이미지 파싱
            getDonate_list()  : 기부자 아이디 , 이미지 파싱
        touch_object();
            dialog_give_money() : 기부하기 버튼을 누를경우 다이얼로그창을 띄우면서 , 기부자의 아이디와 캠패인 시퀀스를 넘겨준다.




리사이클러뷰 :

나눔 대상
메인 : campaign_detail_pageActivity
adapter : sub_mainAdapter
Object :Image_and_id_object
activity : activity_camapgin_detail_page
item : sub_item_main


기부 참여자
메인 : campaign_detail_pageActivity
adapter : sub_mainAdapter
Object : Image_and_id_object
activity : activity_camapgin_detail_page
item : sub_item_main

 */


public class campaign_detail_pageActivity extends AppCompatActivity {


    SharedPreferences pref;

    TextView writer,subject,startDate,endDate,content,collection;
    ImageView imageview;
    RecyclerView share_recyclerview,donation_recyclerview;
    Button donateBtn,backBtn,endBtn;
    campaign_detail_pageAdapter share_adapter,donation_adapter;
    ArrayList<Image_and_id_object> donation_arry,beneficiary_array;
    private ProgressBar mProgressBar;
    String mode;
    String campaign_seq;
    String id;
    Map queryInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_campaign_detail_page);




        initiate();
        getSharedPrefence();
        check_mode();
        touch_object();



    }

    @Override
    protected void onStart() {
        super.onStart();
        getShare_list_from_server();
    }

    void initiate(){
        writer=findViewById(R.id.writer);
        subject=findViewById(R.id.subject);
        startDate=findViewById(R.id.startDate);
        endDate=findViewById(R.id.endDate);
        content=findViewById(R.id.content);
        imageview=findViewById(R.id.profile);
        share_recyclerview=findViewById(R.id.share_list);
        donation_recyclerview=findViewById(R.id.donation_list);
        donateBtn=findViewById(R.id.donate);
        backBtn=findViewById(R.id.back);
        mProgressBar = findViewById(R.id.progress);
        donation_arry=new ArrayList<>();
        beneficiary_array=new ArrayList<>();
        donation_recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL
                ,false));
        donation_recyclerview.setHasFixedSize(true);
        share_recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL
                ,false));
        share_recyclerview.setHasFixedSize(true);
        collection=findViewById(R.id.collection);
        endBtn=findViewById(R.id.end);

    }

    void getSharedPrefence(){
        pref = getSharedPreferences("auto_login", MODE_PRIVATE);
        id=pref.getString("id","");
        pref = getSharedPreferences("campaign_detail_page_info", MODE_PRIVATE);
        campaign_seq=pref.getString("seq","");
    }

    void check_mode(){
        pref = getSharedPreferences("auto_login", MODE_PRIVATE);
        mode=pref.getString("mode","");
        if(mode.equals("mode:2")){
            donateBtn.setVisibility(View.GONE);
            endBtn.setVisibility(View.VISIBLE);
        }else if(mode.equals("mode:1")){
            endBtn.setVisibility(View.GONE);
            donateBtn.setVisibility(View.VISIBLE);
        }else if(mode.equals("mode:3")){
            endBtn.setVisibility(View.GONE);
            donateBtn.setVisibility(View.GONE);
        }
    }

    void getShare_list_from_server(){
            // 서버로부터 나눔 대상을 불러온다
        queryInfo = new HashMap();
        queryInfo.put("request","campaign_detail_page");
        queryInfo.put("seq",campaign_seq);


        UploadService Retrofit= MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<response_campaign_detail_page> call = Retrofit.campaign_detail_page(queryInfo);
        call.enqueue(new Callback<response_campaign_detail_page>() {
            @Override
            public void onResponse(Call<response_campaign_detail_page> call, Response<response_campaign_detail_page> response) {
                response_campaign_detail_page repo = response.body();

                if (repo.getResult().equals("ok")) {

                    //서버로부터 result , campaign 정보 , donate_list , share_list를 받았다 각각 파싱하여 입력해야한다.

                    try {
                        getCampaign(repo.getCampaign());
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    getShare_list(repo.getShare_list());
                    getDonate_list(repo.getDonate_list());

                }else if(repo.getResult().equals("no")){
                    Toast.makeText(getApplicationContext(),"리스트가존재하지않습니다.",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<response_campaign_detail_page> call, Throwable t) {

            }
        });
    }

    void getCampaign( String s ) throws ParseException {

        if(s.length()<=2){

        }else{
            Gson gson = new Gson();
            campaign[] array = gson.fromJson(s, campaign[].class);
            List<campaign> resultList =Arrays.asList(array);

            campaign_seq=resultList.get(0).getSeq();
            writer.setText(resultList.get(0).getWriter());
            subject.setText(resultList.get(0).getSubject());
            Glide.with(imageview.getContext())
                    .load(info.upload_ip+resultList.get(0).getImagePath())
                    .into(imageview);
            startDate.setText(resultList.get(0).getStartDate());
            endDate.setText(resultList.get(0).getEndDate());
            content.setText(resultList.get(0).getContent());
            collection.setText(resultList.get(0).getCollection());


            //기부 단체일때 가능한지 불가능한지
            //1.
            if(resultList.get(0).getDoing().equals("false")){
                endBtn.setVisibility(View.GONE);
                donateBtn.setVisibility(View.GONE);;
            }
            if(!id.equals(resultList.get(0).getWriter())){
                //작성자와 로그인한 사람의 아이디가 같지않을 경우
                endBtn.setVisibility(View.GONE);
            }else{

            }




            //기부자 회원일때 기부 가능한지 불가능한지
            if(resultList.get(0).getDoing().equals("false")){
                donateBtn.setEnabled(false);
                donateBtn.setText("이미 종료");
            }else{
                //현재 시간 밀리세컨즈구하기
                long currnt_millis = System.currentTimeMillis();

                //종료 시간 밀리세컨즈 구하기
                String tmp_startDate=resultList.get(0).getStartDate();
                String tmp_endDate=resultList.get(0).getEndDate();
                SimpleDateFormat fm = new SimpleDateFormat("yyyy년MM월dd일");


                Date to1 = fm.parse(tmp_startDate);
                long start_Date_millis = to1.getTime();

                Date to = fm.parse(tmp_endDate);
                long end_Date_millis = to.getTime();

                long one_Day=1599350400000l;

                if(currnt_millis<=end_Date_millis+one_Day){

                }else{
                    //기부 할수있는 시간을 이미 지남
                    donateBtn.setEnabled(false);
                    donateBtn.setText("이미 종료");
                }
                Log.d("test111","currnt_millis : " + currnt_millis + "  ,     start_Date_millis :  "+ start_Date_millis +" ,  end_Date_millis : " + end_Date_millis);
            }
        }

    }

    void getShare_list( String s ){
        beneficiary_array.clear();
        Gson gson = new Gson();

        if(s.length()<=2){

        }else{
            Image_and_id_object[] array = gson.fromJson(s, Image_and_id_object[].class);
            List<Image_and_id_object> tmp_shareList =Arrays.asList(array);
            for(int i=0;i<tmp_shareList.size();i++){
                Image_and_id_object donation = new Image_and_id_object(tmp_shareList.get(i).getMemberid(),tmp_shareList.get(i).getImagepath());
                beneficiary_array.add(donation);
            }
        }


        share_adapter = new campaign_detail_pageAdapter(beneficiary_array,getApplicationContext());
        share_recyclerview.setAdapter(share_adapter);
    }

    void getDonate_list(  String s ){
        donation_arry.clear();
        Gson gson = new Gson();
        if(s.length()<=2){

        }else{
            Image_and_id_object[] gsom_list = gson.fromJson(s, Image_and_id_object[].class);
            List<Image_and_id_object> tmp_donationList = Arrays.asList(gsom_list);
            for(int i=0;i<tmp_donationList.size();i++){
                Image_and_id_object donation = new Image_and_id_object(tmp_donationList.get(i).getMemberid(),tmp_donationList.get(i).getImagepath());
                donation_arry.add(donation);
            }
        }

        donation_adapter = new campaign_detail_pageAdapter(donation_arry,getApplicationContext());
        donation_recyclerview.setAdapter(donation_adapter);
    }

    void touch_object(){


        donateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //기부자로 로그인할 경우만 가능
                //서버에서 캠페인 정보를 받아올때 doing 상태를 비교하여 버튼을 비활성화 시키거나 활성화 시킨다.
                // doing 상태는 날자가 지났거나 , 작성자가 종료하기를 눌렀을떄 false가 된다.
                //  활성화 상태일 경우 다이어로그 창을 띄워 기부할 금액을 입력할수있게한다.

                donate();
            }
        });


        endBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //작성자와 로그인한 사람의 아이디가 같을 경우에만 동작
                alterDialog();
            }
        });
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void donate(){

        //기부자로 로그인할경우 클릭할 수있음
        //다이얼로그 표시

        donateDialog customDialog = new donateDialog(campaign_detail_pageActivity.this);
        customDialog.callFunction(id,campaign_seq,"money");

    }

    void alterDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("기부 종료").setMessage("정말 종료하시겠습니까?\n(종료시 모금액은 자동 나눔됩니다.)");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Log.d("test111","예누름");
                mProgressBar.setVisibility(View.VISIBLE);
                endBtn.setEnabled(false);
                backBtn.setEnabled(false);
                end_campaign();

            }
        });

        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Toast.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
     }

     void end_campaign(){


         Log.d("test111","end_campaign");

        Map update = new HashMap();
        update.put("request","end_signal_campaign");
        update.put("seq",campaign_seq);

        UploadService Retrofit=MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<responseResult>call = Retrofit.end_signal_campaign(update);
        call.enqueue(new Callback<responseResult>() {
            @Override
            public void onResponse(Call<responseResult> call, Response<responseResult> response) {
                mProgressBar.setVisibility(View.GONE);
                Log.d("test111","onResponse");
                backBtn.setEnabled(true);
                responseResult repo = response.body();
                String[] result =repo.getResult().split(":");
                if(result[0].trim().equals("ok")){
                    Toast.makeText(getApplicationContext(),"처리 완료 ",Toast.LENGTH_LONG).show();

                }else{
                    Toast.makeText(getApplicationContext(),"처리 실패 에러남",Toast.LENGTH_LONG).show();
                    endBtn.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<responseResult> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                endBtn.setEnabled(true);
                backBtn.setEnabled(true);
            }
        });


     }








}

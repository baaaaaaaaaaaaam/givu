package com.login.donation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.login.donation.adapter.mypage_donate_challenge_Adapter;
import com.login.donation.Object.response_mypage_donate;
import com.login.donation.Object.response_mypage_donate_result;
import com.login.donation.Object.response_mypage_donate_volunteer;
import com.login.donation.Object.time_campaign;
import com.login.donation.Object.volunteer_recruitment;
import com.login.donation.adapter.mypage_donate_Adapter;
import com.login.donation.adapter.mypage_donate_time_Adapter;
import com.login.donation.adapter.mypage_donate_volunteer_Adapter;
import com.login.donation.bootpay.bootpayActivity;
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




/*
이 페이지는 일반 회원 ( 기부자 ) 이 로그인 한 경우 자신의 마이페이를 볼 수 있다.
이 페이지에서는 자신의 잔액, 총 기부금 ,총 기부 횟수 ,내개 기부한 글 리스트 등을 볼 수 있으며 , 자신의 프로필을 볼 수 도있다.
또한 충전하기 버튼을 통해 부트페이와 연동하여 실제 결제를 하여 돈을 충전하는것처럼 만들었다.

이페이지에서는 쉐어드에 저장된 자신의 프로필과 아이디를 불러오고
서버로부터는 쉐어드에저장되지않는 정보를 불러온다




         initiate(); 사용할 객체 생성
        getSharedPrefence(); 기존에 쉐어드에 저장된 아이디와 자신의 프로필 이미지 불러온다
        getDonate_list_from_server();  서버에서는  내가 기부한 내역,기부한 총 금액 ( donate_list 테이블 ) , 내가 기부한 캠페인 정보 ( campaign 테이블 ) , 캠페인 작성자 이미지 ( member 테이블 )
내 총 자산 (member 테이블 ) , 기부한 횟수 를 가지고 온다.
        touch_object();
            donateBtn () : 버튼을 누를경우 부트페이 충전하기 버튼이있다 . 부트페이의경우 거래가 성공하면 done 이 콜백되고 , close는 창을 닫으면 무조건 콜백된다
                            충전을 한 금액을 done에서 서버로 전송하고 boolean을 만들어 체크하고 upload 메소드를 실행 시킨다
                            이 boolean이 true일경우 거래가 성공적이고 false 일경우 거래가 done에 도달하지 못햇다.
                            이후 close에서 false 일 경우 finish를 하여 결제 창을 닫는다
                            만약 close 에서 true 일 경우 충전 금액이 서버로 업로드 중이니 finish 하지 않는다
                            이후 upload가 끝난 후 인텐트의 플래그를 실행시켜 mypage_Donate를 onCreate부터 갱신하게 한다.



json 파일 :
서버로부터 응답 : response_mypage_donate_result
파싱 : response_mypage_donate

리사이클러뷰

main : mypage_DonationActivity
adapter : mypage_donate_Adapter
Object : response_mypage_donate
activity : activity_donate_mypage
item : item_mypage_donate_list

 */




public class mypage_DonationActivity extends AppCompatActivity implements mypage_donate_Adapter.MyrecyclerViewClickListener,
        mypage_donate_volunteer_Adapter.MyrecyclerViewClickListener,mypage_donate_challenge_Adapter.MyrecyclerViewClickListener,mypage_donate_time_Adapter.MyrecyclerViewClickListener{

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ImageView homeBtn,profile,tmp_volunteer,tmp_volunteer_recruitment;
    TextView id_Textview,collection,account,time,volunteer_time,donate_time;
    RecyclerView donation_recyclerview,donation_volunteer_recyclerview;
    TextView donateBtn,logoutBtn;
    String id;
    int total_money=0;

    mypage_donate_Adapter adapter;
    mypage_donate_volunteer_Adapter volunteer_adapter;

    ArrayList<response_mypage_donate> response_mypage_donate_list;
    ArrayList<response_mypage_donate_volunteer> response_mypage_donate_volunteer_list;


    RecyclerView challenge_list_recyclerview;
    mypage_donate_challenge_Adapter challenge_list_adapter;
    ArrayList<time_campaign>challenge_list_array;


    RecyclerView donate_time_recyclerview;
    mypage_donate_time_Adapter donate_timet_adapter;
    ArrayList<time_campaign>donate_time_array;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_donate_mypage);

        Log.d("test111","oncreate();");
        initiate();
        getSharedPrefence();

        touch_object();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("test111","onStart() , getDonate_list_from_server()");
        getDonate_list_from_server();
    }


    void initiate() {
        logoutBtn = findViewById(R.id.logout);
        homeBtn = findViewById(R.id.tmp_home);
        donateBtn = findViewById(R.id.donation);
        profile = findViewById(R.id.profile);
        id_Textview = findViewById(R.id.id);
        collection = findViewById(R.id.collection);
        donate_time=findViewById(R.id.donate_time);
        account = findViewById(R.id.account);
        time = findViewById(R.id.time);
        volunteer_time = findViewById(R.id.volunteer_time);
        tmp_volunteer=findViewById(R.id.tmp_volunteer);
        tmp_volunteer_recruitment=findViewById(R.id.tmp_weneedyou);
        pref = getSharedPreferences("auto_login", MODE_PRIVATE);
        editor = pref.edit();
        response_mypage_donate_list = new ArrayList<>();
        response_mypage_donate_volunteer_list = new ArrayList<>();
        donation_recyclerview = findViewById(R.id.recyclerview123);
        donation_volunteer_recyclerview = findViewById(R.id.recyclerview_volunteer);
        donation_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        donation_volunteer_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        challenge_list_recyclerview = findViewById(R.id.recyclerview_challenge);
        challenge_list_recyclerview.setLayoutManager(new LinearLayoutManager(this));



        donate_time_recyclerview = findViewById(R.id.recyclerview_donate_time);
        donate_time_recyclerview.setLayoutManager(new LinearLayoutManager(this));




    }
    void getSharedPrefence(){
        id=pref.getString("id","");

        id_Textview.setText(id);
        Glide.with(profile.getContext())
                .load(info.upload_ip+pref.getString("imagePath",""))
                .into(profile);
    }



    void getDonate_list_from_server(){

        response_mypage_donate_list.clear();
        // 요청할 내용 : 계정의 account ,기부 횟수 count , 내가 기부한 글 작성자 , 이미지 , 제목 ,기간 , 내 잔고
        // 전달할 key : id 만있으면 전부 조회 가능
        Map getDonation_list_qeury = new HashMap();
        getDonation_list_qeury.put("request","mypage_Donation");
        getDonation_list_qeury.put("id",id);

        UploadService queryRetrofit=MyRetrofit2.getRetrofit2().create(UploadService.class);

        Call<response_mypage_donate_result> call=queryRetrofit.mypage_lookup_donate_list(getDonation_list_qeury);

        call.enqueue(new Callback<response_mypage_donate_result>() {
            @Override
            public void onResponse(Call<response_mypage_donate_result> call, Response<response_mypage_donate_result> response) {
                response_mypage_donate_result repo = response.body();

                total_money=Integer.parseInt(repo.getSum());


                //member 테이블에서 가져온 정보
                account.setText(repo.getAccount()+" 원");
                time.setText(repo.getTime()+" 분");

                if(repo.getResult().equals("ok")){

                    get_donate_list(repo.getResponse_mypage_donate());
                    get_volunteer_list(repo.getResponse_mypage_donate_volunteer());
                    get_challenge(repo.getResponse_mypage_challenge());
                    get_donate_time(repo.getResponse_mypage_donate_time());
                }else{
                    Toast.makeText(getApplicationContext(),"서버에서 응답이 잘못됨",Toast.LENGTH_LONG).show();
                }
            }
            @Override
            public void onFailure(Call<response_mypage_donate_result> call, Throwable t) {

            }
        });



    }

    void touch_object(){
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent(getApplicationContext(),logoutActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });

        donateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), bootpayActivity.class));
            }
        });
        homeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move_intent(MainActivity.class);
            }
        });
        tmp_volunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move_intent(time_campaignActivity.class);
            }
        });
        tmp_volunteer_recruitment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move_intent(volunteer_recruitmentActivity.class);
            }
        });
    }

    void move_intent(Class s){
        Intent intent=new Intent(getApplicationContext(),s);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onItemClicked(int position,String adapter_name) {
        pref = getSharedPreferences("campaign_detail_page_info", MODE_PRIVATE);
        editor = pref.edit();
        if(adapter_name.equals("donate")){
            editor.putString("seq",response_mypage_donate_list.get(position).getCampaign_seq());
            editor.putString("id",response_mypage_donate_list.get(position).getWriter());
            editor.commit();
            startActivity(new Intent(getApplicationContext(),campaign_detail_pageActivity.class));
        }else if(adapter_name.equals("volunteer")){
            editor.putString("seq",response_mypage_donate_volunteer_list.get(position).getVolunteer().getSeq());
            editor.putString("id",response_mypage_donate_volunteer_list.get(position).getVolunteer().getId());
            editor.commit();
            startActivity(new Intent(getApplicationContext(),volunteer_recruitment_detail_pageActivity.class));
        }else if(adapter_name.equals("challege")){
            editor.putString("seq",challenge_list_array.get(position).getSeq());
            editor.putString("id",challenge_list_array.get(position).getId());
            editor.commit();
            startActivity(new Intent(getApplicationContext(),time_campaign_detail_pageActivity.class));
        }else if(adapter_name.equals("donate_time")){
            editor.putString("seq",donate_time_array.get(position).getSeq());
            editor.putString("id",donate_time_array.get(position).getId());
            editor.commit();
            startActivity(new Intent(getApplicationContext(),time_campaign_detail_pageActivity.class));
        }

    }
    void get_donate_list(String s){
        Gson gson = new Gson();
        response_mypage_donate[] gsom_tmp= gson.fromJson(s, response_mypage_donate[].class);
        List<response_mypage_donate> tmp_List = Arrays.asList(gsom_tmp);

        for (int i=0;i<tmp_List.size();i++){
            response_mypage_donate r =new response_mypage_donate(tmp_List.get(i).getCampaign_seq(),tmp_List.get(i).getDonate_money(),tmp_List.get(i).getWriter(),
                    tmp_List.get(i).getSubject(),tmp_List.get(i).getStartDate(),tmp_List.get(i).getEndDate(),tmp_List.get(i).getImagePath());
            response_mypage_donate_list.add(r);

        }
        adapter=new mypage_donate_Adapter(response_mypage_donate_list);
        adapter.setOnclickListener(mypage_DonationActivity.this);
        donation_recyclerview.setAdapter(adapter);
    }


    void get_volunteer_list(String s){
        int total_volunteer_time=0;
        response_mypage_donate_volunteer_list.clear();


        //봉사활동 게시글과 해당 게시글에 참여한 시간으로 파싱
        Gson gson = new Gson();
        response_mypage_donate_volunteer[] gsom_tmp= gson.fromJson(s, response_mypage_donate_volunteer[].class);
        List<response_mypage_donate_volunteer> tmp_List = Arrays.asList(gsom_tmp);
//
        for (int i=0;i<tmp_List.size();i++){

            //봉사활동 게시글 파싱
            Gson gson1 = new Gson();
            volunteer_recruitment gsom_tmp1= gson1.fromJson(tmp_List.get(i).getCampaign(), volunteer_recruitment.class);
            response_mypage_donate_volunteer item=new response_mypage_donate_volunteer(tmp_List.get(i).getTime(),gsom_tmp1);
            response_mypage_donate_volunteer_list.add(item);
            total_volunteer_time+=Integer.parseInt(tmp_List.get(i).getTime());
        }
        volunteer_adapter=new mypage_donate_volunteer_Adapter(response_mypage_donate_volunteer_list);
        volunteer_adapter.setOnclickListener(mypage_DonationActivity.this);
        donation_volunteer_recyclerview.setAdapter(volunteer_adapter);

        volunteer_time.setText(total_volunteer_time+" 분");
    }

    //시간과 켐페인 정보
    void get_challenge(String s){


        if(s.length()==2){

        }else{
            Gson gson = new Gson();
            time_campaign[] gsom_tmp= gson.fromJson(s, time_campaign[].class);
            challenge_list_array = new ArrayList<>(Arrays.asList(gsom_tmp));
            for(int i=0;i<challenge_list_array.size();i++){
                if(challenge_list_array.get(i).getMission().equals("success")){
                    total_money+=Integer.parseInt(challenge_list_array.get(i).getMoney());
                }
            }
            challenge_list_adapter=new mypage_donate_challenge_Adapter(challenge_list_array);
            challenge_list_adapter.setOnclickListener(mypage_DonationActivity.this);
            challenge_list_recyclerview.setAdapter(challenge_list_adapter);
        }
        collection.setText(total_money+" 원");
    }

  // 봉사시간기부 내용
    void get_donate_time(String s){
        int time=0;


        if(s.length()==2){

        }else{
            Gson gson = new Gson();
            time_campaign[] gsom_tmp= gson.fromJson(s, time_campaign[].class);
            donate_time_array = new ArrayList<>(Arrays.asList(gsom_tmp));

            for (int i=0;i<donate_time_array.size();i++){
                time+=Integer.parseInt(donate_time_array.get(i).getTime());
            }

            donate_timet_adapter=new mypage_donate_time_Adapter(donate_time_array);
            donate_timet_adapter.setOnclickListener(mypage_DonationActivity.this);
            donate_time_recyclerview.setAdapter(donate_timet_adapter);

            donate_time.setText(time+" 분");

        }
    }
}

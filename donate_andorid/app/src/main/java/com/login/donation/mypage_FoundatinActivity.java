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
import com.login.donation.Object.campaign;
import com.login.donation.Object.response_mypage_foundation_join_member;
import com.login.donation.Object.response_mypage_foundation_result;
import com.login.donation.Object.time_campaign;
import com.login.donation.Object.volunteer_recruitment;
import com.login.donation.adapter.mypage_donate_challenge_Adapter;
import com.login.donation.adapter.mypage_donate_time_Adapter;
import com.login.donation.adapter.mypage_foundation_campaign_Adapter;
import com.login.donation.adapter.mypage_foundation_join_member_Adapter;
import com.login.donation.adapter.mypage_foundation_volunteer_recruitmentAdapter;
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

기부단체로 로그인하면 자신의 아이디로 서버 campagin 테이블에 등록된 캠페인 정보를 모두 받아온다.
추가로 donation_list table에 자신이 올린 캠페인 seq별 로 등록된 기부금을 모아서 가져온다

ex) 내가올린 캠페인 seq =1 , donation_list에 campaign_seq = 1, donation = "user1" , money = '10000',
                                                campaign_seq = 1, donation = "user1" , money = '20000'

    ==> campaign 1의 총 기부금 30000 원

또한 groupmember table에서 groupid가 내id 일때의 memberid와 그 memberid에 해당하는 이미지를 가져와 보여준다.

     initiate(); 각 객체를 활성화한다.
     getSharedPrefence(); : 쉐어드에 저장된 내아이디와 이미지를 불러와 프로필에 보여준다
     touch_object();
         logoutBtn() : 로그아웃 페이지로 이동한다
         homeBtn() :   메인 페이지로 이동한다.

     onstart() :
        getDonate_list_from_server():  request와 id를 레트로핏 mypage_lookup_foundation 으로 전달한다.
            request value는 mypage_foundation이다
            응답은 response_mypage_foundation_result으로 받는데 값으로는 result , campaign ,foundation_member로 받는다.

            setCampaign_list(repo) :  캠페인 정보를 파싱하여 doing 이 true인경우와 false 인 경우를 분리하여 저장하고 , 모든 캠페인의 collection을 모아 총 모금 금액에 적용한다
            setJoin_member(repo);  기부단체에포함된 멤버와 멤버의 이미지를 파싱하여 등록된 나눔 대상자에 보여준다


리사이클러뷰

진행중 캠페인
main : mypage_FoundatinActivity
adapter  mypage_foundation_campaign_Adapter
object   campaign
activity  activity_foundation_mypage
item  item_mypage_campaign_info


종료 캠페인
main : mypage_FoundatinActivity
adapter  mypage_foundation_campaign_Adapter
object   campaign
activity  activity_foundation_mypage
item  item_mypage_campaign_info


등록된 나눔 대상자
main : mypage_FoundatinActivity
adapter  mypage_foundation_join_member_Adapter
object   response_mypage_foundation_join_member
activity  activity_foundation_mypage
item  item_foundation_join_member

 */


public class mypage_FoundatinActivity extends  AppCompatActivity implements mypage_foundation_campaign_Adapter.MyrecyclerViewClickListener,
        mypage_foundation_volunteer_recruitmentAdapter.MyrecyclerViewClickListener,mypage_donate_challenge_Adapter.MyrecyclerViewClickListener{

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ImageView writeBtn,homeBtn,profile,tmp_write,tmp_volunteer,tmp_volunteer_recruitment;
    TextView logoutBtn,id_Textview,collection,doing,end,volunteer_time;
    RecyclerView doing_recyclerview,beneficiary_list_recyclerview,volunteer_recruitment_recyclerview;
    String id,myImagePath;


    ArrayList<campaign> campaign_doing_array;

    ArrayList<volunteer_recruitment> volunteer_recruitment_array;
    //등록된 나눔 대상자
    mypage_foundation_join_member_Adapter member_adapter;
    // 진행 한 모금 - 종료 캠페인
    mypage_foundation_campaign_Adapter campaign_doing_adapter;

    //진행 한 시간 모금 캠페인
    RecyclerView recyclerview_donate_time;
    ArrayList<time_campaign> time_campaign_array;
    mypage_donate_challenge_Adapter time_campaign_adapter;

    //진행 한 봉사 활동
    mypage_foundation_volunteer_recruitmentAdapter volunteer_recruitment_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foundation_mypage);

        initiate();
        getSharedPrefence();
        touch_object();

    }
    @Override
    protected void onStart() {
        super.onStart();

        getDonate_list_from_server();
    }


    void initiate(){
        logoutBtn=findViewById(R.id.logout);
        writeBtn=findViewById(R.id.tmp_write);
        homeBtn=findViewById(R.id.tmp_home);
        profile=findViewById(R.id.profile);
        id_Textview=findViewById(R.id.id);
        collection=findViewById(R.id.collection);
        doing=findViewById(R.id.doing);
        end=findViewById(R.id.end);
        volunteer_time=findViewById(R.id.volunteer_recruitment);

        tmp_write=findViewById(R.id.tmp_write);
        tmp_volunteer=findViewById(R.id.tmp_volunteer);
        tmp_volunteer_recruitment=findViewById(R.id.tmp_weneedyou);
        pref = getSharedPreferences("auto_login", MODE_PRIVATE);
        editor=pref.edit();
        doing_recyclerview=findViewById(R.id.recyclerview1);
        doing_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        beneficiary_list_recyclerview=findViewById(R.id.recyclerview3);
        beneficiary_list_recyclerview.setLayoutManager(new LinearLayoutManager(this));


        volunteer_recruitment_recyclerview=findViewById(R.id.recyclerview_volunteer_recruitment);
        volunteer_recruitment_recyclerview.setLayoutManager(new LinearLayoutManager(this));



        recyclerview_donate_time=findViewById(R.id.recyclerview_donate_time);
        recyclerview_donate_time.setLayoutManager(new LinearLayoutManager(this));

    }

    void getSharedPrefence(){
        id=pref.getString("id","");
        myImagePath=info.upload_ip+pref.getString("imagePath","");
        id_Textview.setText(id);
        Glide.with(profile.getContext())
                .load(myImagePath)
                .into(profile);
    }


    void getDonate_list_from_server(){

//        response_mypage_donate_list.clear();
//        // 요청할 내용 : 계정의 account ,기부 횟수 count , 내가 기부한 글 작성자 , 이미지 , 제목 ,기간 , 내 잔고
//        // 전달할 key : id 만있으면 전부 조회 가능
        Map select = new HashMap();
        select.put("request","mypage_foundation");
        select.put("id",id);
        UploadService Retrofit= MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<response_mypage_foundation_result> call=Retrofit.mypage_lookup_foundation(select);
//
        call.enqueue(new Callback<response_mypage_foundation_result>() {
            @Override
            public void onResponse(Call<response_mypage_foundation_result> call, Response<response_mypage_foundation_result> response) {

                response_mypage_foundation_result repo = response.body();

                if(repo.getResult().equals("ok")){

                    // 각 파싱하여 리사이클러뷰로 표시ㅣ
                    setCampaign_list(repo);
                    setJoin_member(repo);
                    setVolunteer_recruitment(repo);
                    setTime_campaign(repo.getTime_campaign());
                }else{
                    Toast.makeText(getApplicationContext(),"서버에서 응답이 잘못됨",Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<response_mypage_foundation_result> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
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
        tmp_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                writeDialog customDialog = new writeDialog(mypage_FoundatinActivity.this);
                customDialog.callFunction();
            }
        });
    }


    void move_intent(Class s){
        Intent intent=new Intent(getApplicationContext(),s);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



    //캠페인을 패싱하여 반복문을사용하여 doing 상태를 체크한다
    //체크한후 true일 경우 진행중 arrayList에 , false 인 경우 종료 캠페인 arrayList에 담는다
    void setCampaign_list( response_mypage_foundation_result repo){


        Gson gson = new Gson();

        campaign[] array= gson.fromJson(repo.getCampaign(), campaign[].class);
        campaign_doing_array=new ArrayList<>(Arrays.asList(array));

//


        int sum_collection=0;
        int _doing=0;
        int _not_doing=0;
        for(int i=0;i<campaign_doing_array.size();i++){
            sum_collection+=Integer.parseInt(campaign_doing_array.get(i).getCollection());
            if(campaign_doing_array.get(i).getDoing().equals("true")){
                _doing+=1;
            }else{
                _not_doing+=1;
            }
        }


        // 총 모금 금액
        collection.setText(sum_collection+" 원");

        // 진행중 캠페인 상태
        doing.setText(_doing+"");
        end.setText(_doing+_not_doing+"");


        // 진행중 캠페인 리사이클러뷰
        campaign_doing_adapter=new mypage_foundation_campaign_Adapter(campaign_doing_array,"doing");
        campaign_doing_adapter.setOnclickListener(mypage_FoundatinActivity.this);
        doing_recyclerview.setAdapter(campaign_doing_adapter);

    }


    void setJoin_member( response_mypage_foundation_result repo){


        Gson gson = new Gson();
        // 기부단체에 포함된 유저의 정보 불러오기
        response_mypage_foundation_join_member[] gsom_tmp2= gson.fromJson(repo.getFoundation_member(), response_mypage_foundation_join_member[].class);
        List<response_mypage_foundation_join_member> join_member_List = Arrays.asList(gsom_tmp2);

        ArrayList<response_mypage_foundation_join_member> join_member_array=new ArrayList<>();
        for ( int i =0;i<join_member_List.size();i++){
            response_mypage_foundation_join_member tmp=new response_mypage_foundation_join_member(join_member_List.get(i).getMemberid(),join_member_List.get(i).getImagepath());
            join_member_array.add(tmp);
        }

        member_adapter=new mypage_foundation_join_member_Adapter(join_member_array);
        beneficiary_list_recyclerview.setAdapter(member_adapter);
    }


    //작성한 자원봉사 게시글 파싱 하여 리사이클러뷰에 보여주기
    void setVolunteer_recruitment(response_mypage_foundation_result repo){

        Gson gson = new Gson();
        // 기부단체에 포함된 유저의 정보 불러오기
        volunteer_recruitment[] arrry= gson.fromJson(repo.getVolunteer_recruitment(), volunteer_recruitment[].class);
         volunteer_recruitment_array = new ArrayList<>(Arrays.asList(arrry));
        volunteer_recruitment_adapter=new mypage_foundation_volunteer_recruitmentAdapter(volunteer_recruitment_array);
        volunteer_recruitment_adapter.setOnclickListener(mypage_FoundatinActivity.this);
        volunteer_recruitment_recyclerview.setAdapter(volunteer_recruitment_adapter);

            volunteer_time.setText(volunteer_recruitment_array.size()+" 회");


    }



    //시간 기부 캠페인 리스트 보여주기
    void setTime_campaign(String s){

        Log.d("test111","setTIme_campaign"+s);
        if(s.length()<=2){

        }else{
            Gson gson = new Gson();
            time_campaign[] array= gson.fromJson(s, time_campaign[].class);
            time_campaign_array=new ArrayList<>(Arrays.asList(array));

            // 진행중 캠페인 리사이클러뷰
            time_campaign_adapter=new mypage_donate_challenge_Adapter(time_campaign_array);
            time_campaign_adapter.setOnclickListener(mypage_FoundatinActivity.this);
            recyclerview_donate_time.setAdapter(time_campaign_adapter);

        }

    }


    //하나의 어댑터를 두가지객체로 만들어 사용하기 때문에 arraylist도 2개이다
    // 어떤 어댑터에서 사용한 arrayList인지를 알아야 하기때문에 string 값을받는다.
    @Override
    public void onItemClicked(int position,String Adapter) {
        Log.d("test111","onItemClicked");
        pref = getSharedPreferences("campaign_detail_page_info", MODE_PRIVATE);
        editor = pref.edit();
        if(Adapter.equals("doing")){
            editor.putString("seq",campaign_doing_array.get(position).getSeq());
            editor.putString("id",campaign_doing_array.get(position).getWriter());
            editor.commit();
            startActivity(new Intent(getApplicationContext(),campaign_detail_pageActivity.class));
        }
        else if(Adapter.equals("volunteer")){
            Log.d("test111","postion :"+volunteer_recruitment_array.get(position).getSeq() );

            editor.putString("seq",volunteer_recruitment_array.get(position).getSeq());
            editor.putString("id",volunteer_recruitment_array.get(position).getId());
            editor.apply();
            startActivity(new Intent(getApplicationContext(),volunteer_recruitment_detail_pageActivity.class));
        }  else if(Adapter.equals("donate_time")){
            Log.d("test111","postion :"+time_campaign_array.get(position).getSeq() );

            editor.putString("seq",time_campaign_array.get(position).getSeq());
            editor.putString("id",time_campaign_array.get(position).getId());
            editor.apply();
            startActivity(new Intent(getApplicationContext(),time_campaign_detail_pageActivity.class));
        }



    }
}


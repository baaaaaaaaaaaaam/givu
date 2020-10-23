package com.login.donation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.login.donation.Object.campaign;
import com.login.donation.Object.responseResult;
import com.login.donation.Object.response_mypage_beneficiary_notify;
import com.login.donation.Object.response_mypage_beneficiary_result;
import com.login.donation.Object.time_campaign;
import com.login.donation.adapter.mypage_beneficiary_campaign_Adatpter;
import com.login.donation.adapter.mypage_beneficiary_notify_Adapter;
import com.login.donation.adapter.mypage_donate_challenge_Adapter;
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
 수혜자가 마이페이지에 들어갈때 실행되는 곳이다.
수혜자는 마이페이지에서 내가 캠페인에 등록되 기부받은 내역과 내가 포함된 진행하고있는 캠페인 ,내가 포함된 종료된 캠페인 정보를 볼수있다.
추가로 총 기부받은 금액과 내가 등록된 기관정보도볼수있다.

처음 회원가입 후 로그인시 등록기관이 설정되어있지 않는 경우 + 버튼을 눌러 mypage_Beneficiary_signupAcitivity로 이동하여 기부단체를 등록할 수 도있다


         initiate();   각 뷰와 쉐어드를 불러온다
        getSharedPrefence(); 쉐어드에서 꺼내온 로그인한 아이디와 로그인시 서버로부터 받은 프로필이미지를 설정한다
        touch_object();
            logoutBtn() : 로그아웃 액티비티로 이동한다
            singup_foundation() : 처음 로그인시 기부기관 등록을 할 수 잇다
            homeBtn() : 홈으로 이동한다.

       onStart() : 다른 페이지로 이동할 경우가 있어 onStart에서는 서버로부터 캠페인 정보를 받아온다
            geBeneficiary_list_from_server() : 레트로핏 mypage_lookup_beneficiary 을 사용하여 Key = mypage_beneficiary , id = id를 요청하여 campaign 테이블로부터 정보를 받아온다.
                받아 온 정보는 response_mypage_beneficiary_result 타입이며 member 테이블에서 내 account와 groupmember 테이블에서 내가 가입한 그룹정보
                campaign_share_list에서 내가 등록된 캠페인 seq를 바탕으로 campaign 테이블에서 campaign정보를 받아온다.
                마지막으로 campaing_end_share_inblock 테이블에서 캠페인이 종료되어 실제 나에게 나눔이 된 금액 정보를 받아온다.

                    getCampaign_info() : 캠페인의 정보를 파싱하여 진행중 캠페인과 종료된 캠페인에 표시한다.
                     getNotify()  : campaign_end_share_inblock 테이블을 파싱하여 "나눔 받은 내역" 에 표시한다.


    리사이클러뷰


나눔 받은 내역
main  mypage_BeneficiaryActivity
adapter mypage_beneficiary_notify_Adapter
object  response_mypage_beneficiary_notify
activity    acivity_beneficiary_mypage
item    item_mypage_beneficiary_notify

진행 중
main  mypage_BeneficiaryActivity
adapter   mypage_beneficiary_campaign_Adatpter
object  campaign
activity    acivity_beneficiary_mypage
item    item_mypage_campaign_info

종료 된
main   mypage_BeneficiaryActivity
adapter  mypage_beneficiary_campaign_Adatpter
object  campaign
activity    acivity_beneficiary_mypage
item    item_mypage_campaign_info

 */


public class mypage_BeneficiaryActivity extends AppCompatActivity implements mypage_beneficiary_campaign_Adatpter.MyrecyclerViewClickListener,
        mypage_donate_challenge_Adapter.MyrecyclerViewClickListener{

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    ImageView homeBtn,tmp_weneedyou,profile,singup_foundation,removeBtn,tmp_volunteer,tmp_volunteer_recruitment;
    TextView id_Textview,collection,doing,end,account,foundation_id,logoutBtn;

    String id,myImagePath;
    Button exchange;
    ArrayList<campaign> doing_campaign_array,doing_end_campaign_array;
    ArrayList<response_mypage_beneficiary_notify> share_array;
    ArrayList<response_mypage_beneficiary_notify> mission_share_array;
    ArrayList<response_mypage_beneficiary_notify> notify_array;
    mypage_beneficiary_campaign_Adatpter doing_campaign_Adatper;
    mypage_beneficiary_notify_Adapter notify_Adapter;
    RecyclerView doing_campaign_recyclerview,notify_recyclerview;
    private ProgressBar mProgressBar;


    RecyclerView challenge_list_recyclerview;
    mypage_donate_challenge_Adapter challenge_list_adapter;
    ArrayList<time_campaign>challenge_list_array;


    int total_beneficiary_money;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beneficiary_mypage);


        initiate();
        getSharedPrefence();
        touch_object();

    }

    @Override
    protected void onStart() {
        super.onStart();
        notify_array.clear();
        geBeneficiary_list_from_server();
    }

    void initiate(){
        logoutBtn=findViewById(R.id.logout);
        id_Textview=findViewById(R.id.id);
//        collection=findViewById(R.id.collection);
        doing=findViewById(R.id.doing);
        end=findViewById(R.id.end);
        account=findViewById(R.id.account);
        exchange=findViewById(R.id.exchange);
        foundation_id=findViewById(R.id.foundation_id);
        singup_foundation=findViewById(R.id.singup_foundation);
        profile=findViewById(R.id.profile);
        homeBtn=findViewById(R.id.tmp_home);
        tmp_weneedyou=findViewById(R.id.tmp_weneedyou);
        removeBtn=findViewById(R.id.remove_foundation);
        mProgressBar = findViewById(R.id.progress);
        tmp_volunteer=findViewById(R.id.tmp_volunteer);
        tmp_volunteer_recruitment=findViewById(R.id.tmp_weneedyou);
        notify_array=new ArrayList<>();

        doing_campaign_recyclerview=findViewById(R.id.doing_campaign_recyclerview);
        doing_campaign_recyclerview.setLayoutManager(new LinearLayoutManager(this));


        notify_recyclerview=findViewById(R.id.notify_recyclerview);
        notify_recyclerview.setLayoutManager(new LinearLayoutManager(this));

        challenge_list_recyclerview = findViewById(R.id.recyclerview_challenge);
        challenge_list_recyclerview.setLayoutManager(new LinearLayoutManager(this));


        pref = getSharedPreferences("auto_login", MODE_PRIVATE);
    }

    void getSharedPrefence(){

        id=pref.getString("id","");
        myImagePath= info.upload_ip+pref.getString("imagePath","");
        id_Textview.setText(id);
        Glide.with(profile.getContext())
                .load(myImagePath)
                .into(profile);
    }

    void touch_object(){
        exchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


            }
        });
        tmp_weneedyou.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),volunteer_recruitmentActivity.class));
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent =new Intent(getApplicationContext(),logoutActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);

            }
        });
        singup_foundation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),mypage_Beneficiary_signupActivity.class));
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

        removeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_dialog();
            }
        });
    }

    void move_intent(Class s){
        Intent intent=new Intent(getApplicationContext(),s);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    void geBeneficiary_list_from_server(){
        total_beneficiary_money=0;
        Map query = new HashMap<>();
        query.put("request","mypage_beneficiary");
        query.put("id",id);


        UploadService Retrofit = MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<response_mypage_beneficiary_result> call = Retrofit.mypage_lookup_beneficiary(query);

        call.enqueue(new Callback<response_mypage_beneficiary_result>() {
            @Override
            public void onResponse(Call<response_mypage_beneficiary_result> call, Response<response_mypage_beneficiary_result> response) {
                response_mypage_beneficiary_result repo = response.body();


                //groupmember 테이블에서 조회한 내가 가입한 테이블이 없는 경우 length 가 2 이다
                if(repo.getGroupid().length()>2){
                    //내가 가입한 테이블이 있는 경우 , + 버튼을 지우고 등록된 기관에 입력한다

                    singup_foundation.setVisibility(View.GONE);
                    removeBtn.setVisibility(View.VISIBLE);
                    foundation_id.setText(repo.getGroupid());
                    account.setText(repo.getAccount()+" 원");

                    getCampaign_info(repo.getCampaign());
                    getTime_Campaign_info(repo.getTime_campaign());
                    getNotify_share_list(repo.getShare_inblock());
                    getNotify_mission_share_list(repo.getMission_share_inblock());

                    notify_Adapter=new mypage_beneficiary_notify_Adapter(notify_array);
                    notify_recyclerview.setAdapter(notify_Adapter);
                }else{
                    // 등록한 기부단체가 없는 경우 length 값이 2 이다.
                    foundation_id.setText("터치하여 기부단체를 선택하세요");
                    singup_foundation.setVisibility(View.VISIBLE);
                    removeBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<response_mypage_beneficiary_result> call, Throwable t) {

            }
        });

    }

    //캠페인 정보를 파싱하여 doing이 true 일 경우와 false경우 구분하여 저장하고 진행중 캠페인 리사이클러뷰와 종료된 캠패인 리사이클러뷰에 따로 보여준다
    void getCampaign_info(String s){
         Log.d("test111","getCampaign_info  " + s);

        Gson gson = new Gson();
        campaign[] array= gson.fromJson(s, campaign[].class);
        doing_campaign_array=new ArrayList<>(Arrays.asList(array));

        int sum_collection=0;
        int _doing=0;
        int _not_doing=0;
        for(int i=0;i<doing_campaign_array.size();i++){
            sum_collection+=Integer.parseInt(doing_campaign_array.get(i).getCollection());
            if(doing_campaign_array.get(i).getDoing().equals("true")){
                _doing+=1;
            }else{
                _not_doing+=1;
            }
        }


        // 등록된 캠페인이 몇개이고 , 현재 진행중인 캠페인이 몇개인지 표시한다
        // 진행중 캠페인 상태
        doing.setText(_doing+"");
        end.setText(_doing+_not_doing+"");

        //mypage_beneficiary_campaign_Adatpter 라는 하나의 어뎁터를 진행중리사이클러뷰와 종료된 리사이클러뷰에서 사용한다
        // 각각 다른 arraylist를 사용하기때문에 어떤 어뎁터에서 주는 event 인지 알기위해 adapt_name으로 구분을 하게 하였다
        // 이정보는 나중에 캠페인을 눌러 이동할때 사용된다.
        doing_campaign_Adatper=new mypage_beneficiary_campaign_Adatpter(doing_campaign_array,"doing");
        doing_campaign_Adatper.setOnclickListener(mypage_BeneficiaryActivity.this);
        doing_campaign_recyclerview.setAdapter(doing_campaign_Adatper);




    }

    void getTime_Campaign_info(String s){
        Log.d("test111","getTime_Campaign_info  " + s);


        if(s.length()==2){

        }else{
            Gson gson = new Gson();
            time_campaign[] gsom_tmp= gson.fromJson(s, time_campaign[].class);
            challenge_list_array = new ArrayList<>(Arrays.asList(gsom_tmp));
            challenge_list_adapter=new mypage_donate_challenge_Adapter(challenge_list_array);
            challenge_list_adapter.setOnclickListener(mypage_BeneficiaryActivity.this);
            challenge_list_recyclerview.setAdapter(challenge_list_adapter);
        }
    }

    //share_list를 블록에서 받아온다
    // 받아온 블록 데이터 seq와 받아온 campaign_seq를 비교하여 일치하는 것들에대해서 시작일 종료일 제목을 넣어준다
    // 넣은 데이터를 "나눔 받은 내역"에 표시한다
    void getNotify_share_list(String s){



        Gson gson = new Gson();
        Log.d("test111" ,  s);


        if(s.length()>2){
            //블록안에 저장된 데이터가 서버로부터받은 응답을 받은 경우
            response_mypage_beneficiary_notify[] gsom_tmp= gson.fromJson(s, response_mypage_beneficiary_notify[].class);

            share_array=new ArrayList<>(Arrays.asList(gsom_tmp));

            for (int i=0;i<share_array.size();i++){
                for(int j=0;j<doing_campaign_array.size();j++){
                    if(share_array.get(i).getSeq().equals(doing_campaign_array.get(j).getSeq())){
                        Log.d("test111" , doing_campaign_array.get(j).getSeq());
                        share_array.get(i).setSubject(doing_campaign_array.get(j).getSubject());
                        share_array.get(i).setStartDate(doing_campaign_array.get(j).getStartDate());
                        share_array.get(i).setEndDate(doing_campaign_array.get(j).getEndDate());
                    }
                }

                total_beneficiary_money+=Integer.parseInt(share_array.get(i).getMoney());
                notify_array.add(share_array.get(i));
            }


        }else{
            //블록안에 저장된 데이터가 서버로부터받은 응답이 fuck 인 경우
//            collection.setText(total_beneficiary_money+" 원");
        }


    }
    //mission_share_list를 블록에서 받아온다
    // 받아온 블록 데이터 seq와 받아온 campaign_seq를 비교하여 일치하는 것들에대해서 시작일 종료일 제목을 넣어준다
    // 넣은 데이터를 "나눔 받은 내역"에 표시한다
    void getNotify_mission_share_list(String s){

        Gson gson = new Gson();
        Log.d("test111" ,  s);


        if(s.length()>2){
            //블록안에 저장된 데이터가 서버로부터받은 응답을 받은 경우
            response_mypage_beneficiary_notify[] gsom_tmp= gson.fromJson(s, response_mypage_beneficiary_notify[].class);

            mission_share_array=new ArrayList<>(Arrays.asList(gsom_tmp));

            for (int i=0;i<mission_share_array.size();i++){
                for(int j=0;j<challenge_list_array.size();j++){
                    if(mission_share_array.get(i).getSeq().equals(challenge_list_array.get(j).getSeq())){
                        Log.d("test111" , challenge_list_array.get(j).getSeq());
                        mission_share_array.get(i).setSubject(challenge_list_array.get(j).getSubject());
                        mission_share_array.get(i).setStartDate(challenge_list_array.get(j).getStartDate());
                        mission_share_array.get(i).setEndDate(challenge_list_array.get(j).getEndDate());
                    }
                }

                total_beneficiary_money+=Integer.parseInt(mission_share_array.get(i).getMoney());
                notify_array.add(mission_share_array.get(i));
            }


        }else{
            //블록안에 저장된 데이터가 서버로부터받은 응답이 fuck 인 경우
//            collection.setText(total_beneficiary_money+" 원");
        }

    }


    @Override
    public void onItemClicked(int position,String Adapt_name) {
        pref = getSharedPreferences("campaign_detail_page_info", MODE_PRIVATE);
        editor = pref.edit();
        if(Adapt_name.equals("doing")){
            editor.putString("seq",doing_campaign_array.get(position).getSeq());
            editor.putString("id",doing_campaign_array.get(position).getWriter());
            editor.commit();
            startActivity(new Intent(getApplicationContext(),campaign_detail_pageActivity.class));
        }else if(Adapt_name.equals("challege")){
            editor.putString("seq",challenge_list_array.get(position).getSeq());

            editor.commit();
            startActivity(new Intent(getApplicationContext(),time_campaign_detail_pageActivity.class));
        }

    }




    // 이미 등록한 기부단체가 있다면 그 단체를 삭제하고 다른 단체로 등록할수있게하는 기능이다
    //onstart에서 geBeneficiary_list_from_server() 를 통해 서버로부터 정보를받아올때 groupid를 받아온게 있는지 없는지를확인한다
    // 만약 없다면 + 버튼을 활성화시키고 있다면 - 버튼을 활성화 시킨다.
    //- 버튼을 누를경우 check_dialog를 통해 예 / 아니요를 물어본다
    // 예를 누를 경우 서버에 update문을 보내 groupmember에서 내아이디로 된 memberid를 찾아 삭제한다
    // 삭제가 완료되면 ok 실패하면 no 를 보내주고 ok일경우 geBeneficiary_list_from_server()을 다시 호출하여 변겅된 정보를 업데이트한다.
    void check_dialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("기부단체 삭제").setMessage("정말 삭제하시겠습니까?\n");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Log.d("test111","예누름");
                mProgressBar.setVisibility(View.VISIBLE);
                homeBtn.setEnabled(false);

                remove_update();

            }
        });

        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Toast.makeText(getApplicationContext(), "취소하였습니다", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    void remove_update(){
        Map update = new HashMap();
        update.put("request","remove_foundation");
        update.put("id",id);

        UploadService update_remove_foundation=MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<responseResult>call = update_remove_foundation.mypage_beneficiary_remove_foundation(update);
        call.enqueue(new Callback<responseResult>() {
            @Override
            public void onResponse(Call<responseResult> call, Response<responseResult> response) {
                mProgressBar.setVisibility(View.GONE);
                homeBtn.setEnabled(true);
                responseResult repo=response.body();

                if(repo.getResult().equals("ok")){
                    geBeneficiary_list_from_server();
                }else{
                    Toast.makeText(getApplicationContext(), "처리안됨 에러남 ", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<responseResult> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                homeBtn.setEnabled(true);
            }
        });
    }
}

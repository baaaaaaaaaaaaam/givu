package com.login.donation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.login.donation.Object.Image_and_id_object;
import com.login.donation.Object.campaign;
import com.login.donation.Object.groupmemberQuery;
import com.login.donation.Object.time_campaign;
import com.login.donation.Object.volunteer_recruitment;
import com.login.donation.adapter.time_campaignAdapter;
import com.login.donation.adapter.volunteer_recruitmentAdapter;
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


//봉사활동 시간 기부 게시글들을 볼수있는 페이지이다
//기본적으로 게시글을 최신수능로 3개씩 가져도오도록 페이징 되어있다.
// 진행중/종료 상태로 표시하고
//진행중에도 기부자가 승락을하지않은 게시글과 승락한 게시글이 있다.
//목표 금액을 달성한 게시글과 달성하지않은 게시글이 존재한다.


//쉐어드에서 로그인한사람의 상태를 가져와 게시글작성 버튼을 지울 수 있다.

public class time_campaignActivity extends AppCompatActivity implements time_campaignAdapter.MyrecyclerViewClickListener {



    SharedPreferences pref;
    SharedPreferences.Editor editor;

    ImageView tmp_write,tmp_mypage,tmp_volunteer_recruitment,tmp_home;
    TextView empty_text;
    String mode;


    int paging_num=0;
    boolean check_paging=false;
    RecyclerView recyclerView;
    time_campaignAdapter adapter;


    ArrayList<time_campaign> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_campaign);

        //객체 초기화
        initiate();

       getshared();
        // 쉐어드에서 로그인 모드 가져옴

        //로그인한 유저 모드 체크
        check_mode();
        //버튼 초기화
        touch_initiate();

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d("test111","onStart()");
        //서버에서 정보 받아옴
        getServerData();
        //맨처음 받아올때만 true이고 받은 이후부터는 false 처리하여 페이징인지 초기화인지 확인한다
        //페이징으로 받아올경우 초기화하지않고 이어서 받는다
        check_paging=false;
        paging_num=0;
    }

    void initiate(){
        tmp_mypage=findViewById(R.id.tmp_mypage);
        empty_text=findViewById(R.id.text);
        tmp_volunteer_recruitment=findViewById(R.id.tmp_weneedyou);
        tmp_home=findViewById(R.id.tmp_home);
        tmp_write=findViewById(R.id.tmp_write);
        recyclerView=findViewById(R.id.recyclerview123);
        list=new ArrayList<>();
        recyclerView=findViewById(R.id.recyclerview123);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.

        adapter = new time_campaignAdapter(list,getApplicationContext());
        adapter.setOnclickListener(time_campaignActivity.this);
        recyclerView.setAdapter(adapter);
    }

    void getshared(){
        pref = getSharedPreferences("auto_login", MODE_PRIVATE);
        mode = pref.getString("mode","0");
        pref = getSharedPreferences("campaign_detail_page_info", MODE_PRIVATE);
        editor = pref.edit();
    }

    void check_mode(){
        if(mode.equals("mode:2")){
            tmp_write.setVisibility(View.VISIBLE);
        }else{
            tmp_write.setVisibility(View.GONE);
        }
    }
    void touch_initiate(){
        tmp_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move_intent(MainActivity.class);
            }
        });

        tmp_volunteer_recruitment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move_intent(volunteer_recruitmentActivity.class);
            }
        });

        tmp_mypage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mode.equals("mode:2")){
                    move_intent(mypage_FoundatinActivity.class);
                }else if(mode.equals("mode:3")){
                    move_intent(mypage_BeneficiaryActivity.class);
                }else if(mode.equals("mode:1")){
                    move_intent(mypage_DonationActivity.class);
                }
            }
        });
        tmp_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                writeDialog customDialog = new writeDialog(time_campaignActivity.this);
                customDialog.callFunction();
            }
        });

        //리사이클러뷰 페이징 처리
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int lastPosition = ((LinearLayoutManager) recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if(lastPosition == (totalCount-1)){
                    paging_num+=3;
                    check_paging=true;
                    getServerData();

                }
            }
        });
    }
    void move_intent(Class s){
        Intent intent=new Intent(getApplicationContext(),s);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    void getServerData(){
        Map select = new HashMap();
        select.put("request","time_campaignActivity");
        select.put("input_paiging_num",paging_num);

        UploadService retrofit= MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<groupmemberQuery> call = retrofit.campaign_list(select);
        call.enqueue(new Callback<groupmemberQuery>() {
            @Override
            public void onResponse(Call<groupmemberQuery> call, Response<groupmemberQuery> response) {
                groupmemberQuery repo = response.body();

                if(repo.getResult().equals("no")){
                    Toast.makeText(getApplicationContext()," 서버로부터 받아오지 못햇습니다."+repo.getQueryResult(),Toast.LENGTH_LONG).show();
                }else if(repo.getResult().equals("paiging")){
                    Toast.makeText(getApplicationContext()," 마지막 게시글 입니다.",Toast.LENGTH_LONG).show();
                } else {

//                    리사이클러 뷰 초기화
                    if (check_paging == true) {
                        //맨처음 받아올때만 true이고 받은 이후부터는 false 처리하여 페이징인지 초기화인지 확인한다
                        //페이징으로 받아올경우 초기화하지않고 이어서 받는다
                    } else {
                        list.clear();
                    }

                    time_campaign_parsing(repo.getQueryResult());

                }
            }

            @Override
            public void onFailure(Call<groupmemberQuery> call, Throwable t) {

            }
        });
    }

    void time_campaign_parsing(String s){

        Gson gson = new Gson();
        time_campaign[] gsom_campaign = gson.fromJson(s, time_campaign[].class);
        ArrayList<time_campaign> tmp=new ArrayList<>(Arrays.asList(gsom_campaign));

        for(int i =0;i<tmp.size();i++){
            list.add(tmp.get(i));

        }
        if (list.size()==0){
            empty_text.setVisibility(View.VISIBLE);
        }else{
            empty_text.setVisibility(View.GONE);
        }
        adapter.notifyDataSetChanged();
    }


    @Override
    public void onItemClicked(int position) {

        editor.putString("seq",list.get(position).getSeq());
        editor.putString("id",list.get(position).getId());
        editor.commit();
        Intent intent =new Intent(getApplicationContext(),time_campaign_detail_pageActivity.class);
        startActivity(intent);
    }
}

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
import com.login.donation.Object.groupmemberQuery;
import com.login.donation.Object.volunteer_recruitment;
import com.login.donation.adapter.volunteer_recruitmentAdapter;
import com.login.donation.retrofit.MyRetrofit2;
import com.login.donation.retrofit.UploadService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//기부단체에서 작성한 봉사활동 모집글을 볼수있는 페이지이다.
// 이 페이지가 열리면 봉사활동 모집 테이블에있는 글을 3개씩 불러 페이징된다.
//봉사활동 게시물을 누르면 상세 페이지로 이동할 수 있다.
//특별한 기능은 없다

public class volunteer_recruitmentActivity extends AppCompatActivity  implements volunteer_recruitmentAdapter.MyrecyclerViewClickListener {

    String mode;
    ArrayList<volunteer_recruitment> list;
    TextView empty_text;
    ImageView tmp_write,tmp_home,tmp_mypage,tmp_volunteer;
    int paging_num=0;
    boolean check_paging=false;
    volunteer_recruitmentAdapter adapter;
    RecyclerView recyclerView;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_recruitment);


        initiate_object();
        check_mode();
        initiate_touch();

    }

    @Override
    protected void onStart() {
        super.onStart();
        empty_text.setVisibility(View.GONE);
        check_paging=false;
        paging_num=0;
        get_volunteer_recruitment();
    }

    void initiate_object(){
        pref = getSharedPreferences("auto_login", MODE_PRIVATE);
        mode = pref.getString("mode","0");

        pref=getSharedPreferences("campaign_detail_page_info",0);
        editor = pref.edit();
        empty_text=findViewById(R.id.text);
        tmp_home=findViewById(R.id.tmp_home);
        tmp_mypage=findViewById(R.id.tmp_mypage);
        tmp_write=findViewById(R.id.tmp_write);
        tmp_volunteer=findViewById(R.id.tmp_volunteer);
        list=new ArrayList<>();
        recyclerView=findViewById(R.id.recyclerview123);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        adapter = new volunteer_recruitmentAdapter(list,getApplicationContext());
        adapter.setOnclickListener(volunteer_recruitmentActivity.this);
        recyclerView.setAdapter(adapter);
    }

    void check_mode(){
        if(mode.equals("mode:2")){
            tmp_write.setVisibility(View.VISIBLE);
        }else{
            tmp_write.setVisibility(View.GONE);
        }
    }

    void initiate_touch(){
        tmp_home.setOnClickListener(new View.OnClickListener() {
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
        tmp_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                writeDialog customDialog = new writeDialog(volunteer_recruitmentActivity.this);
                customDialog.callFunction();
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
                    get_volunteer_recruitment();

                }
            }
        });
    }

    void move_intent(Class s){
        Intent intent=new Intent(getApplicationContext(),s);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



    void get_volunteer_recruitment(){
        Map getAll = new HashMap();
        getAll.put("request","get_volunteer_recruitment");
        getAll.put("input_paiging_num",paging_num);
        UploadService Retrofit= MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<groupmemberQuery> call=Retrofit.volunteer_recuitment(getAll);

        call.enqueue(new Callback<groupmemberQuery>() {
            @Override
            public void onResponse(Call<groupmemberQuery> call, Response<groupmemberQuery> response) {
                groupmemberQuery repo = response.body();
                if(repo.getResult().equals("no")){
                    Toast.makeText(getApplicationContext()," 서버로부터 받아오지 못햇습니다."+repo.getQueryResult(),Toast.LENGTH_LONG).show();
                }else if(repo.getResult().equals("paiging")){
                    Toast.makeText(getApplicationContext()," 마지막 게시글 입니다.",Toast.LENGTH_LONG).show();
                } else {

                    Log.d("test111","ok");
//                    리사이클러 뷰 초기화
                    if (check_paging == true) {
                        //페이징으로 받아올경우 초기화하지않고 이어서 받는다
                    } else {
                        list.clear();
                    }
                    //받아온 queryResult 파싱

                    if(repo.getQueryResult().length()==0){
                        Toast.makeText(getApplicationContext()," 받아온 데이터 없음.",Toast.LENGTH_LONG).show();
                    }else{
                        Gson gson = new Gson();
                        volunteer_recruitment[] gsom_campaign = gson.fromJson(repo.getQueryResult(), volunteer_recruitment[].class);
                        ArrayList<volunteer_recruitment> tmp=new ArrayList<>(Arrays.asList(gsom_campaign));

                        for(int i =0;i<tmp.size();i++){
                            list.add(tmp.get(i));
                        }
                    }



                    if (list.size()==0){
                        empty_text.setVisibility(View.VISIBLE);
                    }else{

                    }
                    adapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onFailure(Call<groupmemberQuery> call, Throwable t) {

            }
        });

    }

    @Override
    public void onItemClicked(int position) {

        editor.putString("seq",list.get(position).getSeq());
        editor.putString("id",list.get(position).getId());
        editor.commit();
         Intent intent =new Intent(getApplicationContext(),volunteer_recruitment_detail_pageActivity.class);
         startActivity(intent);
    }
}

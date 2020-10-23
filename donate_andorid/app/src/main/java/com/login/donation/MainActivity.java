package com.login.donation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.login.donation.Object.campaign;
import com.login.donation.Object.Image_and_id_object;
import com.login.donation.Object.groupmemberQuery;
import com.login.donation.adapter.mainAdapter;
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
이페이지는 로그인하면 맨 처음나오는 페이지이고 등록된 캠페인을 보여주는것이 주 목적이다

캠페인 정보를 받아오기위해 onStart 에서 서버에 getCampaign 요청을 Retrofit을 통해 전달한다

만약 받아온 캠페인 정보가 0개 라면 리사이클러뷰를 gone 시키고 textview를 활성화 시키고 "등록된 캠페인이 없습니다" 라고 표시한다.

캠페인을 누를경우 해당 캠페인 상세 페이지로 이동할 수 있다.

액티비티 하단에는 이동할 수 있는 기능이있는데 첫번째 로는 현재 페이지로 이동한다

두번째는 글쓰기 기능인데 접속한 유저의 모드가 2( 기부단체) 일 경우에만 활성화 된다

세번째는 마이페이지 기능인데 자신이 등록하거나 등록된 캠페인 정보를 볼수있고 , 각 유저의 모드에 따라 조금씩 화면이 달라 각각 다른 액티비티로 만들었다



메소드:
         initiate(); 쉐어드 및 이 페이지에서 사용하는 객체들을 연결한다
        check_mode();  initiate에서 불러온 쉐어드의 mode 을 체크하여 2일경우 write 버튼을 활성화 시키고 1이나 3일 경우 비활성화 시킨다
        touch_initiate();
            - write  : 캠페인을 작성하는페이지로 이동한다 . 기부단체로 로그인 할 경우에만 해당된다
            - mypage : 각 유저에 맞는 마이페이지로 이동한다
         onStart() :
               - getCampaign(); Map 타입으로 request : getCampaign 요청을 하여 campaign 테이블에서 모든 캠페인을 역순으로 받아온다
                요청에 대한 응답은 groupmemberQuery 클래스 타입으로 result 와 캠페인 정보를 받는다.
                캠페인 정보에는 캠페인의 seq,id,imagePath,startDate,endDate,collection,doing 뿐 아니라 캠페인에 참여한 유저의 id,imagePath가 json형태로 담겨져있다.
                이 캠페인 정보를 1차적으로 campaign 클래스 타입으로 Gson을 사용하여 파싱하고 campaign 클래스안에 Image_and_id_object라는 변수를 한번더 json으로 파싱하여
                캠페인 정보와 참여한 기부자 정보를 보여준다.
        onItemClicked() :  리사이클러뷰에 아이템을 클릭할 경우 쉐어드 campaign_detail_page_info에 자신의 id와 seq를 저장한 후 디테일 페이지로 이동한다.


리사이클러뷰 :

메인 리사이클러뷰

    main : MainActivity
    adapter : mainAdapter
    object : campaign
    activity : activity_main
    item : item_main

메인 리사이클뷰 안의 서브 리사이클러뷰

    main : mainAdapter
    adapter : sub_mainAdapter
    object : Image_and_id_object
    activity : item_main
    item : item_sub_main

 */

public class MainActivity extends AppCompatActivity implements mainAdapter.MyrecyclerViewClickListener {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String mode;
    ArrayList<campaign> list;
    mainAdapter adapter;
    RecyclerView recyclerView;
    ImageView tmp_write,tmp_mypage,tmp_volunteer_recruitment,tmp_volunteer;
    TextView empty_text;
    //페이징에 사용되는 변수
    //맨처음 메인화면을 들어오면 가장 최신순으로 보여줘야 하기 때문에 0 으로 시작한다
    // 서버로부터 데이터를받아오는 경우는 onstart 이거나 리사이클러뷰 맨하단을 터치하게되거나 두가지 경우이다
    // onstart일 경우 새로 받아오는것이 맞고 , 페이징이 된경우 기존 데이터에추가되는형태가 올바르다
    // onstart인지 페이징인지 boolean으로 구분하여 false일경우 처음부터받아오고 , true 일경우 기존데이터에 추가로 받아올 것이다.

    int paging_num=0;
    boolean check_paging=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initiate();
        check_mode();
        touch_initiate();


        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        adapter = new mainAdapter(list,getApplicationContext());
        adapter.setOnclickListener(MainActivity.this);
        recyclerView.setAdapter(adapter);
    }

    // 서버에서 모든 캠페인 데이터를 받아온다.
    @Override
    protected void onStart() {
        super.onStart();
        empty_text.setVisibility(View.GONE);
        check_paging=false;
        paging_num=0;
        getCampaign();
    }

    void initiate(){
        pref = getSharedPreferences("auto_login", MODE_PRIVATE);
        editor = pref.edit();
        tmp_volunteer=findViewById(R.id.tmp_volunteer);
        mode = pref.getString("mode","0");
        tmp_write=findViewById(R.id.tmp_write);
        recyclerView = findViewById(R.id.recyclerview123);
        list = new ArrayList<>();
        tmp_mypage=findViewById(R.id.tmp_mypage);
        empty_text=findViewById(R.id.text);
        tmp_volunteer_recruitment=findViewById(R.id.tmp_weneedyou);
    }

    // write 버튼을 활성화 할지 하지않을지 로그인 한사람의 모드를 확인한다.
    void check_mode(){
            if(mode.equals("mode:2")){
                tmp_write.setVisibility(View.VISIBLE);
            }else{
                tmp_write.setVisibility(View.GONE);
            }
    }



    // 기부단체로 로그인할 경우 캠페인을 작성할 수 있다.
    void touch_initiate(){
        tmp_volunteer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                move_intent(time_campaignActivity.class);
            }
        });
        tmp_write.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                writeDialog customDialog = new writeDialog(MainActivity.this);
                customDialog.callFunction();
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
                    getCampaign();

                }
            }
        });
    }


    void move_intent(Class s){
        Intent intent=new Intent(getApplicationContext(),s);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP|Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    void getCampaign(){

        Map select = new HashMap();
        select.put("request","getCampaign");
        select.put("input_paiging_num",paging_num);
        UploadService getCampaign= MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<groupmemberQuery> call = getCampaign.campaign_list(select);
        call.enqueue(new Callback<groupmemberQuery>() {
            @Override
            public void onResponse(Call<groupmemberQuery> call, Response<groupmemberQuery> response) {
                groupmemberQuery repo = response.body();

                if(repo.getResult().equals("no")){
                    Toast.makeText(getApplicationContext()," 서버로부터 받아오지 못햇습니다."+repo.getQueryResult(),Toast.LENGTH_LONG).show();
                }else if(repo.getResult().equals("paiging")){
                    Toast.makeText(getApplicationContext()," 마지막 게시글 입니다.",Toast.LENGTH_LONG).show();
                } else{
//                    리사이클러 뷰 초기화
                    if(check_paging==true){
                        //페이징으로 받아올경우 초기화하지않고 이어서 받는다
                    }else{
                        list.clear();
                    }


                    //서버에서 받아온 정보
                    // seq,id,subject,imagePath,startDate,endDate,collection,content,doing 과 같은 campaign 테이블의 데이터 와
                    //  donation_list 테이블 에서 campaign의 seq와 같은 seq의를 가진 donation(기부자ID)와 member 테이블에서 기부자ID에 해당하는 imagePath를 가져온다
                    // 기부자의 아이디와 이미지는 list라는 key에 담겨 있다.
                    //결국 서버로 부터 받은 데이터는 Key : result ,queryResult 두가지 이고 queryResult에는 캠페인 정보와 각 캠페인의 기부자 정보가 담겨 있다
                    //이렇게 파싱한 데이터를 campaign 객체에 담아 리사이클러뷰로 표시한다.


                    // queryResult 캠페인 정보 파싱
                    Gson gson = new Gson();
                    campaign[] gsom_campaign = gson.fromJson(repo.getQueryResult(), campaign[].class);
                    List<campaign> tmp_campaignList = Arrays.asList(gsom_campaign);

                    //각 캠페인 정보 객체
                    for(int i=0;i<tmp_campaignList.size();i++){
                        ArrayList<Image_and_id_object> donation_array =new ArrayList<>();
                        Log.d("test111",tmp_campaignList.get(i).getList().length()+"");

                        if(tmp_campaignList.get(i).getList().length()!=0){
                            // 각 캠페인의 기부자정보 파싱
                            Gson gson1 = new Gson();
                            Image_and_id_object[] gson_imageandidobject = gson1.fromJson(tmp_campaignList.get(i).getList(), Image_and_id_object[].class);
                            List<Image_and_id_object> tmp_donationList = Arrays.asList(gson_imageandidobject);

                            //각 기부자 정보 객체
                            for(int j=0;j<tmp_donationList.size();j++){
                                Image_and_id_object donation =new Image_and_id_object(tmp_donationList.get(j).getMemberid(), info.upload_ip+tmp_donationList.get(j).getImagepath());
                                donation_array.add(donation);
                            }
                        }


                        //객체의 담는 정보 seq , writer ,subject ,imagePath ,startDate ,endDate ,collection ,content , donation_list , doing
                        campaign campaign_member=new campaign(tmp_campaignList.get(i).getSeq(),tmp_campaignList.get(i).getWriter(),tmp_campaignList.get(i).getSubject(),
                                info.upload_ip+tmp_campaignList.get(i).getImagePath(),tmp_campaignList.get(i).getStartDate(),
                                tmp_campaignList.get(i).getEndDate(), tmp_campaignList.get(i).getCollection(),tmp_campaignList.get(i).getContent(),
                                donation_array,tmp_campaignList.get(i).getDoing());

                        list.add(campaign_member);
                    }

                    //파싱이 끝낫는데 캠페인 arraylist 갯수가 0이라면 등록된 캠페인이없다는 메시지 표시
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


    // 캠페인을 선택할 경우 디테일한 페이지로 이동한다.
    @Override
    public void onItemClicked(int position) {
        //아이템 클릭으로 인한 position 값을 adapter에서 연산하고  이곳에서 콜백 받는다.
        pref = getSharedPreferences("campaign_detail_page_info", MODE_PRIVATE);
        editor = pref.edit();
        editor.putString("seq",list.get(position).getSeq());
        editor.putString("id",list.get(position).getWriter());
        editor.commit();
        startActivity(new Intent(getApplicationContext(),campaign_detail_pageActivity.class));


    }
}
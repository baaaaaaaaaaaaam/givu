package com.login.donation;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.login.donation.Object.campaign_add_list_member;
import com.login.donation.Object.groupmemberQuery;
import com.login.donation.adapter.campaign_add_list_dialogAdapter;
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
기부 단체로 로그인 할 경우 캠페인을 작성할 수 있다
캠페인 작성시 나눔 대상을 선택하는 + 버튼을 누를경우 해당 페이지가 실행된다.
나눔을 받기위해서는 작성자 (기부단체)에 등록된 사람만 가능하다. 때문에 서버의 groupmember table 를 조회하여야 한다.
조회를 하기 위해 작성자( 기부단체) 의 id를 서버에 전달한 후 해당 id에 해당하는 groupmember를 return 받는다


이떄 return 인 queryResult 를 json 형태로 받아 Gson으로 파싱하여 campaign_add_list_member 형태로 저장한다


역활 :
0. write_campaignActivity에서 자신의 id를 전달 받는다.
1. 페이지가 실행되면 자신의 id를 서버에 전달하고 id에 해당하는 groupmember를 불러온다
2. groupmember를 리사이클러뷰를 통해 리스트로 보여주고 체크박스를 통해 선택할 수 있도록한다
3. 선택된 사람들은  write_campaignActivity에  list형태로 전달하여 수혜대상자에 추가되도록 한다.



메소드 :
object_initiate();  ==> 이 클래스에서 사용되는  객체를 생성한다
query_groupmember(); ==>  자신(기부단체)에게 속한 그룹 멤버를 조회한다
    -groupmemberQuery 클래스의 경우 서버의응답을 받기위해  result 와 queryResult 변수만 존재한다.
    - queryResult 의 경우 그룹에 속한 유저 아이디와 이미지 경로를  json 배열 형태로 가지고 있다 ( ex) {\"memberid\":\"beneficiary\",\"imagepath\":\"default.jpg\"}
    - 나눔을 받는 사람을 선택하는 리스트에는 아이디 , 이미지 , 체크 박스가 존재 하기때문에 campaign_add_list_member 형태로 변환해야한다
    - campaign_add_list_member 는 id ,imagePath, check를 가지고있다.
    - 그래서 서버로 부터 받은 queryResult json파일을 Gson으로 campaign_add_list_member의 id와 imagePath에 입력한 후 check 값을 false로 저장한다.
    이렇게 저장한 campaign_add_list_member 객체를 ArrayList에 넣고 RecyclerView Adapter와 연결해준다.



touch_initiate() ==>  ok,cancle 을 누를경우 동작에 관련된 메소드이다
    ok() ==> 리스트에서 체크박스를 선택한 대상을 write_campaignActivity로 전송하여 나눔 대상에 추가하도록 한다
    cancle()==> 다이얼로그를 종료시킨다.

CustomDialogListener : 인터페이스를 한다
    ok(



리사이클러뷰 :

 실행코드 : add_share_listDialog
 adapter : campaign_add_list_dialogAdapter
 Item_object  : campaign_add_list_member
 activity : dial_add_share_list.xml
 item :item_add_share

 */






public class add_share_listDialog extends Dialog implements campaign_add_list_dialogAdapter.MyrecyclerViewClickListener {


    String id;
    RecyclerView recyclerView;
    campaign_add_list_dialogAdapter adapter;
    private Context context;
    Dialog dlg;
    Button ok,cancle;
    ArrayList<campaign_add_list_member> list;
    Map groupmemberInfo;
    private CustomDialogListener customDialogListener;



    public add_share_listDialog(Context context,String id) {
        super(context);
        this.context = context;
        this.id=id;
    }


        @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        출처: https://bottlecok.tistory.com/38 [잡캐의 IT 꿀팁]

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        dlg = new Dialog(context);
        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_add_share_list);
        // 커스텀 다이얼로그를 노출한다.
        dlg.show();
        // 커스텀 다이얼로그의 각 위젯들을 정의한다.

        object_initiate();
        query_groupmember();
        touch_initiate();


        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        adapter = new campaign_add_list_dialogAdapter(list,getContext());

        // 리사이클러뷰에 클릭이벤트를 실행시키기 위해 반드시 필요한 부분
        adapter.setOnclickListener(add_share_listDialog.this);
        recyclerView.setAdapter(adapter);

    }
        void object_initiate(){
            ok=dlg.findViewById(R.id.ok);
            cancle=dlg.findViewById(R.id.cancle);
            recyclerView = dlg.findViewById(R.id.recyclerview123);
            list=new ArrayList<>();
        }

        void query_groupmember(){

            groupmemberInfo = new HashMap();
            groupmemberInfo.put("request","querygroupmember");
            groupmemberInfo.put("id",id);



            UploadService uploadID= MyRetrofit2.getRetrofit2().create(UploadService.class);



            //campaign_add_list_dialog_query 은  Http GET 으로 요청을 하고 전송할 때 Map type으로 전송한다
            // 서버에서 groupmember 테이블을 조회한 후  return 조회값을 리턴한다
            // 리턴 값으로는 result 와 queryResult 가 있다.
            // 이 return 값을 call 로 받아 큐에 쌓는다.
            // 큐에 저장된 내용을 불러온다 ( response.body() )
            // 불러온 내용을 userAccount repo라는 객체 담는다 . 이때 객체 변수 이름과 return 되는 json key값이 같으면 매칭된다
            // repo의 변수를 불러 사용한다.



            Call<groupmemberQuery> call = uploadID.campaign_add_list_dialog_query(groupmemberInfo);
            call.enqueue(new Callback<groupmemberQuery>() {
                @Override
                public void onResponse(Call<groupmemberQuery> call, Response<groupmemberQuery> response) {
                    groupmemberQuery repo = response.body();

                    if (repo.getResult().equals("ok")) {

                        // 서버로부터 return 받는 값
                        // {"result":"ok","queryResult":"[{\"memberid\":\"beneficiary\",\"imagepath\":\"default.jpg\"},{\"memberid\":\"beneficial1\",\"imagepath\":\"default.jpg\"}
                        //  응답받은 값중 queryResult 값이 json 형태 임으로 파싱이 필요하다
                        //campaign_add_list_member 는 memberid와 imagepath 를 가지고 있어 응답받은 값을 받을 수있다
                        // 또한 불러온 리스트에 체크박스를 추가하여 선택할수있게 할 것이기 때문에 boolean 값 check를 추가하였다.
                        // campaign_add_list_member ==> 리사이클러뷰와 연결하기 위한 클래스
                        // groupmemberQuery 서버로부터의 json 응답을 받기위한 클래스

                        Gson gson = new Gson();
                        campaign_add_list_member[] array = gson.fromJson(repo.getQueryResult(), campaign_add_list_member[].class);
                        List<campaign_add_list_member> resultList =Arrays.asList(array);

                        for (int j = 0; j < resultList.size(); j++) {


                            campaign_add_list_member member=new campaign_add_list_member(resultList.get(j).getMemberid(), info.upload_ip+resultList.get(j).getImagepath(),false);
                            list.add(member);
                            Log.d("test111",resultList.get(j).getMemberid() + "    :     "  + info.upload_ip+resultList.get(j).getImagepath());
                        }
                        adapter.notifyDataSetChanged();

                    }else if(repo.getResult().equals("no")){
                        Toast.makeText(getContext(),"리스트가존재하지않습니다.",Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onFailure(Call<groupmemberQuery> call, Throwable t) {

                }
            });
        }

         void touch_initiate(){
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                adapter.notifyDataSetChanged();

                customDialogListener.ok(list);
                dlg.dismiss();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });
    }




    // write_campaignActivity와 연결할 인터페이스
    interface CustomDialogListener{
        void ok(ArrayList<campaign_add_list_member> list);
    }

    // CustomDialogListener 선언부와 객체를 연결할 수 있는 메소드이다
    public void setDialogListener(CustomDialogListener customDialogListener){
        this.customDialogListener = customDialogListener;
    }


    @Override
    public void onItemClicked(int position) {


    }


}

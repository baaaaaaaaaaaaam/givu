package com.login.donation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.login.donation.Object.Image_and_id_object;
import com.login.donation.Object.campaign_add_list_member;
import com.login.donation.Object.campaign_write;
import com.login.donation.Object.groupmemberQuery;
import com.login.donation.Object.responseResult;
import com.login.donation.adapter.mypage_beneficiary_signup_Adapter;
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
이페이지는 mypage_BeneficiaryActivity에서 서버로 부터 받아온 groupid의 length가 2 ( 데이터가없을경우 length가 2 임)일
경우 버튼이 활성화되면서 해당 버튼을 누르면 이곳으로 이동한다

이 페이지에서는 Retrofit을 mypage_beneficiary_lookup_foundation을 사용하여 key = lookupFoundation 을 요청하고
서버는 해당 요청을 받아 member 테이블에서 mode가 "2" 인 것만 골라 가져온다

TextWatcher의 onTextChanged() 사용하여 입력시 실시간으로 내용이 변경된다

검색기능이 있으면 검색시 리사이클러뷰의 내용이 검색 내용과 일치하는것들로 추려진다

검색후 해당 아이템을 클릭하면 등록버튼이 생기게된다

검색 후 아이템을 클릭한 상태에서 내용을 변경하면 클릭한 아이템의 id와 현재 변경된 id를 체크하여 등록버튼을 지운다

결국 클릭한 아이템이름과 검색뷰에 적혀있는 아이디가 다른경우 등록을 할 수 없다.


        initiate();  각 객체 및 리사이클러뷰를 생성한다
        getShare(); 쉐어드에 저장된 id를 불러온다. 이 아이디는
        touch_initiate(); 등록하기 버튼을 활성화한다
            singup(): 등록하기 버튼을 누르면 실행되는 메소드, 레트로핏 mypage_beneficiary_singup_foundation 를 사용하여 Key = singupFoundation과 내 id , 선택한 그룹 id
                를 전송한다. 응답으로는 responseResult 를 사용하여 ok 와 no 만 받는다. ok 나 no를 받을 경우 beneficiary_signup_foundationDialog 다이얼로그를 실행시키고
                다이얼로그에서 버튼을 누를 경우 callback을 받아 해당 화면을 종료한다.

       onStart() :  load_foundation_list() 를 실행시킨다
                load_foundation_list(): 레트로핏의 mypage_beneficiary_lookup_foundation사용하여 key = lookupFoundation  요청한다. 요청에 응답값으로
                    member 테이블에서 mode가 2인 유저의 아이디와 이미지를 받아온다. 받아온 데이터 타입은 groupmemberQuery으로 queryResult에 기관이미지와 아이디가 담겨있다
                    이 데이터를 get_Image_and_id로 전달하여 파싱한다

                    get_Image_and_id() : 기부단체의 아이디와 이미지를 파싱하여 리사이클러뷰에 표시한다.

        onTextChanged() : 텍스트에 입력할때마다 이벤트를 받는다. 이 이벤트마다 리사이클러뷰에 텍스트 값을 넘겨주어 list에 담긴값중 같은것을 찾아 새로운 list를 만들고
            해당 list를 리사이클러뷰에 표시한다

        onClickedItem()  :  리사이클러뷰에 표시된 아이템을 클릭할경우 해당 아이템의 텍스트를 가져와 저장하고  Edittext에 표시한다
            저장한 값과 Edittext의 값이 같은경우 등록하기 버튼을 표시하도록 onTextChanged()에 설정해두었다. 만약 클릭한후 텍스트 내용을 조금이라두 바꾸면 등록하기버튼은
            사라진다.

 */


public class mypage_Beneficiary_signupActivity extends AppCompatActivity implements TextWatcher,mypage_beneficiary_signup_Adapter.MyrecyclerViewClickListener {

    SharedPreferences pref;
    RecyclerView recyclerView;
    EditText editText;
    Button signup;
    mypage_beneficiary_signup_Adapter adapter;

    String tmp_foundation_id;
    String id;

    ArrayList<Image_and_id_object> object_array;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beneficiary_mypage_signup_foundation);


        initiate();
        getShare();
        touch_initiate();

    }

    @Override
    protected void onStart() {
        super.onStart();
        load_foundation_list();
    }

    void initiate(){
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);
        editText = (EditText)findViewById(R.id.edittext);
        signup=findViewById(R.id.signup);
        editText.addTextChangedListener(this);
        object_array = new ArrayList<>();
        adapter = new mypage_beneficiary_signup_Adapter(getApplicationContext(), object_array);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnclickListener(mypage_Beneficiary_signupActivity.this);
        adapter.getFilter().filter(editText.toString());
        recyclerView.setAdapter(adapter);
    }

    void getShare(){
        pref=getSharedPreferences("auto_login",MODE_PRIVATE);
        id=pref.getString("id","");
    }




    void touch_initiate(){
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                singup();
            }
        });
    }


    //서버에 요청한 후 응답 값이 ok 나 no 일경우 다이얼로그를 활성화시킨다.
    void singup(){
        Map insert= new HashMap();
        insert.put("request","singupFoundation");
        insert.put("id",id);
        insert.put("foundation_id",tmp_foundation_id);

        UploadService retrofit = MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<responseResult> call = retrofit.mypage_beneficiary_singup_foundation(insert);

        call.enqueue(new Callback<responseResult>() {
            @Override
            public void onResponse(Call<responseResult> call, Response<responseResult> response) {
                Log.d("test111","onResponse");
                responseResult repo=response.body();
                if(repo.getResult().equals("ok")){
                    Log.d("test111","ok");
                    // 정상등록
                    beneficiary_signup_foundationDialog dialog = new beneficiary_signup_foundationDialog(mypage_Beneficiary_signupActivity.this,"ok");

                    dialog.setDialogListener(new beneficiary_signup_foundationDialog.CustomDialogListener() {
                        @Override
                        public void ok(String message) {

                            finish();
                        }


                    });
                    dialog.show();

                }else if(repo.getResult().equals("no")){
                    //등록 안됨 다시 처리해야함
                    Log.d("test111","no");
                    beneficiary_signup_foundationDialog dialog = new beneficiary_signup_foundationDialog(mypage_Beneficiary_signupActivity.this,"no");

                    dialog.setDialogListener(new beneficiary_signup_foundationDialog.CustomDialogListener() {
                        @Override
                        public void ok(String message) {

                            Toast.makeText(getApplicationContext(),"다시 등록해주세요",Toast.LENGTH_LONG).show();
                        }


                    });
                    dialog.show();
                }
            }

            @Override
            public void onFailure(Call<responseResult> call, Throwable t) {

            }
        });

    }

    // 서버에 기부단체 이미지와 아이디 , 조회결과를 파싱함
    void load_foundation_list(){
        Map query = new HashMap();
        query.put("request","lookupFoundation");


        UploadService Retrofit= MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<groupmemberQuery>call = Retrofit.mypage_beneficiary_lookup_foundation(query);

        call.enqueue(new Callback<groupmemberQuery>() {
            @Override
            public void onResponse(Call<groupmemberQuery> call, Response<groupmemberQuery> response) {
                groupmemberQuery repo=response.body();
                if(repo.getResult().equals("ok")){

                    get_Image_and_id(repo);

                }else{
                    Toast.makeText(getApplicationContext(),"조회가 안되거나 서버에서 에러낫거나",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<groupmemberQuery> call, Throwable t) {

            }
        });

    }

    //기부단체리스트 파싱하여 리사이클러뷰에 표시함
    void get_Image_and_id(groupmemberQuery repo){
        Gson gson = new Gson();
        Image_and_id_object[] gson_imageandidobject = gson.fromJson(repo.getQueryResult(), Image_and_id_object[].class);
        List<Image_and_id_object> Image_and_idList = Arrays.asList(gson_imageandidobject);
        object_array.clear();
        object_array.addAll(Image_and_idList);
        adapter.notifyDataSetChanged();
    }



    ////////////////////////////////////////////////////////////////////////////////
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    //Adapter의 filter 부분과 연동되어 사용
    //입력하는 텍스트가 변화할때마다 호출하는 부분으로 해당 값을 adapter에보내 매번 조회를 하여 같은 값이있는지 체크한다.
    //입력한 값과 선택한 값이 다를경우 등록하기버튼을 지운다.
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        adapter.getFilter().filter(charSequence);


        if(editText.getText().toString().equals(tmp_foundation_id)){
            signup.setVisibility(View.VISIBLE);
        }else{
            signup.setVisibility(View.GONE);
        }

    }
    @Override
    public void afterTextChanged(Editable editable) {

    }
    ////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onItemClicked(String memberid) {

        tmp_foundation_id="";
        //아이템 클릭으로 인한 position 값을 adapter에서 연산하고  이곳에서 콜백 받는다.

        tmp_foundation_id=memberid;
        editText.setText(memberid);

    }
}
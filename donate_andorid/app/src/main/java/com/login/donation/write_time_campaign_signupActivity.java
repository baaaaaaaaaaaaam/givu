package com.login.donation;


//이페이지는 기부단체가 봉사활동 시간 기부를 게시할대 기부자가 누가 될지를 선택하는 리사이클러뷰이다
// 아이디를 검색하게되면 일치하는 문자가있는 유저리스트를 보여준다
//단순히 검색만하면안되고 검색후 해당 아이디를 클릭한 후 등록하기 버튼을 누르면 해당아이디를 write_time_campaign화면으로 가져온다.


//일단 페이지가 일리면 기부자 아이디 리스트를 전부 가져온다

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.login.donation.Object.Image_and_id_object;
import com.login.donation.Object.groupmemberQuery;
import com.login.donation.adapter.mypage_beneficiary_signup_Adapter;
import com.login.donation.adapter.time_campaign_donation_signup_Adapter;
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

public class write_time_campaign_signupActivity extends AppCompatActivity implements TextWatcher , time_campaign_donation_signup_Adapter.MyrecyclerViewClickListener {
    String tmp_foundation_id;
    EditText editText;
    Button signup;
    RecyclerView donation_recyclerview;
    ArrayList<Image_and_id_object> object_array;
    time_campaign_donation_signup_Adapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wrtie_time_campaign_signup_donation);
        object_initiate();
        touch_initiate();
        getDonation_list();
    }

    void object_initiate(){
        editText=findViewById(R.id.edittext);

        donation_recyclerview=findViewById(R.id.recyclerview);
        object_array = new ArrayList<>();
        signup=findViewById(R.id.signup);
        editText.addTextChangedListener(this);
        adapter = new time_campaign_donation_signup_Adapter(getApplicationContext(), object_array);
        donation_recyclerview.setLayoutManager(new LinearLayoutManager(this));
        adapter.setOnclickListener(write_time_campaign_signupActivity.this);
        adapter.getFilter().filter(editText.toString());
        donation_recyclerview.setAdapter(adapter);
    }

    void touch_initiate(){
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.putExtra("id", editText.getText().toString());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    void getDonation_list(){
        Map select = new HashMap();
        select.put("request","write_time_campaign_signupActivity");

        UploadService retrofit = MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<groupmemberQuery> call=retrofit.write_time_campaign_signupActivity(select);
        call.enqueue(new Callback<groupmemberQuery>() {
            @Override
            public void onResponse(Call<groupmemberQuery> call, Response<groupmemberQuery> response) {
                groupmemberQuery repo=response.body();
                if(repo.getResult().equals("ok")){
                    //받아온 데이터 파싱해서 리사이클러뷰에 표시하기
                    donation_parsing(repo.getQueryResult());
                }else{
                    Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<groupmemberQuery> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"실패",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

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

    void donation_parsing(String s){
        Gson gson = new Gson();
        Image_and_id_object[] gson_imageandidobject = gson.fromJson(s, Image_and_id_object[].class);
        List<Image_and_id_object> Image_and_idList = Arrays.asList(gson_imageandidobject);
        object_array.clear();
        object_array.addAll(Image_and_idList);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClicked(String memberid) {
        tmp_foundation_id="";
        //아이템 클릭으로 인한 position 값을 adapter에서 연산하고  이곳에서 콜백 받는다.

        tmp_foundation_id=memberid;
        editText.setText(memberid);
    }
}

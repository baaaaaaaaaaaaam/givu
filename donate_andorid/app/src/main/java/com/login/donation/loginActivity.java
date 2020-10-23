package com.login.donation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.login.donation.Object.userAccount;
import com.login.donation.retrofit.MyRetrofit2;
import com.login.donation.retrofit.UploadService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/*
로그인을 할수있는 액티비티
앱을 실행하면 맨처음 표시되는 페이지로 회원가입과 로그인 기능이있다



동작 방식
1. 자동로그인 체크  쉐어드에서 auto_login이란 파일에 key: auto_login이 true 인지 체크한다  ==> true일경우 profileActivity로 넘어감
2. false 일경우 로그인 화면이 표시된다
3. createIdBtn 버튼을 누를 경우 createIdDialog 를 띄운다
4. loginBtn을 누를 경우 현재 입력한 아이디와 패스워드를 가져와서 Map 타입으로 request = login , id = 입력한 id ,pw = 입력한 패스워드 로 하여
    레트로핏을 사용하여 업로드 한다.
5.  서버에는 클라이언트 요청에 따라 ID/PW를 검사한후 있는 경우  ok:login과 해당ID의 imagePath , mode 를 전달한다 . 없는 경우 no:login 응답

6. ok:login이 응답으로 올 경우 auto_login 쉐어드에 id,mode,imagePath,auto_login = "true" 를 저장하고 profileActitivity로 이동한다


메소드
object_initiate();  ==>이 클래스에서 사용되는  객체를 생성한다
check_auto_login(); ==>  액티비티 화면에 View들이 표시되기전 쉐어드에 저장된 auto_login의 값이 true일 경우 다음 페이지로 이동한다.
touch_initiate() ==>  createIdBtn,loginBtn 버튼을 누를경우 동작에 관련된 메소드이다
    - createIdBtn : 이 버튼을 누를경우 customDialog 를 생성하여 회원가입을 받는다
    - loginBtn : upload()를 실행한다.
        -upload() : loginBtn 에서 전달받은 Map 객체와 Retrofit2를 사용하여 Http.Get 요청을 전달하고 응답 받는다.




 */








public class loginActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String TAG="login";
    EditText loginId,loginPw;
    Button loginBtn, createIdBtn;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        object_initiate();

//       쉐어드에 자동 로그인 확인
        check_auto_login();
        touch_initiate();


    }

//    객체생성
    private void object_initiate(){
        loginId=findViewById(R.id.inputId);
        loginPw=findViewById(R.id.inputPw);
        loginBtn=findViewById(R.id.loginBtn);
        createIdBtn=findViewById(R.id.createId);
        pref = getSharedPreferences("auto_login", MODE_PRIVATE);
        editor = pref.edit();

    }





    // 자동 로그인 체크
    private void check_auto_login(){

        String auto_login_check = pref.getString("auto_login", "");
        if (auto_login_check.equals("true")){
            Intent intent =new Intent(getApplicationContext(),profileActivity.class);
            startActivity(intent);
            finish();
        }

    }
//    터치 리스너
    private void touch_initiate(){

//        회원가입 다이얼로그를 만듬
        createIdBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createIdDialog customDialog = new createIdDialog(loginActivity.this);
                customDialog.callFunction();
            }
        });


        // 서버로 보낼 Map 생성
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //레트로픽 실행 부분
                upload();
            }
        });
    }


    //레트로핏을 사용하여 서버로 전송
    private void upload() {

        Map select = new HashMap();
        select.put("request","login");
        select.put("id",loginId.getText().toString());
        select.put("passwd",loginPw.getText().toString());


        //객체 생성
        UploadService uploadID= MyRetrofit2.getRetrofit2().create(UploadService.class);

        //getLogin 은  Http GET 으로 요청을 하고 전송할 때 Map type으로 전송할 수 있다.
        // 서버에서 처리한 후 return 값으로  mode , reulst ,imagePath를 준다.
        // 이 return 값을 userAccount 클래스 타입으로 받아 큐에 쌓는다.
        // 큐에 저장된 내용을 불러온다 ( response.body() )
        // 불러온 내용을 userAccount의  repo라는 객체 담는다 . userAccount의 변수는 서버에서 return해주는 json의 키와 같으면 자동으로 매치되어 저장된다.
        // repo의 변수를 불러 사용한다.



        Call<userAccount> call = uploadID.getLogin(select);
        call.enqueue(new Callback<userAccount>() {
            @Override
            public void onResponse(Call<userAccount> call, Response<userAccount> response) {
                userAccount repo = response.body();

                if (repo.result.equals("ok:login")) {
                    // 커스텀 다이얼로그를 종료한다.
                    editor.putString("auto_login", "true");
                    editor.putString("id", loginId.getText().toString());
                    editor.putString("mode",repo.mode);
                    editor.putString("imagePath",repo.imagepath);
                    editor.commit();
                    Intent intent =new Intent(getApplicationContext(),profileActivity.class);
                    startActivity(intent);
                    finish();
                } else   if (repo.result.equals("no:login")){
                    Toast.makeText(getApplicationContext(), "패스워드가 잘못됫습니다", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onFailure(Call<userAccount> call, Throwable t) {

            }
        });
//        출처: https://flymogi.tistory.com/entry/Retrofit을-사용해보자-v202 [하늘을 난 모기]
    }




}

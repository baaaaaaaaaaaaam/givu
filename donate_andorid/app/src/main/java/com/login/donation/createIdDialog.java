package com.login.donation;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.login.donation.Object.userAccount;
import com.login.donation.retrofit.MyRetrofit2;
import com.login.donation.retrofit.UploadService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
이 클래스 파일은 loginActivity에서 회원가입 버튼을 누르면 이동되는 페이지이다
이 페이지에서는 생성할 아이디 및 패스워드 , 패스워드 확인을 한다

역활
1. 생성할 아이디 및 패스워드 , 패스워드 체크 , 회원 모드 ( 일반 회원 , 기부 단체 , 수혜자 ) 를 선택할 수 있다
2. 등록버튼을 누를 경우 패스워드와 패스워크 체크가 일치하는지 검사한다.
3. 회원 모드 중 반드시 하나는 체크해야 한다
4. 등록버튼을 누를경우 ID와 password를 서버로 전송한다.
5. 서버에서 응답이 ok 일 경우 회원가입이되고 다이얼로그가 종료된다
6. 서버에서 응답이 no 일 경우 아이디가 중복된 경우라 표시하고 다시 입력을 받는다.

메소드
callFunction() == > 다이얼로그를 띄움
object_initiate() ==> 객채 생성 및 View와 연결
touch_initiate() ==> okBtn 과 cancleBtn , checkbox1,2,3을 클릭 했을때 메소드를 가지고있다.
    - cancleBtn() == > 다이얼로그를 종료한다
    - checkbox1,2,3 ==>  만약 1을 누르면 2,3 을 체크 해제 한다 ( 중복체크 못함 )
    -okBtn() ==>  pw 와 check_pw 가 같은지 검사한다. 이후 checkbox1,2,3이 모두 false ( 아무것도 선택안함 ) 일경우를 체크한다.
            ==> 만약 pw와 check_pw가 같고 checkbox1,2,3 중 하나를 선택할 경우  id, pw , mode (회원 모드)를 Map에 담아 upload() 에 넘긴다
        - upload() ==> okBtn 에서 전달받은 Map 객체와 Retrofit2를 사용하여 Http.Get 요청을 전달하고 응답 받는다.
 */



public class createIdDialog {


    private Context context;
    EditText id,pw,check_pw;
    Button okBtn,cancelBtn;
    Dialog dlg;
    CheckBox checkbox1,checkbox2,checkbox3;


    public createIdDialog(Context context) {
        this.context = context;
    }

    // 호출할 다이얼로그 함수를 정의한다.
    public void callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
         dlg = new Dialog(context);
        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_createid);
        // 커스텀 다이얼로그를 노출한다.
        dlg.show();
        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        object_initiate();

        touch_initiate();


    }
    private void object_initiate(){
        id = (EditText) dlg.findViewById(R.id.id);
        pw = (EditText) dlg.findViewById(R.id.pw);
        check_pw = (EditText) dlg.findViewById(R.id.check_pw);
        okBtn = (Button) dlg.findViewById(R.id.okButton);
        cancelBtn = (Button) dlg.findViewById(R.id.cancelButton);
        checkbox1=dlg.findViewById(R.id.checkBox1);
        checkbox2=dlg.findViewById(R.id.checkBox2);
        checkbox3=dlg.findViewById(R.id.checkBox3);
    }


    private  void touch_initiate(){

        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // '확인' 버튼 클릭시 메인 액티비티에서 설정한 main_label에
                // 커스텀 다이얼로그에서 입력한 메시지를 대입한다.
//              전송할 데이터를 Map 타입으로 전송한다.

                String _pw=pw.getText().toString();
                String _check_pw=check_pw.getText().toString();
                String mode="";
                if (checkbox1.isChecked()){
                    mode="1";
                }else if (checkbox2.isChecked()){
                    mode="2";
                }else if (checkbox3.isChecked()){
                    mode="3";
                }


                if(_pw.equals(_check_pw)){
                    if(!checkbox1.isChecked()&& !checkbox2.isChecked()&&!checkbox3.isChecked()){

                    }else{
                        Map userInfo = new HashMap();
                        userInfo.put("request","createId");
                        userInfo.put("id",id.getText().toString());
                        userInfo.put("passwd",pw.getText().toString());
                        userInfo.put("mode",mode);
                        upload(userInfo);
                    }

                }else{
                   Toast.makeText(context, "패스워드가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                }



            }
        });
        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "취소 했습니다.", Toast.LENGTH_SHORT).show();

                // 커스텀 다이얼로그를 종료한다.
                dlg.dismiss();
            }
        });
        checkbox1.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkbox2.setChecked(false);
                checkbox3.setChecked(false);
            }
        }) ;
        checkbox2.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkbox1.setChecked(false);
                checkbox3.setChecked(false);
            }
        }) ;
        checkbox3.setOnClickListener(new CheckBox.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkbox1.setChecked(false);
                checkbox2.setChecked(false);
            }
        }) ;
    }



    private void upload(Map parameters) {


        UploadService uploadID= MyRetrofit2.getRetrofit2().create(UploadService.class);



        //getLogin 은  Http GET 으로 요청을 하고 전송할 때 Map type으로 전송할 수 있다.
        // 전달 하는 값의 키는 id,pw,mode 이다
        // 서버에서 처리한 후 return 값으로  result 를 준다.
        // 이 return 값을 call 로 받아 큐에 쌓는다.
        // 큐에 저장된 내용을 불러온다 ( response.body() )
        // 불러온 내용을 userAccount repo라는 객체 담는다 . 이때 객체 변수 이름과 return 되는 json key값이 같으면 매칭된다
        // repo의 변수를 불러 사용한다.

        Call<userAccount> call = uploadID.getLogin(parameters);
        call.enqueue(new Callback<userAccount>() {
            @Override
            public void onResponse(Call<userAccount> call, Response<userAccount> response) {
                userAccount repo = response.body();

                if (repo.result.equals("ok")) {
                    Toast.makeText(context, "아이디생성 성공.", Toast.LENGTH_SHORT).show();

                    dlg.dismiss();
                } else  if (repo.result.equals("no")) {
                    Toast.makeText(context, " 아이디 생성 실패", Toast.LENGTH_SHORT).show();

                } else  if (repo.result.equals("no1")) {

                    Toast.makeText(context, " 생성된 아이디 이미 있음", Toast.LENGTH_SHORT).show();
                }

            }
            @Override
            public void onFailure(Call<userAccount> call, Throwable t) {

            }
        });
//        출처: https://flymogi.tistory.com/entry/Retrofit을-사용해보자-v202 [하늘을 난 모기]
    }
}

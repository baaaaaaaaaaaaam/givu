package com.login.donation;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.login.donation.Object.responseResult;
import com.login.donation.R;
import com.login.donation.retrofit.MyRetrofit2;
import com.login.donation.retrofit.UploadService;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
이 페이지는 campaign_detail_pageActivityd에서 일반회원이 기부하기를 누를 경우 표시되는 다이얼로그 창이다
이전 campaign_detail_pageActivityd에서 기부자의 id와 캠페인의 seq 를 전달 받는다 . 이 정보는 서버에 기부금 정보를 전송할떄 사용된다
기부가 성공할 경우 다이얼로그가 닫히며 실패할 경우 닫히지 않는다


     object_initiate() :  입력할 기부금액 , 전송 ,취소 객체를 생성한다
       touch_initiate() : 전송과 취소 버튼이 있다
            okBtn() : 입력한 money 를 가져와 give_money() 를 실행한다
            cancleBtn() : 기부를 취소한다
      give_money() : 해당 seq, id , 기부 금액을 서버에 key : give_donate 라는 K-V 로 전송한다.
                       응답값으로는 responseResult에서 받고 데이터는  단순 ok와 no 두가지 종류이다
 */


public class donateDialog {

    Dialog dlg;
    Context context;
    TextView title;
    EditText input;
    Button ok,cancle;
    String id,seq,type;

    private ProgressBar mProgressBar;
    public donateDialog(Context context) {
        this.context = context;
    }

    public void callFunction(String id,String seq,String type) {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        dlg = new Dialog(context);
        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_give_donate);
        // 커스텀 다이얼로그를 노출한다.
        dlg.show();
        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        this.id=id;
        this.seq=seq;
        this.type=type;
        dlg.setCancelable(false);

        object_initiate();
        touch_initiate();

    }

    void object_initiate(){
        title=dlg.findViewById(R.id.title);
        input=dlg.findViewById(R.id.money);
        ok=dlg.findViewById(R.id.okButton);
        cancle=dlg.findViewById(R.id.cancelButton);
        mProgressBar = dlg.findViewById(R.id.progress);

        if(type.equals("money")){
            input.setHint("금액을 입력해주세요");
        }else if(type.equals("point")){
            input.setHint("포인트를 입력해주세요");
        }
    }
    void touch_initiate(){
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("test111","ok");
                String regExp="^[0-9]*$";
                if(input.getText().toString().length()==0){
                    Toast.makeText(context,"입력해주세요",Toast.LENGTH_LONG).show();
                }else{
                    Log.d("test111","ok1");
                    String tmp_money=input.getText().toString();


                    if(tmp_money.matches(regExp)){
                        int int_input =Integer.parseInt(tmp_money);
                        if(int_input==0){
                            Log.d("test111","ok2");
                            if(type.equals("money")){
                                Toast.makeText(context,"0원은 기부할수 없습니다.",Toast.LENGTH_LONG).show();
                            }else if(type.equals("time")){
                                Toast.makeText(context,"0포인트는 기부할수 없습니다.",Toast.LENGTH_LONG).show();
                            }

                        }else{
                            Log.d("test111","ok3");
                            if(type.equals("money")){
                                give_money(int_input);
                            }else if(type.equals("point")){
                                give_time(int_input);
                            }

                        }
                    }else{
                        Toast.makeText(context,"문자는 넣을수없습니다",Toast.LENGTH_LONG).show();
                    }
                }




            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dlg.dismiss();
            }
        });
    }



    void give_money(int int_money){

        Map insert=new HashMap();
        insert.put("request","give_donate");
        insert.put("id",id);
        insert.put("seq",seq);
        insert.put("give_money",int_money);

        UploadService Retrofit = MyRetrofit2.getRetrofit2().create(UploadService.class);


        // 프로그래스 바 넣을부분 . 등록 , 취소 ,텍스트 버튼 막기
        ok.setEnabled(false);
        cancle.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
        input.setEnabled(false);


        Call<responseResult> call = Retrofit.give_donate(insert);
        call.enqueue(new Callback<responseResult>() {
            @Override
            public void onResponse(Call<responseResult> call, Response<responseResult> response) {
                mProgressBar.setVisibility(View.GONE);
                responseResult repo=response.body();


                if(repo.getResult().equals("ok")){
                    Toast.makeText(context,"기부하였습니다",Toast.LENGTH_LONG).show();
                    dlg.dismiss();
                }else{
                    Toast.makeText(context,"기부를 실패 하였습니다.",Toast.LENGTH_LONG).show();
                    dlg.dismiss();
                }
            }

            @Override
            public void onFailure(Call<responseResult> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }
    void give_time(int int_money){

        Map insert=new HashMap();
        insert.put("request","give_time_donate");
        insert.put("id",id);
        insert.put("seq",seq);
        insert.put("give_time",int_money);

        UploadService Retrofit = MyRetrofit2.getRetrofit2().create(UploadService.class);


        // 프로그래스 바 넣을부분 . 등록 , 취소 ,텍스트 버튼 막기
        ok.setEnabled(false);
        cancle.setEnabled(false);
        mProgressBar.setVisibility(View.VISIBLE);
        input.setEnabled(false);


        Call<responseResult> call = Retrofit.give_donate(insert);
        call.enqueue(new Callback<responseResult>() {
            @Override
            public void onResponse(Call<responseResult> call, Response<responseResult> response) {
                mProgressBar.setVisibility(View.GONE);
                responseResult repo=response.body();

                String[] result =repo.getResult().split(":");
                if(result[0].equals("ok")){
                    Toast.makeText(context,"기부하였습니다",Toast.LENGTH_LONG).show();
                    dlg.dismiss();
                }else{
                    Toast.makeText(context,"기부를 실패 하였습니다.",Toast.LENGTH_LONG).show();
                    dlg.dismiss();
                }
            }

            @Override
            public void onFailure(Call<responseResult> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
            }
        });
    }
}

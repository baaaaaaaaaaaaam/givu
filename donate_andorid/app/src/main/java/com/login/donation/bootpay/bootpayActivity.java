package com.login.donation.bootpay;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.login.donation.Object.responseResult;
import com.login.donation.R;
import com.login.donation.info.info;
import com.login.donation.mypage_DonationActivity;
import com.login.donation.retrofit.MyRetrofit2;
import com.login.donation.retrofit.UploadService;

import java.util.HashMap;
import java.util.Map;

import kr.co.bootpay.Bootpay;
import kr.co.bootpay.BootpayAnalytics;
import kr.co.bootpay.BootpayKeyValue;
import kr.co.bootpay.enums.UX;
import kr.co.bootpay.listener.CancelListener;
import kr.co.bootpay.listener.CloseListener;
import kr.co.bootpay.listener.ConfirmListener;
import kr.co.bootpay.listener.DoneListener;
import kr.co.bootpay.listener.ErrorListener;
import kr.co.bootpay.listener.ReadyListener;
import kr.co.bootpay.model.BootExtra;
import kr.co.bootpay.model.BootUser;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/*
mypage_DonationActivity에서 충전하기 버튼을 누를경우 실행된다.

 */


public class bootpayActivity extends AppCompatActivity {

    boolean doneCheck;

    private String application_id = info.bootpay_id;
    Context context;



    Spinner spinner_pg;
    Spinner spinner_method;
    Spinner spinner_ux;
    EditText edit_price;
    EditText edit_non_tax;
    Button btn;
    int price;
    SharedPreferences pref;
    String id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bootpay);
        BootpayAnalytics.init(this, application_id);
        this.context = this;


        initiate();
    }


    void initiate(){
        spinner_pg = findViewById(R.id.spinner_pg);
        spinner_method = findViewById(R.id.spinner_method);
        spinner_ux = findViewById(R.id.spinner_ux);
        edit_price = findViewById(R.id.edit_price);
        edit_non_tax = findViewById(R.id.edit_non_tax);
        btn=findViewById(R.id.btn_pg);
        pref=getSharedPreferences("auto_login",0);
        id=pref.getString("id","");
    }

    //액티비티에서 바로 실행시키도록 함
    public void goRequest(View v) {
//        runOnUiThread();

//        BootpayRestService

//        Spinner mySpinner = (Spinner) findViewById(R.id.your_spinner);
//        String text = mySpinner.getSelectedItem().toString();
        BootUser bootUser = new BootUser().setPhone("010-1234-5678"); // 구매자 정보
        BootExtra bootExtra = new BootExtra().setQuotas(new int[] {0,2,3});  // 일시불, 2개월, 3개월 할부 허용, 할부는 최대 12개월까지 사용됨 (5만원 이상 구매시 할부허용 범위)
        price = 1000;
        try {
            price = Integer.parseInt(edit_price.getText().toString());
        } catch (Exception e){}


        String pg = BootpayKeyValue.getPGCode(spinner_pg.getSelectedItem().toString());
        String method = BPValue.methodToString(spinner_method.getSelectedItem().toString());
        UX ux = UX.valueOf(spinner_ux.getSelectedItem().toString());
        Context context = this;

        BootpayAnalytics.init(this, application_id);



        Bootpay.init(getFragmentManager())
                .setContext(context)
                .setApplicationId(application_id) // 해당 프로젝트(안드로이드)의 application id 값
                .setPG(pg) // 결제할 PG 사
                .setBootUser(bootUser)
                .setBootExtra(bootExtra)
//                .setUserPhone("010-1234-5678") // 구매자 전화번호
                .setUX(ux)
                .setMethod(method) // 결제수단
                .setName("충전하기") // 결제할 상품명
                .setOrderId("1234") // 결제 고유번호expire_month
//                .setAccountExpireAt("2018-09-22") // 가상계좌 입금기간 제한 ( yyyy-mm-dd 포멧으로 입력해주세요. 가상계좌만 적용됩니다. 오늘 날짜보다 더 뒤(미래)여야 합니다 )

                .setPrice(price) // 결제할 금액
                .addItem("마우's 스", 1, "ITEM_CODE_MOUSE", 100d) // 주문정보에 담길 상품정보, 통계를 위해 사용
                .addItem("키보드", 1, "ITEM_CODE_KEYBOARD", 200d, "패션", "여성상의", "블라우스") // 주문정보에 담길 상품정보, 통계를 위해 사용
                .onConfirm(new ConfirmListener() { // 결제가 진행되기 바로 직전 호출되는 함수로, 주로 재고처리 등의 로직이 수행
                    @Override
                    public void onConfirm(@Nullable String message) {

                        if (true) Bootpay.confirm(message); // 재고가 있을 경우.
                        else Bootpay.removePaymentWindow(); // 재고가 없어 중간에 결제창을 닫고 싶을 경우
                        Log.d("confirm", message);
                    }
                })
                .onDone(new DoneListener() { // 결제완료시 호출, 아이템 지급 등 데이터 동기화 로직을 수행합니다
                    @Override
                    public void onDone(@Nullable String message) {
                        Log.d("done", message);
                        doneCheck=true;
                        upload();
                    }
                })
                .onReady(new ReadyListener() { // 가상계좌 입금 계좌번호가 발급되면 호출되는 함수입니다.
                    @Override
                    public void onReady(@Nullable String message) {
                        Log.d("ready", message);
                    }
                })
                .onCancel(new CancelListener() { // 결제 취소시 호출
                    @Override
                    public void onCancel(@Nullable String message) {
                        Log.d("cancel", message);
                        if(doneCheck==false){
                            finish();
                        }

                    }
                })
                .onError(new ErrorListener() { // 에러가 났을때 호출되는 부분
                    @Override
                    public void onError(@Nullable String message) {

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        Log.d("error3", message);
                    }
                })
                .onClose(
                        new CloseListener() { //결제창이 닫힐때 실행되는 부분
                            @Override
                            public void onClose(String message) {
                                Log.d("close", "close");
                            }
                        })
                .request();

    }

    void upload(){
        Map money_info = new HashMap<>();

        String money= Integer.toString(price);
        money_info.put("request","money_charge");
        money_info.put("id",id);
        money_info.put("money_charge",money);

        UploadService retorfit= MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call <responseResult> call=retorfit.charge_money(money_info);

        call.enqueue(new Callback<responseResult>() {
            @Override
            public void onResponse(Call<responseResult> call, Response<responseResult> response) {
                responseResult repo =response.body();
                if (repo.getResult().equals("ok")){
                    Toast.makeText(getApplicationContext(),"충전 완료",Toast.LENGTH_LONG).show();
                    Log.d("test","업로드완료 ?");
                    Intent intent=new Intent(getApplicationContext(), mypage_DonationActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();

                }else{
                    Toast.makeText(getApplicationContext(),"서버 처리 실패",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<responseResult> call, Throwable t) {

            }
        });


    }
}
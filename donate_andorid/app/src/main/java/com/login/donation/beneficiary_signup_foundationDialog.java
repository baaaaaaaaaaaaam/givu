package com.login.donation;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


/*
mypage_Beneficiary_signupActivity에서 등록하기버튼을눌러 서버의 groupmember 테이블에 자신의 id와 기부단체의 id를 등록하게된다
등록이 정상이거나 실패할경우 발생하는 다이얼로그로 확인버튼을 눌러 mypage_Beneficiary_signupActivity로 callback한 후 해당 페이지를 닫는다

mypage_Beneficiary_signupActivity에서 받은 string을 바탕으로 다이얼로그에 표시된다
 */

public class beneficiary_signup_foundationDialog  extends Dialog {

    private Context context;
    Dialog dlg;
    private beneficiary_signup_foundationDialog.CustomDialogListener customDialogListener;
    String message;
    TextView textView;
    Button button;

    public beneficiary_signup_foundationDialog(Context context,String message) {
        super(context);
        this.context = context;
        this.message=message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        dlg = new Dialog(context);
        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_beneficiary_mypage_signup_result);
        // 커스텀 다이얼로그를 노출한다.
        dlg.show();
        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        button=dlg.findViewById(R.id.ok);
        textView=dlg.findViewById(R.id.log);
        if(message.equals("ok")){
            textView.setText("등록되었습니다");
        }else{
            textView.setText("등록 실패 하였습니다");
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                customDialogListener.ok(message);
                dlg.dismiss();
            }
        });
    }

    interface CustomDialogListener{
        void ok(String message);
    }

    // CustomDialogListener 선언부와 객체를 연결할 수 있는 메소드이다
    public void setDialogListener(beneficiary_signup_foundationDialog.CustomDialogListener customDialogListener){
        this.customDialogListener = customDialogListener;
    }
}
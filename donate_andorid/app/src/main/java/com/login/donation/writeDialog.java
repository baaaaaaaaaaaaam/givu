package com.login.donation;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

public class writeDialog {
    private Context context;
    Dialog dlg;
    TextView money_donate,volunteer_donate,volunteer_recruitment;
    public writeDialog(Context context) {
        this.context = context;
    }

    public void callFunction() {

        // 커스텀 다이얼로그를 정의하기위해 Dialog클래스를 생성한다.
        dlg = new Dialog(context);
        // 액티비티의 타이틀바를 숨긴다.
        dlg.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 커스텀 다이얼로그의 레이아웃을 설정한다.
        dlg.setContentView(R.layout.dialog_write);
        // 커스텀 다이얼로그를 노출한다.
        dlg.show();
        // 커스텀 다이얼로그의 각 위젯들을 정의한다.
        object_initiate();

        touch_initiate();

    }
    void object_initiate(){
        money_donate=dlg.findViewById(R.id.money_donate);
        volunteer_donate=dlg.findViewById(R.id.volunteer_donate);
        volunteer_recruitment=dlg.findViewById(R.id.volunteer_recruitment);
    }
    void touch_initiate(){
        money_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(context,write_campaignAcivity.class);
                context.startActivity(a);
                dlg.dismiss();
            }
        });
        volunteer_donate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(context,write_time_campaignActivity.class);
                context.startActivity(a);
                dlg.dismiss();
            }
        });
        volunteer_recruitment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent a=new Intent(context,write_volunteer_recruitmentActivity.class);
                context.startActivity(a);
                dlg.dismiss();
            }
        });
    }
}

package com.login.donation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

public class logoActivity extends AppCompatActivity {

    ImageView imageView;
    TextView textView;
    private Animation alphaAni;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        object_initiate();//객체 생성

        textView.startAnimation(alphaAni); //애니메이션 시작
        Glide.with(this).load(R.raw.st).into(imageView);  //이미지랑 gif랑 연결 디렉토리 이름 반드시 raw
        touch_initiate(); //터치 리스터

    }

    //변수 초기화
    private void object_initiate(){

        imageView=findViewById(R.id.background);  // gif 이미지
        textView=findViewById(R.id.introduce);   // 인트로 버트
        alphaAni = AnimationUtils.loadAnimation(this, R.anim.alpha); // 서서히보이는 애니메이션 효과 , 애니메이션 효과 파일 명 반드시 anim ,animation으로 해야함

    }

    //터치 관련 메소드
    private  void touch_initiate(){
        //텍스트 터치할 경우 다음 login activity로 이동
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), loginActivity.class));
                finish();

            }
        });
    }

}

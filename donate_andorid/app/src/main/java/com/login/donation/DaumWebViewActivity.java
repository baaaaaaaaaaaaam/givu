package com.login.donation;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.login.donation.info.info;


public class DaumWebViewActivity  extends AppCompatActivity {

    private WebView daum_webView;
    private TextView daum_result;
    private Handler handler;


    @Override

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_daum_web_view);

        // WebView 초기화
        init_webView();
        // 핸들러를 통한 JavaScript 이벤트 반응
        handler = new Handler();
        Log.d("test111","다음 지도 실행32");
    }


    public void init_webView() {
        // WebView 설정
        daum_webView = (WebView) findViewById(R.id.daum_webview);
        // JavaScript 허용
        daum_webView.getSettings().setJavaScriptEnabled(true);
        daum_webView.setWebChromeClient(new WebChromeClient());
        // JavaScript의 window.open 허용 //이거 false처리하면 애뮬에서도팝업 문제로 안됨
        daum_webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);




        // JavaScript이벤트에 대응할 함수를 정의 한 클래스를 붙여줌
        daum_webView.addJavascriptInterface(new AndroidBridge(), "TestApp");

        // web client 를 chrome 으로 설정
        Log.d("test111","다음 지도 실행1");
        //         webview url load. php 파일 주소
        daum_webView.loadUrl(info.map_address);
        Log.d("test111","다음 지도 실행2");
    }


    private class AndroidBridge {

        @JavascriptInterface
        public void setAddress(final String arg1, final String arg2, final String arg3) {

            handler.post(new Runnable() {

                @Override
                public void run() {
                    Log.d("test111","다음 지도 실행1.5");
                    //arg1 : 지번  , arg2 : 주소 , arg3 : 지명
                    String address=arg2;

                    Intent intent = new Intent();
                    intent.putExtra("result", address);
                    setResult(RESULT_OK, intent);
                    finish();

                }

            });

        }

    }


}

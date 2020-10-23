package com.login.donation;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.login.donation.Object.groupmemberQuery;
import com.login.donation.Object.responseResult;
import com.login.donation.Object.volunteer_recruitment;
import com.login.donation.info.info;
import com.login.donation.retrofit.MyRetrofit2;
import com.login.donation.retrofit.UploadService;


import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class volunteer_recruitment_detail_pageActivity extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private IntentIntegrator qrScan;

    TextView subject, startDate, endDate, startTime, endTime, location, content, modified, delete;
    ImageView profile;
    Button createQRcode,volunteer_start,volunteer_end,back_btn;

    String id, seq, writer,mode;
    Double latitude,longitude;
    String barcode,current,date;

    boolean check_volunteer_start;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_recruitment_detail_page);
        //객체 초기화
        initiate_object();
        //쉐어드에서 아이디와 선택한 게시물 seq 가져오기
        getShared();
        check_mode();
        //터치 리스너 초기화
        initiate_touch();



    }

    @Override
    protected void onStart() {
        super.onStart();
        //서버에서 받아온 자원봉사 내용 보여주기
        volunteer_recruitment_detail_page();

    }

    void initiate_object() {
        subject = findViewById(R.id.subject);
        startDate = findViewById(R.id.startDate);
        endDate = findViewById(R.id.endDate);
        startTime = findViewById(R.id.startTime);
        endTime = findViewById(R.id.endTime);
        location = findViewById(R.id.location);
        content = findViewById(R.id.content);
        profile = findViewById(R.id.profile);
        modified = findViewById(R.id.modified);
        delete = findViewById(R.id.delete);
        createQRcode=findViewById(R.id.createQRcode);
        volunteer_start=findViewById(R.id.volunteer_start);
        volunteer_end=findViewById(R.id.volunteer_end);
        back_btn=findViewById(R.id.back_btn);


    }

    void getShared() {
        pref = getSharedPreferences("auto_login", 0);
        id = pref.getString("id", "");
        mode = pref.getString("mode","0");
        pref = getSharedPreferences("campaign_detail_page_info", MODE_PRIVATE);
        seq = pref.getString("seq", "");
        writer = pref.getString("id", "");
        editor=pref.edit();



    }

    void check_mode(){
        //먼저 글쓴이와 접속자를 확인하여 같은경우 수정 /삭제 / QR코드 활성화 버튼을 생성한다
        // 다른 경우 수정 / 삭제 / QR코트 활성화버튼을 제거하고 mode1인지를 체크한다
        //작성자가 아니고 mode가 1인경우 봉사활동 시작, 종료버튼을 생성한다
        // 작성자도 아니고 , mode가 1도 아닌 mode2와 mode3은 참가할 수 없다.


        //글쓴사람과 게시글을 클릭한 사람 아이디 비교
        //비교 해서 같은 경우 수정/삭제버튼을  다른경우 뒤로가기 버튼만 보여주기
        //글쓴이와 접속자 아이디가 같은경우 수정 , 삭제 , QR코드 보기 코드 활성화
        if (id.equals(writer)) {
            modified.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            createQRcode.setVisibility(View.VISIBLE);

        } else {
            //디폴트는 뒤로가기
            modified.setVisibility(View.INVISIBLE);
            delete.setVisibility(View.INVISIBLE);
            createQRcode.setVisibility(View.GONE);
        }

        //기부자인경우에만 봉사활동 시작 , 종료버튼 활성화 , 나머지는 뒤로가기버튼
        if(mode.equals("mode:1")){
            volunteer_start.setVisibility(View.VISIBLE);
            volunteer_end.setVisibility(View.VISIBLE);
            back_btn.setVisibility(View.GONE);
        }else{
            volunteer_start.setVisibility(View.GONE);
            volunteer_end.setVisibility(View.GONE);
            back_btn.setVisibility(View.GONE);
        }
    }
    void initiate_touch() {

        createQRcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editor.putString("barcode",barcode);
                editor.commit();
                startActivity(new Intent(getApplicationContext(),make_QRcodeActivity.class));
            }
        });

        modified.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getApplicationContext(),volunteer_recruitment_modified_pageActivity.class));
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               alterDialog();
            }
        });

        volunteer_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_volunteer_start=true;
                QRcode_scan("봉사활동 시작");

            }
        });
        volunteer_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                QRcode_scan("봉사활동 종료");
            }
        });

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    void volunteer_recruitment_detail_page() {
        Map select = new HashMap();
        select.put("request", "volunteer_recruitment_detail_page");
        select.put("seq", seq);

        UploadService retrofit = MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<groupmemberQuery> call = retrofit.volunteer_recruitment_detail_page(select);
        call.enqueue(new Callback<groupmemberQuery>() {
            @Override
            public void onResponse(Call<groupmemberQuery> call, Response<groupmemberQuery> response) {
                groupmemberQuery repo = response.body();


                if (repo.getResult().equals("ok")) {
                    Gson gson = new Gson();
                    volunteer_recruitment gsom_campaign = gson.fromJson(repo.getQueryResult(), volunteer_recruitment.class);

                    Glide.with(profile.getContext())
                            .load(info.upload_ip + gsom_campaign.getImagePath())
                            .into(profile);
                    subject.setText(gsom_campaign.getSubject());
                    startDate.setText(gsom_campaign.getStartDate());
                    endDate.setText(gsom_campaign.getEndDate());
                    startTime.setText(gsom_campaign.getStartTime());
                    endTime.setText(gsom_campaign.getEndTime());
                    location.setText(gsom_campaign.getLocation());
                    content.setText(gsom_campaign.getContent());
                    barcode=gsom_campaign.getBarcode();

                    Log.d("test111","barcode "+barcode);
                    //시간비교
                    diff_time();

                    //받아온 주소로 위경도 바꾸기
                    if(gsom_campaign.getDoing().equals("false")){
                        modified.setVisibility(View.INVISIBLE);
                        delete.setVisibility(View.INVISIBLE);
                    }
                    if(mode.equals("mode:2")){

                    }else{
                        change_address();
                    }
//
                } else {
                    Toast.makeText(getApplicationContext(), "데이터 못받음" + repo.getResult(), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<groupmemberQuery> call, Throwable t) {
                Log.d("test111", t.toString());
            }
        });


    }


    //서버로부터 받아온 주소정보를 위도경도로 변환한후 해당 위치를 중심으로 지도에 그리도록설정
    // 지도그릴때 마커를 찍어 해당주소 정확히 표시
    void change_address() {
        final Geocoder geocoder = new Geocoder(volunteer_recruitment_detail_pageActivity.this);

        List<Address> list = null;
        Log.d("test111","location : "+location.getText().toString() );

        String str = location.getText().toString();
        try {
            list = geocoder.getFromLocationName(
                    str, // 지역 이름
                    5); // 읽을 개수
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("test111","입출력 오류 - 서버에서 주소변환시 에러발생");
        }

        if (list != null) {
            if (list.size() == 0) {
                Log.d("test111","해당되는 주소 없음");
            } else {
                Log.d("test111","해당되는 주소 있음 getLatitude : "+list.get(0).getLatitude() +  " , list.get(0).getLongitude();"+list.get(0).getLongitude());
                //          list.get(0).getCountryName();  // 국가명
                          latitude=list.get(0).getLatitude();        // 위도
                          longitude=list.get(0).getLongitude();    // 경도



                //바꾼 위도경도로 다음 맵에서 마커 표시하기
                MapView mapView = new MapView(this);
                ViewGroup mapViewContainer = findViewById(R.id.map_view);
                MapPoint mapPoint = MapPoint.mapPointWithGeoCoord(latitude, longitude);
                mapView.setMapCenterPoint(mapPoint, true);
                mapViewContainer.addView(mapView);
                MapPOIItem marker = new MapPOIItem();
                marker.setItemName("여기!!!");
                marker.setTag(0);
                marker.setMapPoint(mapPoint);
                // 기본으로 제공하는 BluePin 마커 모양.
                marker.setMarkerType(MapPOIItem.MarkerType.BluePin);
                // 마커를 클릭했을때, 기본으로 제공하는 RedPin 마커 모양.
                marker.setSelectedMarkerType(MapPOIItem.MarkerType.RedPin);
                mapView.addPOIItem(marker);
            }
        }
    }


    void alterDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("게시글 삭제").setMessage("정말 삭제하시겠습니까?");

        builder.setPositiveButton("예", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Log.d("test111","예누름");
                delete_update();

            }
        });

        builder.setNegativeButton("아니요", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                Toast.makeText(getApplicationContext(), "Cancel Click", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }



    void delete_update(){

        Map delete = new HashMap();
        delete.put("request","delete_volunteer_recruitment");
        delete.put("seq",seq);

        UploadService retrofit = MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<responseResult> call= retrofit.delete_volunteer_recruitment(delete);
        call.enqueue(new Callback<responseResult>() {
            @Override
            public void onResponse(Call<responseResult> call, Response<responseResult> response) {
                responseResult repo=response.body();

                if(repo.getResult().equals("ok")){
                    Toast.makeText(getApplicationContext(),"삭제 성공",Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"삭제 실패",Toast.LENGTH_LONG).show();
                }


            }

            @Override
            public void onFailure(Call<responseResult> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }




    void diff_time(){
        //2020년9월12일을 처음 년 기준으로 잘라 2020과 9월12일로 나눈다
        //나눈 9월12일을 월로 다시 자르고 자른 문자의 길이가 1 일 경우 0을 붙인다
        //마지막으로 남은 12일을 일 기준으로 자르고 자른 것들은 붙인다

        String tmp_start_date=startDate.getText().toString();
        String _start_date=date_split(tmp_start_date);
        String tmp_end_date=endDate.getText().toString();
        String _end_date=date_split(tmp_end_date);


        //6시 30분을 "시" 기준으로 자르고 첫번쨰 배열의 길이가 1인경우 앞에 0을 붙인다
        //이후 두번쨰 배열도 "분"기준으로 자르고 자른 첫번째 배열의 길이가 1인경우 앞에 9을 붙인다
        String tmp_start_time=startTime.getText().toString().trim();
        String tmp_end=endTime.getText().toString().trim();
        tmp_start_time=tmp_start_time.replace(" ","");
        tmp_end=tmp_end.replace(" ","");
        Log.d("test111","tmp_start_time   " +tmp_start_time + " , tmp_end  " +tmp_end  );


        String _start_time=time_split(tmp_start_time);
        String _end_time=time_split(tmp_end);
        Log.d("test111","_start_time   " +_start_time + " , _end_time  " +_end_time  );




        Date now = new Date();
        SimpleDateFormat dateFormat= new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateFormat1= new SimpleDateFormat("HHmm", Locale.KOREA);

        try {

            //현재시간
             current= dateFormat1.format(now);
             date=dateFormat.format(now);
            ///날자 비교하는데 사용됨
            Date start_tmp=dateFormat.parse(_start_date);
            Date end_tmp=dateFormat.parse(_end_date);
            long start_time=start_tmp.getTime();
            long end_time=end_tmp.getTime()+86000000;




            int int_currnt=Integer.parseInt(current);
            int int_start_time=Integer.parseInt(_start_time);
            int int_end_time=Integer.parseInt(_end_time);


            //날자비교
            //1일 86000000ms
            if(now.getTime()>=start_time && now.getTime()<end_time){
                //날짜가 기간 내에 있는경우

                if(int_currnt>=int_start_time && int_currnt<int_end_time){
                    //시간이 기간 내에 있는 경우
                    createQRcode.setEnabled(true);
                    volunteer_start.setEnabled(true);
                    volunteer_end.setEnabled(true);
                }else{
                    createQRcode.setEnabled(false);
                    volunteer_start.setEnabled(false);
                    volunteer_end.setEnabled(false);
                }
            }else{
                //QR코드 만들기 버튼 비활성화
                createQRcode.setEnabled(false);
            }


        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    //2019년 9월 10일을   20190910으로 바꿔준다
    String date_split(String s){
        String result;
        String[] year=s.split("년");
        String[] month=year[1].split("월");
        if(month[0].length()<=1){
            result =  year[0]+"0"+month[0];
        }else{
            result =  year[0]+month[0];
        }
        String[] day=month[1].split("일");
        result+=day[0];
        return result;
    }


    //6시 10분을 0610 으로 바꿔준다
    String time_split(String s){
        String result;
        String[] time=s.split("시");
        if(time[0].length()<=1){
            result = "0"+time[0];
        }else{
            result = time[0];
        }
        String[] minute=time[1].split("분");
        if(minute[0].length()<=1){
            result = result+"0"+minute[0];
        }else{
            result += minute[0];
        }

        return result;
    }


    void QRcode_scan(String s){
        qrScan = new IntentIntegrator(this);
        qrScan.setOrientationLocked(false); // default가 세로모드인데 휴대폰 방향에 따라 가로, 세로로 자동 변경됩니다.
        qrScan.setPrompt(s);
        qrScan.initiateScan();


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show();
                // todo
            } else {
                //서버로 qr코드값과 시작인지 종료인지 값을전달한다
                //서버에서는 이 게시글 seq , 봉사활동 참가자 ,참가 날자, 시작시간, qr코드 를 저장한다.
                    update_volunteer_time(result.getContents());

//                Toast.makeText(this, "Scanned: " + result.getContents(), Toast.LENGTH_LONG).show();
                // todo
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    void update_volunteer_time(String s){
        Date now = new Date();
        Map update =new HashMap();
        update.put("request","volunteer_time");
        update.put("seq",seq);
        update.put("id",id);
        update.put("barcode",s);
        update.put("date",date);
        update.put("current",now.getTime());
        update.put("check_volunteer_start",check_volunteer_start);

        UploadService retrofit = MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<responseResult> call=retrofit.volunteer_time(update);
        call.enqueue(new Callback<responseResult>() {
            @Override
            public void onResponse(Call<responseResult> call, Response<responseResult> response) {
                responseResult repo= response.body();

                if(repo.getResult().equals("ok")){
                    if(check_volunteer_start){
                        Toast.makeText(getApplicationContext(),"봉사활동 시작 등록!!",Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"봉사활동 종료 등록!!",Toast.LENGTH_LONG).show();
                    }
                    check_volunteer_start=false;
                }else{
                    Toast.makeText(getApplicationContext(),"already :"+repo.getResult(),Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<responseResult> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"실패 :"+t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

    }
}
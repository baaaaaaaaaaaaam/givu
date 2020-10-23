package com.login.donation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.login.donation.Object.groupmemberQuery;
import com.login.donation.Object.responseResult;
import com.login.donation.Object.volunteer_recruitment;
import com.login.donation.info.info;
import com.login.donation.retrofit.MyRetrofit2;
import com.login.donation.retrofit.UploadService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


//게시글을 선택햇을때 저장해놓은 seq를 서버로부터 받아와  먼저 데이터를 넣는다
// 이후 데이터를 변경하면 해당데이터를 volunteer_recruitment_modified_pageActivity 이름으로 서버에 보내 update하도록 한다.
//다른 기능은 write_volunteer_recruitment와 똑같다

public class volunteer_recruitment_modified_pageActivity extends AppCompatActivity {


    private  final int Album_CODE = 1;
    private  final int Map_CODE = 2;
    ProgressBar mProgressBar;
    SharedPreferences shared;
    String seq,id;
    int setYear,setMonth,setDay;
    EditText subject,content;
    TextView startDate,endDate,startTime,endTime,location;
    ImageView profile;
    Button submit,cancle;
    Uri uri;
    boolean imageviewCheck;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_volunteer_recruitment_modified_page);


        //객체 생성
        initiate_object();
        //쉐어드에서 아이디 꺼내옴
        getshared();
        //서버에서 정보 받아옴
        get_volunteer_recruitment_modified();

        //터치리스너
        initiate_touch();
    }

    void initiate_object(){
        shared=getSharedPreferences("campaign_detail_page_info",0);
        subject=findViewById(R.id.subject);
        content=findViewById(R.id.content);
        startDate=findViewById(R.id.startDate);
        endDate=findViewById(R.id.endDate);
        startTime=findViewById(R.id.startTime);
        endTime=findViewById(R.id.endTime);
        location=findViewById(R.id.location);
        profile=findViewById(R.id.profile);
        submit=findViewById(R.id.submit);
        cancle=findViewById(R.id.cancle);
        mProgressBar=findViewById(R.id.progress);
    }
    void getshared(){
        seq=shared.getString("seq","");
    }

    void initiate_touch(){
        //이미지 등록
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, Album_CODE);
            }
        });

        //날자 적용
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDay();
            }
        });
        //시간 적용
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startClock();
            }
        });
        //주소 지정하기
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getlocation();
            }
        });
        //등록
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checking();
            }
        });

        //취소
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }



    //서버로 부터 받아오는 데이터를 화면에 연결시킨다.
    void get_volunteer_recruitment_modified() {
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
                    id=gsom_campaign.getId();

//                    Gson gson = new Gson();
//                    volunteer_recruitment[] gsom_campaign = gson.fromJson(repo.getQueryResult(), volunteer_recruitment[].class);
//                    ArrayList<volunteer_recruitment> tmp = new ArrayList<>(Arrays.asList(gsom_campaign));
//                    Glide.with(profile.getContext())
//                            .load(info.upload_ip + tmp.get(0).getImagePath())
//                            .into(profile);
//                    subject.setText(tmp.get(0).getSubject());
//                    startDate.setText(tmp.get(0).getStartDate());
//                    endDate.setText(tmp.get(0).getEndDate());
//                    startTime.setText(tmp.get(0).getStartTime());
//                    endTime.setText(tmp.get(0).getEndTime());
//                    location.setText(tmp.get(0).getLocation());
//                    content.setText(tmp.get(0).getContent());
//                    id=tmp.get(0).getId();

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



    //시작일
    void startDay(){
        //날짜 받아오는 포맷 지정
        SimpleDateFormat yyyy = new SimpleDateFormat ( "yyyy");
        SimpleDateFormat MM = new SimpleDateFormat ( "MM");
        SimpleDateFormat dd = new SimpleDateFormat ( "dd");

        //날짜 받아옴
        Calendar pickedDate = Calendar.getInstance();


        //오늘 날짜 받아온  포맷대로 파싱
        String Year = yyyy.format(pickedDate.getTime());
        String Month = MM.format(pickedDate.getTime());
        String Day = dd.format(pickedDate.getTime());


        //달력 캘린더 열었을때 지정할 날자
        // 캘린더 함수는 0부터 시작을 함으로 1월은 0 이 들어가야한다.  현재 9월임으로 9를 넣으면 10월부터 선택할수잇음 ( 그래서 -1 해줘야함 )
        //실제로 값을 가져올때에도 9월1일을 선택하면 8을 반환 함으로 +1 해서 처리해야한다
        pickedDate.set(Integer.parseInt(Year),Integer.parseInt(Month)-1,Integer.parseInt(Day));

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                volunteer_recruitment_modified_pageActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        setYear=year;
                        setMonth=month;
                        setDay=dayOfMonth;

                        startDate.setText(year + "년" + (month+1) + "월" + dayOfMonth + "일");

                        endDay(setYear,setMonth,setDay);
                    }
                },

                pickedDate.get(Calendar.YEAR),
                pickedDate.get(Calendar.MONTH),
                pickedDate.get(Calendar.DATE)
        );
        datePickerDialog.getDatePicker().setMinDate(pickedDate.getTime().getTime());
        datePickerDialog.show();
    }
    //종료일
    void endDay(int year,int month,int dayOfMonth){
        Calendar pickedDate = Calendar.getInstance();


        //달력 캘린더 열었을때 지정할 날자
        // 캘린더 함수는 0부터 시작을 함으로 1월은 0 이 들어가야한다.  현재 9월임으로 9를 넣으면 10월부터 선택할수잇음 ( 그래서 -1 해줘야함 )
        //실제로 값을 가져올때에도 9월1일을 선택하면 8을 반환 함으로 +1 해서 처리해야한다
        pickedDate.set(year,month,dayOfMonth);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                volunteer_recruitment_modified_pageActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        endDate.setText(year + "년" + (month+1) + "월" + dayOfMonth + "일");
                    }
                },
                pickedDate.get(Calendar.YEAR),
                pickedDate.get(Calendar.MONTH),
                pickedDate.get(Calendar.DATE)
        );
        datePickerDialog.getDatePicker().setMinDate(pickedDate.getTime().getTime());
        datePickerDialog.show();
    }

    //시작 시간적용
    void startClock(){
        TimePickerDialog tpd =
                new TimePickerDialog(volunteer_recruitment_modified_pageActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay, int minute) {
                                startTime.setText(hourOfDay +"시 " + minute+"분");
                                endClock(hourOfDay,minute);
                            }
                        }, // 값설정시 호출될 리스너 등록
                        4,19, false); // 기본값 시분 등록
        // true : 24 시간(0~23) 표시
        // false : 오전/오후 항목이 생김
        tpd.show();


    }
    //종료 시간
    void endClock(final int hour, int minute){
        TimePickerDialog tpd =
                new TimePickerDialog(volunteer_recruitment_modified_pageActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view,
                                                  int hourOfDay, int minute) {

                                if(hour<hourOfDay){
                                    endTime.setText(hourOfDay +"시 " + minute+"분");
                                }else{
                                    Toast.makeText(getApplicationContext(),"시간 설정이 잘못되었습니다",Toast.LENGTH_LONG).show();
                                }
                            }
                        }, // 값설정시 호출될 리스너 등록
                        hour,minute, false); // 기본값 시분 등록
        // true : 24 시간(0~23) 표시
        // false : 오전/오후 항목이 생김
        tpd.show();
    }
    //주소 지정하기 , 지도로 이동
    void getlocation(){
        Intent intent=new Intent(getApplicationContext(),DaumWebViewActivity.class);
        startActivityForResult(intent,Map_CODE);
    }

    //등록누를경우 서버로 전송
    void checking(){
        if(subject.length()!=0){
            if(uri!=null){
                if(startDate.length()!=0&&endDate.length()!=0){
                    if(startTime.length()!=0&&endTime.length()!=0){
                        if(location.length()!=0){
                            if(content.length()!=0){
                                //모든 조건이 충족되면 upload()실행
                                change_image_update();
                            }else{
                                Toast.makeText(getApplicationContext(),"내용을 적어주세요",Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(getApplicationContext(),"장소를 선택하세요",Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(),"시간을 선택하세요",Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),"기간을 선택하세요",Toast.LENGTH_LONG).show();
                }
            }else{
                //수정 페이지의경우 이미지를안바꿀수있다
                //기존 코드에는 이미지를 선택해서 uri에 값이들어있는 경우이지만 이미지를 안바꿀경우 uri는 null 이다
                // 이럴 경우 서버로부터 받아온 이미지값은 update에 포함하지않는다 .
                not_image_change_update();

            }
        }else{

        }
    }

    //이미지를 변경한 경우 기존 write에 seq만 추가하여 동일한 코드로 업데이트를 한다
    void change_image_update(){
        UploadService upload= MyRetrofit2.getRetrofit2().create(UploadService.class);
        mProgressBar.setVisibility(View.VISIBLE);
        File file= new File(getPath(uri));
        RequestBody requestFile=RequestBody.create(MediaType.parse("image/jpeg"),file);
        MultipartBody.Part body=MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        RequestBody request = RequestBody.create(MediaType.parse("multipart/form-data"), "change_image_update_volunteer_recruitment");
        RequestBody upload_seq = RequestBody.create(MediaType.parse("multipart/form-data"), seq);
        RequestBody upload_id = RequestBody.create(MediaType.parse("multipart/form-data"), id);
        RequestBody upload_subject = RequestBody.create(MediaType.parse("multipart/form-data"), subject.getText().toString());
        RequestBody upload_startDay = RequestBody.create(MediaType.parse("multipart/form-data"), startDate.getText().toString());
        RequestBody upload_endDay = RequestBody.create(MediaType.parse("multipart/form-data"), endDate.getText().toString());
        RequestBody upload_startTime = RequestBody.create(MediaType.parse("multipart/form-data"), startTime.getText().toString());
        RequestBody upload_endTime = RequestBody.create(MediaType.parse("multipart/form-data"), endTime.getText().toString());
        RequestBody upload_location = RequestBody.create(MediaType.parse("multipart/form-data"), location.getText().toString());
        RequestBody upload_content = RequestBody.create(MediaType.parse("multipart/form-data"), content.getText().toString());

        Call<responseResult> call = upload.modified_volunteer_recruitment(body,request,upload_seq,upload_id,upload_subject,upload_startDay,upload_endDay,upload_startTime,upload_endTime,upload_location,upload_content);
        call.enqueue(new Callback<responseResult>() {
            @Override
            public void onResponse(Call<responseResult> call, Response<responseResult> response) {
                mProgressBar.setVisibility(View.GONE);
                responseResult repo=response.body();
                if (repo.getResult().equals("ok")){
                    Toast.makeText(getApplicationContext(),"글 등록 성공",Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"글 등록 실패",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<responseResult> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),t.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });
    }

    //이미지를 변경하지 않은 경우 다른 방식으로 업로드한다.
    void not_image_change_update(){
        Map update=new HashMap();
        update.put("request","not_change_image_update_volunteer_recruitment");
        update.put("seq",seq);
        update.put("subject",subject.getText().toString());
        update.put("startDate",startDate.getText().toString());
        update.put("endDate",endDate.getText().toString());
        update.put("startTime",startTime.getText().toString());
        update.put("endTime",endTime.getText().toString());
        update.put("location",location.getText().toString());
        update.put("content",content.getText().toString());

        UploadService retrofit = MyRetrofit2.getRetrofit2().create(UploadService.class);
        Call<responseResult>call = retrofit.not_image_modified_volunteer_recruitment(update);
        call.enqueue(new Callback<responseResult>() {
            @Override
            public void onResponse(Call<responseResult> call, Response<responseResult> response) {
                mProgressBar.setVisibility(View.GONE);
                responseResult repo=response.body();
                if (repo.getResult().equals("ok")){
                    Toast.makeText(getApplicationContext(),"글 등록 성공",Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"글 등록 실패",Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<responseResult> call, Throwable t) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getApplicationContext(),t.getMessage().toString(),Toast.LENGTH_LONG).show();
            }
        });

    }



    //갤러리에서 이미지 불러와서 bitmap 에 넣기
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == Album_CODE) {
//            앨범에서 가져온 이미지 관련 코드
            if (resultCode == RESULT_OK) {
//                불러온 이미지 담는 Uri
                uri = data.getData();

                try {
//                   앨범에서 가져온 이미지를 bitmap으로 변환하여 imageview에 표시한다.
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);

//                    이미지 방향 검사하여 적용
                    getRotate(bitmap);
//                    imageview.setImageBitmap(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소 "+RESULT_CANCELED, Toast.LENGTH_LONG).show();
            }

        }else if(requestCode==Map_CODE){
            if (resultCode == RESULT_OK) {

                //다음 지도에서 불러온 주소 가져오기
                String get_address=data.getStringExtra("result");
                location.setText(get_address);
            }
        }
    }
    //불어온 이미지 정방향 적용하기
    private void getRotate(Bitmap b){
        float rotation = rotationForImage(getApplicationContext(), uri);
        if(rotation!=0){
            //New rotation matrix
            Matrix matrix = new Matrix();
            matrix.preRotate(rotation);
            profile.setImageBitmap(Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true));
            imageviewCheck=true;
        } else {
            //No need to rotate
            profile.setImageBitmap(b);
            imageviewCheck=true;
        }
    }
    //불어온 이미지 정방향 체크하기
    public  float rotationForImage(Context context, Uri uri) {
        try{
            if (uri.getScheme().equals("content")) {

                //From the media gallery
                String[] projection = { MediaStore.Images.ImageColumns.ORIENTATION };
                Cursor c = context.getContentResolver().query(uri, projection, null, null, null);
                if (c.moveToFirst()) {
                    return c.getInt(0);
                }
            } else if (uri.getScheme().equals("file")) {
                //From a file saved by the camera
                ExifInterface exif = new ExifInterface(uri.getPath());
                int rotation = (int) exifOrientationToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL));
                return rotation;
            }
            return 0;

        } catch (IOException e) {
            Log.e("Test", "Error checking exif", e);
            return 0;
        }
    }
    //불어온 이미지 content형태인지 file형태인지 확인하기
    private  float exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
    //이미지 업로드할때 uri를  절대경로로 변환하기
    private String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index =cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

}
package com.login.donation;

import android.app.DatePickerDialog;
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
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.login.donation.Object.campaign_add_list_member;
import com.login.donation.Object.campaign_write;
import com.login.donation.Object.responseResult;
import com.login.donation.adapter.campaign_writeAdapter;
import com.login.donation.retrofit.MyRetrofit2;
import com.login.donation.retrofit.UploadService;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

//이페이지는 기부단체가 봉사활동 시간 기부 모금을 개설할때 사용된다
//기부단체만 이페이지를 열수 있으며,제목,이미지,목표 시간 , 기부금액 ,기간 , 기부자 , 나눔 수혜자 , 내용을 적을 수 있다
// 기부자를 선택할때 수혜자가 기부단체를 선택하듯이 리사이클러뷰 해당 페이지를 열어주고 , 선택한대상자를 가져온다
//나머지는 다른 게시판과 다름 없음


public class write_time_campaignActivity extends AppCompatActivity {
    private  final int Album_CODE = 1;
    private  final int getDonation_id=3;
    int setYear,setMonth,setDay;
    Uri uri;
    String id;
    SharedPreferences pref;
    EditText subject,mission_time,mission_money,content;
    TextView start_Date,end_Date,donation;
    ImageView profile,add_beneficiary;
    Button proposal,cancle;
    RecyclerView find_beneficiary;
    boolean imageviewCheck; //이미지를 선택했는지 하지않앗는지 체크
    ArrayList<campaign_write> list;
    campaign_writeAdapter adapter;

    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_time_campaign);

        object_initiate();
        getShared();
        touch_initiate();

    }

    void object_initiate(){
        pref=getSharedPreferences("auto_login",MODE_PRIVATE);
        subject=findViewById(R.id.subject);
        profile=findViewById(R.id.profile);
        mission_money=findViewById(R.id.mission_money);
        mission_time=findViewById(R.id.mission_time);
        start_Date=findViewById(R.id.startDate);
        end_Date=findViewById(R.id.endDate);
        donation=findViewById(R.id.donation);
        content=findViewById(R.id.content);
        proposal=findViewById(R.id.proposal);
        cancle=findViewById(R.id.cancle);
        add_beneficiary=findViewById(R.id.add);
        find_beneficiary=findViewById(R.id.recyclerview123);
        mProgressBar = findViewById(R.id.progress);

        list = new ArrayList<>();
        adapter = new campaign_writeAdapter(list);
        find_beneficiary.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL
                ,false));
        find_beneficiary.setHasFixedSize(true);

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
        find_beneficiary.setAdapter(adapter);
    }
    void getShared(){
        id=pref.getString("id","");
    }
    void touch_initiate(){
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //갤러리 인텐트
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, Album_CODE);
            }
        });
        donation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //기부자 리사이클러뷰 startActivityForResult
                Intent intent=new Intent(getApplicationContext(),write_time_campaign_signupActivity.class);
                startActivityForResult(intent,getDonation_id);
            }
        });
        start_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //시작일 설정
                startday();
            }
        });
        end_Date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //시작일 설정
                startday();
            }
        });
        add_beneficiary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //수혜자 다이얼로그 띄움
                add_share_listDialog dialog = new add_share_listDialog(write_time_campaignActivity.this,id);
                list.clear();
                dialog.setDialogListener(new add_share_listDialog.CustomDialogListener() {
                    @Override
                    public void ok(ArrayList<campaign_add_list_member> tmp_list) {
                        for(int i=0;i<tmp_list.size();i++){
                            if(tmp_list.get(i).isCheck()==true){
                                campaign_write tmp=new campaign_write(tmp_list.get(i).getMemberid(),tmp_list.get(i).getImagepath());
                                list.add(tmp);
                            }
                        }
                        adapter.notifyDataSetChanged();

                    }


                });
                dialog.show();
            }
        });
        proposal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                check_upload();
            }
        });
        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


    //이미지 로직
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

        }else  if(requestCode == getDonation_id) {
            if (resultCode == RESULT_OK) {
                donation.setText(data.getStringExtra("id"));
            }else{

            }
        }
    }
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


    //수혜자 다이얼로그

    //시작일 종료일 설정
    void startday(){
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
                write_time_campaignActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        setYear=year;
                        setMonth=month;
                        setDay=dayOfMonth;

                        start_Date.setText(year + "년" + (month+1) + "월" + dayOfMonth + "일");

                        endday(setYear,setMonth,setDay);
                    }
                },

                pickedDate.get(Calendar.YEAR),
                pickedDate.get(Calendar.MONTH),
                pickedDate.get(Calendar.DATE)
        );
        datePickerDialog.getDatePicker().setMinDate(pickedDate.getTime().getTime());
        datePickerDialog.show();
    }

    void endday(int year,int month,int dayOfMonth){
        Calendar pickedDate = Calendar.getInstance();


        //달력 캘린더 열었을때 지정할 날자
        // 캘린더 함수는 0부터 시작을 함으로 1월은 0 이 들어가야한다.  현재 9월임으로 9를 넣으면 10월부터 선택할수잇음 ( 그래서 -1 해줘야함 )
        //실제로 값을 가져올때에도 9월1일을 선택하면 8을 반환 함으로 +1 해서 처리해야한다
        pickedDate.set(year,month,dayOfMonth);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                write_time_campaignActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        end_Date.setText(year + "년" + (month+1) + "월" + dayOfMonth + "일");
                    }
                },
                pickedDate.get(Calendar.YEAR),
                pickedDate.get(Calendar.MONTH),
                pickedDate.get(Calendar.DATE)
        );
        datePickerDialog.getDatePicker().setMinDate(pickedDate.getTime().getTime());
        datePickerDialog.show();
    }

    //업로드 로직
    void check_upload(){

        //목표 시간 , 기부금 정규화 검사
        boolean tmp_time =match_type(mission_time.getText().toString());
        boolean tmp_money =match_type(mission_money.getText().toString());

        if(subject.getText().length()!=0){
            if(imageviewCheck){
                if(tmp_money){
                    if(tmp_time){
                        if(start_Date.getText().length()!=0){
                            if(end_Date.getText().length()!=0){
                                if(donation.getText().length()!=0){
                                    if(list.size()!=0){
                                        if(content.getText().length()!=0){
                                            upload();
                                        }else{
                                            Toast.makeText(this, "내용을 입력주세요 ", Toast.LENGTH_LONG).show();
                                        }
                                    }else{
                                        Toast.makeText(this, "나눔대상자를 선택해주세요 ", Toast.LENGTH_LONG).show();
                                    }
                                }else{
                                    Toast.makeText(this, "기부자를 선택해주세요 ", Toast.LENGTH_LONG).show();
                                }
                            }else{
                                Toast.makeText(this, "종료 시간을 입력주세요 ", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toast.makeText(this, "시작 시간을 입력주세요 ", Toast.LENGTH_LONG).show();
                        }
                    }else{
                        Toast.makeText(this, "목표시간을 입력주세요 ", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(this, "금액을 입력주세요 ", Toast.LENGTH_LONG).show();
                }
            }else{
                Toast.makeText(this, "이미지를 선택해 주세요  ", Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(this, "제목을 입력주세요 ", Toast.LENGTH_LONG).show();
        }


    }

    boolean match_type(String s){

        Log.d("Test111", " s : " +s);
        String regExp="^[0-9]*$";
        String tmp_money=s;

        if(s.length()!=0){
            if(tmp_money.matches(regExp)){
                int int_money =Integer.parseInt(tmp_money);

                    Log.d("test111","int_money : " + int_money);
                    return true;

            }else{
                Log.d("test111","조건 false");
                return false;
            }
        }else{
            Log.d("test111","조건 false");
            return false;
        }

    }

    void upload(){

        String share_list="";

        for(int i=0;i<list.size();i++){

            //나눔 대상자 선택시 "&*" 넣어서  수혜자를 구분한다
            //ex) admin&*user1&*korea&*ansgyqja
            // 마지막 유저는 구분자 "&*" 가 필요없음으로 조건문을 걸어 아이디만 추가한다
            if(i+1==list.size()){
                share_list+=list.get(i).getAddId();
            }else{
                share_list+=list.get(i).getAddId() +"&*";
            }

        }

        UploadService upload= MyRetrofit2.getRetrofit2().create(UploadService.class);

        File file= new File(getPath(uri));
        RequestBody requestFile=RequestBody.create(MediaType.parse("image/jpeg"),file);
        MultipartBody.Part body=MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        RequestBody request = RequestBody.create(MediaType.parse("multipart/form-data"), "time_campaign_upload");
        RequestBody upload_id = RequestBody.create(MediaType.parse("multipart/form-data"), id);
        RequestBody upload_subject = RequestBody.create(MediaType.parse("multipart/form-data"), subject.getText().toString());
        RequestBody upload_money = RequestBody.create(MediaType.parse("multipart/form-data"), mission_money.getText().toString());
        RequestBody upload_time = RequestBody.create(MediaType.parse("multipart/form-data"), mission_time.getText().toString());
        RequestBody upload_donation = RequestBody.create(MediaType.parse("multipart/form-data"), donation.getText().toString());
        RequestBody upload_share_list = RequestBody.create(MediaType.parse("multipart/form-data"), share_list);
        RequestBody upload_startDay = RequestBody.create(MediaType.parse("multipart/form-data"), start_Date.getText().toString());
        RequestBody upload_endDay = RequestBody.create(MediaType.parse("multipart/form-data"), end_Date.getText().toString());
        RequestBody upload_content = RequestBody.create(MediaType.parse("multipart/form-data"), content.getText().toString());
        Call<responseResult> call = upload.time_campaign_upload(body,request,upload_id,upload_subject,upload_money,upload_time,upload_donation,upload_share_list,upload_startDay,upload_endDay,upload_content);

        // 프로세스 바 실행
        mProgressBar.setVisibility(View.VISIBLE);
        call.enqueue(new Callback<responseResult>() {
            @Override
            public void onResponse(Call<responseResult> call, Response<responseResult> response) {
                responseResult repo = response.body();
                //프로세스 바 종료

                mProgressBar.setVisibility(View.GONE);
                if(repo.getResult().equals("ok")){


                    Toast.makeText(getApplicationContext(),"글 등록 성공",Toast.LENGTH_LONG).show();
                    Intent intent=new Intent(getApplicationContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                }else{
                    proposal.setEnabled(true);
                    Toast.makeText(getApplicationContext(),repo.getResult(),Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<responseResult> call, Throwable t) {
                //프로세스 바 종료
                mProgressBar.setVisibility(View.GONE);
                proposal.setEnabled(true);
            }
        });
    }



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

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

/*
이 페이지는 기부 단체에서만 글을 쓸수 있는 페이지이다.

이페이지에서 작성된 글을 서버의 저장 할 수 있다.
서버로 전달할 데이터는 request, subject (20자 )  , id , image , 시작일 ,종료일 , 내용(400자) , 나눔 대상 이다
이 중 request, subject (20자 )  , id , image , 시작일 ,종료일 , 내용(400자) 은 campaign 테이블에 저장하고
나눔 대상은 campaign 의 seq 와 함께 campaign_beneficiary  테이블에 저장 될 것이다.

역활
1. 제목 , 이미지 ,기간 설정 ( 시작 날을 지정하면 종료시점은 시작날 이후부터 가능하다 ) ,나눔 대상 , 내용을 쓸수있다
2. 나눔 대상을 선택할 때 add_share_listDialog에서 체크해서 가져올 수 있다.
3. 가져온 나눔 대상에서 추가로 나눔 대상을 선택 할 경우 기존에 추가한 데이터는 삭제된다.


메소드

object_initiate();  ==> 이 클래스에서 사용되는  객체를 생성한다
 touch_initiate() ==> startDate,endDate,imageview,add , submit ,cancle 을 클릭할 수 있다
    startDate ,endDate  : 기간 설정에 시작일이나 종료일 아무거나 눌러도 startday()가 시작된다
        startday() : 다이얼로그창으로 띄워 기간을 날짜를 선택할 수 있다. 시작시 현재 날자를 받아와 현재 보다 이전 날자는 선택할 수 없다.
        endday() : startday에서 확인 버튼을 누를 경우 endday()가 시작되며 startday()에서 선택한 날짜 이후만 선택 가능하다

    imageview : 이미지등록을 할수잇다. startforactivityresult를 실행하여 onResultActivity에서 값을 받는다.
        onResultActivity() :  선택한 이미지를 intent로 받아 Uri 로 변형한다. 변형한 데이터를 다시 Bitmap 타입으로 변형하고 getRotate을 호출하여 각도를 계산한다
            getRotate() :  각로들 계산하기위하여 rotationForImage 를 호출하고 계산된 각도를 가지고 이미지를 회전시켜 imageview에 표시한다
                rotationForImage() : 이미지이름을 content 와 file로 구분한다 content의 경우 직접 각도를 구하고 , file일 경우 exifOrientationToDegrees() 을 호출한다
                    exifOrientationToDegrees() :  각도 비교 하여 리턴한다.


    add : 나눔 대상을 추가하기 위하여 add_share_listDialog 를 실행시킨다. 실행시키기전 미리 만들어놓은 list를 초기화한다.
        setDialogListener() : add_share_listDialog 에서 확인 버튼을 누르면 해당 메소드가 콜백을 받는다. 콜백 받는 데이터는 add_share_listDialog의 리스트에서 체크한 아이템들이다


    submit : subject , image , startDate ,endDate ,나눔 대상 ,content 를 모두 입력해야지만 upload()를 통해 서버로 전송 할 수 있다
        upload(): image, request ,id , subject , 기간 content , 나눔 대상을 서버에 등록한다
            campaign table에는 image , id ,subject , 기간 content 가 저장된다
            campaign_share_list 에는 campaign의 seq를 조회하여 가져온 seq와 나눔 대상이 저장된다

    cancle : 글작성을 취소하기 위하여 finish() 한다.


리사이클러뷰

메인 :write_CampaignActivity

어뎁터 : campaign_writeAdapter

Object : campaign_write

activity : activity_write_campaign

item : item_write_campaign

 */



public class write_campaignAcivity extends AppCompatActivity {

    SharedPreferences pref;
    String id;
    private TextView startDate,endDate;
    private ImageView imageview,add;
    private RecyclerView recyclerview;
    private EditText content,subject;
    private Button submit,cancle;
    Uri uri;
    private ProgressBar mProgressBar;
    private  final int Album_CODE = 1;
    int setYear,setMonth,setDay;
    ArrayList<campaign_write> list;
    campaign_writeAdapter adapter;
    boolean imageviewCheck;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_campaign);

        initiate();
        touch_initiate();




        // 가로 리사이클러뷰를 선언해주는 부분
        recyclerview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL
                ,false));
        recyclerview.setHasFixedSize(true);

        // 리사이클러뷰에 SimpleTextAdapter 객체 지정.
         adapter = new campaign_writeAdapter(list);
        recyclerview.setAdapter(adapter);
    }

    void initiate(){
        startDate=findViewById(R.id.startDate);
        endDate=findViewById(R.id.endDate);
        imageview=findViewById(R.id.profile);
        add=findViewById(R.id.add);
        subject=findViewById(R.id.subject);
        content=findViewById(R.id.content);
        submit=findViewById(R.id.submit);
        cancle=findViewById(R.id.cancle);
        mProgressBar = findViewById(R.id.progress);
        recyclerview = findViewById(R.id.recyclerview123);
        list = new ArrayList<>();
        pref = getSharedPreferences("auto_login", MODE_PRIVATE);
        id = pref.getString("id", "");


    }

    void touch_initiate() {

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startday();
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startday();
            }
        });


        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_PICK);
                startActivityForResult(intent, Album_CODE);
            }
        });



        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                add_share_listDialog dialog = new add_share_listDialog(write_campaignAcivity.this,id);
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

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(subject.getText().toString().length()==0 ){
                    Toast.makeText(getApplicationContext(),"제목을 입력하세요",Toast.LENGTH_LONG).show();
                }else{
                    if(!imageviewCheck){
                        Toast.makeText(getApplicationContext(),"이미지를 등록하세요",Toast.LENGTH_LONG).show();
                    } else{
                        if(startDate.getText().toString().length()==0 ||endDate.getText().toString().length()==0 ){
                            Toast.makeText(getApplicationContext(),"반드시 기간을 선택하십시요",Toast.LENGTH_LONG).show();
                        }else{
                            if(list.size()==0){
                                Toast.makeText(getApplicationContext(),"나눔 대상을 선택하세요",Toast.LENGTH_LONG).show();
                            }else{
                                if(content.getText().toString().length()==0){
                                    Toast.makeText(getApplicationContext(),"내용을 추가해주세요",Toast.LENGTH_LONG).show();
                                }else{
                                    submit.setEnabled(false);
                                    upload();
                                }
                            }
                        }
                    }
                }





            }
        });


        cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }


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
                write_campaignAcivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                        setYear=year;
                        setMonth=month;
                        setDay=dayOfMonth;

                        startDate.setText(year + "년" + (month+1) + "월" + dayOfMonth + "일");

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
                write_campaignAcivity.this,
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

        }
    }

    private void getRotate(Bitmap b){
        float rotation = rotationForImage(getApplicationContext(), uri);
        if(rotation!=0){
            //New rotation matrix
            Matrix matrix = new Matrix();
            matrix.preRotate(rotation);
            imageview.setImageBitmap(Bitmap.createBitmap(b, 0, 0, b.getWidth(), b.getHeight(), matrix, true));
            imageviewCheck=true;
        } else {
            //No need to rotate
            imageview.setImageBitmap(b);
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
        RequestBody request = RequestBody.create(MediaType.parse("multipart/form-data"), "campaign_upload");
        RequestBody upload_id = RequestBody.create(MediaType.parse("multipart/form-data"), id);
        RequestBody upload_subject = RequestBody.create(MediaType.parse("multipart/form-data"), subject.getText().toString());
        RequestBody upload_share_list = RequestBody.create(MediaType.parse("multipart/form-data"), share_list);
        RequestBody upload_startDay = RequestBody.create(MediaType.parse("multipart/form-data"), startDate.getText().toString());
        RequestBody upload_endDay = RequestBody.create(MediaType.parse("multipart/form-data"), endDate.getText().toString());
        RequestBody upload_content = RequestBody.create(MediaType.parse("multipart/form-data"), content.getText().toString());
        Call<responseResult> call = upload.campaign_upload(body,request,upload_id,upload_subject,upload_share_list,upload_startDay,upload_endDay,upload_content);

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
                    submit.setEnabled(true);
                    Toast.makeText(getApplicationContext(),repo.getResult(),Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onFailure(Call<responseResult> call, Throwable t) {
                //프로세스 바 종료
                mProgressBar.setVisibility(View.GONE);
                submit.setEnabled(true);
            }
        });
    }
}

package com.login.donation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.login.donation.Object.responseResult;
import com.login.donation.retrofit.MyRetrofit2;
import com.login.donation.retrofit.UploadService;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/*
로그인 후 이미지 프로필을 등록하는 액티비티
로그인 하였을때 서버에 저장된 이미지 경로가를 받아 쉐어드에 저장한다
 저장한 imagePath 경로가 default.jpg 일 경우 이 페이지에서 이미지를 등록하도록 요청한다.
 이미 이미지를 등록한 적이 있으면 해당 페이지는 바로 finish() 된다

 역활
 1. 이미지를 등록하게 한다.
 2. 쉐어드에 저장된 imagePath 값이 default.jpg 가 아닌 경우 바로 메인 액티비티로 이동한다


메소드
object_initiate();  ==> 이 클래스에서 사용되는  객체를 생성한다
check_auto_login(); ==>  액티비티 화면에 View들이 표시되기전 쉐어드에 저장된 imagePath 값이 defualt.jpg 가 아닐 경우 다음 페이지로 이동한다.
touch_initiate() ==>  imageview,okBtn 을 누를경우 동작에 관련된 메소드이다
    - imageview : 다이얼 로그를 띄운다
        - dialogshow() : 클릭을 할 경우 사진첩 과 카메라 두가지 다이얼 로그를 띄운다
            -get_image() : 카메라일 경우 0 번을 , 사진첩일 경우 1번을 받아 startActivityForResult를 실행한다
                -카메라일경우 : 진행중
                - 사진첩 일 경우 : Uri 를 bitmap으로 변환하고 getRotate() 를 통해 사진 각도를 계산한다
                    -getRotate() : rotationForImage() 을 호출 하여 받은 각도만큼 이미지를 회전시켜 imageview에 표시한다
                        -rotationForImage() : 해당 파일의 경로가 file인지 content인지 구분한 후 해당 파일의 각도를 계산한다.
                                               content일 경우 각도를 리턴한다.
                                               만약 파일 경로가 file로 시작하면 exifOrientationToDegrees에 전달한다
                            - exifOrientationToDegrees() : file 자체에 저장된 각도를 불러온다

    - okBtn : send() 메소를 실행한다
        - send() : 먼저 getPath()메소드를 사용하여 파일의 절대경로를 가져온 후 파일을 저장한다
                    Retrofit2 객체를 POST 메소드를 사용하고 multipart 타입으로 만든 후 file (이미지 ) , id , request 정로를 업로드 한다
 */




public class profileActivity extends AppCompatActivity {


    SharedPreferences pref;
    SharedPreferences.Editor editor;

    ImageView imageview;
    Button okBtn;
    private  final int Album_CODE = 1;
    private  final int Camera_CODE = 0;
    private  final int Crop_CODE=2;
    Uri uri; //이미지 받아올 uri
    private ProgressBar mProgressBar;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        pref = getSharedPreferences("auto_login", MODE_PRIVATE);
        editor = pref.edit();
        object_initiate();
        check_auto_login();
        touch_initiate();

    }



    private void object_initiate(){
        imageview=findViewById(R.id.profile_img);
        okBtn=findViewById(R.id.okBtn);
        mProgressBar = findViewById(R.id.progress);
    }
    void check_auto_login(){
        String imagePath = pref.getString("imagePath", "");
        if(!imagePath.equals("default.jpg")){
            Intent intent =new Intent(getApplicationContext(),MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
    private void touch_initiate(){
        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialogshow();

            }
        });
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    send();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    void send() throws IOException {


//        전달할 이미지 파일을 File 형태로 만든다
            File file= new File(getPath(uri));
            String loginId = pref.getString("id", "");


            UploadService uploadImage= MyRetrofit2.getRetrofit2().create(UploadService.class);

            // profile_upload 는 multipart 타입이고 Post메소드를 사용한다
            // 첨부하는 데이터는 이미지 , request , id 세가지 값을 전달한다
            // 이미지의 경우 아래의 규칙( ReqeustBody , MultipartBody를 반드시 지켜야한다
            // 이미지 전송을 하게되면 프로그레스 바가 실행되고 서버로부터 응답이 올경우 사라진다.
            // 이미지의 키는 "image" , request의 키는 "request" value는 "profileImage", 아이디의 키는 "id" 형태로 전달된다.

            RequestBody requestFile=RequestBody.create(MediaType.parse("image/jpeg"),file);
            RequestBody request = RequestBody.create(MediaType.parse("multipart/form-data"), "profileImage");
            RequestBody id = RequestBody.create(MediaType.parse("multipart/form-data"), loginId);
            MultipartBody.Part body=MultipartBody.Part.createFormData("image", file.getName(), requestFile);

            Call<responseResult> call = uploadImage.profile_upload(body,request,id);
            mProgressBar.setVisibility(View.VISIBLE);


//         레트로핏 실행후 응답값을 queue에 저장하여 값을 확인할수 있다.
            call.enqueue(new Callback<responseResult>() {
                @Override
                public void onResponse(Call<responseResult> call, Response<responseResult> response) {
                    responseResult repo=response.body();
                    mProgressBar.setVisibility(View.GONE);
                    String[] tmp_array=repo.getResult().split(":");
                    if(tmp_array[0].equals("ok")){
                        editor.putString("imagePath", tmp_array[1]);
                        editor.commit();
                        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                        startActivity(intent);
                        finish();
                        Log.d("test123","프로필 이미지 업로드 성공 " +response.body().toString());
                    }else{
                        Toast.makeText(getApplicationContext(),"프로필 업로드 실패 ",Toast.LENGTH_LONG).show();
                    }

//
                }

                @Override
                public void onFailure(Call<responseResult> call, Throwable t) {

                    mProgressBar.setVisibility(View.GONE);
                    Log.d("test123","실패"+ t);
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

    void dialogshow() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final String str[] = {"사진첩","갤러리"};
        builder.setTitle("선택하세요")
                .setNegativeButton("취소",null)
                .setItems(str,
                        new DialogInterface.OnClickListener(){
                    @Override
                            public void onClick(DialogInterface dialog,int n){
//                        선택에 따라 카메라나 앨범 불러오기
                        get_image(n);

                    }
                        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void get_image(int n){
        if(n==0){
//            카메라
//            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//            startActivityForResult(intent,Camera_CODE);
            Toast.makeText(getApplicationContext(),"개발 중인 기능 입니다. ",Toast.LENGTH_LONG).show();
        }else if(n==1){
//            갤러리
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_PICK);
            startActivityForResult(intent, Album_CODE);
        }
    }


    //    버튼누를 경우 startActivityForResult 응답
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


        if(requestCode==Camera_CODE){
            if (resultCode == RESULT_OK) {
                uri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    getRotate(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "카메라 선택 취소 "+RESULT_CANCELED, Toast.LENGTH_LONG).show();
            }
        }
        else if(requestCode == Album_CODE) {
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
        }else if(requestCode==Crop_CODE){
            if (resultCode == RESULT_OK) {

            }else if(resultCode == RESULT_CANCELED){

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
        } else {
            //No need to rotate
            imageview.setImageBitmap(b);
//            imageview.setImageBitmap(BitmapFactory.decodeFile(imageURI, options));
        }
    }



    //    이미지 각도 계산
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

    /** Get rotation in degrees */
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


}

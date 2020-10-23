package com.login.donation;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class make_QRcodeActivity extends AppCompatActivity {

    SharedPreferences pref;
    Button back;
    ImageView barcode_view;
    String barcode;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_qrcode);

        initiate_object();
        initiate_touch();
        make_code();
    }

    void initiate_object(){

        back=findViewById(R.id.back);
        barcode_view=findViewById(R.id.barcode);
        pref = getSharedPreferences("campaign_detail_page_info", 0);
        barcode=pref.getString("barcode","");
    }
    void initiate_touch(){
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              finish();
            }
        });
    }

    void make_code(){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(barcode, BarcodeFormat.QR_CODE,300,300);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            barcode_view.setImageBitmap(bitmap);
//            Intent intent = new Intent(getApplicationContext(), QrActivity.class);
//            intent.putExtra("pic",bitmap);
//             startActivity(intent);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }
}

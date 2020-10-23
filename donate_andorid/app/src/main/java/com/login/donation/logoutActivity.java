package com.login.donation;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class logoutActivity extends AppCompatActivity {


    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Button logout;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout);

        logout=findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pref = getSharedPreferences("auto_login", MODE_PRIVATE);
                editor=pref.edit();
                editor.clear();
                editor.commit();
                finish();
            }
        });
    }
}

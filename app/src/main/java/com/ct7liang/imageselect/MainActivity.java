package com.ct7liang.imageselect;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.ct7liang.pictureselector.SingleSelectImgActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE_IMAGE1 = 99;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btn_open_local_photo).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_open_local_photo:
                //8.0
                Intent i1 = new Intent(MainActivity.this, SingleSelectImgActivity.class);
                startActivityForResult(i1, REQUEST_CODE_IMAGE1);
                break;
        }
    }
}


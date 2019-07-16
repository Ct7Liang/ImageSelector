package com.ct7liang.imageselect;

import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ct7liang.pictureselector.SingleSelectImgActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE_IMAGE1 = 99;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image);

        findViewById(R.id.btn_open_local_photo).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_open_local_photo:
                //8.0
//                Intent i1 = new Intent(MainActivity.this, SingleSelectImgActivity.class);
//                startActivityForResult(i1, REQUEST_CODE_IMAGE1);
//                SingleSelectImgActivity.startImageSelect(MainActivity.this, 4, false, 30);
                SingleSelectImgActivity.startImageSelect(MainActivity.this, 4, true, 30);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 30 && data != null){
            Glide.with(MainActivity.this).load(data.getStringExtra("image")).into(imageView);
        }
    }
}


package com.ct7liang.imageselect;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ct7liang.pictureselector.SingleSelectImgActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public static final int REQUEST_CODE_IMAGE1 = 99;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image);

        RxPermissions rxPermissions = new RxPermissions(this);
        Disposable subscribe = rxPermissions
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            findViewById(R.id.btn_open_local_photo).setOnClickListener(MainActivity.this);
                            findViewById(R.id.btn_open_local_photo_crop).setOnClickListener(MainActivity.this);
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_open_local_photo:
                //8.0
//                Intent i1 = new Intent(MainActivity.this, SingleSelectImgActivity.class);
//                startActivityForResult(i1, REQUEST_CODE_IMAGE1);
//                SingleSelectImgActivity.startImageSelect(MainActivity.this, 4, false, 30);
                SingleSelectImgActivity.startImageSelect(MainActivity.this, 4, 30);
                break;
            case R.id.btn_open_local_photo_crop:
                SingleSelectImgActivity.startImageSelect(MainActivity.this, 4, true, "", 31);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 30 && data != null){
            Glide.with(MainActivity.this).load(data.getStringExtra("image")).into(imageView);
        }
        if (requestCode == 31 && data != null){
            String imgPath = data.getStringExtra("imgPath");
            Glide.with(MainActivity.this).load(imgPath).into(imageView);
//            Log.i("imgSelector", "imgPath: " + imgPath);

//            imageView.setImageBitmap((Bitmap) data.getParcelableExtra("Bitmap"));
//            Glide.with(MainActivity.this).load(data.getStringExtra("image")).into(imageView);
        }
    }
}


package com.ct7liang.imageselect;

import android.Manifest;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.ct7liang.pictureselector.ui.MultipleSelectActivity;
import com.ct7liang.pictureselector.ui.SingleSelectActivity;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;

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
                            findViewById(R.id.btn_single).setOnClickListener(MainActivity.this);
                            findViewById(R.id.btn_single_crop).setOnClickListener(MainActivity.this);
                            findViewById(R.id.btn_multiple).setOnClickListener(MainActivity.this);
                        }
                    }
                });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_single:
                SingleSelectActivity.startImageSelect(MainActivity.this, 4, false, 31);
                break;
            case R.id.btn_single_crop:
                SingleSelectActivity.startImageSelect(MainActivity.this, 4, true, 31);
                break;
            case R.id.btn_multiple:
                MultipleSelectActivity.startImageSelect(MainActivity.this, 4, 9, 31);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 31 && data != null){
            Bundle bundle = data.getExtras();
            if (bundle!=null){
                ArrayList<String> images = bundle.getStringArrayList("images");
                Log.i("imgSelector", images.toString());
            }
        }
    }
}


package com.ct7liang.imageselect;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ct7liang.pictureselector.PictureSelector;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.ArrayList;

import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imageView;
    private RecyclerView recyclerView;
    private Disposable rxPermission;
    private PictureSelector pictureSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.image);
        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));

        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermission = rxPermissions
                .request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) {
                        if (aBoolean) {
                            findViewById(R.id.btn_single).setOnClickListener(MainActivity.this);
                            findViewById(R.id.btn_single_crop).setOnClickListener(MainActivity.this);
                            findViewById(R.id.btn_multiple).setOnClickListener(MainActivity.this);
                            findViewById(R.id.btn_camera).setOnClickListener(MainActivity.this);
                            findViewById(R.id.btn_camera_crop).setOnClickListener(MainActivity.this);
                            pictureSelector = new PictureSelector(MainActivity.this, "com.ct7liang.imageselect", 888);
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (rxPermission!=null){
            rxPermission.dispose();
        }
        if (pictureSelector!=null){
            pictureSelector.onDestroy();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_single:
                pictureSelector.selectPhoto(false);
//                SingleSelectActivity.startImageSelect(MainActivity.this, 4, false, 31);
                break;
            case R.id.btn_single_crop:
                pictureSelector.selectPhoto(true);
//                SingleSelectActivity.startImageSelect(MainActivity.this, 4, true, 31);
                break;
            case R.id.btn_multiple:
//                String trim = edMaxNum.getText().toString().trim();
//                int maxNum = Integer.parseInt(trim);
//                if (maxNum <1|| maxNum >9){
//                    Toast.makeText(this, "最大可选数量最小为1,最大为9", Toast.LENGTH_LONG).show();
//                    return;
//                }
//                String trim1 = edColumnNum.getText().toString().trim();
//                int columnNum = Integer.parseInt(trim1);
//                if (columnNum <2|| columnNum >4){
//                    Toast.makeText(this, "列数最小为2,最大为4", Toast.LENGTH_LONG).show();
//                    return;
//                }
                pictureSelector.selectPhotos(9);
//                MultipleSelectActivity.startImageSelect(MainActivity.this, columnNum, maxNum, 31);
                break;
            case R.id.btn_camera:
                pictureSelector.takePhoto(false);
//                CameraSelectActivity.startCamera(this, false, 31);
                break;
            case R.id.btn_camera_crop:
                pictureSelector.takePhoto(true);
//                CameraSelectActivity.startCamera(this, true, 31);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 888 && data != null){
            Bundle bundle = data.getExtras();
            if (bundle!=null){
                ArrayList<String> images = bundle.getStringArrayList("images");
                Log.i("imgSelector", images.toString());
                if (images.size() == 1){
                    recyclerView.setVisibility(View.GONE);
                    imageView.setVisibility(View.VISIBLE);
                    Glide.with(MainActivity.this).load(images.get(0)).into(imageView);
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    imageView.setVisibility(View.GONE);
                    recyclerView.setAdapter(new ImageAdapter(MainActivity.this, images));
                }
            }
        }

//        if (requestCode == 31 && data != null){
//            Bundle bundle = data.getExtras();
//            if (bundle!=null){
//                ArrayList<String> images = bundle.getStringArrayList("images");
//                Log.i("imgSelector", images.toString());
//                if (images.size() == 1){
//                    recyclerView.setVisibility(View.GONE);
//                    imageView.setVisibility(View.VISIBLE);
//                    Glide.with(MainActivity.this).load(images.get(0)).into(imageView);
//                }else{
//                    recyclerView.setVisibility(View.VISIBLE);
//                    imageView.setVisibility(View.GONE);
//                    recyclerView.setAdapter(new ImageAdapter(MainActivity.this, images));
//                }
//            }
//        }
    }



}
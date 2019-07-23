package com.ct7liang.pictureselector.ui;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;

import com.ct7liang.pictureselector.R;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class CameraSelectActivity extends AppCompatActivity {

    //缓存图片根目录
    private File file;
    //配置文件-是否需要裁剪
    private boolean isCrop;
    //标识
    private String appId;
    //拍照后的照片存储位置
    private File tempCameraFile;
    //拍照后的照片的Uri
    private Uri tempCameraUri;
    //拍照并裁剪之后的照片存储位置
    private File tempCropFile;

    public static void startCamera(Activity activity, boolean isCrop, String appId, int requestCode){
        Intent i = new Intent(activity, CameraSelectActivity.class);
        i.putExtra("isCrop", isCrop);
        i.putExtra("appId", appId);
        activity.startActivityForResult(i, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_select);

        isCrop = getIntent().getBooleanExtra("isCrop", false);
        appId = getIntent().getStringExtra("appId");

        //生成缓存文件夹
        file = new File(Environment.getExternalStorageDirectory(), "/Ct7liang/img_select");
        if (!file.exists()){
            //如果不存在则直接创建文件夹
            file.mkdirs();
        }

        //用于保存调用相机拍照后所生成的文件
        tempCameraFile = new File(file.getPath(), System.currentTimeMillis() + "_camera.jpg");
        try {
            tempCameraFile.createNewFile();
            //跳转到调用系统相机
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //判断版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {   //如果在Android7.0以上,使用FileProvider获取Uri
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                tempCameraUri = FileProvider.getUriForFile(getApplicationContext(), appId, tempCameraFile);
            } else {    //否则使用Uri.fromFile(file)方法获取Uri
                tempCameraUri = Uri.fromFile(tempCameraFile);
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, tempCameraUri);
            startActivityForResult(intent, 48);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //相机拍照返回
        if (requestCode == 48){
            //判断是否需要裁剪
            if (isCrop){
                //需要裁剪
                try {
                    //设置裁剪之后的图片的保存路径
                    tempCropFile = new File(file, System.currentTimeMillis() + "_camera_crop.jpg");
                    tempCropFile.createNewFile();

                    Intent intent = new Intent("com.android.camera.action.CROP");
                    intent.setDataAndType(tempCameraUri, "image/*");
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    intent.putExtra("crop", "true");
                    intent.putExtra("scale", "true");
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("outputX", 1080);
                    intent.putExtra("outputY", 1080);
                    intent.putExtra("return-data", false);

                    //获取裁剪之后的图片的Uri
                    Uri uriCrop = Uri.fromFile(tempCropFile);
                    //在设置裁剪要保存的 intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)的时候,这个outUri是要使用Uri.fromFile(file)生成的，而不是使用FileProvider.getUriForFile
                    intent.putExtra(MediaStore.EXTRA_OUTPUT, uriCrop);

                    intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                    intent.putExtra("noFaceDetection", true); // no face detection

                    //加上下面的这两句之后，系统就会把图片给我们拉伸了
                    intent.putExtra("scale", true);
                    intent.putExtra("scaleUpIfNeeded", true);

                    startActivityForResult(intent, 110);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else{
                //不需要裁剪
                ArrayList<String> loads = new ArrayList<>();
                loads.add(tempCameraFile.getAbsolutePath());
                Intent i = new Intent();
                i.putStringArrayListExtra("images", loads);
                setResult(86 ,i);
                finish();
            }
        }
        if (requestCode == 110){
            //裁剪完毕
            ArrayList<String> loads = new ArrayList<>();
            loads.add(tempCropFile.getAbsolutePath());
            Intent i = new Intent();
            i.putStringArrayListExtra("images", loads);
            setResult(86 ,i);
            finish();
        }
    }
}

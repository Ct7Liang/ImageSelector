package com.ct7liang.pictureselector;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.widget.Toast;

import com.ct7liang.pictureselector.ui.CameraSelectActivity;
import com.ct7liang.pictureselector.ui.MultipleSelectActivity;
import com.ct7liang.pictureselector.ui.SingleSelectActivity;

import java.io.File;
import java.util.ArrayList;

public class PictureSelector {

    //请求REQUEST_CODE
    private int requestCode;

    //标识
    private String appId;

    private Activity activity;
    private Fragment fragment;

    //图片选择页面列数
    private int columnNum = 4;

    /**
     * 构造方法
     * @param activity Activity
     * @param requestCode requestCode
     */
    public PictureSelector(Activity activity, @NonNull String appId, int requestCode) {
        if (TextUtils.isEmpty(appId)){
            Toast.makeText(activity, "appId不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        this.activity = activity;
        this.appId = appId;
        this.fragment = null;
        this.requestCode = requestCode;
    }

    /**
     * 构造方法
     * @param fragment Fragment
     * @param requestCode requestCode
     */
    public PictureSelector(Fragment fragment, @NonNull String appId, int requestCode) {
        this.fragment = fragment;
        this.appId = appId;
        this.activity = null;
        this.requestCode = requestCode;
    }

    //设置列数
    public void setColumnNum(int columnNum){
        this.columnNum = columnNum;
    }

    //选择图片(单选)
    public void selectPhoto(boolean isCrop){
        if (activity != null){
            SingleSelectActivity.startImageSelect(activity, columnNum, isCrop, appId, requestCode);
        }else if(fragment != null){
            SingleSelectActivity.startImageSelect(fragment, columnNum, isCrop, appId, requestCode);
        }
    }

    //选择图片(多选)
    public void selectPhotos(int maxNum){
        if (maxNum<1){
            Toast.makeText(activity, "图片选择最大数不能小于1", Toast.LENGTH_SHORT).show();
        }
        if (activity != null){
            MultipleSelectActivity.startImageSelect(activity, columnNum, maxNum, requestCode);
        }else if (fragment != null){
            MultipleSelectActivity.startImageSelect(fragment, columnNum, maxNum, requestCode);
        }
    }

    //拍照
    public void takePhoto(boolean isCrop){
        if (activity != null){
            CameraSelectActivity.startCamera(activity, isCrop, appId, requestCode);
        }else if (fragment != null){
            CameraSelectActivity.startCamera(fragment, isCrop, appId, requestCode);
        }

    }

    /**
     * 删除缓存目录
     */
    public void onDestroy(){
        File cacheDir = new File(Environment.getExternalStorageDirectory(), "/Ct7liang/img_select");
        if (cacheDir.exists()){
            File[] files = cacheDir.listFiles();
            for(File file:files){
                // 路径为文件且不为空则进行删除
                if (file.isFile() && file.exists()) {
                    System.gc();
                    file.delete();
                }
            }
        }
    }

    /**
     * 解析数据, 获取图片绝对地址的集合
     * @param requestCode
     * @param intent
     * @return
     */
    public ArrayList<String> getImages(int requestCode, Intent intent){
        ArrayList<String> images = new ArrayList<>();
        if (requestCode == this.requestCode && intent != null){
            Bundle bundle = intent.getExtras();
            if (bundle!=null){
                images = bundle.getStringArrayList("images");
            }
        }
        return images;
    }
}

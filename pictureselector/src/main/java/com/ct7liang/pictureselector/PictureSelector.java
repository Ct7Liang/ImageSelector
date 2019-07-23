package com.ct7liang.pictureselector;

import android.app.Activity;
import android.os.Environment;
import android.util.Log;

import com.ct7liang.pictureselector.ui.CameraSelectActivity;
import com.ct7liang.pictureselector.ui.MultipleSelectActivity;
import com.ct7liang.pictureselector.ui.SingleSelectActivity;

import java.io.File;

public class PictureSelector {

    //请求REQUEST_CODE
    private int requestCode;

    private Activity activity;

    //图片选择页面列数
    private int columnNum = 4;

    /**
     * 构造方法
     * @param activity Activity
     * @param requestCode requestCode
     */
    public PictureSelector(Activity activity, int requestCode) {
        this.activity = activity;
        this.requestCode = requestCode;
    }

    //设置列数
    public void setColumnNum(int columnNum){
        this.columnNum = columnNum;
    }

    //选择图片(单选)
    public void selectPhoto(boolean isCrop){
        SingleSelectActivity.startImageSelect(activity, columnNum, isCrop, requestCode);
    }

    //选择图片(多选)
    public void selectPhotos(int maxNum){
        MultipleSelectActivity.startImageSelect(activity, columnNum, maxNum, requestCode);
    }

    //拍照
    public void takePhoto(boolean isCrop){
        CameraSelectActivity.startCamera(activity, isCrop, requestCode);
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
}

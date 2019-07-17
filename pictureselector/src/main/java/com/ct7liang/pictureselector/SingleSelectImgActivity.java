package com.ct7liang.pictureselector;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class SingleSelectImgActivity extends AppCompatActivity {

    private List<String> list = new ArrayList<>();
    private List<String> folderNameList = new ArrayList<>();
    private List<List<String>> folderList = new ArrayList<>();
    private int count = 0;
    private ImageViewAdapter imageViewAdapter;

    private PopupWindow popupWindow;

    private TextView title;

    //配置项
    private int column_num = 3;
    private boolean isCrop = false;
    private File file;

    public static void startImageSelect(Activity context, int columnNum, int requestCode){
        Intent i = new Intent(context, SingleSelectImgActivity.class);
        i.putExtra("columnNum", columnNum);
        context.startActivityForResult(i, requestCode);
    }

    public static void startImageSelect(Activity context, int columnNum, boolean isCrop, String newFilePath, int requestCode){
        Intent i = new Intent(context, SingleSelectImgActivity.class);
        i.putExtra("columnNum", columnNum);
        i.putExtra("isCrop", isCrop);
        i.putExtra("newFilePath", newFilePath);
        context.startActivityForResult(i, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_single_select_img);


        file = new File(Environment.getExternalStorageDirectory(), "/Ct7liang/img_select");
        if (!file.exists()){
            file.mkdirs();
        }


        column_num = getIntent().getIntExtra("columnNum", 3);
        isCrop = getIntent().getBooleanExtra("isCrop", false);

        title = findViewById(R.id.title);

        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //展示相册列表
                View view = View.inflate(SingleSelectImgActivity.this, R.layout.popup_window_list, null);
                RecyclerView folderRecyclerView = view.findViewById(R.id.folder_recycler_view);
                folderRecyclerView.setLayoutManager(new LinearLayoutManager(SingleSelectImgActivity.this));
                FolderAdapter folderAdapter = new FolderAdapter(SingleSelectImgActivity.this, folderNameList, folderList, count);
                folderAdapter.setOnFolderItemClickListener(new FolderAdapter.OnFolderItemClick() {
                    @Override
                    public void onItemClick(View v, int position) {
                        if (position == 0){
                            title.setText("全部照片("+count+")");
                            imageViewAdapter.refreshData(list);
                            popupWindow.dismiss();
                        }else{
                            title.setText(folderNameList.get(position-1));
                            imageViewAdapter.refreshData(folderList.get(position-1));
                            popupWindow.dismiss();
                        }
                    }
                });
                folderRecyclerView.setAdapter(folderAdapter);
                popupWindow = new PopupWindow(view, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                popupWindow.showAsDropDown(title, 0, 0);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(SingleSelectImgActivity.this, column_num));
        imageViewAdapter = new ImageViewAdapter(this);
        imageViewAdapter.setOnImageViewItemClick(new ImageViewAdapter.OnImageViewItemClick() {
            @Override
            public void onImageItemClick(View v, int position) {
                String path = imageViewAdapter.imgList.get(position);
                if (isCrop){
                    //需要裁剪
                    try {
                        File tempFile = new File(SingleSelectImgActivity.this.file, System.currentTimeMillis()+"_copy.jpg");

                        tempFile.createNewFile();

                        FileHelper.copy(new File(path), tempFile);

                        Intent intent = new Intent("com.android.camera.action.CROP");
                        Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.ct7liang.imageselect.provider", tempFile);//file即为所要共享的文件的file
                        intent.setDataAndType(uri, "image/*");
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.putExtra("crop", "true");
                        intent.putExtra("aspectX", 1);
                        intent.putExtra("aspectY", 1);
                        intent.putExtra("outputX", 1080);
                        intent.putExtra("outputY", 1080);
                        intent.putExtra("return-data", true);
                        startActivityForResult(intent, 113);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }else{
                    //不需要裁剪, 直接返回图片
                    Intent i = new Intent();
                    i.putExtra("image", path);
                    setResult(96, i);
                    finish();
                }
            }
        });
        recyclerView.setAdapter(imageViewAdapter);

        new Thread(){
            @Override
            public void run() {
                ContentResolver contentResolver = getContentResolver();
                Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
                if (cursor==null){
                    return;
                }
                while (cursor.moveToNext()){
                    String data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    list.add(data);
                    Log.i("pictureSelector", data);
                    count++;
                    String[] split = data.split("/");
                    String folderName = split[split.length - 2];
                    if (!folderNameList.contains(folderName)){
                        folderNameList.add(folderName);
                        ArrayList<String> strings = new ArrayList<>();
                        strings.add(data);
                        folderList.add(strings);
                    }else{
                        int i = folderNameList.indexOf(folderName);
                        folderList.get(i).add(data);
                    }
                }

                cursor.close();
//                Log.i("pictureSelector", "图片读取结束, 共获取到" + count +"张图片");
//                Log.i("pictureSelector", "获取到: " + folderList.size());
//                Log.i("pictureSelector", "获取到: " + folderNameList.size());
//                for (int i = 0; i < folderList.size(); i++) {
//                    Log.i("pictureSelector", "子文件夹: " + folderList.get(i).size());
//                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        title.setText("全部照片("+count +")");
                        imageViewAdapter.refreshData(list);
                    }
                });
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 113 && data!=null){
            Bundle bundle = data.getExtras();
            Bitmap bitmap = bundle.getParcelable("data");
            try {
                File tempFile = new File(SingleSelectImgActivity.this.file, System.currentTimeMillis()+"_copy.jpg");
                tempFile.createNewFile();
                FileHelper.saveBitmap(bitmap, tempFile);

                Intent i = new Intent();
                i.putExtra("imgPath", tempFile.getAbsolutePath());
                setResult(97, i);
                finish();

//                Intent i = new Intent();
//                i.putExtra("Bitmap", bitmap);
//                setResult(97, i);
//                finish();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
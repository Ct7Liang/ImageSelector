package com.ct7liang.pictureselector.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ct7liang.pictureselector.FileHelper;
import com.ct7liang.pictureselector.adapter.FolderAdapter;
import com.ct7liang.pictureselector.ImageBean;
import com.ct7liang.pictureselector.R;
import com.ct7liang.pictureselector.adapter.SingleSelectAdapter;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class SingleSelectActivity extends AppCompatActivity {

    //缓存图片根目录
    private File file;

    //图片列表的列数(默认为3列)
    private int column_num = 3;
    //图片选择完毕之后是否需要裁剪(默认为不需要)
    private boolean isCrop = false;
    //图片选择的最大可选择数(默认为1)
    private int maxNum = 1;

    //标题栏TextView(用于展示图片文件夹名称以及图片数量)
    private TextView tvTitle;

    //用于展示相册列表的弹窗
    private PopupWindow popupWindow;

    //裁剪之后的图片的File对象
    private File tempFileCrop;

    //手机本地图片集合
    private List<ImageBean> list = new ArrayList<>();
    //手机本地相册名称集合
    private List<String> folderNameList = new ArrayList<>();
    //手机相册集合
    private List<List<ImageBean>> folderList = new ArrayList<>();

    //获取到的手机本地的图片的总数
    private int count = 0;

    //图片列表适配器
    private SingleSelectAdapter singleSelectAdapter;

    /**
     * 任务: 子线程中,获取手机图片,并生成数据集合
     */
    private static class LoadImageRunnable implements Runnable{
        private WeakReference<SingleSelectActivity> wActivity;
        LoadImageRunnable(SingleSelectActivity activity) {
            wActivity = new WeakReference<>(activity);
        }
        @Override
        public void run() {
            SingleSelectActivity mActivity = wActivity.get();
            if (mActivity == null){
                return;
            }
            ContentResolver contentResolver = mActivity.getContentResolver();
            Cursor cursor = contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, null, null, null, null);
            if (cursor==null){
                return;
            }
            while (cursor.moveToNext()){
                String data = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                int width = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.WIDTH));
                int height = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media.HEIGHT));
                ImageBean imageBean = new ImageBean(data, width, height);
                mActivity.list.add(imageBean);
                mActivity.count++;
                String[] split = data.split("/");
                String folderName = split[split.length - 2];
                if (!mActivity.folderNameList.contains(folderName)){
                    mActivity.folderNameList.add(folderName);
                    ArrayList<ImageBean> images = new ArrayList<>();
                    images.add(imageBean);
                    mActivity.folderList.add(images);
                }else{
                    int i = mActivity.folderNameList.indexOf(folderName);
                    mActivity.folderList.get(i).add(imageBean);
                }
            }
            cursor.close();
            mActivity.runOnUiThread(new SetUIRunnable(mActivity));
        }
    }

    /**
     * 任务: 主线程中,展示数据
     */
    private static class SetUIRunnable implements Runnable{
        private WeakReference<SingleSelectActivity> wActivity;
        SetUIRunnable(SingleSelectActivity activity) {
            wActivity = new WeakReference<>(activity);
        }
        @Override
        public void run() {
            SingleSelectActivity mActivity = wActivity.get();
            if (mActivity == null){
                return;
            }
            mActivity.tvTitle.setText("全部照片("+mActivity.count +")");
            mActivity.singleSelectAdapter.refreshData(mActivity.list);
        }
    }

    public static void startImageSelect(Activity context, int columnNum, boolean isCrop, int requestCode){
        Intent i = new Intent(context, SingleSelectActivity.class);
        i.putExtra("columnNum", columnNum);
        i.putExtra("isCrop", isCrop);
        i.putExtra("maxNum", 1);
        context.startActivityForResult(i, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_single_select);

        //生成缓存文件夹
        file = new File(Environment.getExternalStorageDirectory(), "/Ct7liang/img_select");
        if (!file.exists()){
            //如果不存在则直接创建文件夹
            file.mkdirs();
        }

        //获取图片显示的列数
        column_num = getIntent().getIntExtra("columnNum", 3);
        //获取是否需要裁剪图片
        isCrop = getIntent().getBooleanExtra("isCrop", false);
        //获取可选择的最大图片数量
        maxNum = getIntent().getIntExtra("maxNum", 1);

        //初始化标题栏TextView
        tvTitle = findViewById(R.id.title);
        //设置标题栏点击事件, 展示相册列表
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //初始化popupwindow的contentView
                View contentView = View.inflate(SingleSelectActivity.this, R.layout.popup_window_list, null);
                RecyclerView folderRecyclerView = contentView.findViewById(R.id.folder_recycler_view);
                folderRecyclerView.setLayoutManager(new LinearLayoutManager(SingleSelectActivity.this));
                FolderAdapter folderAdapter = new FolderAdapter(SingleSelectActivity.this, folderNameList, folderList, count);
                //设置相册目录列表的点击事件
                folderAdapter.setOnFolderItemClickListener(new FolderAdapter.OnFolderItemClick() {
                    @Override
                    public void onItemClick(View v, int position) {
                        if (position == 0){
                            //点击第一行(全部图片)
                            //更新标题栏
                            tvTitle.setText("全部照片("+count+")");
                            //设置数据
                            singleSelectAdapter.refreshData(list);
                            //弹窗消失
                            popupWindow.dismiss();
                        }else{
                            //点击其他相册位置(索引需要减1)
                            //更新标题栏
                            tvTitle.setText(folderNameList.get(position-1));
                            //设置数据
                            singleSelectAdapter.refreshData(folderList.get(position-1));
                            //弹窗消失
                            popupWindow.dismiss();
                        }
                    }
                });
                //设置适配器
                folderRecyclerView.setAdapter(folderAdapter);
                //设置弹窗
                popupWindow = new PopupWindow(contentView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, true);
                //显示弹窗
                popupWindow.showAsDropDown(tvTitle, 0, 0);
            }
        });

        //设置返回按钮点击事件
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //初始化图片列表
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(SingleSelectActivity.this, column_num));
        singleSelectAdapter = new SingleSelectAdapter(this);
        //设置图片列表点击事件
        singleSelectAdapter.setOnImageViewItemClick(new SingleSelectAdapter.OnImageViewItemClick() {
            @Override
            public void onImageItemClick(View v, int position) {
                //返回图片信息或者开始裁剪
                returnOrCrop(position);
            }
        });
        //列表设置适配器
        recyclerView.setAdapter(singleSelectAdapter);

        //开启子线程获取本地图片数据
        new Thread(new LoadImageRunnable(this)).start();
    }

    /**
     * 返回图片信息或者开始裁剪
     */
    private void returnOrCrop(int position) {
        if (isCrop){
            //需要裁剪
            ImageBean imageBean = singleSelectAdapter.imgList.get(position);
            String path = imageBean.load;
            try {
                //设置复制文件的保存路径
                File copyTempFile = new File(SingleSelectActivity.this.file, System.currentTimeMillis()+"_copy.jpg");
                copyTempFile.createNewFile();
                //复制文件
                FileHelper.copy(new File(path), copyTempFile);
                //获取复制文件的Uri
                Uri uri = FileProvider.getUriForFile(getApplicationContext(), "com.ct7liang.imageselect.provider", copyTempFile);

                //设置裁剪之后的图片的保存路径
                tempFileCrop = new File(SingleSelectActivity.this.file, System.currentTimeMillis()+"_copy_crop.jpg");
                tempFileCrop.createNewFile();
                //获取裁剪之后的图片的Uri(这里的Uri不能为)
                //在设置裁剪要保存的 intent.putExtra(MediaStore.EXTRA_OUTPUT, outUri)的时候,这个outUri是要使用Uri.fromFile(file)生成的，而不是使用FileProvider.getUriForFile
                Uri uriCrop = Uri.fromFile(tempFileCrop);

                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(uri, "image/*");
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.putExtra("crop", "true");
                intent.putExtra("scale", "true");
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                int width = imageBean.width;
                int height = imageBean.height;
                int output = width<height?width:height;
                intent.putExtra("outputX", output);
                intent.putExtra("outputY", output);
                intent.putExtra("return-data", false);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, uriCrop);
                intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
                intent.putExtra("noFaceDetection", true); // no face detection
                startActivityForResult(intent, 113);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else{
            //不需要裁剪, 直接返回图片
            ArrayList<String> loads = new ArrayList<>();
            loads.add(singleSelectAdapter.imgList.get(position).load);
            Intent i = new Intent();
            i.putStringArrayListExtra("images", loads);
            setResult(96, i);
            finish();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 113 && data!=null){
            if (tempFileCrop.exists()){
                ArrayList<String> loads = new ArrayList<>();
                loads.add(tempFileCrop.getAbsolutePath());
                Intent i = new Intent();
                i.putExtra("images", loads);
                setResult(97, i);
                finish();
            }
        }
    }
}

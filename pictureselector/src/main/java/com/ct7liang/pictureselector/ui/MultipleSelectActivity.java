package com.ct7liang.pictureselector.ui;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.ct7liang.pictureselector.ImageBean;
import com.ct7liang.pictureselector.R;
import com.ct7liang.pictureselector.adapter.FolderAdapter;
import com.ct7liang.pictureselector.adapter.MultipleSelectAdapter;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MultipleSelectActivity extends AppCompatActivity {

    //缓存图片根目录
    private File file;

    //图片列表的列数(默认为3列)
    private int column_num = 3;

    //图片选择的最大可选择数(默认为1)
    private int maxNum = 1;

    //标题栏TextView(用于展示图片文件夹名称以及图片数量)
    private TextView tvTitle;
    //标题栏右侧提交TextView(用于提交选择的图片和展示图片的已选择数量和总可选数量)
    private TextView tvCommit;

    //用于展示相册列表的弹窗
    private PopupWindow popupWindow;

    //手机本地图片集合
    private List<ImageBean> list = new ArrayList<>();
    //手机本地相册名称集合
    private List<String> folderNameList = new ArrayList<>();
    //手机相册集合
    private List<List<ImageBean>> folderList = new ArrayList<>();
    //当前列表中已选择的图片索引集合
    public static ArrayList<ImageBean> selectPoi = new ArrayList<>();

    //获取到的手机本地的图片的总数
    private int count = 0;

    //图片列表适配器
    private MultipleSelectAdapter imageViewAdapter;

    /**
     * 任务: 子线程中,获取手机图片,并生成数据集合
     */
    private static class LoadImageRunnable implements Runnable{
        private WeakReference<MultipleSelectActivity> wActivity;
        LoadImageRunnable(MultipleSelectActivity activity) {
            wActivity = new WeakReference<>(activity);
        }
        @Override
        public void run() {
            MultipleSelectActivity mActivity = wActivity.get();
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
        private WeakReference<MultipleSelectActivity> wActivity;
        SetUIRunnable(MultipleSelectActivity activity) {
            wActivity = new WeakReference<>(activity);
        }
        @Override
        public void run() {
            MultipleSelectActivity mActivity = wActivity.get();
            if (mActivity == null){
                return;
            }
            mActivity.tvTitle.setText("全部照片("+mActivity.count +")");
            mActivity.imageViewAdapter.refreshData(mActivity.list, mActivity.selectPoi);
        }
    }

    public static void startImageSelect(Activity context, int columnNum, int maxNum, int requestCode){
        Intent i = new Intent(context, MultipleSelectActivity.class);
        i.putExtra("columnNum", columnNum);
        i.putExtra("maxNum", maxNum);
        context.startActivityForResult(i, requestCode);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_single_select_img);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#3A95FF"));
        }

        selectPoi.clear();

        //生成缓存文件夹
        file = new File(Environment.getExternalStorageDirectory(), "/Ct7liang/img_select");
        if (!file.exists()){
            //如果不存在则直接创建文件夹
            file.mkdirs();
        }

        //获取图片显示的列数
        column_num = getIntent().getIntExtra("columnNum", 3);

        //获取可选择的最大图片数量
        maxNum = getIntent().getIntExtra("maxNum", 1);

        //初始化提交TextView
        tvCommit = findViewById(R.id.tv_commit);
        tvCommit.setText("确定(0/"+maxNum+")");
        //设置提交按钮点击事件
        tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPoi.size()==0){
                    //判断是否已经选择有图片,如果没有选择图片直接返回
                    Toast.makeText(MultipleSelectActivity.this, "请选择图片", Toast.LENGTH_SHORT).show();
                    return;
                }
                //选择已有图片,返回图片信息
                returnOrCrop();
            }
        });

        //初始化标题栏TextView
        tvTitle = findViewById(R.id.title);
        //设置标题栏点击事件, 展示相册列表
        tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //初始化popupwindow的contentView
                View contentView = View.inflate(MultipleSelectActivity.this, R.layout.popup_window_list, null);
                RecyclerView folderRecyclerView = contentView.findViewById(R.id.folder_recycler_view);
                folderRecyclerView.setLayoutManager(new LinearLayoutManager(MultipleSelectActivity.this));
                FolderAdapter folderAdapter = new FolderAdapter(MultipleSelectActivity.this, folderNameList, folderList, count);
                //设置相册目录列表的点击事件
                folderAdapter.setOnFolderItemClickListener(new FolderAdapter.OnFolderItemClick() {
                    @Override
                    public void onItemClick(View v, int position) {
                        if (position == 0){
                            //点击第一行(全部图片)
                            //更新标题栏
                            tvTitle.setText("全部照片("+count+")");
                            //设置数据
                            imageViewAdapter.refreshData(list, selectPoi);
                            //弹窗消失
                            popupWindow.dismiss();
                        }else{
                            //点击其他相册位置(索引需要减1)
                            //更新标题栏
                            tvTitle.setText(folderNameList.get(position-1)+"("+folderList.get(position-1).size()+")");
                            //设置数据
                            imageViewAdapter.refreshData(folderList.get(position-1), selectPoi);
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

        //设置预览点击事件
        findViewById(R.id.tv_look).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectPoi.size()!=0){
                    ImageScanActivity.startScan(MultipleSelectActivity.this, 224);
                }
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
        recyclerView.setLayoutManager(new GridLayoutManager(MultipleSelectActivity.this, column_num));
        imageViewAdapter = new MultipleSelectAdapter(this, maxNum);
        //设置图片列表点击事件
        imageViewAdapter.setOnImageViewItemClick(new MultipleSelectAdapter.OnImageViewItemClick() {
            @Override
            public void onImageItemClick(View v, int position) {
                ImageBean imageBean = imageViewAdapter.imgList.get(position);
                if (selectPoi.contains(imageBean)){
                    //若该点击的索引已经存在已选中的索引集合中,则删除
                    selectPoi.remove(imageBean);
                }else if (selectPoi.size()<maxNum){
                    //若该索引不存在已选中的索引集合中,且选中的索引数量还没有达到上限,则添加
                    selectPoi.add(imageBean);
                }else {
                    //若该索引不存在已选中的索引集合中,且选中的索引数量已经达到上限,则提示
                    Toast.makeText(MultipleSelectActivity.this, "图片选择数量已经达到上限", Toast.LENGTH_SHORT).show();
                }
                //更新已选中的图片数量
                tvCommit.setText("确定("+selectPoi.size()+"/"+maxNum+")");
                //更新适配器中的已选中索引集合
                imageViewAdapter.refreshSelectPoi(selectPoi);
            }
        });
        //列表设置适配器
        recyclerView.setAdapter(imageViewAdapter);

        //开启子线程获取本地图片数据
        new Thread(new LoadImageRunnable(this)).start();
    }

    /**
     * 选择有图片,返回图片信息
     */
    private void returnOrCrop() {
        ArrayList<String> loads = new ArrayList<>();
        for (int i = 0; i < selectPoi.size(); i++) {
            loads.add(selectPoi.get(i).load);
        }
        //不需要裁剪, 直接返回图片
        Intent i = new Intent();
        i.putStringArrayListExtra("images", loads);
        setResult(96, i);
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 224 && resultCode == 115){
            tvCommit.setText("确定("+selectPoi.size()+"/"+maxNum+")");
            imageViewAdapter.refreshSelectPoi(selectPoi);
            imageViewAdapter.notifyDataSetChanged();
        }
        if (requestCode == 224 && resultCode == 104){
            returnOrCrop();
        }
    }
}
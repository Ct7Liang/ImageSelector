package com.ct7liang.pictureselector.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ct7liang.pictureselector.R;
import com.ct7liang.pictureselector.adapter.ImageScanAdapter;

public class ImageScanActivity extends AppCompatActivity {

    private ImageView ivImage;
    private ImageScanAdapter adapter;
    //当前选中展示的图片索引
    private int selPoi = 0;

    private View topView;
    private View bottomView;
    private boolean isShow = true;

    public static void startScan(Activity activity, int requestCode){
        Intent i = new Intent(activity, ImageScanActivity.class);
        activity.startActivityForResult(i, requestCode);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //设置无标题栏
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_image_scan);

        topView = findViewById(R.id.top_view);
        bottomView = findViewById(R.id.bottom_view);

        ivImage = findViewById(R.id.iv_image);
        ivImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isShow){
                    topView.setVisibility(View.GONE);
                    bottomView.setVisibility(View.GONE);
                }else{
                    topView.setVisibility(View.VISIBLE);
                    bottomView.setVisibility(View.VISIBLE);
                }
                isShow=!isShow;
            }
        });

        //设置RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        adapter = new ImageScanAdapter(this, MultipleSelectActivity.selectPoi, selPoi);
        //设置RecyclerView条目点击事件
        adapter.setOnImageViewItemClick(new ImageScanAdapter.OnImageViewItemClick() {
            @Override
            public void onImageItemClick(View v, int position) {
                //切换大图展示图片
                Glide.with(ImageScanActivity.this).load(MultipleSelectActivity.selectPoi.get(position).load).into(ivImage);
                //更新选中位置
                selPoi = position;
                //更新适配器中的显示位置
                adapter.setPosition(selPoi);
                //刷新适配器
                adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(adapter);

        //设置第一张图片显示
        Glide.with(ImageScanActivity.this).load(MultipleSelectActivity.selectPoi.get(0).load).asBitmap().into(ivImage);

        //编辑完已选的图片,返回继续选择
        findViewById(R.id.iv_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });

        //点击删除当前图片
        findViewById(R.id.iv_delete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除集合中的数据
                MultipleSelectActivity.selectPoi.remove(selPoi);
                //判断删除的数据是否是最后一条
                if (MultipleSelectActivity.selectPoi.size()>0){
                    //设置点击位置为-1(即当前没有可选)
                    selPoi = 0;
                    //更新适配器
                    adapter.setPosition(0);
                    adapter.notifyDataSetChanged();
                    //更新图片
                    Glide.with(ImageScanActivity.this).load(MultipleSelectActivity.selectPoi.get(0).load).asBitmap().into(ivImage);
                }else{
                    //删除的数据是最后一条
                    back();
                }
            }
        });

        findViewById(R.id.iv_commit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(104);
                finish();
            }
        });

    }

    /**
     * 编辑完已选的图片,返回继续选择
     */
    private void back(){
        setResult(115);
        finish();
    }
}

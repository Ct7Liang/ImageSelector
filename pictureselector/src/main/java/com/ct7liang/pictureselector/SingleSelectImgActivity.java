package com.ct7liang.pictureselector;

import android.content.ContentResolver;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SingleSelectImgActivity extends AppCompatActivity {

    private List<String> list = new ArrayList<>();
    private ListView listView;
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_single_select_img);

        listView = findViewById(R.id.list_view);

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
//                    String name = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME));
                    list.add(data);
                    Log.i("pictureSelector", data);
                    count++;
                }

                Log.i("pictureSelector", "图片读取结束, 共获取到" + count +"张图片");

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        ((TextView)findViewById(R.id.title)).setText(count +"张图片");

                        listView.setAdapter(new ListAdapter());

                    }
                });



            }
        }.start();
    }

    private class ListAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view;
            if (convertView == null){
                view = View.inflate(SingleSelectImgActivity.this, R.layout.item_img_1, null);
            }else {
                view = convertView;
            }
            TextView tv = view.findViewById(R.id.tv);
            tv.setText(list.get(position));
            return view;
        }
    }
}

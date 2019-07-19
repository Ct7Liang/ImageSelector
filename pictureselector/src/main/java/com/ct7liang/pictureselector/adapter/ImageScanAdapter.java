package com.ct7liang.pictureselector.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ct7liang.pictureselector.ImageBean;
import com.ct7liang.pictureselector.R;
import com.ct7liang.pictureselector.WHView;

import java.util.ArrayList;
import java.util.List;

public class ImageScanAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    private int selPoi = -1;
    private Context context;
    public List<ImageBean> imgList;

    public ImageScanAdapter(Context context, ArrayList<ImageBean> imgList, int selPoi){
        this.context = context;
        this.imgList = imgList;
        this.selPoi = selPoi;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setPosition(int position){
        selPoi = position;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ImageViewHolder(layoutInflater.inflate(R.layout.item_img_scan, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ImageViewHolder){
            Log.i("imageSelector", imgList.get(i).load);
            Glide.with(context).load(imgList.get(i).load).asBitmap().into(((ImageViewHolder) viewHolder).imageView);
            if (selPoi == i){
                ((ImageViewHolder) viewHolder).whView.setBackgroundColor(Color.parseColor("#3B9AFF"));
            }else{
                ((ImageViewHolder) viewHolder).whView.setBackgroundColor(Color.parseColor("#00000000"));
            }
        }
    }

    @Override
    public int getItemCount() {
        return imgList.size();
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ViewGroup whView;
        ImageView imageView;
        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            whView = itemView.findViewById(R.id.wh_view);
            imageView = itemView.findViewById(R.id.image);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onImageViewItemClick!=null){
                onImageViewItemClick.onImageItemClick(v, getAdapterPosition());
            }
        }
    }

    private OnImageViewItemClick onImageViewItemClick;
    public interface OnImageViewItemClick{
        void onImageItemClick(View v, int position);
    }
    public void setOnImageViewItemClick(OnImageViewItemClick onImageViewItemClick){
        this.onImageViewItemClick = onImageViewItemClick;
    }
}

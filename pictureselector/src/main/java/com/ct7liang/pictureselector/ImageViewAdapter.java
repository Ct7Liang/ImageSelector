package com.ct7liang.pictureselector;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ImageViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    public List<String> imgList = new ArrayList<>();

    public ImageViewAdapter(Context context){
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
    }

    public void refreshData(List<String> imgList){
        this.imgList = imgList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ImageViewHolder(layoutInflater.inflate(R.layout.item_img_1, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ImageViewHolder){
            Glide.with(context).load(imgList.get(i)).asBitmap().into(((ImageViewHolder) viewHolder).imageView);
        }
    }

    @Override
    public int getItemCount() {
        return imgList.size();
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        WHView whView;
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
    interface OnImageViewItemClick{
        void onImageItemClick(View v, int position);
    }
    public void setOnImageViewItemClick(OnImageViewItemClick onImageViewItemClick){
        this.onImageViewItemClick = onImageViewItemClick;
    }
}

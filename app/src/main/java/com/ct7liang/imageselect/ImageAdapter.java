package com.ct7liang.imageselect;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.ct7liang.pictureselector.WHView;

import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    public List<String> imgList;

    public ImageAdapter(Context context, List<String> imgList){
        this.context = context;
        this.imgList = imgList;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ImageViewHolder(layoutInflater.inflate(R.layout.item_image, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ImageViewHolder){
            if (i < imgList.size()){
                Glide.with(context).load(imgList.get(i)).asBitmap().into(((ImageViewHolder) viewHolder).imageView);
            }else{
                Glide.with(context).load(R.mipmap.img_empty).into(((ImageViewHolder) viewHolder).imageView);
            }
        }
    }

    @Override
    public int getItemCount() {
        return 9;
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

        }
    }
}

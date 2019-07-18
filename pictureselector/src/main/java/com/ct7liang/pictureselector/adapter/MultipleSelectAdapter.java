package com.ct7liang.pictureselector.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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

public class MultipleSelectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    public List<ImageBean> imgList = new ArrayList<>();
    public List<ImageBean> selPoiList = new ArrayList<>();
    private int maxNum;

    public MultipleSelectAdapter(Context context, int maxNum){
        this.context = context;
        this.maxNum = maxNum;
        layoutInflater = LayoutInflater.from(context);
    }

    public void refreshData(List<ImageBean> imgList, ArrayList<ImageBean> selPoiList){
        this.imgList = imgList;
        this.selPoiList = selPoiList;
        notifyDataSetChanged();
    }

    public void refreshSelectPoi(List<ImageBean> selPoiList){
        this.selPoiList = selPoiList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ImageViewHolder(layoutInflater.inflate(R.layout.item_img_1, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof ImageViewHolder){
            Glide.with(context).load(imgList.get(i).load).asBitmap().into(((ImageViewHolder) viewHolder).imageView);
            if (selPoiList.contains(imgList.get(i))){
                ((ImageViewHolder) viewHolder).ivSelect.setImageResource(R.drawable.select_true);
            }else{
                ((ImageViewHolder) viewHolder).ivSelect.setImageResource(R.drawable.select_false);
            }
        }
    }

    @Override
    public int getItemCount() {
        return imgList.size();
    }

    private class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        WHView whView;
        ImageView imageView;
        ImageView ivSelect;
        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            whView = itemView.findViewById(R.id.wh_view);
            imageView = itemView.findViewById(R.id.image);
            ivSelect = itemView.findViewById(R.id.iv_select);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onImageViewItemClick!=null){
                if (selPoiList.contains(imgList.get(getAdapterPosition()))){
                    ivSelect.setImageResource(R.drawable.select_false);
                }else if (selPoiList.size()<maxNum){
                    ivSelect.setImageResource(R.drawable.select_true);
                }
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

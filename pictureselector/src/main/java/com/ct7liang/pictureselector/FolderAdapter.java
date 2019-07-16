package com.ct7liang.pictureselector;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

public class FolderAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater layoutInflater;
    private Context context;
    private List<String> nameList;
    private List<List<String>> dataList;
    private int totalCount;

    public FolderAdapter(Context context, List<String> nameList, List<List<String>> dataList, int totalCount){
        this.context = context;
        this.nameList = nameList;
        this.dataList = dataList;
        this.totalCount = totalCount;
        layoutInflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new FolderHolder(layoutInflater.inflate(R.layout.item_folder_view, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof FolderHolder){
            if (i == 0){
                ((FolderHolder) viewHolder).tvFolder.setText("全部照片("+totalCount+")");
                Glide.with(context).load(dataList.get(0).get(0)).asBitmap().into(((FolderHolder) viewHolder).ivFolder);
            }else{
                ((FolderHolder) viewHolder).tvFolder.setText(nameList.get(i-1) + "(" + dataList.get(i-1).size() + ")");
                Glide.with(context).load(dataList.get(i-1).get(0)).asBitmap().into(((FolderHolder) viewHolder).ivFolder);
            }
        }
    }

    @Override
    public int getItemCount() {
        return nameList.size()<dataList.size()?nameList.size()+1:dataList.size()+1;
    }

    private class FolderHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivFolder;
        TextView tvFolder;
        FolderHolder(@NonNull View itemView) {
            super(itemView);
            ivFolder = itemView.findViewById(R.id.iv_folder);
            tvFolder = itemView.findViewById(R.id.tv_folder);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (onFolderItemClick!=null){
                onFolderItemClick.onItemClick(v, getAdapterPosition());
            }
        }
    }

    private OnFolderItemClick onFolderItemClick;
    public void setOnFolderItemClickListener(OnFolderItemClick onFolderItemClick){
        this.onFolderItemClick = onFolderItemClick;
    }
    interface OnFolderItemClick{
        void onItemClick(View v, int position);
    }
}

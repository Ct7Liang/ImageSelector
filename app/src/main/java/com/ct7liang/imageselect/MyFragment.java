package com.ct7liang.imageselect;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ct7liang.pictureselector.PictureSelector;

import java.util.ArrayList;

public class MyFragment extends Fragment implements View.OnClickListener {

    private PictureSelector pictureSelector;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.btn1).setOnClickListener(this);
        view.findViewById(R.id.btn2).setOnClickListener(this);
        view.findViewById(R.id.btn3).setOnClickListener(this);
        view.findViewById(R.id.btn4).setOnClickListener(this);
        view.findViewById(R.id.btn5).setOnClickListener(this);

        pictureSelector = new PictureSelector(this, "com.ct7liang.imageselect", 999);

        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn1:
                pictureSelector.selectPhotos(9);
                break;
            case R.id.btn2:
                pictureSelector.selectPhoto(false);
                break;
            case R.id.btn3:
                pictureSelector.selectPhoto(true);
                break;
            case R.id.btn4:
                pictureSelector.takePhoto(false);
                break;
            case R.id.btn5:
                pictureSelector.takePhoto(true);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.i("ct7liang123", "requestCode: " + requestCode);

        if (requestCode == 999){
            ArrayList<String> images = pictureSelector.getImages(999, data);
            Log.i("ct7liang123", "images: " + images.toString());
            recyclerView.setAdapter(new ImageAdapter(getActivity(), images));
        }
    }
}
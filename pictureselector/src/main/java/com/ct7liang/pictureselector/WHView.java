package com.ct7liang.pictureselector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class WHView extends ViewGroup {

    public WHView(Context context) {
        super(context);
    }

    public WHView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //获取父控件的宽度
        int size = MeasureSpec.getSize(widthMeasureSpec);
        //设置父控件的高度和宽度为一致
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (getChildCount()>0){
            //如果父控件含有子控件, 则取第一个子控件填充满父控件
            View child = getChildAt(0);
            child.layout(l, t, r, b);
        }
    }
}

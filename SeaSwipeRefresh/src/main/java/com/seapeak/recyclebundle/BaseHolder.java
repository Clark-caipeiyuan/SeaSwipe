package com.seapeak.recyclebundle;

import androidx.recyclerview.widget.RecyclerView;

import android.view.View;


/**
 * Created by seapeak on 16/6/28.
 * <p>
 * BaseViewHolder 多一个容器视图，方便操作背景 点击 事件
 *
 */
public class BaseHolder extends RecyclerView.ViewHolder {

    //主视图
    public final View mView;

    public BaseHolder(View view) {
        super(view);
        mView = view;
    }

    /**
     * 获取主视图
     * @return
     */
    public View getMainView() {
        return mView;
    }
}
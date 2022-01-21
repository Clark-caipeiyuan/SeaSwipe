package com.seapeak.recyclebundle;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.seapeak.ayswiperefresh.R;


/**
 * Created by seapeak on 16/6/28.
 * <p>
 * RecycleView的空ViewHolder
 */
public class EmptyHolder extends RecyclerView.ViewHolder {
    //视图
    public final View mView;

    public final TextView emptyText;
    public final ImageView emptyImage;

    public EmptyHolder(View view) {
        super(view);
        mView = view;
        emptyText = (TextView) view.findViewById(R.id.empty_text);
        emptyImage = (ImageView) view.findViewById(R.id.empty_image);
    }

    public static EmptyHolder newEmptyView(Context context) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_empty_layout, null, false);
        return new EmptyHolder(view);
    }


}
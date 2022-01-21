package com.seapeak.recyclebundle;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.seapeak.ayswiperefresh.R;


/**
 * Created by seapeak on 16/6/28.
 * <p>
 * RecycleView的空ViewHolder
 */
public class EmptyErrorHolder extends RecyclerView.ViewHolder {
    //视图
    public final View mView;

    public final TextView emptyErrorText;
    public final ImageView emptyErrorImage;
    public final Button emptyErrorButton;

    public EmptyErrorHolder(View view) {
        super(view);
        mView = view;
        emptyErrorText = (TextView) view.findViewById(R.id.empty_error_text);
        emptyErrorImage = (ImageView) view.findViewById(R.id.empty_error_image);
        emptyErrorButton = (Button) view.findViewById(R.id.empty_error_button);
    }

    public static EmptyErrorHolder newEmptyErrorView(Context context) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.recycle_empty_error_layout, null, false);
        return new EmptyErrorHolder(view);
    }

}
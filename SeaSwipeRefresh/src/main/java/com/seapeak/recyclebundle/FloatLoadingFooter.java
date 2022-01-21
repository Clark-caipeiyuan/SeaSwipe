package com.seapeak.recyclebundle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.seapeak.ayswiperefresh.R;

/**
 * Created by seapeak on 2018/6/5.
 * <p/>
 * 浮窗展示加载状态
 */
public class FloatLoadingFooter extends LinearLayout {

    protected State mState = null;
    private LinearLayout bottom_loading_bar;
    private LinearLayout bottom_loading_complete;
    private LinearLayout bottom_loading_error;

    private Runnable removeRunnable;

    /**
     * 四种状态的文字描述
     */
    private TextView loading_text, end_text, error_text;

    public FloatLoadingFooter(Context context) {
        super(context);
        init(context);
    }

    public FloatLoadingFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public void init(Context context) {
        View root = inflate(context, R.layout.float_footer_view, this);
        bottom_loading_bar = (LinearLayout) root.findViewById(R.id.bottom_loading_bar);
        bottom_loading_complete = (LinearLayout) root.findViewById(R.id.bottom_loading_complete);
        bottom_loading_error = (LinearLayout) root.findViewById(R.id.bottom_loading_error);

        loading_text = (TextView) root.findViewById(R.id.loading_text);
        end_text = (TextView) root.findViewById(R.id.end_text);
        error_text = (TextView) root.findViewById(R.id.error_text);
        removeRunnable = new Runnable() {
            @Override
            public void run() {
                setState(State.UN_SHOW);
            }
        };

        mState = State.UN_SHOW;
    }

    public void setLoadText(String loadText) {
        this.loading_text.setText(loadText);
    }

    public void setEndText(String endText) {
        this.end_text.setText(endText);
    }

    public void setErrorText(String errorText) {
        this.error_text.setText(errorText);
    }

    public State getState() {
        return mState;
    }

    public void setState(State status) {
        setState(status, false);
    }

    /**
     * 设置状态
     *
     * @param status
     * @param needAutoDismiss 自动隐藏
     */
    public void setState(State status, boolean needAutoDismiss) {
        if (mState == status)
            return;

        try {
            mState = status;
            if (status == State.UN_SHOW) {
                bottom_loading_bar.setVisibility(GONE);
                bottom_loading_complete.setVisibility(GONE);
                bottom_loading_error.setVisibility(GONE);
            } else {
                bottom_loading_bar.setVisibility(status == State.Loading ? VISIBLE : GONE);
                bottom_loading_complete.setVisibility(status == State.END ? VISIBLE : GONE);
                bottom_loading_error.setVisibility(status == State.ERROR ? VISIBLE : GONE);
                if (needAutoDismiss) {
                    postDelayed(removeRunnable, 1500);
                } else {
                    removeCallbacks(removeRunnable);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public enum State {
        UN_SHOW/**不展示*/, END/**加载到最底了*/, Loading/**加载中..*/, ERROR/**网络异常*/
    }
}
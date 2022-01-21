package com.seapeak.recyclebundle;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.seapeak.ayswiperefresh.R;

/**
 * Created by cundong on 2015/10/9.
 * <p/>
 * ListView/GridView/RecyclerView 分页加载时使用到的FooterView
 */
public class LoadingFooter extends FrameLayout {

    protected State mState = null;
    private View mNormalView;
    private View mLoadingView;
    private View mNetworkErrorView;
    private View mTheEndView;
    private ProgressBar mLoadingProgress;

    /**
     * 四种状态的文字描述
     */
    private String normalText, loadText, endText, errorText;

    public LoadingFooter(Context context) {
        super(context);
        init(context);
    }

    public LoadingFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public LoadingFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void init(Context context) {

        inflate(context, R.layout.sample_common_list_footer, this);
        setOnClickListener(null);

//        setState(State.Normal, true);
    }

    public void setNormalText(String normalText) {
        this.normalText = normalText;
    }

    public void setLoadText(String loadText) {
        this.loadText = loadText;
    }

    public void setEndText(String endText) {
        this.endText = endText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

    public State getState() {
        return mState;
    }

    public void setState(State status ) {
        setState(status, true);
    }

    /**
     * 设置状态
     *
     * @param status
     * @param showView 是否展示当前View
     */
    public void setState(State status, boolean showView) {
        if (mState == status) {
            return;
        }
        mState = status;

        switch (status) {

            case Normal:
                findViewById(R.id.loading_view).setVisibility(VISIBLE);
                setOnClickListener(null);

                if (mNormalView == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.normal_view_stub);
                    mNormalView = viewStub.inflate();
                }

                mNormalView.setVisibility(showView ? VISIBLE : GONE);

                if (mLoadingView != null)
                    mLoadingView.setVisibility(GONE);

                if (mTheEndView != null)
                    mTheEndView.setVisibility(GONE);

                if (mNetworkErrorView != null)
                    mNetworkErrorView.setVisibility(GONE);

                ((TextView) mNormalView.findViewById(R.id.normal_text)).setText(normalText);
                break;
            case Loading:
                findViewById(R.id.loading_view).setVisibility(VISIBLE);
                setOnClickListener(null);

                if (mNormalView != null)
                    mNormalView.setVisibility(GONE);

                if (mTheEndView != null)
                    mTheEndView.setVisibility(GONE);

                if (mNetworkErrorView != null)
                    mNetworkErrorView.setVisibility(GONE);

                if (mLoadingView == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.loading_view_stub);
                    mLoadingView = viewStub.inflate();
                    mLoadingProgress = (ProgressBar) mLoadingView.findViewById(R.id.loading_progress);
                }

                mLoadingView.setVisibility(showView ? VISIBLE : GONE);
                mLoadingProgress.setVisibility(View.VISIBLE);
                ((TextView) mLoadingView.findViewById(R.id.loading_text)).setText(loadText);
                break;
            case TheEnd:
                findViewById(R.id.loading_view).setVisibility(VISIBLE);
                setOnClickListener(null);

                if (mNormalView != null)
                    mNormalView.setVisibility(GONE);

                if (mLoadingView != null)
                    mLoadingView.setVisibility(GONE);

                if (mNetworkErrorView != null)
                    mNetworkErrorView.setVisibility(GONE);

                if (mTheEndView == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.end_view_stub);
                    mTheEndView = viewStub.inflate();
                }

                mTheEndView.setVisibility(showView ? VISIBLE : GONE);
                ((TextView) mTheEndView.findViewById(R.id.end_text)).setText(endText);
                break;
            case NetWorkError:
                findViewById(R.id.loading_view).setVisibility(VISIBLE);

                if (mNormalView != null)
                    mNormalView.setVisibility(GONE);

                if (mLoadingView != null)
                    mLoadingView.setVisibility(GONE);

                if (mTheEndView != null)
                    mTheEndView.setVisibility(GONE);

                if (mNetworkErrorView == null) {
                    ViewStub viewStub = (ViewStub) findViewById(R.id.network_error_view_stub);
                    mNetworkErrorView = viewStub.inflate();
                }

                mNetworkErrorView.setVisibility(showView ? VISIBLE : GONE);
                ((TextView) mNetworkErrorView.findViewById(R.id.error_text)).setText(errorText);
                break;
            default:
                break;
        }
    }

    public enum State {
        Normal/**正常*/, TheEnd/**加载到最底了*/, Loading/**加载中..*/, NetWorkError/**网络异常*/
    }
}
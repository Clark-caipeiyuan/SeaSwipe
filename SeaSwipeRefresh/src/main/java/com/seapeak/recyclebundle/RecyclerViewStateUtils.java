package com.seapeak.recyclebundle;

import android.app.Activity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by cundong on 2015/11/9.
 *
 * 分页展示数据时，RecyclerView的FooterView State 操作工具类
 *
 * RecyclerView一共有几种State：Normal/Loading/Error/TheEnd
 */
public class RecyclerViewStateUtils {

    /**
     * 设置headerAndFooterAdapter的FooterView State
     *
     * @param instance      context
     * @param recyclerView  recyclerView
     * @param state         FooterView State
     * @param errorListener FooterView处于Error状态时的点击事件
     */
    public static void setFooterViewState(Activity instance, RecyclerView recyclerView, LoadingFooter.State state, View.OnClickListener errorListener, int footBg,
                                          String pullText, String loadText, String endText, String errorText) {

        if(instance==null || instance.isFinishing()) {
            return;
        }

        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();

        if (outerAdapter == null || !(outerAdapter instanceof HeaderAndFooterRecyclerViewAdapter)) {
            return;
        }

        HeaderAndFooterRecyclerViewAdapter headerAndFooterAdapter = (HeaderAndFooterRecyclerViewAdapter) outerAdapter;

        //只有一页的时候，就别加什么FooterView了
        if (headerAndFooterAdapter.getInnerAdapter().getItemCount() < headerAndFooterAdapter.getShowFootOffset()) {
            removeFooteLoadingView(recyclerView);
            return;
        }

        try {
            LoadingFooter footerView;
            //已经有footerView了
            if (headerAndFooterAdapter.getFooterViewsCount() > 0) {
                for (View footView : ((HeaderAndFooterRecyclerViewAdapter) outerAdapter).getAllFooterView()) {
                    if (footView instanceof LoadingFooter) {
                        footerView = (LoadingFooter) footView;
                        //设置文字描述
                        footerView.setNormalText(pullText);
                        footerView.setLoadText(loadText);
                        footerView.setEndText(endText);
                        footerView.setErrorText(errorText);
                        footerView.setBackgroundColor(footBg);

                        footerView.setState(state);

                        if (state == LoadingFooter.State.NetWorkError) {
                            footerView.setOnClickListener(errorListener);
                        }
                        break;
                    }
                }
            } else {
                footerView = new LoadingFooter(instance);
                footerView.setNormalText(pullText);
                footerView.setLoadText(loadText);
                footerView.setEndText(endText);
                footerView.setErrorText(errorText);
                footerView.setBackgroundColor(footBg);

                footerView.setState(state);

                if (state == LoadingFooter.State.NetWorkError) {
                    footerView.setOnClickListener(errorListener);
                }

                headerAndFooterAdapter.addFooterView(footerView);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取当前RecyclerView.FooterView的状态
     *
     * @param recyclerView
     */
    public static LoadingFooter.State getFooterViewState(RecyclerView recyclerView) {

        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter != null && outerAdapter instanceof HeaderAndFooterRecyclerViewAdapter) {
            if (((HeaderAndFooterRecyclerViewAdapter) outerAdapter).getFooterViewsCount() > 0) {
                for (View footView : ((HeaderAndFooterRecyclerViewAdapter) outerAdapter).getAllFooterView()) {
                    if (footView instanceof LoadingFooter)
                        return ((LoadingFooter) footView).getState();
                }
            }
        }
        return null;
    }

    /**
     * 清除所有底部加载条
     *
     * @param recyclerView
     */
    public static void removeFooteLoadingView(RecyclerView recyclerView) {
        RecyclerView.Adapter outerAdapter = recyclerView.getAdapter();
        if (outerAdapter != null && outerAdapter instanceof HeaderAndFooterRecyclerViewAdapter) {
            if (((HeaderAndFooterRecyclerViewAdapter) outerAdapter).getFooterViewsCount() > 0) {
                for (View footView : ((HeaderAndFooterRecyclerViewAdapter) outerAdapter).getAllFooterView()) {
                    if (footView instanceof LoadingFooter)
                        ((HeaderAndFooterRecyclerViewAdapter) outerAdapter).removeFooterView(footView);
                }
            }
        }
    }
}

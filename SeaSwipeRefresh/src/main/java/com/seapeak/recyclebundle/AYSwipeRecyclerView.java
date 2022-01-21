package com.seapeak.recyclebundle;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.seapeak.ayswiperefresh.R;


/**
 * Created by seapeak on 2017/2/15.
 * <p>
 * 继承SwipeRefreshLayout 内部包含 RecyclerView
 * 包含 下拉刷新上拉加载监听 空图展示 设置头与尾 设置需要显示加载更多的阈值<默认6> 设置滚动 X Y 轴监听
 * <p>
 * 在网络请求时，若加载第一次 请使用 @startLoadFirst() 包含了自动显示下拉圈与调用 @loadFirst() 操作
 * 网络请求结束时，推荐使用 @onFinishRequest(boolean, boolean) （是否加载出错 , 是否可以加载更多）
 * 该操作包含 取消加载动作，设置空图展示，加载状态设置
 */
public class AYSwipeRecyclerView extends SwipeRefreshLayout implements SwipeRefreshLayout.OnRefreshListener {

    public String normalText = "上滑加载更多";
    public String loadText = "正在加载中...";
    public String endText = "已经没有更多了";
    public String errorText = "加载出错，点击重新加载";

    //    暴露给用户设置的adapter
    private BaseRecyclerAdapter<BaseHolder> mAdapter;
    //    内部的RecyclerView
    private RecyclerView mRecyclerView;
    //    recycleView 的状态
    private LoadingFooter.State state;
    //    使得RecyclerView可以添加头与尾，并且显示加载更多撞他的Adapter，具体用法见下方
    private HeaderAndFooterRecyclerViewAdapter mHeaderAndFooterRecyclerViewAdapter = null;
    //    是否加载更多
    private boolean hasMore = false;
    //    是否加载出错
    private boolean isError = false;
    //    上下文
    private Activity context;
    //    加载状态
    private boolean isLoading = false;
    //    滚动监听
    private OnScrollerListener scrollerListener;
    //    是否展示加载更多bottom
    private boolean showBottom = true;
    //    是否展示空图
    private boolean showEmpty = true;
    //    从某个count开始算空
    private int emptyStartCount = 0;
    //    加载更多背景色
    private int footBg = 0x00ffffff;

    private OnRefreshLoadListener refreshLoadLister;
    //    空图展示
    private View emptyView;
    //    错空图展示
    private View emptyErrorView;

    private EndlessRecyclerOnScrollListener mOnScrollListener = new EndlessRecyclerOnScrollListener() {

        int totalDy = 0;
        int totalDx = 0;

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            totalDy += dy;
            totalDx += dx;
            if (scrollerListener != null)
                scrollerListener.onScrolling(totalDy, totalDx);
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                if (!recyclerView.canScrollVertically(-1)) {
                    Log.i("zhong", "到顶了");
                    totalDy = 0;
                    if (scrollerListener != null)
                        scrollerListener.onScrolling(totalDy, totalDx);
                }

                if (!recyclerView.canScrollHorizontally(-1)) {
                    totalDx = 0;
                    if (scrollerListener != null)
                        scrollerListener.onScrolling(totalDy, totalDx);
                }
            }
        }

        @Override
        public void onLoadNextPage(View view) {
            super.onLoadNextPage(view);
            if (!isLoading && hasMore) {
                Log.i("AYSwipeRecycler", "onLoadNextPage: hasMore = " + hasMore);
                isLoading = true;
                if (showBottom)
                    RecyclerViewStateUtils.setFooterViewState((Activity) context, mRecyclerView, LoadingFooter.State.Loading, null, footBg,
                            normalText, loadText, endText, errorText);
                if (refreshLoadLister != null)
                    refreshLoadLister.loadNext();
            }
        }
    };

    /**
     * 加载出错的时候点击事件
     */
    private OnClickListener mFooterClick = new OnClickListener() {
        @Override
        public void onClick(View v) {
            RecyclerViewStateUtils.setFooterViewState((Activity) context, mRecyclerView, LoadingFooter.State.Loading, null, footBg,
                    normalText, loadText, endText, errorText);
            if (refreshLoadLister != null)
                refreshLoadLister.loadNext();
        }
    };

    public AYSwipeRecyclerView(Context context) {
        super(context);
        init(context);
    }

    public AYSwipeRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 初始化控件
     *
     * @param con
     */
    private void init(Context con) {
        this.context = (Activity) con;
        View view = View.inflate(getContext(), R.layout.swipe_recycle_view, this);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycle_list);
        if (mRecyclerView == null)
            return;

        this.setOnRefreshListener(this);
        if (mOnScrollListener != null)
            mRecyclerView.addOnScrollListener(mOnScrollListener);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        RecyclerView.ItemAnimator animator = new DefaultItemAnimator();
        animator.setChangeDuration(0);
        mRecyclerView.setItemAnimator(animator);

        setColorSchemeColors(0xff25b6ed, 0xff6dbcdb, 0xfff0b650, 0xffff534e, 0xffbed73d, 0xff04bfbf, 0xffff530d, 0xff61bc46);

        //空白视图
        emptyView = EmptyHolder.newEmptyView(context).mView;
        //空白错误视图
        EmptyErrorHolder holder = EmptyErrorHolder.newEmptyErrorView(context);
        emptyErrorView = holder.mView;
        holder.emptyErrorButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                startLoadFirst();
            }
        });
//        emptyView = View.inflate(getContext(), R.layout.recycle_empty_ay, null);

        if (state != null)
            RecyclerViewStateUtils.setFooterViewState(context, mRecyclerView, state, null, footBg,
                    normalText, loadText, endText, errorText);

        isLoading = false;

        if (mAdapter == null) {
            mAdapter = new BaseRecyclerAdapter<BaseHolder>() {
                @Override
                public BaseHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    return null;
                }

                @Override
                public int getItemCount() {
                    return 0;
                }
            };
        }

        mHeaderAndFooterRecyclerViewAdapter = new HeaderAndFooterRecyclerViewAdapter(mAdapter);
        mRecyclerView.setAdapter(mHeaderAndFooterRecyclerViewAdapter);
    }

    /**
     * 设置加载更多背景色
     * @param footBg
     */
    public void setFootBg(int footBg) {
        this.footBg = footBg;
    }

    /**
     * 设置target的视图类型
     *
     * @param layoutManager
     */
    public void setLayoutManager(RecyclerView.LayoutManager layoutManager) {
        mRecyclerView.setLayoutManager(layoutManager);
    }

    /**
     * 在ViewPager里的Fragment里使用时，出现下拉不回弹，请在fragment 的 onHiddenChanged()使用该方法
     *
     * @param hidden
     */
    public void onFragmentHiddenChanged(boolean hidden) {
        if (hidden) {
            if (this != null && isRefreshing()) {
                setRefreshing(false);
                destroyDrawingCache();
                clearAnimation();
            }
        }
    }

    /**
     * 在ViewPager里的Fragment里使用时，出现下拉视图错乱，请在fragment 的 onDestroyView()使用该方法
     */
    public void onDestroyView() {
        setRefreshing(false);
        state = RecyclerViewStateUtils.getFooterViewState(mRecyclerView);
    }

    /**
     * UI网络请求时结束语，用于让控件是否加载更多
     * LoadingFooter
     *
     * @param error   错误
     * @param hasmore 是否还有更多
     */
    public void onFinishRequest(boolean error, boolean hasmore) {
        this.hasMore = hasmore;
        this.isError = error;
//        如果没有设置显示底部加载框 showBottom 为false
        notifyDataSetChanged();
        if (showBottom) {
            if (isError)
                RecyclerViewStateUtils.setFooterViewState((Activity) context, mRecyclerView, LoadingFooter.State.NetWorkError, mFooterClick, footBg,
                        normalText, loadText, endText, errorText);
            else if (!hasmore)
                RecyclerViewStateUtils.setFooterViewState((Activity) context, mRecyclerView, LoadingFooter.State.TheEnd, null, footBg,
                        normalText, loadText, endText, errorText);
            else
                RecyclerViewStateUtils.setFooterViewState((Activity) context, mRecyclerView, LoadingFooter.State.Normal, null, footBg,
                        normalText, loadText, endText, errorText);
        }
        if (isRefreshing())
            setRefreshing(false);
        isLoading = false;
    }

    protected RecyclerView.Adapter getAdapter() {
        return mAdapter;
    }

    /**
     * 设置Adapter
     *
     * @param mAdapter
     */
    public void setAdapter(RecyclerView.Adapter mAdapter) {
        this.mAdapter = (BaseRecyclerAdapter<BaseHolder>) mAdapter;
        mHeaderAndFooterRecyclerViewAdapter.setAdapter(mAdapter);
    }

    /**
     * 更新数据
     */
    public void notifyDataSetChanged() {
        mHeaderAndFooterRecyclerViewAdapter.notifyDataSetChanged();

        //显示空图
        if (mHeaderAndFooterRecyclerViewAdapter.getInnerAdapter().getItemCount() == emptyStartCount && showEmpty) {
            RecyclerViewUtils.setEmptyView(mRecyclerView, isError ? emptyErrorView : emptyView);
        } else
            RecyclerViewUtils.removeEmptyView(mRecyclerView);
    }

    /**
     * 开始第一次加载
     */
    public void startLoadFirst() {
        this.post(new Runnable() {
            @Override
            public void run() {
                isLoading = true;
                if (refreshLoadLister != null)
                    refreshLoadLister.loadFirst();
                setRefreshing(true);
            }
        });
    }

    public void showLoading() {
        this.post(new Runnable() {
            @Override
            public void run() {
                isLoading = true;
                setRefreshing(true);
            }
        });
    }

    @Override
    public void onRefresh() {
        if (!isLoading) {
            isLoading = true;
            if (refreshLoadLister != null)
                refreshLoadLister.loadFirst();
        } else {
            setRefreshing(false);
        }
    }

    public void setHeadView(View view) {
        RecyclerViewUtils.setHeaderView(mRecyclerView, view);
    }

    public void setFootView(View view) {
        RecyclerViewUtils.setFooterView(mRecyclerView, view);
    }

    public void setEmptyView(View view) {
        emptyView = view;
    }

    public View getEmptyView() {
        return emptyView;
    }

    /**
     * 设置是否显示空图
     *
     * @param isShowEmpty
     */
    public void setShowEmpty(boolean isShowEmpty) {
        this.showEmpty = isShowEmpty;
    }

    public boolean isShowEmpty() {
        return showEmpty;
    }

    /**
     * 设置是否显示底部加载框
     *
     * @param showBottom
     */
    private void setShowBottom(boolean showBottom) {
        this.showBottom = showBottom;
        if (!showBottom) {
            RecyclerViewStateUtils.removeFooteLoadingView(mRecyclerView);
        } else {
            RecyclerViewStateUtils.setFooterViewState((Activity) context, mRecyclerView, LoadingFooter.State.Normal, null, footBg,
                    normalText, loadText, endText, errorText);
        }
    }

    public boolean isHasMore() {
        return hasMore;
    }

    /**
     * 设置是否可以加载更多
     *
     * @param hasMore
     */
    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
        //        如果没有设置显示底部加载框 showBottom 为false
        if (showBottom) {
            RecyclerViewStateUtils.setFooterViewState((Activity) context, mRecyclerView, LoadingFooter.State.Normal, null, footBg,
                    normalText, loadText, endText, errorText);
            if (!hasMore) {
                RecyclerViewStateUtils.setFooterViewState((Activity) context, mRecyclerView, LoadingFooter.State.TheEnd, null, footBg,
                        normalText, loadText, endText, errorText);
            }
        }
    }

    /**
     * 设置底部展示bottom的阈值 （比如低于5个item不展示底部）
     *
     * @param num
     */
    public void setShowBottomOffset(int num) {
        mHeaderAndFooterRecyclerViewAdapter.setShowFootOffset(num);
    }

    /**
     * 设置空开始计数 默认0为空
     *
     * @param emptyStartCount
     */
    public void setEmptyStartCount(int emptyStartCount) {
        this.emptyStartCount = emptyStartCount;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    /**
     * 设置刷新加载监听
     *
     * @param refreshLoadLister
     */
    public void setOnRefreshLoadLister(OnRefreshLoadListener refreshLoadLister) {
        this.refreshLoadLister = refreshLoadLister;
    }

    /**
     * 设置自定义滚动监听
     *
     * @param scrollerListener
     */
    public void setOnScrollListener(OnScrollerListener scrollerListener) {
        this.scrollerListener = scrollerListener;
    }

    /**
     * 设置原生RecyclerView滚动监听
     *
     * @param onScrollerListener
     */
    public void addOnScrollListener(RecyclerView.OnScrollListener onScrollerListener) {
        mRecyclerView.addOnScrollListener(onScrollerListener);
    }

    /**
     * 点击事件必须在设置adapter后
     * 并且复写 onBindViewHolder 里 super. 方法一定要保留
     * @param onItemClickListener
     */
    public void setOnItemClickListener(BaseRecyclerAdapter.OnItemClickListener onItemClickListener) {
        mAdapter.setOnItemClickListener(onItemClickListener);
    }

    public void setOnItemLongClickListener(BaseRecyclerAdapter.OnItemLongClickListener onItemLongClickListener) {
        mAdapter.setOnItemLongClickListener(onItemLongClickListener);
    }

    /**
     * 上拉刷新， 下拉加载监听
     */
    public interface OnRefreshLoadListener {
        void loadFirst();

        void loadNext();
    }

    /**
     * 加载刷新模式
     *
     * @param type
     */
    public void setMode(SwipeType type) {
        switch (type) {
            case BOTH:
                setEnabled(true);
                setShowBottom(true);
                break;
            case ONLY_START:
                setEnabled(true);
                setShowBottom(false);
                break;
            case ONLY_END:
                setEnabled(false);
                setShowBottom(true);
                break;
            case DISABLE:
                setEnabled(false);
                setShowBottom(false);
                break;
        }
    }

    /**
     * 滚动设置
     */
    public enum SwipeType {
        BOTH, //上拉下拉都支持
        ONLY_START,  //仅支持下拉刷新
        ONLY_END, //仅支持上拉加载
        DISABLE //都不支持
    }

    /**
     * 自定义滚动监听 X Y 轴
     */
    public interface OnScrollerListener {
        void onScrolling(int y, int x);
    }

    /**
     * 添加item分割线
     *
     * @param itemDecoration
     */
    public void addItemDecoration(RecyclerView.ItemDecoration itemDecoration) {
        mRecyclerView.addItemDecoration(itemDecoration);
    }

    /**
     * 设置四种状态的文字描述
     */
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
}

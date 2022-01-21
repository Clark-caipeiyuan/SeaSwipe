package com.seapeak.recyclebundle;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Created by cundong on 2015/10/9.
 * <p/>
 * 继承自RecyclerView.OnScrollListener，可以监听到是否滑动到页面最低部
 */
public class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener implements OnListLoadNextPageListener {

    /**
     * 当前RecyclerView类型
     */
    protected LayoutManagerType layoutManagerType;

    /**
     * 最后一个的位置
     */
    private int[] lastPositions;

    /**
     * 最后一个可见的item的位置
     */
    private int lastVisibleItemPosition;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();

        if (layoutManagerType == null) {
            if (layoutManager instanceof LinearLayoutManager) {
                layoutManagerType = LayoutManagerType.LinearLayout;
            } else if (layoutManager instanceof GridLayoutManager) {
                layoutManagerType = LayoutManagerType.GridLayout;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                layoutManagerType = LayoutManagerType.StaggeredGridLayout;
            } else {
                throw new RuntimeException(
                        "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
            }
        }

        switch (layoutManagerType) {
            case LinearLayout:
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case GridLayout:
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case StaggeredGridLayout:
                StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
                if (lastPositions == null) {
                    lastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
                }
                staggeredGridLayoutManager.findLastVisibleItemPositions(lastPositions);
                lastVisibleItemPosition = findMax(lastPositions);
                break;
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        if (newState == RecyclerView.SCROLL_STATE_IDLE && !recyclerView.canScrollVertically(1)) {
            Log.i("zhong", "到底了");
            onLoadNextPage(recyclerView);
        }
//        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//        int totalItemCount = layoutManager.getItemCount();
//        int visibleItemCount = layoutManager.getChildCount();
//        if ((visibleItemCount > 0/*可见的item是否大于0*/
//                && currentScrollState == RecyclerView.SCROLL_STATE_IDLE /*是否停止滚动*/
//                && (lastVisibleItemPosition) >= totalItemCount - 1) /*最后可见的item是否是最后一个*/
//                && !recyclerView.canScrollVertically(1)/*是否可以垂直滚动*/) {
//            onLoadNextPage(recyclerView);
//        }
//        checkIfScrollToBottom(recyclerView,lastVisibleItemPosition,totalItemCount);
    }

    /**
     * 取数组中最大值
     *
     * @param lastPositions
     * @return
     */
    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }

        return max;
    }

    @Override
    public void onLoadNextPage(final View view) {
    }

    public enum LayoutManagerType {
        LinearLayout,
        StaggeredGridLayout,
        GridLayout
    }

//    /**
//     * 检测是否滑动到底部item并回调事件
//     * 源代码: https://github.com/CrazyTaro/recycle_header_adapter
//     * @param recyclerView
//     * @param lastItemPosition 最后一个可见itemView的position
//     * @param itemCount        adapter的itemCount
//     * @return boolean 是否滑动至屏幕底部
//     */
//    private boolean checkIfScrollToBottom(RecyclerView recyclerView, int lastItemPosition, int itemCount) {
//        if (lastItemPosition + 1 == itemCount) {
//            //是否进行满屏的判断处理
//            //未满屏的情况下将永远不会被回调滑动到低部或者顶部
//            int childCount = recyclerView.getChildCount();
//            //获取最后一个childView
//            View lastChildView = recyclerView.getChildAt(childCount - 1);
//            //获取第一个childView
//            View firstChildView = recyclerView.getChildAt(0);
//            int top = firstChildView.getTop();
//            int bottom = lastChildView.getBottom();
//            //recycleView显示itemView的有效区域的bottom坐标Y
//            int bottomEdge = recyclerView.getHeight() - recyclerView.getPaddingBottom();
//            //recycleView显示itemView的有效区域的top坐标Y
//            int topEdge = recyclerView.getPaddingTop();
//            //第一个view的顶部小于top边界值,说明第一个view已经部分或者完全移出了界面
//            //最后一个view的底部小于bottom边界值,说明最后一个view已经完全显示在界面
//            //若不处理这种情况,可能会存在recycleView高度足够高时,itemView数量很少无法填充一屏,但是滑动到最后一项时依然会发生回调
//            //此时其实并不需要任何刷新操作的
//            if (bottom <= bottomEdge && top < topEdge) {
//                onLoadNextPage(recyclerView);
//                return true;
//            }
//        }
//        return false;
//    }
}

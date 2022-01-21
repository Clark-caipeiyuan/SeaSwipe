package com.seapeak.recyclebundle;

import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

/**
 * Created by seapeak on 2017/2/22.
 *
 * BaseRecyclerAdapter
 */
public abstract class BaseRecyclerAdapter<V extends BaseHolder> extends RecyclerView.Adapter<V> {

    public OnItemClickListener onItemClickListener;
    public OnItemLongClickListener onItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    @Override
    public void onBindViewHolder(final V holder, final int position) {
        if (onItemClickListener != null)
            holder.getMainView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(v, position, holder);
                }
            });
        if (onItemLongClickListener != null)
            holder.getMainView().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    return onItemLongClickListener.onItemLongClick(v, position);
                }
            });
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position, RecyclerView.ViewHolder viewHolder);
    }

    public interface OnItemLongClickListener {
        boolean onItemLongClick(View v, int position);
    }
}

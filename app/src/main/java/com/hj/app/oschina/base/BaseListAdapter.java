package com.hj.app.oschina.base;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hj.app.oschina.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by huangjie08 on 2016/8/19.
 */
public abstract class BaseListAdapter<T> extends RecyclerView.Adapter {

    public static final int STATE_NO_MORE = 1;
    public static final int STATE_LOAD_MORE = 2;
    public static final int STATE_INVALID_NETWORK = 3;
    public static final int STATE_HIDE = 5;
    public static final int STATE_LOAD_ERROR = 6;
    public static final int STATE_LOADING = 7;

    public int BEHAVIOR_MODE = NEITHER;
    public static final int NEITHER = 0;
    public static final int ONLY_HEADER = 1;
    public static final int ONLY_FOOTER = 2;
    public static final int BOTH_HEADER_FOOTER = 3;

    protected static final int VIEW_TYPE_NORMAL = 0;
    protected static final int VIEW_TYPE_HEADER = -1;
    protected static final int VIEW_TYPE_FOOTER = -2;

    protected Context mContext;
    protected List<T> mDatas;
    protected LayoutInflater mInflater;
    protected int mState;

    private OnLoadingHeaderCallback onLoadingHeaderCallback;

    public BaseListAdapter(Context context, int mode) {
        this(context, new ArrayList<T>(), mode);
    }


    public BaseListAdapter(Context context, List<T> datas, int mode) {
        this.mContext = context;
        this.mDatas = datas;
        mInflater = LayoutInflater.from(mContext);
        BEHAVIOR_MODE = mode;
        mState = STATE_HIDE;
    }

    public boolean hasHeaderView() {
        return BEHAVIOR_MODE == ONLY_HEADER || BEHAVIOR_MODE == BOTH_HEADER_FOOTER;
    }

    public boolean hasFooterView() {
        return BEHAVIOR_MODE == ONLY_FOOTER || BEHAVIOR_MODE == BOTH_HEADER_FOOTER;
    }

    public final void addItem(T obj) {
        checkListNull();
        mDatas.add(obj);
        int position = mDatas.indexOf(obj);
        notifyItemChanged(position);
    }

    public final void addItem(int position, T obj) {
        checkListNull();
        mDatas.add(position, obj);
        notifyItemInserted(position);
    }

    public final void addItems(List<T> objs) {
        checkListNull();
        addItems(mDatas.size() == 0 ? 0 : mDatas.size() - 1, objs);
    }

    public final void addItems(int position, List<T> objs) {
        if (mDatas != null && objs != null && !objs.isEmpty()) {
            mDatas.addAll(position, objs);
            notifyItemRangeChanged(position, objs.size());
        }
    }

    public void removeItem(int position) {
        if (mDatas == null || mDatas.isEmpty()) {
            return;
        }
        mDatas.remove(position);
        notifyItemRemoved(position);
    }

    public void clear() {
        if (mDatas == null || mDatas.isEmpty()) {
            return;
        }
        mDatas.clear();
        notifyDataSetChanged();
    }

    public void checkListNull() {
        if (mDatas == null) {
            mDatas = new ArrayList<T>();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER:
                if (onLoadingHeaderCallback != null) {
                    return onLoadingHeaderCallback.onCreateHeaderHolder(parent);
                } else {
                    throw new IllegalArgumentException("你使用了VIEW_TYPE_HEADER模式,但是你没有实现相应的接口");
                }
            case VIEW_TYPE_FOOTER:
                return new FooterViewHolder(mInflater.inflate(R.layout.list_footer, parent, false));
            default:
                return onCreateDefaultViewHolder(parent, viewType);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case VIEW_TYPE_HEADER:
                if (onLoadingHeaderCallback != null) {
                    onLoadingHeaderCallback.onBindHeaderHolder(holder, position);
                }
                break;
            case VIEW_TYPE_FOOTER:
                FooterViewHolder fvh = (FooterViewHolder) holder;
                fvh.itemView.setVisibility(View.VISIBLE);
                switch (mState) {
                    case STATE_INVALID_NETWORK:
                        fvh.mStateText.setText(mContext.getResources().getText(R.string.footer_type_net_error));
                        fvh.probar.setVisibility(View.GONE);
                        break;
                    case STATE_LOAD_MORE:
                    case STATE_LOADING:
                        fvh.mStateText.setText(mContext.getResources().getText(R.string.loading));
                        fvh.probar.setVisibility(View.VISIBLE);
                        break;
                    case STATE_NO_MORE:
                        fvh.mStateText.setText(mContext.getResources().getText(R.string.footer_type_not_more));
                        fvh.probar.setVisibility(View.GONE);
                        break;
                    case STATE_LOAD_ERROR:
                        fvh.mStateText.setText(mContext.getResources().getText(R.string.footer_type_error));
                        fvh.probar.setVisibility(View.GONE);
                        break;
                    case STATE_HIDE:
                        fvh.itemView.setVisibility(View.GONE);
                        break;
                }
                break;
            default:
                onBindDefaultViewHolder(holder, getIndex(position));
        }
    }

    @Override
    public int getItemCount() {
        if (BEHAVIOR_MODE == ONLY_HEADER || BEHAVIOR_MODE == ONLY_FOOTER) {
            return mDatas.size() + 1;
        } else if (BEHAVIOR_MODE == BOTH_HEADER_FOOTER) {
            return mDatas.size() + 2;
        } else {
            return mDatas.size();
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0 && (BEHAVIOR_MODE == ONLY_HEADER || BEHAVIOR_MODE == BOTH_HEADER_FOOTER)) {
            return VIEW_TYPE_HEADER;
        }
        if (position + 1 == getItemCount() && (BEHAVIOR_MODE == ONLY_FOOTER || BEHAVIOR_MODE == BOTH_HEADER_FOOTER)) {
            return VIEW_TYPE_FOOTER;
        }
        return VIEW_TYPE_NORMAL;
    }

    private int getIndex(int position) {
        return BEHAVIOR_MODE == ONLY_HEADER || BEHAVIOR_MODE == BOTH_HEADER_FOOTER ? position - 1 : position;
    }

    public final void setState(int state) {
        mState = state;
    }

    public final int getState() {
        return mState;
    }

    public final void setMode(int mode) {
        BEHAVIOR_MODE = mode;
    }

    public int getDataSize() {
        return mDatas.size();
    }

    public final void setOnLoadingHeaderCallBack(OnLoadingHeaderCallback listener) {
        onLoadingHeaderCallback = listener;
    }

    protected abstract RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int viewType);

    protected abstract void onBindDefaultViewHolder(RecyclerView.ViewHolder h, int position);

    /**
     * 当添加到RecyclerView时获取GridLayoutManager布局管理器，修正header和footer显示整行
     *
     * @param recyclerView the mRecyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if (layoutManager instanceof GridLayoutManager) {
            final GridLayoutManager gridManager = (GridLayoutManager) layoutManager;
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (getItemViewType(position) == VIEW_TYPE_HEADER || getItemViewType(position) == VIEW_TYPE_FOOTER) {
                        return gridManager.getSpanCount();
                    }
                    return 1;
                }
            });
        }
    }

    /**
     * 当RecyclerView在windows活动时获取StaggeredGridLayoutManager布局管理器，修正header和footer显示整行
     *
     * @param holder the RecyclerView.ViewHolder
     */
    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams lp = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            if (BEHAVIOR_MODE == ONLY_HEADER) {
                lp.setFullSpan(holder.getLayoutPosition() == 0);
            } else if (BEHAVIOR_MODE == ONLY_FOOTER) {
                lp.setFullSpan(holder.getLayoutPosition() == mDatas.size() + 1);
            } else if (BEHAVIOR_MODE == BOTH_HEADER_FOOTER) {
                if (holder.getLayoutPosition() == 0 || holder.getLayoutPosition() == mDatas.size() + 1) {
                    lp.setFullSpan(true);
                }
            }
        }
    }

    public static class FooterViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar probar;
        public TextView mStateText;

        public FooterViewHolder(View view) {
            super(view);
            probar = (ProgressBar) view.findViewById(R.id.progressbar);
            mStateText = (TextView) view.findViewById(R.id.state_text);
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnLoadingHeaderCallback {
        RecyclerView.ViewHolder onCreateHeaderHolder(ViewGroup parent);

        void onBindHeaderHolder(RecyclerView.ViewHolder holder, int position);
    }
}

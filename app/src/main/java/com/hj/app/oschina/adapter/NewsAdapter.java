package com.hj.app.oschina.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hj.app.oschina.AppContext;
import com.hj.app.oschina.R;
import com.hj.app.oschina.base.BaseListAdapter;
import com.hj.app.oschina.bean.News;
import com.hj.app.oschina.util.StringUtils;
import com.hj.app.oschina.util.ThemeSwitchUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by huangjie08 on 2016/8/22.
 */
public class NewsAdapter extends BaseListAdapter<News> {

    private final static String PREF_READED_NEWS_LIST = "readed_news_list.pref";

    public NewsAdapter(Context context, int mode) {
        super(context, mode);
    }

    @Override
    protected RecyclerView.ViewHolder onCreateDefaultViewHolder(ViewGroup parent, int viewType) {
        return new NewsViewHolder(mInflater.inflate(R.layout.list_cell_news, parent, false));
    }

    @Override
    protected void onBindDefaultViewHolder(RecyclerView.ViewHolder h, int position) {
        NewsViewHolder holder = (NewsViewHolder) h;
        News news = mDatas.get(position);
        holder.title.setText(news.getTitle());

        if (AppContext.isOnReadedPostList(PREF_READED_NEWS_LIST,
                news.getId() + "")) {
            holder.title.setTextColor(mContext.getResources()
                    .getColor(ThemeSwitchUtils.getTitleReadedColor()));
        } else {
            holder.title.setTextColor(mContext.getResources()
                    .getColor(ThemeSwitchUtils.getTitleUnReadedColor()));
        }

        String description = news.getBody();
        holder.description.setVisibility(View.GONE);
        if (description != null && !StringUtils.isEmpty(description)) {
            holder.description.setVisibility(View.VISIBLE);
            holder.description.setText(description.trim());
        }

        holder.source.setText(news.getAuthor());
        if (StringUtils.isToday(news.getPubDate())) {
            holder.tip.setVisibility(View.VISIBLE);
        } else {
            holder.tip.setVisibility(View.GONE);
        }
        holder.time.setText(StringUtils.friendly_time(news.getPubDate()));
        holder.comment_count.setText(news.getCommentCount() + "");

    }

    public static final class NewsViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_title)
        TextView title;
        @BindView(R.id.tv_description)
        TextView description;
        @BindView(R.id.tv_source)
        TextView source;
        @BindView(R.id.tv_time)
        TextView time;
        @BindView(R.id.tv_comment_count)
        TextView comment_count;
        @BindView(R.id.iv_tip)
        ImageView tip;

        public NewsViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}

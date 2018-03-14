package com.rh.toutiao.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rh.materialdemo.R;
import com.rh.neihan.activity.WebActivity;
import com.rh.neihan.utils.DateUtils;
import com.rh.toutiao.gson.NewsArticleBeanContent;
import com.rh.toutiao.widget.CircleImageView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;


/**
 * @author RH
 * @date 2018/2/6
 */
public class NewsArticleAdapter extends RecyclerView.Adapter<NewsArticleAdapter.ViewHolder> {
    private static final String TAG = "NewsArticleAdapter";
    private List<NewsArticleBeanContent> contentList = new ArrayList<>();

    public NewsArticleAdapter(List<NewsArticleBeanContent> contentList) {
        this.contentList = contentList;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView iv_media;
        private ImageView iv_image;
        private TextView tv_title;
        private TextView tv_abstract;
        private TextView tv_extra;
        private ImageView iv_dots;
        public ViewHolder(View itemView) {
            super(itemView);
            this.iv_media = itemView.findViewById(R.id.iv_media);
            this.iv_image = itemView.findViewById(R.id.iv_image);
            this.tv_title = itemView.findViewById(R.id.tv_title);
            this.tv_abstract = itemView.findViewById(R.id.tv_abstract);
            this.tv_extra = itemView.findViewById(R.id.tv_extra);
            this.iv_dots = itemView.findViewById(R.id.iv_dots);
        }
    }

    @Override
    public NewsArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_news_article_img ,null);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(NewsArticleAdapter.ViewHolder holder, int position) {
        final Context context = holder.itemView.getContext();
        try {
            String imgUrl = "http://p3.pstatp.com/";
            List<NewsArticleBeanContent.ImageListBean> image_list = contentList.get(position).getImage_list();
            if (image_list != null && image_list.size() != 0) {
                holder.iv_image.setVisibility(View.VISIBLE);
                Log.e(TAG, "onBindViewHolder: 有图片" );

                String url = image_list.get(0).getUrl();
                Glide.with(context).load(url).into(holder.iv_image);
                if (!TextUtils.isEmpty(image_list.get(0).getUri())) {
                    imgUrl += image_list.get(0).getUri().replace("list", "large");
                }
            }else {
                holder.iv_image.setVisibility(View.GONE);
                Log.e(TAG, "onBindViewHolder: 没有图片" );
            }

            if (null != contentList.get(position).getUser_info()) {
                String avatar_url = contentList.get(position).getUser_info().getAvatar_url();
                if (!TextUtils.isEmpty(avatar_url)) {
                    Glide.with(context).load(avatar_url).into(holder.iv_media);
                }
            }

            String tv_title = contentList.get(position).getTitle();
            String tv_abstract = contentList.get(position).getAbstractX();
            String tv_source = contentList.get(position).getSource();
            String tv_comment_count = contentList.get(position).getComment_count() + "评论";
            String tv_datetime = contentList.get(position).getBehot_time() + "";
            if (!TextUtils.isEmpty(tv_datetime)) {
                tv_datetime = DateUtils.getTimeStampAgo(tv_datetime);
            }

            holder.tv_title.setText(tv_title);
            holder.tv_title.setTextSize(16);
            holder.tv_abstract.setText(tv_abstract);
            holder.tv_extra.setText(tv_source + " - " + tv_comment_count + " - " + tv_datetime);
            holder.iv_dots.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                }
            });

            final String shareUrl = !TextUtils.isEmpty(contentList.get(position).getShare_url()) ? contentList.get(position).getShare_url() : contentList.get(position).getDisplay_url();
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e(TAG, "onClick: "+shareUrl);
                    Intent intent = new Intent(context, WebActivity.class);
                    intent.putExtra("joke_url", shareUrl);
                    intent.putExtra("joke_content", "");
                    context.startActivity(intent);
                }
            });

        } catch (Exception e) {
            //ErrorAction.print(e);
        }

    }

    @Override
    public int getItemCount() {
        return contentList.size();
    }
}

package com.rh.neihan.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.rh.materialdemo.R;
import com.rh.materialdemo.Util.MyToast;
import com.rh.neihan.activity.WebActivity;
import com.rh.neihan.gson.JokeDataDataEntity;
import com.rh.neihan.utils.DataUtils;
import java.util.ArrayList;
import java.util.List;

/**
 * @author RH
 * @date 2018/1/18
 */
public class JokeAdapter extends RecyclerView.Adapter<JokeAdapter.ViewHold> {
    private Context context;
    private List<JokeDataDataEntity> jokeDataDataEntityList = new ArrayList<>();

    public JokeAdapter(List<JokeDataDataEntity> jokeDataDataGroupEntityList) {
        this.jokeDataDataEntityList = jokeDataDataGroupEntityList;
    }

    class ViewHold extends RecyclerView.ViewHolder {
        ImageView ivUserIcon;
        TextView tvAuthor;
        TextView tvContent;
        TextView tvCategory;
        TextView tvTime;
        TextView tvDiggCount;
        TextView tvBurryCount;
        TextView tvCommentCount;
        ImageView imgShareLink;

        private ViewHold(View itemView) {
            super(itemView);
            ivUserIcon = itemView.findViewById(R.id.joke_adapter_item_iv_user);
            tvAuthor = itemView.findViewById(R.id.joke_adapter_item_tv_author);
            tvContent = itemView.findViewById(R.id.joke_adapter_item_tv_content);
            tvCategory = itemView.findViewById(R.id.joke_adapter_item_tv_category);
            tvTime = itemView.findViewById(R.id.joke_adapter_item_tv_time);
            tvDiggCount = itemView.findViewById(R.id.joke_adapter_item_tv_like);
            tvBurryCount = itemView.findViewById(R.id.joke_adapter_item_tv_unlike);
            tvCommentCount = itemView.findViewById(R.id.joke_adapter_item_tv_comment_count);
            imgShareLink = itemView.findViewById(R.id.joke_adapter_item_img_share);


        }
    }


    @Override
    public ViewHold onCreateViewHolder(ViewGroup parent, int viewType) {
        if (context == null) {
            context = parent.getContext();
        }
        View view = LayoutInflater.from(context).inflate(R.layout.joke_adapter_item, parent, false);
        ViewHold viewHold = new ViewHold(view);

        return viewHold;
    }

    @Override
    public void onBindViewHolder(ViewHold holder, int position) {
        JokeDataDataEntity jokeDataDataEntity = jokeDataDataEntityList.get(position);
        if (null != jokeDataDataEntity.getGroup()) {
            Glide.with(context).load(jokeDataDataEntity.getGroup().getUser().getAvatar_url()).into(holder.ivUserIcon);
            holder.tvAuthor.setText(jokeDataDataEntity.getGroup().getUser().getName());
            holder.tvContent.setText(jokeDataDataEntity.getGroup().getContent());
            holder.tvCategory.setText(String.format("#%s", jokeDataDataEntity.getGroup().getCategory_name()));
            holder.tvTime.setText(DataUtils.millis2CurrentTime(jokeDataDataEntity.getGroup().getCreate_time()));
            holder.tvDiggCount.setText(jokeDataDataEntity.getGroup().getDigg_count());
            holder.tvBurryCount.setText(jokeDataDataEntity.getGroup().getBury_count());
            holder.tvCommentCount.setText(jokeDataDataEntity.getGroup().getComment_count());
            holder.imgShareLink.setOnClickListener(v -> {
            });

            holder.tvCommentCount.setOnClickListener(v -> {
                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra("joke_url","http://m.neihanshequ.com/group/"+jokeDataDataEntity.getGroup().getGroup_id());
                intent.putExtra("joke_content",jokeDataDataEntity.getGroup().getContent());
                context.startActivity(intent);
            });

        } else {
            MyToast.show("加载中！");
        }

    }

    @Override
    public int getItemCount() {
        return jokeDataDataEntityList.size();
    }
}

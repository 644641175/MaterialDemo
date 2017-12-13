package com.rh.materialdemo.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rh.materialdemo.R;
import com.rh.materialdemo.activity.PictureActivity;
import com.rh.materialdemo.bean.Picture;
import com.rh.materialdemo.gson.BingDaily;

import java.util.List;

/**
 * Created by RH on 2017/11/3.
 */

public class PictureAdapter extends RecyclerView.Adapter<PictureAdapter.ViewHolder>{
    private static final String TAG = "PictureAdapter";
    private Context mContext;
    private List<BingDaily> mPictureList;

    static class ViewHolder extends RecyclerView.ViewHolder{
        ImageView pictureImage;
        TextView pictureName;
        CardView cardView;
        private ViewHolder(View itemView) {
            super(itemView);
            pictureImage = itemView.findViewById(R.id.picture);
            pictureName = itemView.findViewById(R.id.picture_name);
            cardView = itemView.findViewById(R.id.cardView);
        }
    }

    public PictureAdapter(List<BingDaily> mPictureList) {
        this.mPictureList = mPictureList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (mContext==null) {
            mContext = parent.getContext();
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.picture_item , parent ,false);
        final ViewHolder holder = new ViewHolder(view);


        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition();
                BingDaily bingDaily = mPictureList.get(position);
                Intent intent = new Intent(mContext, PictureActivity.class);
                intent.putExtra("BingDaily_data",bingDaily);
                //intent.putExtra(PictureActivity.NAME,bingDaily.date);
                //intent.putExtra(PictureActivity.IMAGE_ID,bingDaily.getUrl());
                mContext.startActivity(intent);
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BingDaily picture = mPictureList.get(position);
        Log.e(TAG, "onBindViewHolder: "+picture.getDate());
        holder.pictureName.setText(picture.getDate());
        Log.e(TAG, "onBindViewHolder: "+picture.getUrl());
        Glide.with(mContext).load(picture.getUrl()).into(holder.pictureImage);

    }

    @Override
    public int getItemCount() {
        return mPictureList.size();
    }


}

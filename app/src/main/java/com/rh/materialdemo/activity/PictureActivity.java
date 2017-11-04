package com.rh.materialdemo.activity;

import android.content.Intent;
import android.drm.DrmStore;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.rh.materialdemo.R;

/**
 * Created by RH on 2017/11/3.
 */

public class PictureActivity extends AppCompatActivity{
    public static final String NAME ="picture_name";
    public static final String IMAGE_ID ="picture_image_id";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);
        String pictureName =getIntent().getStringExtra(NAME);
        int pictureImageId =getIntent().getIntExtra(IMAGE_ID,0);

        Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbar1);
        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_tool);
        ImageView pictureImageView = (ImageView) findViewById(R.id.picture_image_view);
        TextView pictureContentText = (TextView) findViewById(R.id.picture_content_text);
        setSupportActionBar(toolbar1);//设置toolbar替代原ActionBar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbar.setTitle(pictureName);
        Glide.with(this).load(pictureImageId).into(pictureImageView);
        pictureContentText.setText(generayePictureContent(pictureName));
    }

    private String generayePictureContent(String pictureName) {
        StringBuilder pictureContent = new StringBuilder();
        for (int i = 0;i<500;i++){
            pictureContent.append(pictureName);
        }
        return pictureContent.toString();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

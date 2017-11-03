package com.rh.materialdemo.bean;

/**
 * Created by RH on 2017/11/2.
 */

public class Picture {
    private String  name;
    private int imageId;

    public String getName() {
        return name;
    }


    public int getImageId() {
        return imageId;
    }


    public Picture(String name, int imageId) {
        this.name = name;
        this.imageId = imageId;

    }
}

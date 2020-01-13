package com.example.dingtu2.myapplication.controls;

/**
 * Created by Dingtu2 on 2018/5/3.
 */

public class Picture {
    private String title;
    private Integer imageId;

    public Picture() {
        super();
    }

    public Picture(String title, Integer imageId) {
        super();
        this.title = title;
        this.imageId = imageId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getImageId() {
        return imageId;
    }

    public void setImageId(Integer imageId) {
        this.imageId = imageId;
    }
}

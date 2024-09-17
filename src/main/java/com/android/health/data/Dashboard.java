package com.android.health.data;

public class Dashboard {

    private int picturesId;
    private String title;

    public Dashboard(int id,String titles){
        this.picturesId = id;
        this.title = titles;
    }

    public String getTitle() {
        return title;
    }

    public int getPicturesId() {
        return picturesId;
    }
}

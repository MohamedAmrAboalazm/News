package com.example.mohamed.news;

import android.app.Application;

import com.example.mohamed.news.DataBase.NewsDataBase;

public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        NewsDataBase.init(this);
    }
}

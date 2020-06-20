package com.coolweather.android.base;

import android.app.Application;
import android.content.Context;

import org.litepal.LitePal;
import org.xutils.x;

public class UniteApp extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
       LitePal.initialize(context);
        x.Ext.init(this);
    }
    public static Context getContext(){
        return context;
    }
}

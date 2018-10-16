package com.turndapage.wear.watchface.watchfacedarko;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private static App mInstance;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
    }

    public static Context getAppContext() {
        return mInstance.getApplicationContext();
    }

}
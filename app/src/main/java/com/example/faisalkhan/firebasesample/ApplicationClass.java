package com.example.faisalkhan.firebasesample;

import android.app.Application;
import android.support.multidex.MultiDex;

/**
 * Created by faisal.khan on 10/12/2016.
 */

public class ApplicationClass extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        MultiDex.install(this);
    }
}

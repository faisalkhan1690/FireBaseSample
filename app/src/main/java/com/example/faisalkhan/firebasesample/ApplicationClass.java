package com.example.faisalkhan.firebasesample;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Application class for Application
 *
 * @author Faisal Khan
 */
public class ApplicationClass extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        //installing multidex
        MultiDex.install(this);
    }

}

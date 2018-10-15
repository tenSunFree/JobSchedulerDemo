package com.home.jobschedulerdemo;

import android.app.Application;

public class JSDApplication extends Application {

    /** 提供ExampleJobService目前運行的數字是多少 */
    public static String currentNumber = "00";

    @Override
    public void onCreate() {
        super.onCreate();
    }
}

package com.linlin.thiefdefender;

import android.app.Application;

public class AppData extends Application {
    private static final String mSharedPfsName = "password";
    private static final String mSharedPfsKey = "password";

    public String getSharedPfsName() {
        return mSharedPfsName;
    }

    public String getSharedPfsKey() {
        return mSharedPfsKey;
    }
}

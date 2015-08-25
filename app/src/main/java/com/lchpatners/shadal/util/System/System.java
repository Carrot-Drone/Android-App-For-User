package com.lchpatners.shadal.util.System;

/**
 * Created by YoungKim on 2015. 8. 21..
 */
public class System {
    public final String minimum_ios_version;
    public final String minimum_android_version;

    public System(String minimum_ios_version, String minimum_android_version) {
        this.minimum_ios_version = minimum_ios_version;
        this.minimum_android_version = minimum_android_version;
    }

    public String getMinimum_android_version() {
        return minimum_android_version;
    }
}

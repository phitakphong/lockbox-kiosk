package com.lockboxth.lockboxkiosk.helpers;

import com.google.gson.Gson;

/**
 * Created by layer on 6/9/2559.
 */
public class Gsoner {
    private static Gsoner instance;

    public static Gsoner getInstance() {
        if (instance == null)
            instance = new Gsoner();
        return instance;
    }

    private Gson mGson;

    private Gsoner() {
    }

    public Gson getGson() {
        if (mGson == null) {
            mGson = new Gson();
        }
        return mGson;
    }
}

package com.unisound.unicar.gui.preference;

public class AppsPreference {


    public static final String TAG = "AppsPreference";

    // "SYSTEM"|"CUSTOM"
    public static String APPS_TYPE = "SYSTEM";

    public static void initPreference() {
        APPS_TYPE = PrivatePreference.getValue("apps_type", APPS_TYPE);
    }

    public static void init() {
        initPreference();
        PrivatePreference.addUpdateListener(new IUpdatePreferenceListener() {
            @Override
            public void onUpdate() {
                initPreference();
            }
        });
    }


}

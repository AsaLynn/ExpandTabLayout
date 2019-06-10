package com.zxn.tablayout.listener;

import android.support.annotation.DrawableRes;

public interface CustomTabEntity {
    String getTabTitle();

    String getTabContent();

    @DrawableRes
    int getTabSelectedIcon();

    @DrawableRes
    int getTabUnselectedIcon();
}
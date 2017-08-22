package com.shop.happy.happyshop.application;

import android.app.Application;

/**
 * Created by karthikeyan on 22/8/17.
 */

public class HappyShop extends Application {

    private ApplicationComponent mComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mComponent = DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public ApplicationComponent getComponent() {
        return mComponent;
    }
}

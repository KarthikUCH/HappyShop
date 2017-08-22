package com.shop.happy.happyshop.application;

import com.shop.happy.happyshop.ui.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by karthikeyan on 22/8/17.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(MainActivity activity);
}

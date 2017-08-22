package com.shop.happy.happyshop.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.shop.happy.happyshop.application.ApplicationComponent;
import com.shop.happy.happyshop.application.HappyShop;

/**
 * Created by karthikeyan on 22/8/17.
 */

public abstract class InjectableActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        injectComponent(((HappyShop) getApplication()).getComponent());
    }

    abstract void injectComponent(ApplicationComponent component);
}

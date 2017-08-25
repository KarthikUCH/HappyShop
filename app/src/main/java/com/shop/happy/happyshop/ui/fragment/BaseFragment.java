package com.shop.happy.happyshop.ui.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.shop.happy.happyshop.application.ApplicationComponent;
import com.shop.happy.happyshop.application.HappyShop;
import com.shop.happy.happyshop.data.ProductManager;

import javax.inject.Inject;

/**
 * Created by karthikeyan on 24/8/17.
 */

public abstract class BaseFragment extends Fragment {

    @Inject
    ProductManager mProductManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        injectComponent(((HappyShop)  context.getApplicationContext()).getComponent());
    }

    abstract void injectComponent(ApplicationComponent component);
}

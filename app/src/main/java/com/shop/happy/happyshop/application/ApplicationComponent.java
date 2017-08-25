package com.shop.happy.happyshop.application;

import com.shop.happy.happyshop.ui.MainActivity;
import com.shop.happy.happyshop.ui.ProductDetailActivity;
import com.shop.happy.happyshop.ui.ProductListActivity;
import com.shop.happy.happyshop.ui.fragment.ProductListFragment;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by karthikeyan on 22/8/17.
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    void inject(MainActivity activity);

    void inject(ProductListActivity activity);

    void inject(ProductDetailActivity activity);

    void inject(ProductListFragment fragment);
}

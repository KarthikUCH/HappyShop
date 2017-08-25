package com.shop.happy.happyshop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.shop.happy.happyshop.application.ApplicationComponent;
import com.shop.happy.happyshop.application.HappyShop;
import com.shop.happy.happyshop.data.CategoryManager;
import com.shop.happy.happyshop.data.ProductManager;
import com.shop.happy.happyshop.data.ShoppingCartManager;
import com.shop.happy.happyshop.network.model.ProductItem;

import javax.inject.Inject;

/**
 * Created by karthikeyan on 22/8/17.
 */

public abstract class InjectableActivity extends AppCompatActivity {

    @Inject
    CategoryManager mCategoryManager;

    @Inject
    ProductManager mProductManager;

    @Inject
    ShoppingCartManager mShoppingCartManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        injectComponent(((HappyShop) getApplication()).getComponent());
    }

    abstract void injectComponent(ApplicationComponent component);

    protected void startProductListActivity(boolean isDisplayCart, String category) {
        Intent intent = new Intent(this, ProductListActivity.class);
        intent.putExtra(ProductListActivity.ARG_EXTRA_BOOLEAN_DISPLAY_CART, isDisplayCart);
        intent.putExtra(ProductListActivity.ARG_EXTRA_STRING_CATEGORY, category);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    protected void startProductDetailActivity(ProductItem item) {
        Intent intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra(ProductDetailActivity.ARG_EXTRA_STRING_PRODUCT_ITEM, item);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}

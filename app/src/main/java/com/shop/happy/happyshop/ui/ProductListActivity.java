package com.shop.happy.happyshop.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.shop.happy.happyshop.R;
import com.shop.happy.happyshop.application.ApplicationComponent;
import com.shop.happy.happyshop.network.model.ProductItem;
import com.shop.happy.happyshop.ui.fragment.ProductListFragment;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductListActivity extends InjectableActivity implements ProductListFragment.ProductClickListener {

    public static final String ARG_EXTRA_BOOLEAN_DISPLAY_CART = "extra_arg_display_cart";
    public static final String ARG_EXTRA_STRING_CATEGORY = "extra_arg_category";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    private ProductListFragment mProductListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_list);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        showProductFragment();
    }

    @Override
    void injectComponent(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProductFragment() {
        boolean isDisplayCart = getIntent().getBooleanExtra(ARG_EXTRA_BOOLEAN_DISPLAY_CART, false);
        String category = getIntent().getStringExtra(ARG_EXTRA_STRING_CATEGORY);
        getSupportActionBar().setTitle(category);

        mProductListFragment = ProductListFragment.newInstance(isDisplayCart, category);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, mProductListFragment, ProductListFragment.class.getName());
        transaction.commit();

    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     LISTENERS                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onProductClick(ProductItem item) {
        Log.d("clicked productId", item.getName());
        startProductDetailActivity(item);
    }
}

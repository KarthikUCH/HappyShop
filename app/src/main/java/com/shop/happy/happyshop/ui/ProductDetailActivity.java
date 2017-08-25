package com.shop.happy.happyshop.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shop.happy.happyshop.R;
import com.shop.happy.happyshop.application.ApplicationComponent;
import com.shop.happy.happyshop.application.GlideApp;
import com.shop.happy.happyshop.network.ResponseListener;
import com.shop.happy.happyshop.network.model.ProductItem;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductDetailActivity extends InjectableActivity {

    public static final String ARG_EXTRA_STRING_PRODUCT_ITEM = "arg_extra_product_item";

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.img_preview)
    ImageView imgProduct;

    @BindView(R.id.tv_name)
    TextView tvName;

    @BindView(R.id.tv_price)
    TextView tvPrice;

    @BindView(R.id.tv_on_sale)
    TextView tvUnderSale;

    @BindView(R.id.tv_description)
    TextView tvDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ProductItem product = getIntent().getParcelableExtra(ARG_EXTRA_STRING_PRODUCT_ITEM);
        getSupportActionBar().setTitle(product.getName());
        showProductDetail(product);
        getProductDetail(product.getId());

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

    private void showProductDetail(ProductItem product) {
        tvName.setText(product.getName());
        tvPrice.setText(product.getPrice());
        tvUnderSale.setText(product.isUnderSale() ? "ON SALE" : "");
        tvDescription.setText(product.getDescription());

        GlideApp.with(this)
                .load(product.getImgURL())
                .placeholder(imgProduct.getDrawable())
                .into(imgProduct);

    }

    private void getProductDetail(int productId) {
        mProductManager.fetchProductDetail(productId, new ProductDescriptionListner(this));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     LISTENERS                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static class ProductDescriptionListner implements ResponseListener<ProductItem> {

        private final WeakReference<ProductDetailActivity> mReference;

        public ProductDescriptionListner(ProductDetailActivity activity) {
            mReference = new WeakReference<ProductDetailActivity>(activity);
        }

        @Override
        public void onResponse(ProductItem product) {
            if (mReference.get() != null) {
                mReference.get().showProductDetail(product);
            }
        }

        @Override
        public void onError(String errorMessage) {

        }

        @Override
        public void onCompleted() {

        }
    }

}

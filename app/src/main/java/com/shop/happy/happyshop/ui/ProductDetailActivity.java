package com.shop.happy.happyshop.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shop.happy.happyshop.R;
import com.shop.happy.happyshop.application.ApplicationComponent;
import com.shop.happy.happyshop.network.ResponseListener;
import com.shop.happy.happyshop.network.model.ProductItem;

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

        showProductDetail();
    }

    void showProductDetail() {
        ProductItem item = getIntent().getParcelableExtra(ARG_EXTRA_STRING_PRODUCT_ITEM);

        tvName.setText(item.getName());
        tvPrice.setText(item.getPrice());
        tvUnderSale.setText(item.isUnderSale() ? "ON SALE" : "");

        Glide.with(this)
                .load(item.getImgURL())
                .into(imgProduct);

    }

    @Override
    void injectComponent(ApplicationComponent component) {
        component.inject(this);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     LISTENERS                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    public static class ProductDescriptionListner implements ResponseListener<ProductItem> {
        @Override
        public void onResponse(ProductItem response) {

        }

        @Override
        public void onError(String errorMessage) {

        }

        @Override
        public void onCompleted() {

        }
    }

}

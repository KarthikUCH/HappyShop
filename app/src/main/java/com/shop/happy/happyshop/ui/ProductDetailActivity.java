package com.shop.happy.happyshop.ui;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.shop.happy.happyshop.R;
import com.shop.happy.happyshop.application.ApplicationComponent;
import com.shop.happy.happyshop.application.GlideApp;
import com.shop.happy.happyshop.data.ProductManager;
import com.shop.happy.happyshop.network.ResponseListener;
import com.shop.happy.happyshop.network.model.ProductItem;

import java.lang.ref.WeakReference;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProductDetailActivity extends InjectableActivity implements ProductManager.Observer{

    public static final String ARG_EXTRA_PARCELABLE_PRODUCT_ITEM = "arg_extra_product_item";

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

    @BindView(R.id.btn_add_cart)
    Button btnAddCart;

    @BindView(R.id.lay_cart_detail)
    LinearLayout layCartDetail;

    @BindView(R.id.tv_cart_count)
    TextView tvCartCount;

    private ProductItem mProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mProduct = getIntent().getParcelableExtra(ARG_EXTRA_PARCELABLE_PRODUCT_ITEM);
        getSupportActionBar().setTitle(mProduct.getName());
        showProductDetail(mProduct);
        mShoppingCartManager.getProductQuantity(mProduct, new UpdateToCartListener(this));
    }

    @Override
    void injectComponent(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mProductManager.attach(mProduct.getId(), this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mProductManager.detach();
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


    private void showCartDetails(int count) {
        if (count == 0 && btnAddCart.getVisibility() == View.GONE) {
            btnAddCart.setVisibility(View.VISIBLE);
            layCartDetail.setVisibility(View.INVISIBLE);
        } else if (count > 0) {
            btnAddCart.setVisibility(View.GONE);
            layCartDetail.setVisibility(View.VISIBLE);
            tvCartCount.setText(count + " " + getString(R.string.text_product_added_to_cart));
        }
    }


    @OnClick(R.id.btn_add_cart)
    public void onAddClick() {
        mShoppingCartManager.updateToShoppingCart(mProduct, 1, new UpdateToCartListener(this));
    }

    @OnClick(R.id.btn_add)
    public void onAdd() {
        onAddClick();
    }

    @OnClick(R.id.btn_delete)
    public void onDelete() {
        mShoppingCartManager.updateToShoppingCart(mProduct, -1, new UpdateToCartListener(this));
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     LISTENERS                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////


    @Override
    public void onProductLoaded(ProductItem product) {
        showProductDetail(product);
    }

    @Override
    public void onError(String errorMsg) {

    }

    public static class UpdateToCartListener implements ResponseListener<Integer> {

        private final WeakReference<ProductDetailActivity> mReference;

        public UpdateToCartListener(ProductDetailActivity activity) {
            mReference = new WeakReference<>(activity);
        }

        @Override
        public void onResponse(Integer itemCount) {
            if (mReference.get() != null) {
                mReference.get().showCartDetails(itemCount);
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

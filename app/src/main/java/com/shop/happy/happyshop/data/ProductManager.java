package com.shop.happy.happyshop.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.shop.happy.happyshop.network.HappyShopService;
import com.shop.happy.happyshop.network.RestServiceFactory;
import com.shop.happy.happyshop.network.model.ProductDetailResponse;
import com.shop.happy.happyshop.network.model.ProductItem;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by karthikeyan on 22/8/17.
 */

public class ProductManager {

    private static final String TAG = ProductManager.class.getName();

    private final Context mContext;
    private final RestServiceFactory mRestServiceFactory;
    private Observer mObserver;

    public interface Observer {
        void onProductLoaded(ProductItem product);

        void onError(String errorMsg);
    }

    public ProductManager(Context mContext, RestServiceFactory mRestServiceFactory) {
        this.mContext = mContext;
        this.mRestServiceFactory = mRestServiceFactory;
    }

    public void attach(int productId, Observer observer) {
        this.mObserver = observer;
        loadProductDetails(productId);
    }

    public void detach() {
        mObserver = null;
    }

    private void loadProductDetails(int productId) {
        HappyShopService service = mRestServiceFactory.create(HappyShopService.class);
        Call<ProductDetailResponse> responseCall = service.getProductDetail(String.valueOf(productId));
        responseCall.enqueue(productDetailResponseCallback);
    }

    private Callback<ProductDetailResponse> productDetailResponseCallback = new Callback<ProductDetailResponse>() {
        @Override
        public void onResponse(Call<ProductDetailResponse> call, Response<ProductDetailResponse> response) {
            if (mObserver != null) {
                if (response.code() == 200) {
                    mObserver.onProductLoaded(response.body().getProduct());
                } else {
                    mObserver.onError(response.message());
                }
            }
        }

        @Override
        public void onFailure(Call<ProductDetailResponse> call, Throwable t) {
            if (mObserver != null) {

            }
        }
    };
}

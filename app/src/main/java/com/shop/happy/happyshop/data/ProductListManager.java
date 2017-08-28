package com.shop.happy.happyshop.data;

import android.content.Context;

import com.shop.happy.happyshop.network.HappyShopService;
import com.shop.happy.happyshop.network.RestServiceFactory;
import com.shop.happy.happyshop.network.model.ProductItem;
import com.shop.happy.happyshop.network.model.ProductListResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by karthikeyan on 27/8/17.
 */

public class ProductListManager {

    private static final String TAG = ProductManager.class.getName();

    private final Context mContext;
    private final RestServiceFactory mRestServiceFactory;
    private Observer mObserver;
    private String mCategory;

    private boolean isLoading = false;
    private int loadPage = 0;

    public interface Observer {
        void onProductsLoaded(ArrayList<ProductItem> productList, int pageLoaded);

        void onError(String errorMsg, Throwable t);
    }

    public ProductListManager(Context mContext, RestServiceFactory mRestServiceFactory) {
        this.mContext = mContext;
        this.mRestServiceFactory = mRestServiceFactory;
    }

    public void attach(Observer observer, String category) {
        this.mObserver = observer;
        this.mCategory = category;
        loadProducts();
    }

    public void refresh() {
        isLoading = false;
        loadPage = 0;
        loadProducts();
    }

    public void loadMoreItems() {
        if (!isLoading) {
            loadProducts();
        }
    }

    public void detach() {
        mObserver = null;
        reset();
    }

    private void reset() {
        isLoading = false;
        loadPage = 0;
    }

    private void loadProducts() {
        isLoading = true;
        loadPage++;
        HappyShopService service = mRestServiceFactory.create(HappyShopService.class);
        Call<ProductListResponse> responseCall = service.getProductList(mCategory, loadPage);
        responseCall.enqueue(produceListResponseCallback);
    }

    private Callback<ProductListResponse> produceListResponseCallback = new Callback<ProductListResponse>() {
        @Override
        public void onResponse(Call<ProductListResponse> call, Response<ProductListResponse> response) {
            isLoading = false;
            if (mObserver != null) {
                if (response.code() == 200) {
                    mObserver.onProductsLoaded(response.body().getProductList(), loadPage);
                } else {
                    mObserver.onError(response.message(), null);
                }
            }
        }

        @Override
        public void onFailure(Call<ProductListResponse> call, Throwable t) {
            isLoading = false;
            loadPage--;
            if (mObserver != null) {
                mObserver.onError("", t);
            }
        }
    };
}

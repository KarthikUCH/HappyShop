package com.shop.happy.happyshop.data;

import android.content.Context;

import com.shop.happy.happyshop.network.HappyShopService;
import com.shop.happy.happyshop.network.RestServiceFactory;
import com.shop.happy.happyshop.network.model.CategoryItem;
import com.shop.happy.happyshop.network.model.CategoryListResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by karthikeyan on 27/8/17.
 */

public class CategoryManager {

    private static final String TAG = CategoryManager.class.getName();

    private final Context mContext;
    private final RestServiceFactory mRestServiceFactory;
    private Observer mObserver;

    public interface Observer {
        void onCategoriesLoaded(ArrayList<CategoryItem> categoryLst);

        void onError(String errorMsg, Throwable t);
    }

    public CategoryManager(Context mContext, RestServiceFactory mRestServiceFactory) {
        this.mContext = mContext;
        this.mRestServiceFactory = mRestServiceFactory;
    }

    public void attach(Observer observer) {
        this.mObserver = observer;
        loadCategories();
    }

    public void refresh() {
        loadCategories();
    }

    public void detach() {
        mObserver = null;
    }


    private void loadCategories() {

        HappyShopService service = mRestServiceFactory.create(HappyShopService.class);
        Call<CategoryListResponse> responseCall = service.getCategoryList();
        responseCall.enqueue(categoryResponseCallback);

    }

    private Callback<CategoryListResponse> categoryResponseCallback = new Callback<CategoryListResponse>() {
        @Override
        public void onResponse(Call<CategoryListResponse> call, Response<CategoryListResponse> response) {
            if (mObserver != null) {
                if (response.code() == 200) {
                    mObserver.onCategoriesLoaded(response.body().getCategoryList());
                } else {
                    mObserver.onError(response.message(), null);
                }
            }

        }

        @Override
        public void onFailure(Call<CategoryListResponse> call, Throwable t) {
            if (mObserver != null) {
                mObserver.onError("", t);
            }
        }
    };
}

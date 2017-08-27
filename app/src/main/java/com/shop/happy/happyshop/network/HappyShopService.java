package com.shop.happy.happyshop.network;

import com.shop.happy.happyshop.network.model.CategoryListResponse;
import com.shop.happy.happyshop.network.model.ProductDetailResponse;
import com.shop.happy.happyshop.network.model.ProductListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by karthikeyan on 22/8/17.
 */

public interface HappyShopService {

    @GET("categories.json")
    Call<CategoryListResponse> getCategoryList();

    @GET("products.json")
    Call<ProductListResponse> getProductList(@Query("category") String category, @Query("page") int page);

    @GET("products/{id}.json")
    Call<ProductDetailResponse> getProductDetail(@Path("id") String id);

}

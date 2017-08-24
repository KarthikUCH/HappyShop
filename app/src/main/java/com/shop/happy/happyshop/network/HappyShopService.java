package com.shop.happy.happyshop.network;

import com.shop.happy.happyshop.network.model.ProductDetailResponse;
import com.shop.happy.happyshop.network.model.ProductListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by karthikeyan on 22/8/17.
 */

public interface HappyShopService {
    @GET("products.json")
    Call<ProductListResponse> getProductList();

    @GET("products/{id}.json")
    Call<ProductDetailResponse> getProductDetail(@Path("id") String id);

}

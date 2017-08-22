package com.shop.happy.happyshop.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by karthikeyan on 22/8/17.
 */

public class ProductDetailResponse {

    @SerializedName("products")
    private ProductItem product;
}

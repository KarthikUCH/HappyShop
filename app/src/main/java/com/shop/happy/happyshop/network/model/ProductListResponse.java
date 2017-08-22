package com.shop.happy.happyshop.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by karthikeyan on 22/8/17.
 */

public class ProductListResponse {

    @SerializedName("products")
    private List<ProductItem> productList;
}

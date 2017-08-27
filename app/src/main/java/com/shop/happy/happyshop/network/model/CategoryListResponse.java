package com.shop.happy.happyshop.network.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by karthikeyan on 27/8/17.
 */

public class CategoryListResponse {

    @SerializedName("categories")
    private ArrayList<CategoryItem> categoryList;

    public ArrayList<CategoryItem> getCategoryList() {
        return categoryList;
    }
}

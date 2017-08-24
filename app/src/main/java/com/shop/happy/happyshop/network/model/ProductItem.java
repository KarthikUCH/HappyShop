package com.shop.happy.happyshop.network.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by karthikeyan on 22/8/17.
 */

public class ProductItem {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("category")
    private String category;

    @SerializedName("description")
    private String description;

    @SerializedName("img_url")
    private String imgURL;

    @SerializedName("price")
    private String price;

    @SerializedName("under_sale")
    private boolean isUnderSale;

    public ProductItem(int id, String name, String category, String imgURL, String price, boolean isUnderSale) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.imgURL = imgURL;
        this.price = price;
        this.isUnderSale = isUnderSale;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getImgURL() {
        return imgURL;
    }

    public String getPrice() {
        return price;
    }

    public boolean isUnderSale() {
        return isUnderSale;
    }

    public String getDescription() {
        return description;
    }
}

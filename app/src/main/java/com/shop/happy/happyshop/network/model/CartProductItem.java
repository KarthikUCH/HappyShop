package com.shop.happy.happyshop.network.model;

/**
 * Created by karthikeyan on 25/8/17.
 */

public class CartProductItem extends ProductItem {

    private int quantity;


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() {
        return quantity;
    }
}

package com.shop.happy.happyshop;

import com.shop.happy.happyshop.network.model.ProductItem;

/**
 * Created by karthikeyan on 27/8/17.
 */

public class MockProductItem {

    private int id = 26;

    private String name = "";

    private String category = "Makeup";

    private String description = "Lorem ipsum dolor sit amet DownBoy 9.9g product description consectetur adipiscing elit, sed do eiusmod tempor incididunt.";

    private String imgURL = "http://luxola-assets-staging-nemesis.s3.amazonaws.com/images/pictures/9583/default_2890446cfff9f964de59276776e36052263c277f_1423036598_THEBALM_downboy.jpg";

    private String price = "2900.0";

    private boolean isUnderSale = true;

    public ProductItem getProduct() {
        return new ProductItem(id, name, category, description, imgURL, price, isUnderSale);
    }
}

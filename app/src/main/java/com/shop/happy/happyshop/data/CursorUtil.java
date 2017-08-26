package com.shop.happy.happyshop.data;

import android.database.Cursor;

import com.shop.happy.happyshop.data.DbConstants.ProductsTable;
import com.shop.happy.happyshop.data.DbConstants.ShoppingCartTable;

import com.shop.happy.happyshop.network.model.CartProductItem;
import com.shop.happy.happyshop.network.model.ProductItem;

/**
 * Created by karthikeyan on 24/8/17.
 */

public class CursorUtil {

    public static ProductItem getProduct(Cursor cursor) {
        ProductItem item = new ProductItem();
        item.setId(cursor.getInt(cursor.getColumnIndex(ProductsTable.COLUMN_PRODUCT_ID)));
        item.setName(cursor.getString(cursor.getColumnIndex(ProductsTable.COLUMN_PRODUCT_NAME)));
        item.setPrice(cursor.getString(cursor.getColumnIndex(ProductsTable.COLUMN_PRODUCT_PRICE)));
        item.setCategory(cursor.getString(cursor.getColumnIndex(ProductsTable.COLUMN_PRODUCT_CATEGORY)));
        item.setDescription(cursor.getString(cursor.getColumnIndex(ProductsTable.COLUMN_PRODUCT_DESCRIPTION)));
        item.setImgURL(cursor.getString(cursor.getColumnIndex(ProductsTable.COLUMN_PRODUCT_IMAGE_URL)));
        item.setUnderSale(cursor.getInt(cursor.getColumnIndex(ProductsTable.COLUMN_PRODUCT_UNDER_SALE)) > 0);

        return item;
    }

    public static CartProductItem getProductFromCart(Cursor cursor) {
        CartProductItem item = new CartProductItem();
        item.setId(cursor.getInt(cursor.getColumnIndex(ShoppingCartTable.COLUMN_CART_PRODUCT_ID)));
        item.setName(cursor.getString(cursor.getColumnIndex(ShoppingCartTable.COLUMN_CART_PRODUCT_NAME)));
        item.setPrice(cursor.getString(cursor.getColumnIndex(ShoppingCartTable.COLUMN_CART_PRODUCT_PRICE)));
        item.setImgURL(cursor.getString(cursor.getColumnIndex(ShoppingCartTable.COLUMN_CART_PRODUCT_IMAGE_URL)));
        item.setQuantity(cursor.getInt(cursor.getColumnIndex(ShoppingCartTable.COLUMN_CART_PRODUCT_QUANTITY)));
        item.setCategory(cursor.getString(cursor.getColumnIndex(ProductsTable.COLUMN_PRODUCT_CATEGORY)));
        item.setDescription(cursor.getString(cursor.getColumnIndex(ProductsTable.COLUMN_PRODUCT_DESCRIPTION)));
        item.setUnderSale(cursor.getInt(cursor.getColumnIndex(ProductsTable.COLUMN_PRODUCT_UNDER_SALE)) > 0);

        return item;
    }
}

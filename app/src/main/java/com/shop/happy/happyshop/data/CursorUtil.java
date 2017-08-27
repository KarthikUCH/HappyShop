package com.shop.happy.happyshop.data;

import android.database.Cursor;

import com.shop.happy.happyshop.data.DbConstants.ShoppingCartTable;
import com.shop.happy.happyshop.network.model.CartProductItem;

/**
 * Created by karthikeyan on 24/8/17.
 */

public class CursorUtil {

    public static CartProductItem getProductFromCart(Cursor cursor) {
        CartProductItem item = new CartProductItem();
        item.setId(cursor.getInt(cursor.getColumnIndex(ShoppingCartTable.COLUMN_CART_PRODUCT_ID)));
        item.setName(cursor.getString(cursor.getColumnIndex(ShoppingCartTable.COLUMN_CART_PRODUCT_NAME)));
        item.setPrice(cursor.getString(cursor.getColumnIndex(ShoppingCartTable.COLUMN_CART_PRODUCT_PRICE)));
        item.setImgURL(cursor.getString(cursor.getColumnIndex(ShoppingCartTable.COLUMN_CART_PRODUCT_IMAGE_URL)));
        item.setQuantity(cursor.getInt(cursor.getColumnIndex(ShoppingCartTable.COLUMN_CART_PRODUCT_QUANTITY)));
        item.setCategory(cursor.getString(cursor.getColumnIndex(ShoppingCartTable.COLUMN_CART_PRODUCT_CATEGORY)));
        item.setDescription(cursor.getString(cursor.getColumnIndex(ShoppingCartTable.COLUMN_CART_PRODUCT_DESCRIPTION)));

        return item;
    }
}

package com.shop.happy.happyshop.data;

import android.provider.BaseColumns;

/**
 * Created by karthikeyan on 22/8/17.
 */

public class DbConstants {

    public static final String DB_NAME = "happyshop.db";
    public static final int DB_VERSION = 1;
    private static final String TEXT_TYPE = " TEXT";
    private static final String INTEGER_TYPE = " INTEGER";
    private static final String LONG_TYPE = " LONG";
    private static final String AUTO_INCREMENT_TYPE = " AUTOINCREMENT";
    private static final String PRIMARY_KEY = " PRIMARY KEY";
    private static final String COMMA_SEP = ",";

    interface Tables {
        String SHOPPING_CART = "shopping_cart";
    }

    interface ShoppingCartTable extends BaseColumns {
        String COLUMN_CART_PRODUCT_ID = "id";
        String COLUMN_CART_PRODUCT_NAME = "name";
        String COLUMN_CART_PRODUCT_PRICE = "price";
        String COLUMN_CART_PRODUCT_IMAGE_URL = "img_url";
        String COLUMN_CART_PRODUCT_QUANTITY = "quantity";
        String COLUMN_CART_PRODUCT_CATEGORY = "category";
        String COLUMN_CART_PRODUCT_DESCRIPTION = "description";
    }

    // CREATE TABLE SQL QUERY

    public static final String SQL_CREATE_SHOPPING_CART_TABLE =
            "CREATE TABLE " + Tables.SHOPPING_CART + " ("
                    + ShoppingCartTable._ID + INTEGER_TYPE + PRIMARY_KEY + AUTO_INCREMENT_TYPE + COMMA_SEP
                    + ShoppingCartTable.COLUMN_CART_PRODUCT_ID + INTEGER_TYPE + COMMA_SEP
                    + ShoppingCartTable.COLUMN_CART_PRODUCT_NAME + TEXT_TYPE + COMMA_SEP
                    + ShoppingCartTable.COLUMN_CART_PRODUCT_PRICE + TEXT_TYPE + COMMA_SEP
                    + ShoppingCartTable.COLUMN_CART_PRODUCT_IMAGE_URL + TEXT_TYPE + COMMA_SEP
                    + ShoppingCartTable.COLUMN_CART_PRODUCT_CATEGORY + TEXT_TYPE + COMMA_SEP
                    + ShoppingCartTable.COLUMN_CART_PRODUCT_DESCRIPTION + TEXT_TYPE + COMMA_SEP
                    + ShoppingCartTable.COLUMN_CART_PRODUCT_QUANTITY + INTEGER_TYPE + COMMA_SEP
                    + " UNIQUE (" + ShoppingCartTable.COLUMN_CART_PRODUCT_ID + ") ON CONFLICT REPLACE)";

    // DROP TABLE SQL QUERY

    public static final String SQL_DROP_SHOPPING_CART_TABLE = "DROP TABLE IF EXISTS" + Tables.SHOPPING_CART;

}

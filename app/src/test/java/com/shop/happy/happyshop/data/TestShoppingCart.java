package com.shop.happy.happyshop.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.shop.happy.happyshop.MockProductItem;
import com.shop.happy.happyshop.network.model.CartProductItem;
import com.shop.happy.happyshop.network.model.ProductItem;
import com.shop.happy.happyshop.util.FileUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.robolectric.RobolectricTestRunner;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by karthikeyan on 27/8/17.
 */

@RunWith(RobolectricTestRunner.class)
public class TestShoppingCart {

    private static final int INSERT_QUANTITY = 2;

    @Mock
    Context mContext;

    private static final String DB_NAME = "happyshop.db";
    private String mDbPath;
    SQLiteDatabase mDbHelper;

    private ShoppingCartManager mShoppingCartManager;

    @Before
    public void setUp() throws Exception {
        File Database = FileUtil.getFileFromPath(this, DB_NAME);
        mDbPath = Database.getPath();
        mDbHelper = SQLiteDatabase.openDatabase(mDbPath, null, SQLiteDatabase.OPEN_READWRITE);

        mShoppingCartManager = new ShoppingCartManager(mContext, mDbHelper);
    }

    @Test
    public void testInsertProductToCart() {
        ProductItem product = new MockProductItem().getProduct();
        long id = mShoppingCartManager.insertProduct(product, INSERT_QUANTITY);
        Assert.assertTrue(id > 0);

        int quantity = mShoppingCartManager.getProductQuantityInCart(product.getId());
        Assert.assertTrue(quantity == INSERT_QUANTITY);

        int rowsAffected = mShoppingCartManager.removeProduct(product.getId());
        Assert.assertTrue(rowsAffected == 1);

    }

    @Test
    public void testGetProductsFromCart() {
        ArrayList<CartProductItem> products = mShoppingCartManager.getProductsFromCart();
        Assert.assertNotNull(products);
    }

    @Test
    public void testGetTotalItemsInCart() {
        int count = mShoppingCartManager.getTotalItemsInCart();
        Assert.assertTrue(count > 0);
    }

    @After
    public void tearDown() throws Exception {
        mDbHelper.close();
    }
}

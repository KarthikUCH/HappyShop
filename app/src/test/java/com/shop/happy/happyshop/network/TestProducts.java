package com.shop.happy.happyshop.network;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.shop.happy.happyshop.data.CategoryManager;
import com.shop.happy.happyshop.data.ProductListManager;
import com.shop.happy.happyshop.data.ProductManager;
import com.shop.happy.happyshop.network.model.ProductItem;
import com.shop.happy.happyshop.util.FileUtil;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import okhttp3.OkHttpClient;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static org.mockito.Mockito.when;

/**
 * Created by karthikeyan on 27/8/17.
 */

@RunWith(MockitoJUnitRunner.class)
public class TestProducts {
    private MockWebServer mMockWebServer;

    @Mock
    Context mContext;
    @Mock
    RestServiceFactory mRestServiceFactory;

    private ProductListManager mProductManager;

    private ArrayList<ProductItem> mProducts;

    private final CountDownLatch latch = new CountDownLatch(1);

    @Before
    public void setUp() throws Exception {
        mMockWebServer = new MockWebServer();
        mMockWebServer.start();
        mProductManager = new ProductListManager(mContext, mRestServiceFactory);
    }


    @Test
    public void testProductList_Success() throws Exception {
        String fileName = "makeup_product_list_response.json";
        String category = "Makeup";
        mProducts = null;
        mMockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(FileUtil.getFileContentFromPath(this, fileName)));

        when(mRestServiceFactory.create(HappyShopService.class)).thenReturn(getHappyShopService());
        mProductManager.attach(new ProductListManager.Observer() {
            @Override
            public void onProductsLoaded(ArrayList<ProductItem> productList, int pageLoaded) {
                latch.countDown();
                mProducts = productList;
            }

            @Override
            public void onError(String errorMsg, Throwable t) {
                latch.countDown();
            }
        }, category);

        latch.await();
        Assert.assertNotNull(mProducts);
    }

    @Test
    public void testProductList_Failure() throws Exception {
        String category = "Makeup";
        mProducts = null;
        mMockWebServer.enqueue(new MockResponse().setResponseCode(400)
                .setBody(""));

        when(mRestServiceFactory.create(HappyShopService.class)).thenReturn(getHappyShopService());
        mProductManager.attach(new ProductListManager.Observer() {
            @Override
            public void onProductsLoaded(ArrayList<ProductItem> productList, int pageLoaded) {
                latch.countDown();
                mProducts = productList;
            }

            @Override
            public void onError(String errorMsg, Throwable t) {
                latch.countDown();
            }
        }, category);

        latch.await();
        Assert.assertNull(mProducts);
    }


    @After
    public void tearDown() throws Exception {
        mMockWebServer.shutdown();
    }

    private HappyShopService getHappyShopService() {
        HappyShopService service = new Retrofit.Builder()
                .baseUrl(mMockWebServer.url("").toString())
                .client(new OkHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(HappyShopService.class);
        return service;
    }

}

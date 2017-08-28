package com.shop.happy.happyshop.network;

import android.content.Context;

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
public class TestProductDetail {
    private MockWebServer mMockWebServer;

    @Mock
    Context mContext;
    @Mock
    RestServiceFactory mRestServiceFactory;

    private ProductManager mProductManager;
    private ProductItem mProduct;

    private final CountDownLatch latch = new CountDownLatch(1);

    @Before
    public void setUp() throws Exception {
        mMockWebServer = new MockWebServer();
        mMockWebServer.start();
        mProductManager = new ProductManager(mContext, mRestServiceFactory);
    }

    @Test
    public void testProductList_Success() throws Exception {
        String fileName = "product_detail_response.json";
        int projectId = 26;
        mProduct = null;
        mMockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(FileUtil.getFileContentFromPath(this, fileName)));

        when(mRestServiceFactory.create(HappyShopService.class)).thenReturn(getHappyShopService());
        mProductManager.attach(projectId, new ProductManager.Observer() {
            @Override
            public void onProductLoaded(ProductItem product) {
                latch.countDown();
                mProduct = product;
            }

            @Override
            public void onError(String errorMsg) {
                latch.countDown();
            }
        });

        latch.await();
        Assert.assertNotNull(mProduct);
    }

    @Test
    public void testProductList_Failure() throws Exception {
        int projectId = 26;
        mProduct = null;
        mMockWebServer.enqueue(new MockResponse().setResponseCode(400)
                .setBody(""));

        when(mRestServiceFactory.create(HappyShopService.class)).thenReturn(getHappyShopService());
        mProductManager.attach(projectId, new ProductManager.Observer() {
            @Override
            public void onProductLoaded(ProductItem product) {
                latch.countDown();
                mProduct = product;
            }

            @Override
            public void onError(String errorMsg) {
                latch.countDown();
            }
        });

        latch.await();
        Assert.assertNull(mProduct);
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

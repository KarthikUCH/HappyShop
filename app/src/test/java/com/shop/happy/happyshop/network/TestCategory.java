package com.shop.happy.happyshop.network;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.shop.happy.happyshop.data.CategoryManager;
import com.shop.happy.happyshop.network.model.CategoryItem;
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
public class TestCategory {

    private MockWebServer mMockWebServer;

    @Mock
    Context mContext;
    @Mock
    SQLiteDatabase mDbHelper;
    @Mock
    RestServiceFactory mRestServiceFactory;
    @Mock
    CategoryManager.Observer mObserver;

    private CategoryManager mCategoryManager;

    private ArrayList<CategoryItem> mCategories = null;

    private final CountDownLatch latch = new CountDownLatch(1);


    @Before
    public void setUp() throws Exception {
        mMockWebServer = new MockWebServer();
        mMockWebServer.start();
        mCategoryManager = new CategoryManager(mContext, mRestServiceFactory);
    }

    /**
     * @throws Exception
     * @see <a href="https://stackoverflow.com/a/35940990/2790197">Reference</a>
     */
    @Test
    public void testCategoryList_Success() throws Exception {
        String fileName = "category_list_response.json";
        mCategories = null;
        mMockWebServer.enqueue(new MockResponse().setResponseCode(200)
                .setBody(FileUtil.getFileContentFromPath(this, fileName)));

        when(mRestServiceFactory.create(HappyShopService.class)).thenReturn(getHappyShopService());
        mCategoryManager.attach(new CategoryManager.Observer() {
            @Override
            public void onCategoriesLoaded(ArrayList<CategoryItem> categoryLst) {
                latch.countDown();
                mCategories = categoryLst;
            }

            @Override
            public void onError(String errorMsg, Throwable t) {
                latch.countDown();
            }
        });

        latch.await();
        Assert.assertNotNull(mCategories);
    }

    @Test
    public void testCategoryList_Failure() throws Exception {
        mCategories = null;
        mMockWebServer.enqueue(new MockResponse().setResponseCode(400)
                .setBody(""));

        when(mRestServiceFactory.create(HappyShopService.class)).thenReturn(getHappyShopService());
        mCategoryManager.attach(new CategoryManager.Observer() {
            @Override
            public void onCategoriesLoaded(ArrayList<CategoryItem> categoryLst) {
                latch.countDown();
                mCategories = categoryLst;
            }

            @Override
            public void onError(String errorMsg, Throwable t) {
                latch.countDown();
            }
        });

        latch.await();
        Assert.assertNull(mCategories);
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

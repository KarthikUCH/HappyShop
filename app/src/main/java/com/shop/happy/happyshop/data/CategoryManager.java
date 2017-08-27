package com.shop.happy.happyshop.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.shop.happy.happyshop.data.DbConstants.ProductsTable;
import com.shop.happy.happyshop.data.DbConstants.Tables;
import com.shop.happy.happyshop.network.HappyShopService;
import com.shop.happy.happyshop.network.RestServiceFactory;
import com.shop.happy.happyshop.network.model.ProductItem;
import com.shop.happy.happyshop.network.model.ProductListResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by karthikeyan on 22/8/17.
 */

public class CategoryManager {

    private static final String TAG = CategoryManager.class.getName();

    private final Context mContext;
    private final SQLiteDatabase mDbHelper;
    private final RestServiceFactory mRestServiceFactory;
    private Observer mObserver;

    public interface Observer {
        void onCategoriesLoaded(ArrayList<String> categoryLst);
    }

    public CategoryManager(Context context, SQLiteDatabase dbHelper, RestServiceFactory restServiceFactory) {
        this.mContext = context;
        this.mDbHelper = dbHelper;
        this.mRestServiceFactory = restServiceFactory;
    }

    public void attach(Observer observer) {
        this.mObserver = observer;
        displayCategories(true);
    }

    public void detach() {
        mObserver = null;
    }

    /**
     * To list the categories
     *
     * @param refreshProduct fetch products for server if true
     */
    private void displayCategories(boolean refreshProduct) {
        Observable.defer(() -> Observable.just(getCategories()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(categories -> {
                    Log.i(TAG, "OnNext");
                    if (mObserver != null) {
                        mObserver.onCategoriesLoaded(categories);
                    }
                }, throwable -> {
                    Log.e(TAG, "onError", throwable);
                }, () -> {
                    Log.i(TAG, "onCompleted");
                    if (refreshProduct) {
                        fetchProducts();
                    }
                });
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     DATABASE CALL                                          //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * To get the category list form {@link Tables#PRODUCTS}
     *
     * @return category list
     */
    @WorkerThread
    private ArrayList<String> getCategories() {
        ArrayList<String> categoryLst = new ArrayList<>();
        String[] columns = new String[]{ProductsTable.COLUMN_PRODUCT_CATEGORY};

        Cursor cursor = mDbHelper.query(Tables.PRODUCTS, columns, null, null
                , ProductsTable.COLUMN_PRODUCT_CATEGORY, null, ProductsTable.COLUMN_PRODUCT_CATEGORY + " ASC");

        while (cursor.moveToNext()) {
            categoryLst.add(cursor.getString(0));
        }
        if (cursor != null) {
            cursor.close();
        }
        return categoryLst;
    }


    /**
     * To parse and insert the products into {@link Tables#PRODUCTS}
     *
     * @param productListResponse Product list response
     */
    public void parseProducts(ProductListResponse productListResponse) {

        mDbHelper.beginTransaction();

        SQLiteStatement insert = mDbHelper.compileStatement(DbConstants.INSERT_PRODUCT_QUERY);
        for (ProductItem item : productListResponse.getProductList()) {
            insert.bindDouble(1, item.getId());
            insert.bindString(2, item.getName());
            insert.bindString(3, item.getPrice());
            insert.bindString(4, item.getImgURL());
            insert.bindString(5, item.getCategory());
            insert.bindString(6, "");
            insert.bindDouble(7, item.isUnderSale() ? 1 : 0);
            insert.execute();
        }
        mDbHelper.setTransactionSuccessful();
        mDbHelper.endTransaction();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     NETWORK CALL                                           //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * Background task to execute {@link #retrieveProducts()}
     */
    private void fetchProducts() {

        Observable.defer(() -> Observable.just(retrieveProducts()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    Log.i(TAG, "onNext");
                }, throwable -> {
                    Log.e(TAG, "onError", throwable);
                }, () -> {
                    Log.i(TAG, "onCompleted");
                    displayCategories(false);
                });
    }

    /**
     * To perform http service to fetch products from server
     *
     * @return
     */
    @WorkerThread
    private boolean retrieveProducts() {
        try {
            HappyShopService service = mRestServiceFactory.create(HappyShopService.class);
            Call<ProductListResponse> responseCall = service.getProductList();
            Response<ProductListResponse> response = responseCall.execute();
            if (response.code() == 200) {
                parseProducts(response.body());
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }
}

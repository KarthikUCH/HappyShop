package com.shop.happy.happyshop.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.shop.happy.happyshop.data.DbConstants.ProductsTable;
import com.shop.happy.happyshop.data.DbConstants.Tables;
import com.shop.happy.happyshop.network.RestServiceFactory;
import com.shop.happy.happyshop.network.model.ProductItem;

import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by karthikeyan on 22/8/17.
 */

public class ProductManager {

    private static final String TAG = ProductManager.class.getName();

    private final Context mContext;
    private final SQLiteDatabase mDbHelper;
    private final RestServiceFactory mRestServiceFactory;
    private Observer mObserver;

    public interface Observer {
        void onProductsLoaded(ArrayList<ProductItem> productList);
    }

    public ProductManager(Context mContext, SQLiteDatabase mDbHelper, RestServiceFactory mRestServiceFactory) {
        this.mContext = mContext;
        this.mDbHelper = mDbHelper;
        this.mRestServiceFactory = mRestServiceFactory;
    }

    public void attach(Observer observer, String category) {
        this.mObserver = observer;
        displayProducts(category);
    }

    public void detach() {
        mObserver = null;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     DATABASE CALL                                          //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private void displayProducts(String category) {
        Observable.defer(() -> Observable.just(getProducts(category)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(products -> {
                    Log.i(TAG, "OnNext");
                    if (mObserver != null) {
                        mObserver.onProductsLoaded(products);
                    }
                }, throwable -> {
                    Log.e(TAG, "onError", throwable);
                }, () -> {
                    Log.i(TAG, "onCompleted");
                });
    }


    @WorkerThread
    private ArrayList<ProductItem> getProducts(String category) {
        ArrayList<ProductItem> items = new ArrayList<>();

        String selection = ProductsTable.COLUMN_PRODUCT_CATEGORY + " =?";
        String[] selectionArgs = new String[]{category};

        Cursor cursor = mDbHelper.query(Tables.PRODUCTS, null, selection, selectionArgs
                , null, null, ProductsTable.COLUMN_PRODUCT_NAME + " ASC");

        while (cursor.moveToNext()) {
            items.add(CursorUtil.getProduct(cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        return items;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     NETWORK CALL                                           //
    ////////////////////////////////////////////////////////////////////////////////////////////////
}

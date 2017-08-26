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
import com.shop.happy.happyshop.network.ResponseListener;
import com.shop.happy.happyshop.network.RestServiceFactory;
import com.shop.happy.happyshop.network.model.ProductDetailResponse;
import com.shop.happy.happyshop.network.model.ProductItem;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Response;
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

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     DATABASE CALL                                          //
    ////////////////////////////////////////////////////////////////////////////////////////////////


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

    @WorkerThread
    public void parseProductDetail(ProductItem product) {

        mDbHelper.beginTransaction();

        SQLiteStatement insert = mDbHelper.compileStatement(DbConstants.INSERT_PRODUCT_QUERY);

        insert.bindDouble(1, product.getId());
        insert.bindString(2, product.getName());
        insert.bindString(3, product.getPrice());
        insert.bindString(4, product.getImgURL());
        insert.bindString(5, product.getCategory());
        insert.bindString(6, product.getDescription());
        insert.bindDouble(7, product.isUnderSale() ? 1 : 0);
        insert.execute();

        mDbHelper.setTransactionSuccessful();
        mDbHelper.endTransaction();
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     NETWORK CALL                                           //
    ////////////////////////////////////////////////////////////////////////////////////////////////


    public void fetchProductDetail(int productId, ResponseListener<ProductItem> product) {

        Observable.defer(() -> Observable.just(retrieveProductDetail(productId)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    Log.i(TAG, "onNext");
                    product.onResponse(result);
                }, throwable -> {
                    Log.e(TAG, "onError", throwable);
                }, () -> {
                    Log.i(TAG, "onCompleted");
                });
    }

    @WorkerThread
    private ProductItem retrieveProductDetail(int productId) {
        try {
            HappyShopService service = mRestServiceFactory.create(HappyShopService.class);
            Call<ProductDetailResponse> responseCall = service.getProductDetail(String.valueOf(productId));
            Response<ProductDetailResponse> response = responseCall.execute();
            if (response.code() == 200) {
                ProductItem item = response.body().getProduct();
                parseProductDetail(item);
                return item;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}

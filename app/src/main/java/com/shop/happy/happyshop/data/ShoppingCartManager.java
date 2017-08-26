package com.shop.happy.happyshop.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.WorkerThread;
import android.util.Log;

import com.shop.happy.happyshop.R;
import com.shop.happy.happyshop.data.DbConstants.ShoppingCartTable;
import com.shop.happy.happyshop.data.DbConstants.ProductsTable;
import com.shop.happy.happyshop.data.DbConstants.Tables;

import com.shop.happy.happyshop.network.ResponseListener;
import com.shop.happy.happyshop.network.model.CartProductItem;
import com.shop.happy.happyshop.network.model.ProductItem;
import com.shop.happy.happyshop.ui.widget.BadgeDrawable;

import java.util.ArrayList;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by karthikeyan on 25/8/17.
 */

public class ShoppingCartManager {

    private static final String TAG = ShoppingCartManager.class.getName();

    private final Context mContext;
    private final SQLiteDatabase mDbHelper;
    private int totalItemInCart;
    private Observer mObserver;

    public interface Observer {
        void onProductsLoaded(ArrayList<CartProductItem> productList);
    }

    public ShoppingCartManager(Context mContext, SQLiteDatabase dbHelper) {
        this.mContext = mContext;
        this.mDbHelper = dbHelper;
        calculateTotalItemsInCart();
    }

    public void attach(Observer observer) {
        this.mObserver = observer;
        displayCartProducts();
    }

    public void detach() {
        this.mObserver = null;
    }

    private void displayCartProducts() {
        Observable.defer(() -> Observable.just(getProductsFromCart()))
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


    public int getCount() {
        return totalItemInCart;
    }

    public void calculateTotalItemsInCart() {

        Observable.defer(() -> Observable.just(getTotalItemsInCart()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(count -> {
                    totalItemInCart = count;
                    Log.i(TAG, "OnNext");
                }, throwable -> {
                    Log.e(TAG, "onError", throwable);
                }, () -> {
                    Log.i(TAG, "onCompleted");
                });

    }

    public void addToShoppingCart(ProductItem product, int quantity, ResponseListener<Boolean> listener) {

        Observable.defer(() -> Observable.just(getProductQuantityInCart(product.getId())))
                .map(currentQuantity -> {
                    int total = currentQuantity + quantity;
                    insertProduct(product, total);
                    return true;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    totalItemInCart += quantity;
                    Log.i(TAG, "OnNext");
                }, throwable -> {
                    Log.e(TAG, "onError", throwable);
                }, () -> {
                    Log.i(TAG, "onCompleted");
                    listener.onResponse(true);
                });

    }

    public void removeProductFromCart(CartProductItem product, ResponseListener<CartProductItem> listener) {
        Observable.defer(() -> Observable.just(removeProduct(product.getId())))
                .map(integer -> {
                    if (integer > 0) {
                        totalItemInCart = getTotalItemsInCart();
                    }
                    return integer > 0 ? true : false;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result) {
                        listener.onResponse(product);
                    }
                    Log.i(TAG, "OnNext");
                }, throwable -> {
                    Log.e(TAG, "onError", throwable);
                }, () -> {
                    Log.i(TAG, "onCompleted");

                });
    }

    public void refreshBadgeCount(Context context, LayerDrawable icon) {

        BadgeDrawable badge;

        // Reuse drawable if possible

        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_badge);
        if (reuse != null && reuse instanceof BadgeDrawable) {
            badge = (BadgeDrawable) reuse;
        } else {
            badge = new BadgeDrawable(context);
        }

        badge.setCount(String.valueOf(getCount()));
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_badge, badge);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     DATABASE CALL                                          //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @WorkerThread
    private int getProductQuantityInCart(int productId) {
        int quantity = 0;
        String selection = ShoppingCartTable.COLUMN_CART_PRODUCT_ID + " =?";
        String[] selectionArgs = new String[]{String.valueOf(productId)};

        Cursor cursor = mDbHelper.query(Tables.SHOPPING_CART, new String[]{ShoppingCartTable.COLUMN_CART_PRODUCT_QUANTITY},
                selection, selectionArgs, null, null, null);

        if (cursor.moveToFirst()) {
            quantity = cursor.getInt(0);
        }
        if (cursor != null) {
            cursor.close();
        }
        return quantity;
    }

    @WorkerThread
    private long insertProduct(ProductItem product, int quantity) {
        ContentValues values = new ContentValues();
        values.put(ShoppingCartTable.COLUMN_CART_PRODUCT_ID, product.getId());
        values.put(ShoppingCartTable.COLUMN_CART_PRODUCT_NAME, product.getName());
        values.put(ShoppingCartTable.COLUMN_CART_PRODUCT_PRICE, product.getPrice());
        values.put(ShoppingCartTable.COLUMN_CART_PRODUCT_IMAGE_URL, product.getImgURL());
        values.put(ShoppingCartTable.COLUMN_CART_PRODUCT_QUANTITY, quantity);

        return mDbHelper.insert(Tables.SHOPPING_CART, null, values);
    }

    private int removeProduct(int productId) {
        String where = ShoppingCartTable.COLUMN_CART_PRODUCT_ID + " =? ";
        String[] whereArgs = new String[]{String.valueOf(productId)};

        return mDbHelper.delete(Tables.SHOPPING_CART, where, whereArgs);
    }

    @WorkerThread
    private int getTotalItemsInCart() {
        int quantity = 0;
        String query = "SELECT SUM(" + ShoppingCartTable.COLUMN_CART_PRODUCT_QUANTITY + ") FROM " + Tables.SHOPPING_CART;
        Cursor cursor = mDbHelper.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            quantity = cursor.getInt(0);
        }
        if (cursor != null) {
            cursor.close();
        }
        return quantity;
    }

    @WorkerThread
    private ArrayList<CartProductItem> getProductsFromCart() {
        ArrayList<CartProductItem> items = new ArrayList<>();
        String query = "SELECT A." + ShoppingCartTable.COLUMN_CART_PRODUCT_ID + ",A." + ShoppingCartTable.COLUMN_CART_PRODUCT_NAME
                + ",A." + ShoppingCartTable.COLUMN_CART_PRODUCT_PRICE + ",A." + ShoppingCartTable.COLUMN_CART_PRODUCT_IMAGE_URL
                + ",A." + ShoppingCartTable.COLUMN_CART_PRODUCT_QUANTITY + ",B." + ProductsTable.COLUMN_PRODUCT_DESCRIPTION
                + ",B." + ProductsTable.COLUMN_PRODUCT_CATEGORY + ",B." + ProductsTable.COLUMN_PRODUCT_UNDER_SALE
                + " FROM " + Tables.SHOPPING_CART + " A LEFT JOIN " + Tables.PRODUCTS + " B ON A.id = B.id "
                + " ORDER BY A." + ShoppingCartTable.COLUMN_CART_PRODUCT_NAME + " ASC";

        Cursor cursor = mDbHelper.rawQuery(query, null);

        while (cursor.moveToNext()) {
            items.add(CursorUtil.getProductFromCart(cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        return items;
    }
}

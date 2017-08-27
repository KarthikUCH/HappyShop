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

    /**
     * To list the product added to the cart
     */
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

    /**
     * @return the number of item added to the cart
     */
    public int getCount() {
        return totalItemInCart;
    }

    /**
     * To calculate the number of item added to the cart
     */
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

    /**
     * To update products to the cart
     *
     * @param product {@link ProductItem} to be updated
     * @param quantity number of item to be added/removed
     * @param listener
     */
    public void updateToShoppingCart(ProductItem product, int quantity, ResponseListener<Integer> listener) {

        Observable.defer(() -> Observable.just(getProductQuantityInCart(product.getId())))
                .map(currentQuantity -> {
                    int total = currentQuantity + quantity;
                    if (total > 0) {
                        insertProduct(product, total);
                    } else {
                        removeProduct(product.getId());
                    }
                    return total;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if (result >= 0) {
                        listener.onResponse(result);
                        totalItemInCart += quantity;
                    }
                    Log.i(TAG, "OnNext");
                }, throwable -> {
                    Log.e(TAG, "onError", throwable);
                }, () -> {
                    Log.i(TAG, "onCompleted");
                });

    }

    /**
     * To delete the product from the cart
     * @param product {@link ProductItem} to be removed
     * @param listener
     */
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

    /**
     * To get the product quantity added to the cart
     * @param product {@link ProductItem}
     * @param listener
     */
    public void getProductQuantity(ProductItem product, ResponseListener<Integer> listener) {
        Observable.defer(() -> Observable.just(getProductQuantityInCart(product.getId())))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    listener.onResponse(result);
                    Log.i(TAG, "OnNext");
                }, throwable -> {
                    Log.e(TAG, "onError", throwable);
                }, () -> {
                    Log.i(TAG, "onCompleted");
                });
    }

    /**
     * To update the badge count in action bar
     * @param context
     * @param icon
     */
    public void setBadgeCount(Context context, LayerDrawable icon) {

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

    /**
     * To get the product quantity added to the cart
     * @param productId unique Id of a product
     * @return
     */
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

    /**
     * To insert {@link ProductItem} to cart
     * @param product
     * @param quantity number of item to be added
     * @return
     */
    @WorkerThread
    private long insertProduct(ProductItem product, int quantity) {
        ContentValues values = new ContentValues();
        values.put(ShoppingCartTable.COLUMN_CART_PRODUCT_ID, product.getId());
        values.put(ShoppingCartTable.COLUMN_CART_PRODUCT_NAME, product.getName());
        values.put(ShoppingCartTable.COLUMN_CART_PRODUCT_PRICE, product.getPrice());
        values.put(ShoppingCartTable.COLUMN_CART_PRODUCT_IMAGE_URL, product.getImgURL());
        values.put(ShoppingCartTable.COLUMN_CART_PRODUCT_CATEGORY, product.getCategory());
        values.put(ShoppingCartTable.COLUMN_CART_PRODUCT_DESCRIPTION, product.getDescription());
        values.put(ShoppingCartTable.COLUMN_CART_PRODUCT_QUANTITY, quantity);

        return mDbHelper.insert(Tables.SHOPPING_CART, null, values);
    }

    /**
     * To delete {@link ProductItem} from cart
     * @param productId unique id of product
     * @return
     */
    @WorkerThread
    private int removeProduct(int productId) {
        String where = ShoppingCartTable.COLUMN_CART_PRODUCT_ID + " =? ";
        String[] whereArgs = new String[]{String.valueOf(productId)};

        return mDbHelper.delete(Tables.SHOPPING_CART, where, whereArgs);
    }

    /**
     * @return total number of item in cart
     */
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

    /**
     * To fetch the list of products from cart
     * @return {@link CartProductItem} list
     */
    @WorkerThread
    private ArrayList<CartProductItem> getProductsFromCart() {
        ArrayList<CartProductItem> items = new ArrayList<>();

        Cursor cursor = mDbHelper.query(Tables.SHOPPING_CART, null, null , null, null, null,
                ShoppingCartTable.COLUMN_CART_PRODUCT_NAME+" ASC");

        while (cursor.moveToNext()) {
            items.add(CursorUtil.getProductFromCart(cursor));
        }
        if (cursor != null) {
            cursor.close();
        }
        return items;
    }
}

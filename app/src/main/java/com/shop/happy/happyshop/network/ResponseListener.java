package com.shop.happy.happyshop.network;

/**
 * Created by karthikeyan on 25/8/17.
 */

public interface ResponseListener<T> {

    /**
     * Fired when request is successful.
     *
     * @param response result.
     */
    void onResponse(T response);

    /**
     * Fired when request is failed.
     *
     * @param errorMessage error message or null.
     */
    void onError(String errorMessage);

    /**
     * Fired when request is completed.
     */
    void onCompleted();
}

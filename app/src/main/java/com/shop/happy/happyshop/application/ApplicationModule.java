package com.shop.happy.happyshop.application;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.shop.happy.happyshop.data.CategoryManager;
import com.shop.happy.happyshop.data.DbManager;
import com.shop.happy.happyshop.data.ProductManager;
import com.shop.happy.happyshop.data.ShoppingCartManager;
import com.shop.happy.happyshop.network.RestServiceFactory;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by karthikeyan on 22/8/17.
 */

@Module
public class ApplicationModule {

    private Application mApp;

    ApplicationModule(Application app) {
        mApp = app;
    }

    @Singleton
    @Provides
    RestServiceFactory providesRestServiceFactory() {
        return new RestServiceFactory.Impl();
    }

    @Provides
    SQLiteDatabase providesSqLiteDatabase() {
        return DbManager.getInstance(mApp).getDbHelper();
    }

    @Singleton
    @Provides
    CategoryManager providesCategoryManager(SQLiteDatabase sqLiteDatabase, RestServiceFactory restServiceFactory) {
        return new CategoryManager(mApp, sqLiteDatabase, restServiceFactory);
    }

    @Singleton
    @Provides
    ProductManager providesProductManager(SQLiteDatabase sqLiteDatabase, RestServiceFactory restServiceFactory) {
        return new ProductManager(mApp, sqLiteDatabase, restServiceFactory);
    }

    @Singleton
    @Provides
    ShoppingCartManager providesShoppingCartManager(SQLiteDatabase sqLiteDatabase) {
        return new ShoppingCartManager(mApp, sqLiteDatabase);
    }


}

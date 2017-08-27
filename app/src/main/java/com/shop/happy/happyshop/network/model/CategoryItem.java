package com.shop.happy.happyshop.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by karthikeyan on 27/8/17.
 */

public class CategoryItem implements Parcelable {

    @SerializedName("name")
    private String name;

    @SerializedName("products_count")
    private int productCount;

    public CategoryItem(String name, int productCount) {
        this.name = name;
        this.productCount = productCount;
    }


    // GETTERS
    public String getName() {
        return name;
    }

    public int getProductCount() {
        return productCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeInt(this.productCount);
    }

    private CategoryItem(Parcel in){
        this.name = in.readString();
        this.productCount= in.readInt();
    }

    public static final Parcelable.Creator<CategoryItem> CREATOR = new Creator<CategoryItem>() {
        @Override
        public CategoryItem createFromParcel(Parcel source) {
            return new CategoryItem(source);
        }

        @Override
        public CategoryItem[] newArray(int size) {
            return new CategoryItem[size];
        }
    };
}

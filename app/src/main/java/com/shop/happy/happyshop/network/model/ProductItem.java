package com.shop.happy.happyshop.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by karthikeyan on 22/8/17.
 */

public class ProductItem implements Parcelable {

    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("category")
    private String category;

    @SerializedName("description")
    private String description;

    @SerializedName("img_url")
    private String imgURL;

    @SerializedName("price")
    private String price;

    @SerializedName("under_sale")
    private boolean isUnderSale;


    public ProductItem() {

    }

    public ProductItem(int id, String name, String category, String imgURL, String price, boolean isUnderSale) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.imgURL = imgURL;
        this.price = price;
        this.isUnderSale = isUnderSale;
    }

    // GETTERS
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public String getImgURL() {
        return imgURL;
    }

    public String getPrice() {
        return price;
    }

    public boolean isUnderSale() {
        return isUnderSale;
    }

    public String getDescription() {
        return description;
    }

    // SETTERS
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setUnderSale(boolean underSale) {
        isUnderSale = underSale;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.price);
        dest.writeString(this.category);
        dest.writeString(this.description);
        dest.writeString(this.imgURL);
        dest.writeByte((byte) (this.isUnderSale ? 1 : 0));

        /*dest.writeStringArray(new String[]{String.valueOf(this.id), this.name, this.price,
                this.category, this.description, this.imgURL, String.valueOf(this.isUnderSale)});*/
    }

    private ProductItem(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.price = in.readString();
        this.category = in.readString();
        this.description = in.readString();
        this.imgURL = in.readString();
        this.isUnderSale = in.readByte() != 0;
    }

    public static final Parcelable.Creator<ProductItem> CREATOR = new Creator<ProductItem>() {
        @Override
        public ProductItem createFromParcel(Parcel source) {
            return new ProductItem(source);
        }

        @Override
        public ProductItem[] newArray(int size) {
            return new ProductItem[size];
        }
    };
}

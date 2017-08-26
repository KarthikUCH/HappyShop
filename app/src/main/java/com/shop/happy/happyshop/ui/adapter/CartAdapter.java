package com.shop.happy.happyshop.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shop.happy.happyshop.R;
import com.shop.happy.happyshop.network.model.CartProductItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by karthikeyan on 25/8/17.
 */

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartProductViewHolder> {

    private final Context mContext;
    private ArrayList<CartProductItem> mProducts;
    private final CartProductClickListener mCartProductClickListner;

    public interface CartProductClickListener {
        void onRemove(CartProductItem product);
    }

    public CartAdapter(Context mContext, ArrayList<CartProductItem> products, CartProductClickListener mCartProductClickListner) {
        this.mContext = mContext;
        this.mProducts = products;
        this.mCartProductClickListner = mCartProductClickListner;
    }

    @Override
    public CartProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_cart_product, parent, false);
        return new CartProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartProductViewHolder holder, int position) {

        CartProductItem item = mProducts.get(position);

        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(item.getPrice());
        holder.tvQuantity.setText(mContext.getString(R.string.text_product_quantity) + "  " + String.valueOf(item.getQuantity()));

        holder.imgRemove.setOnClickListener(v -> mCartProductClickListner.onRemove(item));

        Glide.with(mContext)
                .load(item.getImgURL())
                .into(holder.imgProduct);
    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public void swapData(ArrayList<CartProductItem> productList) {
        this.mProducts = productList;
        notifyDataSetChanged();
    }

    public void removeProduct(CartProductItem product) {
        int position = -1;
        for (int i = 0; i < mProducts.size(); i++) {
            if (mProducts.get(i).getId() == product.getId()) {
                position = i;
                break;
            }
        }
        mProducts.remove(position);
        notifyItemRemoved(position);
    }

    class CartProductViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_preview)
        ImageView imgProduct;

        @BindView(R.id.img_remove)
        ImageView imgRemove;

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.tv_price)
        TextView tvPrice;

        @BindView(R.id.tv_quantity)
        TextView tvQuantity;

        public CartProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

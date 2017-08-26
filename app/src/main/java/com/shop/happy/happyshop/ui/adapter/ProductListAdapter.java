package com.shop.happy.happyshop.ui.adapter;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.shop.happy.happyshop.R;
import com.shop.happy.happyshop.application.GlideApp;
import com.shop.happy.happyshop.network.model.ProductItem;
import com.shop.happy.happyshop.ui.fragment.ProductListFragment.ProductClickListener;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by karthikeyan on 24/8/17.
 */

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductViewHolder> {

    private Context mContext;
    private ArrayList<ProductItem> mProducts;
    private final ProductClickListener mProductClickListener;

    public ProductListAdapter(Context context, ArrayList<ProductItem> products, ProductClickListener mProductClickListener) {
        this.mContext = context;
        this.mProducts = products;
        this.mProductClickListener = mProductClickListener;
    }

    @Override
    public ProductListAdapter.ProductViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_product, parent, false);
        return new ProductListAdapter.ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProductListAdapter.ProductViewHolder holder, int position) {
        ProductItem item = mProducts.get(position);

        holder.tvName.setText(item.getName());
        holder.tvPrice.setText(item.getPrice());
        holder.tvUnderSale.setText(item.isUnderSale() ? "ON SALE" : "");

        GlideApp.with(mContext)
                .load(item.getImgURL())
                .placeholder(ContextCompat.getDrawable(mContext, R.drawable.ic_thumbnail_img_24dp))
                .into(holder.imgProduct);

        holder.itemView.setOnClickListener(v -> mProductClickListener.onProductClick(item));

    }

    @Override
    public int getItemCount() {
        return mProducts.size();
    }

    public void swapData(ArrayList<ProductItem> productList) {
        this.mProducts = productList;
        notifyDataSetChanged();
    }

    class ProductViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.img_preview)
        ImageView imgProduct;

        @BindView(R.id.tv_name)
        TextView tvName;

        @BindView(R.id.tv_price)
        TextView tvPrice;

        @BindView(R.id.tv_on_sale)
        TextView tvUnderSale;

        public ProductViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}

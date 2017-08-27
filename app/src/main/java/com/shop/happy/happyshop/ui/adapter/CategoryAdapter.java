package com.shop.happy.happyshop.ui.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shop.happy.happyshop.R;
import com.shop.happy.happyshop.network.model.CategoryItem;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by karthikeyan on 24/8/17.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private ArrayList<CategoryItem> mCategoryList;
    private final CategoryClickListener mCategoryClickListener;

    public interface CategoryClickListener {
        void onCategoryClick(CategoryItem category);
    }

    public CategoryAdapter(ArrayList<CategoryItem> mCategoryList, CategoryClickListener categoryClickListener) {
        this.mCategoryList = mCategoryList;
        this.mCategoryClickListener = categoryClickListener;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        CategoryItem category = mCategoryList.get(position);
        holder.tvCategory.setText(category.getName());
        holder.itemView.setOnClickListener(v -> mCategoryClickListener.onCategoryClick(category));
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    public void swapDate(ArrayList<CategoryItem> categoryList) {
        this.mCategoryList = categoryList;
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.tv_category)
        TextView tvCategory;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}

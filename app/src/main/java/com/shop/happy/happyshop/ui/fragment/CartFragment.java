package com.shop.happy.happyshop.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shop.happy.happyshop.R;
import com.shop.happy.happyshop.application.ApplicationComponent;
import com.shop.happy.happyshop.data.ShoppingCartManager;
import com.shop.happy.happyshop.network.model.CartProductItem;
import com.shop.happy.happyshop.ui.adapter.CartAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by karthikeyan on 25/8/17.
 */

public class CartFragment extends BaseFragment implements ShoppingCartManager.Observer {

    @BindView(R.id.recycler_view_product)
    RecyclerView mRecyclerView;

    private CartAdapter mAdapter;

    public CartFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CartFragment.
     */
    public static CartFragment newInstance() {
        CartFragment fragment = new CartFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_list, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProducts();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    void injectComponent(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mShoppingCartManager.detach();
    }

    private void showProducts() {
        mShoppingCartManager.attach(this);
        mAdapter = new CartAdapter(getContext(), new ArrayList<>(), cartProductClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setAdapter(mAdapter);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     LISTENERS                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    private static CartAdapter.CartProductClickListener cartProductClickListener = new CartAdapter.CartProductClickListener() {
        @Override
        public void onRemove(int position, int productId) {

        }
    };

    @Override
    public void onProductsLoaded(ArrayList<CartProductItem> productList) {
        mAdapter.swapData(productList);
    }
}

package com.shop.happy.happyshop.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.shop.happy.happyshop.R;
import com.shop.happy.happyshop.application.ApplicationComponent;
import com.shop.happy.happyshop.data.ProductListManager;
import com.shop.happy.happyshop.network.model.CategoryItem;
import com.shop.happy.happyshop.network.model.ProductItem;
import com.shop.happy.happyshop.ui.ProductListActivity;
import com.shop.happy.happyshop.ui.adapter.ProductListAdapter;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProductClickListener} interface
 * to handle interaction events.
 * Use the {@link ProductListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProductListFragment extends BaseFragment implements ProductListManager.Observer {

    private static final int RECYCLER_VIEW_SPAN_COUNT = 2;
    @BindView(R.id.recycler_view_product)
    RecyclerView mRecyclerView;

    private CategoryItem mCategory;

    private ProductListAdapter mAdapter;
    private ProductClickListener mListener;
    private GridLayoutManager mGridLayoutManager;

    public ProductListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param category .
     * @return A new instance of fragment ProductListFragment.
     */
    public static ProductListFragment newInstance(CategoryItem category) {
        ProductListFragment fragment = new ProductListFragment();
        Bundle args = new Bundle();
        args.putParcelable(ProductListActivity.ARG_EXTRA_PARCELABLE_CATEGORY, category);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCategory = getArguments().getParcelable(ProductListActivity.ARG_EXTRA_PARCELABLE_CATEGORY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
        if (context instanceof ProductClickListener) {
            mListener = (ProductClickListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    void injectComponent(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mProductListManager.detach();
        mListener = null;
    }

    private void showProducts() {
        mProductListManager.attach(this, mCategory.getName());
        mGridLayoutManager = new GridLayoutManager(getContext(), RECYCLER_VIEW_SPAN_COUNT);
        mAdapter = new ProductListAdapter(getContext(), new ArrayList<>(), mListener);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(recyclerViewOnScrollListener);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     LISTENERS                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface ProductClickListener {
        void onProductClick(ProductItem item);
    }


    @Override
    public void onProductsLoaded(ArrayList<ProductItem> productList, int pageLoaded) {
        if (pageLoaded == 1) {
            mAdapter.swapData(productList);
        } else {
            mAdapter.addData(productList);
        }
    }

    @Override
    public void onError(String errorMsg) {

    }

    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            int visibleItemCount = mGridLayoutManager.getChildCount();
            int totalItemCount = mGridLayoutManager.getItemCount();
            int firstVisibleItemPosition = mGridLayoutManager.findFirstVisibleItemPosition();
            if (firstVisibleItemPosition >= 0 && totalItemCount < mCategory.getProductCount()
                    &&(visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                mProductListManager.loadMoreItems();
            }
        }
    };
}

package com.shop.happy.happyshop.ui.fragment;

import android.content.Context;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.shop.happy.happyshop.R;
import com.shop.happy.happyshop.application.ApplicationComponent;
import com.shop.happy.happyshop.data.ProductListManager;
import com.shop.happy.happyshop.network.model.CategoryItem;
import com.shop.happy.happyshop.network.model.ProductItem;
import com.shop.happy.happyshop.ui.ProductListActivity;
import com.shop.happy.happyshop.ui.adapter.ProductListAdapter;
import com.shop.happy.happyshop.util.NetworkUtility;

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

    @BindView(R.id.swiperefresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.recycler_view_product)
    RecyclerView mRecyclerView;

    @BindView(R.id.lay_no_network)
    LinearLayout layNetworkError;

    private CategoryItem mCategory;
    private boolean mIsRefresh = false;

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
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            mCategory = getArguments().getParcelable(ProductListActivity.ARG_EXTRA_PARCELABLE_CATEGORY);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.main, menu);
        refreshBadgeCount(menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().invalidateOptionsMenu();
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
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mIsRefresh = true;
            mProductListManager.refresh();
        });
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
        mSwipeRefreshLayout.setRefreshing(true);
        mProductListManager.attach(this, mCategory.getName());
        mGridLayoutManager = new GridLayoutManager(getContext(), RECYCLER_VIEW_SPAN_COUNT);
        mAdapter = new ProductListAdapter(getContext(), new ArrayList<>(), mListener);
        mRecyclerView.setLayoutManager(mGridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(recyclerViewOnScrollListener);
    }

    private void refreshBadgeCount(Menu menu) {
        MenuItem itemCart = menu.findItem(R.id.action_cart);
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        mShoppingCartManager.setBadgeCount(getContext(), icon);
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
        mSwipeRefreshLayout.setRefreshing(false);
        layNetworkError.setVisibility(View.GONE);
        if (pageLoaded == 1 || mIsRefresh) {
            mIsRefresh = false;
            mAdapter.swapData(productList);
        } else {
            mAdapter.addData(productList);
        }
    }

    @Override
    public void onError(String errorMsg, Throwable t) {
        mSwipeRefreshLayout.setRefreshing(false);
        mIsRefresh = false;
        if (t != null && NetworkUtility.isKnownException(t)) {
            if (!NetworkUtility.isNetworkAvailable(getContext()) && mAdapter.getItemCount() == 0) {
                layNetworkError.setVisibility(View.VISIBLE);
            }
        }
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
                    && (visibleItemCount + firstVisibleItemPosition) >= totalItemCount) {
                mProductListManager.loadMoreItems();
            }
        }
    };
}

package com.shop.happy.happyshop.ui;

import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.shop.happy.happyshop.R;
import com.shop.happy.happyshop.application.ApplicationComponent;
import com.shop.happy.happyshop.network.model.CategoryItem;
import com.shop.happy.happyshop.data.CategoryManager;
import com.shop.happy.happyshop.ui.adapter.CategoryAdapter;
import com.shop.happy.happyshop.util.NetworkUtility;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends InjectableActivity
        implements NavigationView.OnNavigationItemSelectedListener, CategoryManager.Observer {

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    @BindView(R.id.recycler_view_category)
    RecyclerView mRecyclerView;

    @BindView(R.id.swiperefresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.lay_no_network)
    LinearLayout layNetworkError;

    private CategoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            mCategoryManager.refresh();
        });
        showCategory();
    }

    @Override
    void injectComponent(ApplicationComponent component) {
        component.inject(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        attach();
        navigationView.getMenu().getItem(0).setChecked(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        invalidateOptionsMenu();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mCategoryManager.detach();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        refreshBadgeCount(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_cart) {
            startProductListActivity(true, null, true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_category) {
            // Handle the camera action
        } else if (id == R.id.nav_shopping_cart) {
            startProductListActivity(true, null, true);
        } else if (id == R.id.nav_settings) {
            showToast(getResources().getString(R.string.action_settings));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showCategory() {
        mAdapter = new CategoryAdapter(new ArrayList<>(), itemClickListener);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAdapter);
    }

    private void attach() {
        if (mAdapter.getItemCount() == 0) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        mCategoryManager.attach(this);
    }

    private void refreshBadgeCount(Menu menu) {
        MenuItem itemCart = menu.findItem(R.id.action_cart);
        LayerDrawable icon = (LayerDrawable) itemCart.getIcon();
        mShoppingCartManager.setBadgeCount(this, icon);
    }


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                     LISTENERS                                              //
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onCategoriesLoaded(ArrayList<CategoryItem> categoryLst) {
        mSwipeRefreshLayout.setRefreshing(false);
        layNetworkError.setVisibility(View.GONE);
        mAdapter.swapDate(categoryLst);
    }

    @Override
    public void onError(String errorMsg, Throwable t) {
        mSwipeRefreshLayout.setRefreshing(false);
        if (t != null && NetworkUtility.isKnownException(t)) {
            if (!NetworkUtility.isNetworkAvailable(getApplicationContext())) {
                layNetworkError.setVisibility(View.VISIBLE);
            }
        }

    }

    private final CategoryAdapter.CategoryClickListener itemClickListener = category -> {
        Log.d("clicked category", category.getName());
        startProductListActivity(false, category, true);
    };
}

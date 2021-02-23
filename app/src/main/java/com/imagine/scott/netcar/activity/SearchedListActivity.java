package com.imagine.scott.netcar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.adapter.SearchedListAdapter;
import com.imagine.scott.netcar.fragment.MapFragment;

import java.util.ArrayList;

public class SearchedListActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    public static ArrayList<PoiItem> poiItems;// poi数据

    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索

    private String keyWord;

    private ProgressDialog progDialog = null;// 搜索时进度条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searched_list);
        Intent intent = getIntent();
        keyWord = intent.getStringExtra("keyWord");
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(keyWord);
        }
        mRecyclerView = (RecyclerView) findViewById(R.id.searched_result_list);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        mAdapter = new SearchedListAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int lastVisibleItem = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
                int totalItemCount = mLayoutManager.getItemCount();

                if (lastVisibleItem >= totalItemCount - 1 && dy > 0) {
                    currentPage++;
                    doSearchQuery();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        poiItems = new ArrayList<>();
        doSearchQuery();
    }

    public void selected(int position) {
        Intent intent = getIntent();
        intent.putExtra("poi", position);
        this.setResult(1, intent);
        this.finish();
    }

    private void doSearchQuery() {
        showProgressDialog();// 显示进度框
        query = new PoiSearch.Query(keyWord, "", MapFragment.amapLocation.getCityCode());// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(15);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查找页数

        PoiSearch poiSearch = new PoiSearch(this, query);
        poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(MapFragment.amapLocation.getLatitude(),
                MapFragment.amapLocation.getLongitude()), 100000));//设置周边搜索的中心点以及区域
        poiSearch.setOnPoiSearchListener(this);//设置数据返回的监听器
        poiSearch.searchPOIAsyn();  //开始搜索
    }

    //region 高德搜索完成
    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        dissmissProgressDialog();
        if (rcode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    if (poiResult.getPois().size() <= 0) {
                        Toast.makeText(this, "没有搜索到更多相关数据！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    poiItems.addAll(poiResult.getPois());// 取得第一页的poiitem数据，页数从数字0开始
                    MapFragment.poiItems = poiItems;
                    if (poiItems != null && poiItems.size() > 0) {
                        mAdapter.notifyDataSetChanged();
                        mRecyclerView.requestLayout();
                    } else {
                        Toast.makeText(this, "对不起，没有搜索到相关数据！", Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(this, "对不起，没有搜索到相关数据！", Toast.LENGTH_LONG).show();
            }
        }
    }
    //endregion

    @Override
    public void onPoiItemSearched(PoiItem var1, int var2) {

    }

    //region 显示进度框
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + keyWord);
        progDialog.show();
    }
    //endregion

    //region 隐藏进度框
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }
    //endregion
}

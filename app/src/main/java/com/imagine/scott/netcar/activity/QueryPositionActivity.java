package com.imagine.scott.netcar.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.MapView;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.adapter.QueryPositionListAdapter;
import com.imagine.scott.netcar.fragment.MapFragment;

import java.util.ArrayList;

public class QueryPositionActivity extends AppCompatActivity implements PoiSearch.OnPoiSearchListener {

    private EditText queryPositionStart;
    private EditText queryPositionFinish;
    private ImageButton queryPositionSwap;
    private RecyclerView queryPositionList;
    private RecyclerView.LayoutManager queryPositionListLayoutManager;
    private QueryPositionListAdapter queryPositionListAdapter;
    private BottomSheetDialog queryPositionMapViewDialog;
    //private MapView queryPositionMapView;

    private ProgressDialog progDialog = null;

    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    public static ArrayList<PoiItem> poiItems;// poi数据

    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;// POI搜索

    private double start_lng = 0;
    private double start_lat = 0;
    private double finish_lng = 0;
    private double finish_lat = 0;
    private String startPositionName = "";
    private String finishPositionName = "";

    private boolean isQueryStartPosition = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_position);
        Toolbar toolbar = (Toolbar) findViewById(R.id.query_position_toolbar);
        setSupportActionBar(toolbar);

        poiItems = new ArrayList<>();
        queryPositionStart = (EditText) findViewById(R.id.query_position_start);
        queryPositionFinish = (EditText) findViewById(R.id.query_position_finish);
        queryPositionSwap = (ImageButton) findViewById(R.id.query_position_swap);
        queryPositionList = (RecyclerView) findViewById(R.id.query_position_list);
        queryPositionListLayoutManager = new LinearLayoutManager(this);
        queryPositionList.setLayoutManager(queryPositionListLayoutManager);
        queryPositionList.setHasFixedSize(true);
        queryPositionListAdapter = new QueryPositionListAdapter(this);
        queryPositionList.setAdapter(queryPositionListAdapter);

        queryPositionMapViewDialog = new BottomSheetDialog(this);

        queryPositionStart.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                isQueryStartPosition = true;
                doSearchQuery(queryPositionStart.getText().toString());
                return true;
            }
        });
        queryPositionFinish.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                isQueryStartPosition = false;
                doSearchQuery(queryPositionFinish.getText().toString());
                return true;
            }
        });
        queryPositionSwap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                swapSF();
            }
        });
        Intent intent = getIntent();
        if (intent.getBooleanExtra("haslocation", false)) {
            start_lng = intent.getDoubleExtra("lng", 0);
            start_lat = intent.getDoubleExtra("lat", 0);
            startPositionName = "当前位置";
            queryPositionStart.setText(startPositionName);
        }
        doSearchQuery(intent.getStringExtra("keyWord"));
    }

    public void swapSF() {
        queryPositionStart.setText(finishPositionName);
        queryPositionFinish.setText(startPositionName);
        startPositionName = queryPositionStart.getText().toString();
        finishPositionName = queryPositionFinish.getText().toString();
        double temp_lng = start_lng;
        double temp_lat = start_lat;
        start_lng = finish_lng;
        start_lat = finish_lat;
        finish_lng = temp_lng;
        finish_lat = temp_lat;
    }

    public boolean checkParamsToRoute() {
        if (start_lng == 0 ||start_lat == 0 || finish_lng == 0 || finish_lat == 0) {
            return false;
        }
        return true;
    }

    public void selected(int position) {
        Intent intent = getIntent();
        intent.putExtra("poi", position);
        this.setResult(1, intent);
        this.finish();
    }
    private void doSearchQuery(String keyWord) {
        showProgressDialog(keyWord);// 显示进度框
        query = new PoiSearch.Query(keyWord, "", MapFragment.amapLocation.getCityCode());// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(15);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查找页数

        PoiSearch poiSearch = new PoiSearch(this,query);
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
                    if (poiResult.getPois().size() <=0) {
                        poiItems.clear();
                        queryPositionListAdapter.notifyDataSetChanged();
                        queryPositionList.requestLayout();
                        Toast.makeText(this, "没有搜索到更多相关数据！", Toast.LENGTH_LONG).show();
                        return;
                    }
                    poiItems.addAll(poiResult.getPois());// 取得第一页的poiitem数据，页数从数字0开始
                    MapFragment.poiItems = poiItems;
                    if (poiItems != null && poiItems.size() > 0) {
                        queryPositionListAdapter.notifyDataSetChanged();
                        queryPositionList.requestLayout();
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

    private void showProgressDialog(String currentWord) {
        if (progDialog == null)
            progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(false);
        progDialog.setMessage("正在搜索:\n" + currentWord);
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

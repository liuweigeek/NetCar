package com.imagine.scott.netcar.fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.PoiItem;
import com.imagine.scott.netcar.Constants;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.activity.AppoActivity;
import com.imagine.scott.netcar.activity.GasStationDetailActivity;
import com.imagine.scott.netcar.activity.MainActivity;
import com.imagine.scott.netcar.activity.MultipleRoutePlanningActivity;
import com.imagine.scott.netcar.activity.QueryPositionActivity;
import com.imagine.scott.netcar.bean.GasStation;
import com.imagine.scott.netcar.juhe.JuHeUtil;
import com.imagine.scott.netcar.operation.GasJSONOperate;
import com.imagine.scott.netcar.operation.Transform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapFragment extends BaseFragment implements LocationSource,
        AMapLocationListener, AMap.OnMapClickListener, AMap.OnMarkerClickListener {

    //region 定义参数
    private MainActivity mainActivity;  //Mainactivity的引用
    private Bundle mSavedInstanceState; //生命周期数据状态
    private CardView gasStationCardView;
    private CardView searchedCardView;
    private FloatingActionButton fab;

    private boolean isGasStationSearching;
    private boolean isGasStationSearched;

    private RequestConvertToBaiduTask requestConvertToBaiduTask;
    private RequestGetGasStationTask requestGetGasStationTask;

    private MapView mMapView;   //地图视图
    private AMap aMap;  //地图组件
    private AMapLocationClient mlocationClient; //定位组件
    private OnLocationChangedListener mListener;    //定位监听器
    private AMapLocationClientOption mLocationOption;   //定位配置
    public static AMapLocation amapLocation;  //定位获取到的坐标
    private boolean isFirstLoc = true;  //是否为第一次定位

    private String keyWord; //搜索关键字
    private SearchView searchView = null;   //菜单搜索框
    private SearchView.OnQueryTextListener queryTextListener;   //搜索框文本监听器
    private ProgressDialog progDialog = null;// 搜索时进度条

    private Marker detailMarker;    //当前Marker
    private Marker mlastMarker; //最后一次点击的Marker
    private ArrayList<Marker> markers = new ArrayList<>();  //搜索到的所有Marker
    public ArrayList<GasStation> gasStations = new ArrayList<>();  //搜索到的所有加油站

    public static ArrayList<PoiItem> poiItems;// poi数据

    private MyPoiOverlay myPoiOverlay;// poi图层
    //endregion

    //region Fragment onCreate()
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mainActivity = MainActivity.mainActivity;

        mSavedInstanceState = savedInstanceState;
    }
    //endregion

    //region 创建Fragment返回View
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);
        fab = (FloatingActionButton) rootView.findViewById(R.id.map_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (amapLocation != null) {
                    convertToBaidu(amapLocation.getLongitude(), amapLocation.getLatitude());
                } else {
                    Snackbar.make(fab, "请等待定位完成", Snackbar.LENGTH_LONG).show();
                }
            }
        });
        mMapView = (MapView) rootView.findViewById(R.id.map_view);
        mMapView.onCreate(mSavedInstanceState);// 此方法必须重写

        gasStationCardView = (CardView) rootView.findViewById(R.id.card_gas_station);
        searchedCardView = (CardView) rootView.findViewById(R.id.card_searched);

        if (checkPermission()) {
            setUpMap();
        }
        return rootView;
    }
    //endregion

    public void onShowFragment() {

    }

    //region 生命周期
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        setUpMap();
    }
    @Override
    public void onPause() {
        super.onPause();
        deactivate();
        mMapView.onPause();
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if(null != mlocationClient){
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mMapView.onDestroy();
    }
    //endregion

    //region 初始化aMap
    private void setUpMap() {
        aMap = mMapView.getMap();
        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
        aMap.setOnMapClickListener(this);
        aMap.setOnMarkerClickListener(this);
        aMap.setLocationSource(this);// 设置定位监听
        aMap.setTrafficEnabled(true);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationType(AMap.LOCATION_TYPE_LOCATE);// 设置定位的类型为定位模式 ，可以由定位、跟随或地图根据面向方向旋转几种
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮是否显示
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
    }
    //endregion

    //region 启动定位
    @Override
    public void activate(LocationSource.OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(getActivity());
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }
    //endregion

    //region 停止定位
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }
    //endregion

    //region 定位发生改变时回调
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                this.amapLocation = amapLocation;
                mListener.onLocationChanged(amapLocation);
                if (isFirstLoc) {
                    isFirstLoc = false;
                    //searchGasStation(118.759877,32.046719);
                    convertToBaidu(amapLocation.getLongitude(), amapLocation.getLatitude());
                }
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Log.e("AmapErr",errText);
            }
        }
    }
    //endregion

    //region 将定位结果转为Baidu坐标
    public void convertToBaidu(double longitude, double latitude) {
        /*searchGasStation(118.759877,32.046719);
        if (true) {
            return;
        }*/
        if (isGasStationSearching) {
            return;
        }
        isGasStationSearching = true;

        StringBuffer url = new StringBuffer("http://v.juhe.cn/offset/index");
        url.append("?");
        url.append("lng=" + longitude + "&lat=" + latitude + "&type=6&dtype=&callback=&key=" + "7b744183b9259efd948b64b62d76b660");
        requestConvertToBaiduTask = new RequestConvertToBaiduTask();
        requestConvertToBaiduTask.execute(url.toString());

        //region old request
        /*JuheData.executeWithAPI(32, "http://v.juhe.cn/offset/index", JuheData.POST, params, new DataCallBack() {

            @Override
            public void resultLoaded(int err, String reason, String result) {
                if (err == 0) {
                    try {
                        Log.v("Scott ", "convertToBaidu === result is:" + result);
                        Map<String, Object> convertResult = GasJSONOperate.getLatLngInfo(result);
                        searchGasStation((double) convertResult.get("off_lng"), (double) convertResult.get("off_lat"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    mainActivity.showSnackbar(reason);
                }
            }
        });*/
        //endregion
    }
    //endregion

    public class RequestConvertToBaiduTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return JuHeUtil.request(params[0]);
        }

        @Override
        protected void onPostExecute(final String result) {
            requestConvertToBaiduTask = null;

            if (!TextUtils.isEmpty(result)) {
                try {
                    Map<String, Object> convertResult = GasJSONOperate.getLatLngInfo(result);
                    searchGasStation((double) convertResult.get("off_lng"), (double) convertResult.get("off_lat"));
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.mainActivity.showSnackbar("坐标转换失败，请稍后重试");
                }

            }
        }
    }

    public class RequestGetGasStationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            return JuHeUtil.request(params[0]);
        }

        @Override
        protected void onPostExecute(final String result) {
            requestGetGasStationTask = null;

            if (!TextUtils.isEmpty(result)) {
                try {
                    ArrayList<Map<String, Object>> allReason = GasJSONOperate.getGasInformation(result);
                    ArrayList<GasStation> gasStationList = GasJSONOperate.getGasStations(allReason);
                    onGasSearched(gasStationList);
                } catch (Exception e) {
                    e.printStackTrace();
                    MainActivity.mainActivity.showSnackbar("查找加油站失败，请稍后重试");
                }

            }
        }
    }

    //region 创建搜索菜单
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_search, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            queryTextListener = new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }
                @Override
                public boolean onQueryTextSubmit(String query) {
                    if (!TextUtils.isEmpty(query)) {
                        isGasStationSearched = false;
                        keyWord = query;
                        Intent intent = new Intent(getActivity(), QueryPositionActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("keyWord", keyWord);
                        if (amapLocation != null) {
                            bundle.putBoolean("haslocation", true);
                            bundle.putDouble("lng", amapLocation.getLongitude());
                            bundle.putDouble("lat", amapLocation.getLatitude());
                        } else {
                            bundle.putBoolean("haslocation", false);
                        }
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                    return true;
                }
            };
            searchView.setOnQueryTextListener(queryTextListener);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }
    //endregion

    //region 菜单项选中回调
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                // Not implemented here
                return false;
            default:
                break;
        }
        searchView.setOnQueryTextListener(queryTextListener);
        return super.onOptionsItemSelected(item);
    }
    //endregion

    //region 搜索加油站
    public void searchGasStation(double longitude, double latitude) {
        clearMarkerState();
        isGasStationSearching = false;
        isGasStationSearched = true;

        StringBuffer url = new StringBuffer("http://apis.juhe.cn/oil/local");
        url.append("?");
        url.append("lon=" + longitude + "&lat=" + latitude + "&r=8000&page=&format=&key=" + "b446f475ac7d3ae03bc5f36001b2e363");
        requestGetGasStationTask = new RequestGetGasStationTask();
        requestGetGasStationTask.execute(url.toString());

        //region ole code
        /* Parameters params = new Parameters();
        params.add("lon", longitude);
        params.add("lat", latitude);
        params.add("r", 10000);
        params.add("key", "b446f475ac7d3ae03bc5f36001b2e363");

        JuheData.executeWithAPI(7, "http://apis.juhe.cn/oil/local", JuheData.POST, params, new DataCallBack() {

            @Override
            public void resultLoaded(int err, String reason, String result) {
                if (err == 0) {
                    try {
                        Log.v("Scott ", "searchGasStation === result is:" + result);
                        ArrayList<Map<String, Object>> allReason = GasJSONOperate.getGasInformation(result);
                        ArrayList<GasStation> gasStationList = GasJSONOperate.getGasStations(allReason);
                        onGasSearched(gasStationList);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    mainActivity.showSnackbar(reason);
                }
            }
        });*/
        //endregion
    }
    //endregion

    //region 加油站搜索完成
    public void onGasSearched(ArrayList<GasStation> gasStationList) {
        this.gasStations.addAll(gasStationList);
        if (gasStations != null && gasStations.size() > 0) {
            myPoiOverlay = new MyPoiOverlay(aMap);
            myPoiOverlay.mAddToMap();
            myPoiOverlay.zoomToSpan();
        }
    }
    //endregion

    //region 调用其它Activity后返回时回调
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == 1 && resultCode == 1) {
            Bundle bundle = intent.getExtras();
            int position = bundle.getInt("poi");
            ArrayList<PoiItem> selectedPois = new ArrayList<>();
            selectedPois.add(poiItems.get(position));
            myPoiOverlay = new MyPoiOverlay(aMap, selectedPois);
            myPoiOverlay.searchedAddToMap();
            myPoiOverlay.zoomToSpan();
        }
    }
    //endregion

    //region 清除标点状态
    private void clearMarkerState() {

        if (mlastMarker != null) {
            resetlastmarker(false);
            mlastMarker = null;
        }
        if (markers != null) markers.clear();
        if (detailMarker != null) detailMarker = null;
        if (gasStations != null) gasStations.clear();
        if (poiItems != null) poiItems.clear();
        if (myPoiOverlay !=null) myPoiOverlay.removeFromMap();
        if (markers != null) markers.clear();
        gasStationCardView.setVisibility(View.INVISIBLE);
        searchedCardView.setVisibility(View.INVISIBLE);
        aMap.clear();
        hideAllCardView();
    }
    //endregion

    //region 切换和隐藏底部卡片
    public void showGasStationCardView() {
        searchedCardView.setVisibility(View.INVISIBLE);
        gasStationCardView.setVisibility(View.VISIBLE);
    }
    public void showSearchedCardView() {
        gasStationCardView.setVisibility(View.INVISIBLE);
        searchedCardView.setVisibility(View.VISIBLE);
    }
    public void hideAllCardView() {
        gasStationCardView.setVisibility(View.INVISIBLE);
        searchedCardView.setVisibility(View.INVISIBLE);
    }
    //endregion

    //region 地图单击事件
    @Override
    public void onMapClick(LatLng arg0) {
        hideAllCardView();
        if (mlastMarker != null) {
            if (isGasStationSearched) {
                resetlastmarker(true);
            } else {
                resetlastmarker(false);
            }
        }
    }
    //endregion

    //region Marker单击事件
    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker == null) return true;

        if (!markers.contains(marker) && !poiItems.contains(marker.getObject())) {
            hideAllCardView();
            if (mlastMarker != null) {
                resetlastmarker(false);
            }
            return true;
        }
        if (isGasStationSearched){
            try {
                if (mlastMarker == null) {
                    mlastMarker = marker;
                } else {
                    // 将之前被点击的marker置为原来的状态
                    resetlastmarker(true);
                    mlastMarker = marker;
                }
                detailMarker = marker;
                detailMarker.setIcon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                getResources(),
                                R.drawable.marker_clicked)));
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), 14));
                setPoiItemDisplayContent(true, markers.indexOf(marker));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            try {
                if (mlastMarker == null) {
                    mlastMarker = marker;
                } else {
                    resetlastmarker(false);
                    mlastMarker = marker;
                }
                detailMarker = marker;
                detailMarker.setIcon(BitmapDescriptorFactory
                        .fromBitmap(BitmapFactory.decodeResource(
                                getResources(),
                                R.drawable.marker_clicked)));
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker.getPosition().latitude, marker.getPosition().longitude), 14));
                setPoiItemDisplayContent(false, poiItems.indexOf(marker.getObject()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }
    //endregion

    //region 单击marker后展示卡片
    private void setPoiItemDisplayContent(boolean isGasStation, int index) {
        if (isGasStation) {
            showGasStationCardView();
            showGasStationCardInfo(index);
        } else {
            showSearchedCardView();
            showSearchedCardInfo(index);
        }
    }
    //endregion

    //region 显示加油站卡片
    public void showGasStationCardInfo(final int index) {

        final GasStation gasStation = gasStations.get(index);
        TextView gasStationCardViewName = (TextView) gasStationCardView.findViewById(R.id.card_gas_station_name);
        TextView gasStationCardViewAddr = (TextView) gasStationCardView.findViewById(R.id.card_gas_station_addr);
        TextView gasStationCardViewDistance = (TextView) gasStationCardView.findViewById(R.id.card_gas_station_distance);
        ImageView gasStationCardViewLogo = (ImageView) gasStationCardView.findViewById(R.id.card_gas_station_logo);
        LinearLayout gasStationCardViewCardNavi = (LinearLayout) gasStationCardView.findViewById(R.id.card_gas_station_navi);
        LinearLayout gasStationCardViewAppo = (LinearLayout) gasStationCardView.findViewById(R.id.card_gas_station_appo);
        LinearLayout gasStationCardViewDetail = (LinearLayout) gasStationCardView.findViewById(R.id.card_gas_station_detail);

        gasStationCardViewName.setText(gasStation.getName());
        gasStationCardViewAddr.setText(gasStation.getAddress());

        String brandName = gasStation.getBrandname();
        switch (brandName) {
            case "中石油":
                gasStationCardViewLogo.setImageDrawable(getResources().getDrawable(R.drawable.logo_cnpc));
                break;
            case "中石化":
                gasStationCardViewLogo.setImageDrawable(getResources().getDrawable(R.drawable.logo_sinopec));
                break;
            case "壳牌":
                gasStationCardViewLogo.setImageDrawable(getResources().getDrawable(R.drawable.logo_shell));
                break;
            default:
                gasStationCardViewLogo.setImageDrawable(getResources().getDrawable(R.drawable.logo_gas_station));
        }

        gasStationCardViewDistance.setText("距离:" + Transform.meterToKilo(gasStation.getDistance()));
        gasStationCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGasDetail(gasStation);
            }
        });
        gasStationCardViewCardNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.mainActivity, MultipleRoutePlanningActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("eName", gasStation.getName());
                bundle.putString("eAddr", gasStation.getAddress());
                bundle.putDouble("sLat", amapLocation.getLatitude());
                bundle.putDouble("sLng", amapLocation.getLongitude());
                bundle.putDouble("eLat", gasStation.getLat());
                bundle.putDouble("eLng", gasStation.getLon());
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        gasStationCardViewAppo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AppoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("gasStation", gasStation);
                intent .putExtras(bundle);
                startActivity(intent);
            }
        });
        gasStationCardViewDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showGasDetail(gasStation);
            }
        });
    }
    //endregion

    public void showGasDetail(GasStation gasStation) {
        Intent intent = new Intent(getActivity(), GasStationDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("gasStation", gasStation);
        bundle.putDouble("mLat", amapLocation.getLatitude());
        bundle.putDouble("mLng", amapLocation.getLongitude());
        intent .putExtras(bundle);
        startActivity(intent);
    }

    //region 显示搜索结果卡片
    public void showSearchedCardInfo(final int index) {
        TextView searchedCardViewName = (TextView) searchedCardView.findViewById(R.id.card_searched_name);
        TextView searchedCardViewDistance = (TextView) searchedCardView.findViewById(R.id.card_searched_distance);
        TextView searchedCardViewAddr = (TextView) searchedCardView.findViewById(R.id.card_searched_addr);
        Button searchedCardViewNavi = (Button) searchedCardView.findViewById(R.id.card_searched_navi);
        searchedCardViewName.setText(poiItems.get(index).getTitle());
        //TODO
        //searchedCardViewDistance.setText(poiItems.get(index).getDistance());
        searchedCardViewAddr.setText(poiItems.get(index).getAdName());
        searchedCardViewNavi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.mainActivity, MultipleRoutePlanningActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("eName", poiItems.get(index).getTitle());
                bundle.putString("eAddr", poiItems.get(index).getAdName());
                bundle.putDouble("sLat", amapLocation.getLatitude());
                bundle.putDouble("sLng", amapLocation.getLongitude());
                bundle.putDouble("eLat", poiItems.get(index).getLatLonPoint().getLatitude());
                bundle.putDouble("eLng", poiItems.get(index).getLatLonPoint().getLongitude());
                intent.putExtras(bundle);
                mainActivity.startActivity(intent);
            }
        });
    }
    //endregion

    //region 加油站获取结果Marker图标
    private int[] markerDraws = {R.drawable.poi_marker_1,
            R.drawable.poi_marker_2,
            R.drawable.poi_marker_3,
            R.drawable.poi_marker_4,
            R.drawable.poi_marker_5,
            R.drawable.poi_marker_6,
            R.drawable.poi_marker_7,
            R.drawable.poi_marker_8,
            R.drawable.poi_marker_9,
            R.drawable.poi_marker_10
    };
    //endregion

    //region 将之前被点击的marker重置为原来的状态
    private void resetlastmarker(boolean isGasStationSearched) {
        if (mlastMarker != null) {
            if (isGasStationSearched) {
                int index = myPoiOverlay.getPoiIndex(mlastMarker);
                if (index < 10) {
                    mlastMarker.setIcon(BitmapDescriptorFactory
                            .fromBitmap(BitmapFactory.decodeResource(
                                    getResources(),
                                    markerDraws[index])));
                }else {
                    mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                            BitmapFactory.decodeResource(getResources(), R.drawable.marker_unclick)));
                }
            } else {
                int index = myPoiOverlay.getPoiIndex(mlastMarker);
                mlastMarker.setIcon(BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.marker_unclick)));

            }
            mlastMarker = null;
        }
    }
    //endregion

    //region 自定义Poi
    private class MyPoiOverlay {
        private AMap mAmap;
        private List<PoiItem> mPois;

        //构造方法
        public MyPoiOverlay(AMap amap ,List<PoiItem> pois) {
            mAmap = amap;
            mPois = pois;
        }
        //构造方法
        public MyPoiOverlay(AMap aMap) {
            this.mAmap = aMap;
        }
        //将搜索到的加油站添加显示到Map
        public void mAddToMap() {
            for (int i = 0; i < gasStations.size(); i++) {

                //定义Maker坐标点
                LatLng point = new LatLng(gasStations.get(i).getLat(), gasStations.get(i).getLon());

                //构建MarkerOption，用于在地图上添加Marker
                Marker marker = mAmap.addMarker(new MarkerOptions()
                        .position(point)
                        .title(gasStations.get(i).getName())
                        .icon(getBitmapDescriptor(i)));
                //在地图上添加Marker，并显示
                markers.add(marker);
            }
        }
        //将内置搜索到的Poi添加显示到Map
        public void addToMap() {
            for (int i = 0; i < mPois.size(); i++) {
                Marker marker = mAmap.addMarker(getMarkerOptions(i));
                PoiItem item = mPois.get(i);
                marker.setObject(item);
                markers.add(marker);
            }
        }
        //将内置搜索到的Poi添加显示到Map
        public void searchedAddToMap() {
            for (int i = 0; i < mPois.size(); i++) {
                Marker marker = mAmap.addMarker(new MarkerOptions()
                        .position(new LatLng(mPois.get(i).getLatLonPoint()
                                .getLatitude(), mPois.get(i)
                                .getLatLonPoint().getLongitude()))
                        .title(poiItems.get(i).getTitle())
                        .icon(BitmapDescriptorFactory.fromBitmap(
                                BitmapFactory.decodeResource(getResources(), R.drawable.marker_unclick))));
                PoiItem item = mPois.get(i);
                marker.setObject(item);
                markers.add(marker);
            }
        }
        //从地图中移除Marker
        public void removeFromMap() {
            for (Marker mark : markers) {
                mark.remove();
            }
        }

        /**
         * 移动镜头到当前的视角。
         * @since V2.1.0
         */
        public void zoomToSpan() {
            if (gasStations != null && gasStations.size() > 0) {
                if (mAmap == null)
                    return;
                LatLngBounds bounds = getLatLngBounds();
                mAmap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }

        private LatLngBounds getLatLngBounds() {
            LatLngBounds.Builder b = LatLngBounds.builder();
            b.include(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude()));
            for (int i = 0; i < gasStations.size(); i++) {
                b.include(new LatLng(gasStations.get(i).getLat(),
                        gasStations.get(i).getLon()));
            }
            return b.build();
        }

        private MarkerOptions getMarkerOptions(int index) {
            return new MarkerOptions()
                    .position(
                            new LatLng(mPois.get(index).getLatLonPoint()
                                    .getLatitude(), mPois.get(index)
                                    .getLatLonPoint().getLongitude()))
                    .title(getTitle(index)).snippet(getSnippet(index))
                    .icon(getBitmapDescriptor(index));
        }

        protected String getTitle(int index) {
            return mPois.get(index).getTitle();
        }

        protected String getSnippet(int index) {
            return mPois.get(index).getSnippet();
        }

        public PoiItem getPoiItem(int index) {
            if (index < 0 || index >= mPois.size()) {
                return null;
            }
            return mPois.get(index);
        }

        public int getPoiIndex(Marker marker) {
            for (int i = 0; i < markers.size(); i++) {
                if (markers.get(i).equals(marker)) {
                    return i;
                }
            }
            return -1;
        }

        public Marker getMarkerByIndex(int index) {
            return markers.get(index);
        }

        protected BitmapDescriptor getBitmapDescriptor(int arg0) {
            if (arg0 < 10) {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), markerDraws[arg0]));
                return icon;
            }else {
                BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(
                        BitmapFactory.decodeResource(getResources(), R.drawable.marker_other_highlight));
                return icon;
            }
        }
    }
    //endregion

    //region 显示进度框
    private void showProgressDialog() {
        if (progDialog == null) {
            progDialog = new ProgressDialog(getActivity());
        }
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

    //region 定位权限
    public boolean checkPermission(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int checkGPSPhonePermission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION);
            if(checkGPSPhonePermission != PackageManager.PERMISSION_GRANTED){
                //若没有则弹出对话框请求
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constants.REQUEST_CODE_ASK_ACCESS_FINE_LOCATION);
                return false;
            }else{
                return true;
            }
        } else {
            return true;
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Constants.REQUEST_CODE_ASK_ACCESS_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission Granted
                    //开启自动定位
                    setUpMap();
                } else {
                    mainActivity.showSnackbar("无法获取定位权限");
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    //endregion
}

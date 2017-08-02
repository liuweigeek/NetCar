package com.imagine.scott.netcar.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.activity.GasStationDetailActivity;
import com.imagine.scott.netcar.activity.MainActivity;
import com.imagine.scott.netcar.adapter.CarsListAdapter;
import com.imagine.scott.netcar.adapter.GasListAdapter;
import com.imagine.scott.netcar.bean.GasStation;

import java.util.ArrayList;

public class GasStationFragment extends BaseFragment {

    private MainActivity mainActivity;
    private ArrayList<GasStation> gasStations;

    private SwipeRefreshLayout mGasSwipeRefreshLayout;
    private TextView mGasInfo;

    private RecyclerView mGasRecycler;
    private GasListAdapter mGasListAdapter;
    private LinearLayoutManager mGasLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivity = MainActivity.mainActivity;
        gasStations = mainActivity.mapFragment.gasStations;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_gas_station, container, false);

        mGasSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.gas_swiperefresh);

        mGasSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.swipe_color_1),
                getResources().getColor(R.color.swipe_color_2),
                getResources().getColor(R.color.swipe_color_3),
                getResources().getColor(R.color.swipe_color_4));


        mGasInfo = (TextView) rootView.findViewById(R.id.gas_station_info);
        mGasRecycler = (RecyclerView) rootView.findViewById(R.id.gas_station_recycler_view);
        mGasRecycler.setHasFixedSize(true);
        mGasLayoutManager = new LinearLayoutManager(getActivity());
        mGasRecycler.setLayoutManager(mGasLayoutManager);
        mGasListAdapter = new GasListAdapter(this, gasStations);

        mGasRecycler.setAdapter(mGasListAdapter);

        mGasSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                gasStations = mainActivity.mapFragment.gasStations;
                mGasListAdapter = new GasListAdapter(GasStationFragment.this, gasStations);
                mGasRecycler.setAdapter(mGasListAdapter);
                mGasSwipeRefreshLayout.setRefreshing(false);
            }
        });
        return rootView;
    }

    public void onGasClick(int position) {
        Intent intent = new Intent(getActivity(), GasStationDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("gasStation", gasStations.get(position));
        bundle.putDouble("mLat", MapFragment.amapLocation.getLatitude());
        bundle.putDouble("mLng", MapFragment.amapLocation.getLongitude());
        intent .putExtras(bundle);
        startActivity(intent);
    }

    public void onShowFragment() {}
}

package com.imagine.scott.netcar.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.activity.AddNewCarActivity;
import com.imagine.scott.netcar.activity.CarDetailActivity;
import com.imagine.scott.netcar.activity.MainActivity;
import com.imagine.scott.netcar.adapter.CarsListAdapter;
import com.imagine.scott.netcar.bean.UserCar;
import com.imagine.scott.netcar.operation.Connection;
import com.imagine.scott.netcar.operation.ResultJSONOperate;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CarsFragment extends BaseFragment {

    private MainActivity mainActivity;
    private FloatingActionButton fab;

    private SwipeRefreshLayout mCarsSwipeRefreshLayout;
    private RecyclerView mCarsRecycler;
    private TextView mCarsInfo;
    private CarsListAdapter mCarsListAdapter;
    private LinearLayoutManager mCarsLayoutManager;

    private List<UserCar> userCarList;

    private GetMyCarListTask mAuthTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mainActivity = MainActivity.mainActivity;
        userCarList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_cars, container, false);

        fab = (FloatingActionButton) rootView.findViewById(R.id.cars_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddNewCarActivity.class);
                startActivity(intent);
            }
        });

        mCarsSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.cars_swiperefresh);

        mCarsSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.swipe_color_1),
                getResources().getColor(R.color.swipe_color_2),
                getResources().getColor(R.color.swipe_color_3),
                getResources().getColor(R.color.swipe_color_4));

        mCarsInfo = (TextView) rootView.findViewById(R.id.cars_info);
        mCarsRecycler = (RecyclerView) rootView.findViewById(R.id.cars_recycler_view);
        mCarsRecycler.setHasFixedSize(true);
        mCarsLayoutManager = new LinearLayoutManager(getActivity());
        mCarsRecycler.setLayoutManager(mCarsLayoutManager);
        mCarsListAdapter = new CarsListAdapter(this, userCarList);

        mCarsRecycler.setAdapter(mCarsListAdapter);

        mCarsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!TextUtils.isEmpty(mainActivity.userInfoPreferences.getString("userphone", ""))) {
                    if (mAuthTask == null) {
                        mCarsSwipeRefreshLayout.setRefreshing(true);
                        mAuthTask = new GetMyCarListTask();
                        mAuthTask.execute(mainActivity.userInfoPreferences.getString("userphone", ""));
                    }
                }
            }
        });

        return rootView;
    }

    public void onShowFragment() {

    }

    @Override
    public void onResume() {
        super.onResume();
        if (!TextUtils.isEmpty(mainActivity.userInfoPreferences.getString("userphone", ""))) {
            if (mAuthTask == null) {
                mAuthTask = new GetMyCarListTask();
                mAuthTask.execute(mainActivity.userInfoPreferences.getString("userphone", ""));
            }
        } else {
            Snackbar.make(fab, "请先登录", Snackbar.LENGTH_LONG).show();
        }
    }

    public void onCarItemSelected(int position) {
        Intent intent = new Intent(getActivity(), CarDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("carinfo", userCarList.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public class GetMyCarListTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Connection connection = new Connection();
            Map<String, Object> map = new HashMap<>();
            map.put("phone", params[0]);
            return connection.uploadParams("", "GetMyCar", map);
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            mCarsSwipeRefreshLayout.setRefreshing(false);
            try {
                userCarList = new ArrayList<>();
                userCarList = ResultJSONOperate.getUserCarsJson(result);
                if (userCarList.size() > 0) {
                    mCarsInfo.setVisibility(View.GONE);
                } else {
                    mCarsInfo.setText("没有任何车辆");
                    mCarsInfo.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                userCarList.clear();
                mCarsInfo.setText("没有任何车辆");
                mCarsInfo.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                userCarList.clear();
                mCarsInfo.setText("请检查网络状况及服务器IP设置");
                mCarsInfo.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }
            mCarsListAdapter = new CarsListAdapter(CarsFragment.this, userCarList);
            mCarsListAdapter.notifyDataSetChanged();
            mCarsRecycler.setAdapter(mCarsListAdapter);
            mCarsRecycler.requestLayout();
        }
    }
}

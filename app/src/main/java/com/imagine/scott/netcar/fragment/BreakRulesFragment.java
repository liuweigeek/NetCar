package com.imagine.scott.netcar.fragment;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.activity.MainActivity;
import com.imagine.scott.netcar.activity.QueryBreakRulesActivity;
import com.imagine.scott.netcar.adapter.BreakRulesListAdapter;
import com.imagine.scott.netcar.bean.UserCar;
import com.imagine.scott.netcar.operation.Connection;
import com.imagine.scott.netcar.operation.ResultJSONOperate;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BreakRulesFragment extends BaseFragment {

    private MainActivity mainActivity;

    private SwipeRefreshLayout mBreakRulesSwipeRefreshLayout;
    private TextView mBreakRulesInfo;
    private RecyclerView mBreakRulesRecycler;
    private BreakRulesListAdapter mBreakRulesListAdapter;
    private LinearLayoutManager mBreakRulesLayoutManager;

    private List<UserCar> userCarList;

    private GetUserCarListTask mAuthTask;

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
        View rootView = inflater.inflate(R.layout.fragment_break_rules, container, false);

        mBreakRulesSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.break_rules_swiperefresh);
        mBreakRulesInfo = (TextView) rootView.findViewById(R.id.break_rules_info);
        mBreakRulesSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.swipe_color_1),
                getResources().getColor(R.color.swipe_color_2),
                getResources().getColor(R.color.swipe_color_3),
                getResources().getColor(R.color.swipe_color_4));

        mBreakRulesRecycler = (RecyclerView) rootView.findViewById(R.id.break_rules_recycler_view);
        mBreakRulesRecycler.setHasFixedSize(true);
        mBreakRulesLayoutManager = new LinearLayoutManager(getActivity());
        mBreakRulesRecycler.setLayoutManager(mBreakRulesLayoutManager);
        mBreakRulesListAdapter = new BreakRulesListAdapter(this, userCarList);

        mBreakRulesRecycler.setAdapter(mBreakRulesListAdapter);

        mBreakRulesSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!TextUtils.isEmpty(mainActivity.userInfoPreferences.getString("userphone", ""))) {
                    if (mAuthTask == null) {
                        mBreakRulesSwipeRefreshLayout.setRefreshing(true);
                        mAuthTask = new GetUserCarListTask();
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
                mAuthTask = new GetUserCarListTask();
                mAuthTask.execute(mainActivity.userInfoPreferences.getString("userphone", ""));
            }
        } else {
            Snackbar.make(mBreakRulesRecycler, "请先登录", Snackbar.LENGTH_LONG).show();
        }
    }

    public void onCarItemSelected(int position) {
        Intent intent = new Intent(getActivity(), QueryBreakRulesActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("usercar", userCarList.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public class GetUserCarListTask extends AsyncTask<String, Void, String> {

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
            mBreakRulesSwipeRefreshLayout.setRefreshing(false);
            try {
                userCarList = new ArrayList<>();
                userCarList = ResultJSONOperate.getUserCarsJson(result);
                if (userCarList.size() > 0) {
                    mBreakRulesInfo.setVisibility(View.GONE);
                } else {
                    mBreakRulesInfo.setText("没有任何车辆");
                    mBreakRulesInfo.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                userCarList.clear();
                mBreakRulesInfo.setText("没有任何车辆");
                mBreakRulesInfo.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                mBreakRulesInfo.setText("请检查网络状况及服务器IP设置");
                mBreakRulesInfo.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }
            mBreakRulesListAdapter = new BreakRulesListAdapter(BreakRulesFragment.this, userCarList);
            mBreakRulesListAdapter.notifyDataSetChanged();
            mBreakRulesRecycler.setAdapter(mBreakRulesListAdapter);
            mBreakRulesRecycler.requestLayout();
        }
    }
}

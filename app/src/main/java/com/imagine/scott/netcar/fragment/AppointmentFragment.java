package com.imagine.scott.netcar.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
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
import com.imagine.scott.netcar.activity.AppoDetailActivity;
import com.imagine.scott.netcar.activity.MainActivity;
import com.imagine.scott.netcar.adapter.AppointmentListAdapter;
import com.imagine.scott.netcar.bean.Order;
import com.imagine.scott.netcar.operation.Connection;
import com.imagine.scott.netcar.operation.ResultJSONOperate;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AppointmentFragment extends BaseFragment {

    private MainActivity mainActivity;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mAppoInfo;
    private RecyclerView mOrdersRecycler;
    private AppointmentListAdapter mAppointmentListAdapter;
    private LinearLayoutManager mOrdersLayoutManager;

    private List<Order> orderList;

    private GetMyOrderListTask mAuthTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mainActivity = MainActivity.mainActivity;
        orderList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_appointment, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swiperefresh);

        mSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.swipe_color_1),
                getResources().getColor(R.color.swipe_color_2),
                getResources().getColor(R.color.swipe_color_3),
                getResources().getColor(R.color.swipe_color_4));

        mAppoInfo = (TextView) rootView.findViewById(R.id.appo_info);
        mOrdersRecycler = (RecyclerView) rootView.findViewById(R.id.appo_recycler_view);
        mOrdersRecycler.setHasFixedSize(true);
        mOrdersLayoutManager = new LinearLayoutManager(getActivity());
        mOrdersRecycler.setLayoutManager(mOrdersLayoutManager);
        mAppointmentListAdapter = new AppointmentListAdapter(this, orderList);

        mOrdersRecycler.setAdapter(mAppointmentListAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mAuthTask == null) {
                    mSwipeRefreshLayout.setRefreshing(true);
                    mAuthTask = new GetMyOrderListTask();
                    mAuthTask.execute(mainActivity.userInfoPreferences.getString("userphone", ""));
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
                mAuthTask = new GetMyOrderListTask();
                mAuthTask.execute(mainActivity.userInfoPreferences.getString("userphone", ""));
            }
        } else {
            Snackbar.make(mOrdersRecycler, "请先登录", Snackbar.LENGTH_LONG).show();
        }
    }

    public void onOrderItemSelected(int position) {
        Intent intent = new Intent(getActivity(), AppoDetailActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("orderInfo", orderList.get(position));
        intent.putExtras(bundle);
        startActivity(intent);
    }

    public class GetMyOrderListTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Connection connection = new Connection();
            Map<String, Object> map = new HashMap<>();
            map.put("phone", params[0]);
            return connection.uploadParams("", "GetMyOrder", map);
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            mSwipeRefreshLayout.setRefreshing(false);
            try {
                orderList = new ArrayList<>();
                orderList = ResultJSONOperate.getOrdersJson(result);
                if (orderList.size() > 0) {
                    mAppoInfo.setVisibility(View.GONE);
                } else {
                    orderList.clear();
                    mAppoInfo.setText("没有任何订单");
                    mAppoInfo.setVisibility(View.VISIBLE);
                }
            } catch (JSONException e) {
                orderList.clear();
                mAppoInfo.setText("没有任何订单");
                mAppoInfo.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                orderList.clear();
                mAppoInfo.setText("请检查网络状况及服务器IP设置");
                mAppoInfo.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }
            mAppointmentListAdapter = new AppointmentListAdapter(AppointmentFragment.this, orderList);
            mAppointmentListAdapter.notifyDataSetChanged();
            mOrdersRecycler.setAdapter(mAppointmentListAdapter);
            mOrdersRecycler.requestLayout();
        }
    }
}

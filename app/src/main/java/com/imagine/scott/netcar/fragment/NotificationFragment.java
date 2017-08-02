package com.imagine.scott.netcar.fragment;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.imagine.scott.netcar.Constants;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.activity.MainActivity;
import com.imagine.scott.netcar.adapter.NotificationListAdapter;
import com.imagine.scott.netcar.bean.Notification;
import com.imagine.scott.netcar.operation.Connection;
import com.imagine.scott.netcar.operation.ResultJSONOperate;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationFragment extends BaseFragment {

    private MainActivity mainActivity;

    private DeleteNotificationTask delTask;

    private SwipeRefreshLayout mNotificationsSwipeRefreshLayout;
    private RecyclerView mNotificationsRecycler;
    private TextView mNotificationsInfo;
    private NotificationListAdapter mNotificationsListAdapter;
    private LinearLayoutManager mNotificationsLayoutManager;

    private List<Notification> notifications;

    private GetNotificationListTask mAuthTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mainActivity = MainActivity.mainActivity;
        notifications= new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_notification, container, false);

        mNotificationsSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.notification_swiperefresh);

        mNotificationsSwipeRefreshLayout.setColorSchemeColors(
                getResources().getColor(R.color.swipe_color_1),
                getResources().getColor(R.color.swipe_color_2),
                getResources().getColor(R.color.swipe_color_3),
                getResources().getColor(R.color.swipe_color_4));

        mNotificationsInfo = (TextView) rootView.findViewById(R.id.notification_info);
        mNotificationsRecycler = (RecyclerView) rootView.findViewById(R.id.notification_recycler_view);
        mNotificationsLayoutManager = new LinearLayoutManager(getActivity());
        mNotificationsRecycler.setLayoutManager(mNotificationsLayoutManager);
        mNotificationsListAdapter = new NotificationListAdapter(this, notifications);

        mNotificationsRecycler.setAdapter(mNotificationsListAdapter);

        mNotificationsSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mAuthTask == null) {
                    mNotificationsSwipeRefreshLayout.setRefreshing(true);
                    mAuthTask = new GetNotificationListTask();
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
                mNotificationsSwipeRefreshLayout.setRefreshing(true);
                mAuthTask = new GetNotificationListTask();
                mAuthTask.execute(mainActivity.userInfoPreferences.getString("userphone", ""));
            }
        } else {
            Snackbar.make(NotificationFragment.this.getView(), "请先登录", Snackbar.LENGTH_LONG).show();
        }
    }

    public void onNotificationItemLongClick(final int position) {
        new AlertDialog.Builder(getActivity())
                .setMessage("确认删除该条通知?")
                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (delTask == null) {
                            delTask = new DeleteNotificationTask();
                            delTask.execute(mainActivity.userInfoPreferences.getString("userphone", ""),
                                    Integer.toString(notifications.get(position).getId()));
                        }
                    }
                })
                .setNegativeButton("取消", null)
                .create()
                .show();
    }

    public class GetNotificationListTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Connection connection = new Connection();
            Map<String, Object> map = new HashMap<>();
            map.put("phone", params[0]);
            return connection.uploadParams("", "GetNotification", map);
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            mNotificationsSwipeRefreshLayout.setRefreshing(false);
            try {
                switch (Integer.parseInt(ResultJSONOperate.getRegisterCode(result))) {
                    case Constants.USER_DONT_HAVE_NOTOFICATION:
                        notifications.clear();
                        mNotificationsInfo.setText("没有任何通知");
                        mNotificationsInfo.setVisibility(View.VISIBLE);
                        break;
                    case Constants.GET_NOTIFICATION_SUCCESS:
                        notifications = new ArrayList<>();
                        notifications = ResultJSONOperate.getNotificationsJson(result);

                        if (notifications.size() > 0) {
                            mNotificationsInfo.setVisibility(View.GONE);
                        } else {
                            notifications.clear();
                            mNotificationsInfo.setText("没有任何通知");
                            mNotificationsInfo.setVisibility(View.VISIBLE);
                        }
                        break;
                    default:
                        notifications.clear();
                        mNotificationsInfo.setText("没有任何通知");
                        mNotificationsInfo.setVisibility(View.VISIBLE);
                        break;
                }
            } catch (JSONException e) {
                notifications.clear();
                mNotificationsInfo.setText("没有任何通知");
                mNotificationsInfo.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                notifications.clear();
                mNotificationsInfo.setText("请检查网络状况及服务器IP设置");
                mNotificationsInfo.setVisibility(View.VISIBLE);
                e.printStackTrace();
            }
            mNotificationsListAdapter = new NotificationListAdapter(NotificationFragment.this, notifications);
            mNotificationsListAdapter.notifyDataSetChanged();
            mNotificationsRecycler.setAdapter(mNotificationsListAdapter);
            mNotificationsRecycler.requestLayout();
        }
    }

    public class DeleteNotificationTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Connection connection = new Connection();
            Map<String, Object> map = new HashMap<>();

            map.put("phone", params[0]);
            map.put("id", params[1]);

            return connection.uploadParams("", "DeleteNotification", map);
        }

        @Override
        protected void onPostExecute(final String result) {
            delTask = null;
            try {
                switch (Integer.parseInt(ResultJSONOperate.getRegisterCode(result))) {
                    case Constants.DELETE_NOTIFI_SUCCESS:
                        if (mAuthTask == null) {
                            mAuthTask = new GetNotificationListTask();
                            mAuthTask.execute(mainActivity.userInfoPreferences.getString("userphone", ""));
                        }
                        break;
                    case Constants.DELETE_ORDER_FAILED :
                        Toast.makeText(getActivity(), "删除通知失败", Toast.LENGTH_LONG).show();
                        break;
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), "删除通知异常", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
        }
    }
}

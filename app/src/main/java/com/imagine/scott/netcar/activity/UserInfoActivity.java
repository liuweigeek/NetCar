package com.imagine.scott.netcar.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

public class UserInfoActivity extends AppCompatActivity {

    public SharedPreferences userInfoPreferences;
    public SharedPreferences addrPreferences;

    //private boolean isEditMode = false;

    private CircleImageView circleImageView;
    private TextView userInfoUsername;
    private TextView userInfoPhone;
    private TextView userInfoSex;
    private TextView userInfoDrivingyears;
    private TextView userInfoRegion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.user_info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        userInfoPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        addrPreferences = getSharedPreferences("ServerAddr", MODE_PRIVATE);
        initView();
        showInfo();
    }

    /*public void switchEditMode(boolean isEditMode) {
        if (isEditMode) {
            userInfoUsername.setEnabled(true);
            userInfoPhone.setEnabled(true);
            userInfoSex.setEnabled(true);
            userInfoDrivingyears.setEnabled(true);
            userInfoRegion.setEnabled(true);
            userInfoUsername.requestFocus();
        } else {
            userInfoUsername.setEnabled(false);
            userInfoPhone.setEnabled(false);
            userInfoSex.setEnabled(false);
            userInfoDrivingyears.setEnabled(false);
            userInfoRegion.setEnabled(false);
        }
    }*/

    public void initView() {
        circleImageView = (CircleImageView) findViewById(R.id.user_info_head_img);
        userInfoUsername = (TextView) findViewById(R.id.user_info_username);
        userInfoPhone = (TextView) findViewById(R.id.user_info_phone);
        userInfoSex = (TextView) findViewById(R.id.user_info_sex);
        userInfoDrivingyears = (TextView) findViewById(R.id.user_info_drivingyears);
        userInfoRegion = (TextView) findViewById(R.id.user_info_region);
    }

    public void showInfo() {

        userInfoUsername.setText(userInfoPreferences.getString("username", "未知"));
        userInfoPhone.setText(userInfoPreferences.getString("userphone", "未知"));
        userInfoSex.setText(userInfoPreferences.getString("usersex", "未知"));
        userInfoDrivingyears.setText(userInfoPreferences.getString("userdrivingyears", "未知"));
        userInfoRegion.setText(userInfoPreferences.getString("userregion", "未知"));

        String url = addrPreferences.getString("IP", null);
        if (!TextUtils.isEmpty(url)) {
            String filename = userInfoPreferences.getString("userheadimage", "");
            Log.v("Scott file name is ", filename);
            if (!TextUtils.isEmpty(filename)) {
                StringBuffer logourl = new StringBuffer("http://" + url + "/NetCar/headimage/" + filename);
                Picasso.with(circleImageView.getContext()).load(logourl.toString()).into(circleImageView);
            }
        }
    }
/*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_userinfo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id) {
            case R.id.home:
                onBackPressed();
                break;
            case R.id.action_edit:
                if (!isEditMode) {
                    item.setIcon(getResources().getDrawable(R.drawable.ic_text_save));
                    switchEditMode(true);
                } else {
                    item.setIcon(getResources().getDrawable(R.drawable.ic_text_edit));
                    switchEditMode(false);
                }
                isEditMode = !isEditMode;
                break;
        }
        return super.onOptionsItemSelected(item);
    }*/
}

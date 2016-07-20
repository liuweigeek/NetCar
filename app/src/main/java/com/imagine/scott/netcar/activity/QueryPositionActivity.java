package com.imagine.scott.netcar.activity;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.amap.api.maps.MapView;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.adapter.QueryPositionListAdapter;

public class QueryPositionActivity extends AppCompatActivity {

    private EditText queryPositionStart;
    private EditText queryPositionFinish;
    private ImageButton queryPositionSwap;
    private RecyclerView queryPositionList;
    private RecyclerView.LayoutManager queryPositionListLayoutManager;
    private QueryPositionListAdapter queryPositionListAdapter;
    private BottomSheetDialog queryPositionMapViewDialog;
    //private MapView queryPositionMapView;

    private double start_lng;
    private double start_lat;
    private double finish_lng;
    private double finish_lat;
    private String startPositionName;
    private String finishPositionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_position);
        Toolbar toolbar = (Toolbar) findViewById(R.id.query_position_toolbar);
        setSupportActionBar(toolbar);
        queryPositionStart = (EditText) findViewById(R.id.query_position_start);
        queryPositionFinish = (EditText) findViewById(R.id.query_position_finish);
        queryPositionSwap = (ImageButton) findViewById(R.id.query_position_swap);
        queryPositionList = (RecyclerView) findViewById(R.id.query_position_list);
        queryPositionListLayoutManager = new LinearLayoutManager(this);
        queryPositionList.setLayoutManager(queryPositionListLayoutManager);
        queryPositionList.setHasFixedSize(true);
        queryPositionMapViewDialog = new BottomSheetDialog(this);

        queryPositionStart.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
    }

    TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
            if (id == R.id.login || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        }
    }

}

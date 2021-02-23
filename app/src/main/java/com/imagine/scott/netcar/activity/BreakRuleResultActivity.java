package com.imagine.scott.netcar.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.cheshouye.api.client.WeizhangClient;
import com.cheshouye.api.client.json.CarInfo;
import com.cheshouye.api.client.json.CityInfoJson;
import com.cheshouye.api.client.json.WeizhangResponseHistoryJson;
import com.cheshouye.api.client.json.WeizhangResponseJson;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.adapter.BreakRuleResultListAdapter;

import java.util.ArrayList;
import java.util.List;

public class BreakRuleResultActivity extends AppCompatActivity {
    final Handler cwjHandler = new Handler();
    WeizhangResponseJson info = null;

    private View breakRuleResultPopLoader;

    private BreakRuleResultListAdapter breakRuleResultListAdapter;
    private LinearLayoutManager mResultLayoutManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_break_rule_result);
        Toolbar toolbar = (Toolbar) findViewById(R.id.break_rule_result_toolbar);
        setSupportActionBar(toolbar);

        breakRuleResultPopLoader = findViewById(R.id.break_rule_result_popLoader);
        breakRuleResultPopLoader.setVisibility(View.VISIBLE);
        Intent intent = this.getIntent();
        CarInfo car = (CarInfo) intent.getSerializableExtra("carInfo");
        step4(car);
        CityInfoJson citys = WeizhangClient.getCity(car.getCity_id());
        getSupportActionBar().setTitle(car.getChepai_no() + "在" + citys.getCity_name() + "的违章");
    }

    public void step4(final CarInfo car) {

        new Thread() {
            @Override
            public void run() {
                try {
                    info = WeizhangClient.getWeizhang(car);
                    cwjHandler.post(mUpdateResults);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

    }

    final Runnable mUpdateResults = new Runnable() {
        public void run() {
            updateUI();
        }
    };

    private void updateUI() {
        TextView result_null = (TextView) findViewById(R.id.break_rule_result_null);
        RecyclerView result_list = (RecyclerView) findViewById(R.id.break_rule_result_list);
        mResultLayoutManager = new LinearLayoutManager(this);
        result_list.setLayoutManager(mResultLayoutManager);

        breakRuleResultPopLoader.setVisibility(View.GONE);

        if (info.getStatus() == 2001) {
            result_null.setVisibility(View.GONE);
            result_list.setVisibility(View.VISIBLE);

            String titleStr = "共违章" + info.getCount() + "次, 计"
                    + info.getTotal_score() + "分, 罚款 " + info.getTotal_money()
                    + "元";

            getSupportActionBar().setSubtitle(titleStr);

            breakRuleResultListAdapter = new BreakRuleResultListAdapter(
                    this, getData());
            result_list.setAdapter(breakRuleResultListAdapter);

        } else {

            if (info.getStatus() == 5000) {
                result_null.setText("请求超时，请稍后重试");
            } else if (info.getStatus() == 5001) {
                result_null.setText("交管局系统连线忙碌中，请稍后再试");
            } else if (info.getStatus() == 5002) {
                result_null.setText("恭喜，当前城市交管局暂无您的违章记录");
            } else if (info.getStatus() == 5003) {
                result_null.setText("数据异常，请重新查询");
            } else if (info.getStatus() == 5004) {
                result_null.setText("系统错误，请稍后重试");
            } else if (info.getStatus() == 5005) {
                result_null.setText("车辆查询数量超过限制");
            } else if (info.getStatus() == 5006) {
                result_null.setText("你访问的速度过快, 请后再试");
            } else if (info.getStatus() == 5008) {
                result_null.setText("输入的车辆信息有误，请查证后重新输入");
            } else {
                result_null.setText("恭喜, 没有查到违章记录！");
            }

            getSupportActionBar().setSubtitle("");
            result_list.setVisibility(View.GONE);
            result_null.setVisibility(View.VISIBLE);
        }
    }

    private List getData() {
        List<WeizhangResponseHistoryJson> list = new ArrayList();

        for (WeizhangResponseHistoryJson weizhangResponseHistoryJson : info
                .getHistorys()) {
            WeizhangResponseHistoryJson json = new WeizhangResponseHistoryJson();
            json.setFen(weizhangResponseHistoryJson.getFen());
            json.setMoney(weizhangResponseHistoryJson.getMoney());
            json.setOccur_date(weizhangResponseHistoryJson.getOccur_date());
            json.setOccur_area(weizhangResponseHistoryJson.getOccur_area());
            json.setInfo(weizhangResponseHistoryJson.getInfo());
            list.add(json);
        }
        return list;
    }

}

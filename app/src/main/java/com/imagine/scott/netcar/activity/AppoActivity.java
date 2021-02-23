package com.imagine.scott.netcar.activity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.bean.GasStation;
import com.imagine.scott.netcar.bean.Order;
import com.imagine.scott.netcar.operation.Connection;
import com.imagine.scott.netcar.operation.ResultJSONOperate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class AppoActivity extends AppCompatActivity {

    private Calendar orderCalendar;

    private String oiltype;
    private float litre;
    private float money;

    private float unitPrice;

    private GasStation gasStation;
    private String phone;

    private RadioButton addAppoByLitre;
    private RelativeLayout addAppoDatetime;
    private TextView addAppoDatetimeText;
    private AppCompatSpinner addAppoType;
    private TextView addAppoValueTitle;
    private TextView addAppoValueName;
    private TextView addAppoValueUnit;
    private AppCompatSeekBar addAppoValue;
    private TextView addAppoUnitPrice;
    private TextView addAppoResultTitle;
    private TextView addAppoResultContent;
    private TextView addAppoResultUnit;

    private Button addAppoSave;

    private AddAppoTask mAuthTask;

    public SharedPreferences userInfoPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appo);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        gasStation = (GasStation) bundle.getSerializable("gasStation");
        userInfoPreferences = getSharedPreferences("UserInfo", MODE_PRIVATE);
        phone = userInfoPreferences.getString("userphone", "");
        getSupportActionBar().setTitle(gasStation.getName());
        initView();
    }

    public void initView() {
        unitPrice = gasStation.getGasPrice_93();

        addAppoByLitre = (RadioButton) findViewById(R.id.add_appo_by_litre);
        addAppoDatetime = (RelativeLayout) findViewById(R.id.add_appo_datetime);
        addAppoDatetimeText = (TextView) findViewById(R.id.add_appo_datetime_text);
        addAppoType = (AppCompatSpinner) findViewById(R.id.add_appo_type);
        addAppoValueTitle = (TextView) findViewById(R.id.add_appo_value_title);
        addAppoValueName = (TextView) findViewById(R.id.add_appo_value_name);
        addAppoValueUnit = (TextView) findViewById(R.id.add_appo_value_unit);
        addAppoValue = (AppCompatSeekBar) findViewById(R.id.add_appo_value);
        addAppoUnitPrice = (TextView) findViewById(R.id.add_appo_unitprice);
        addAppoResultTitle = (TextView) findViewById(R.id.add_appo_result_title);
        addAppoResultContent = (TextView) findViewById(R.id.add_appo_result_content);
        addAppoResultUnit = (TextView) findViewById(R.id.add_appo_result_unit);
        addAppoSave = (Button) findViewById(R.id.add_appo_save);

        Typeface fontFace = Typeface.createFromAsset(getAssets(), "fonts/DroidSans-Bold.ttf");
        addAppoValueName.setTypeface(fontFace);
        addAppoUnitPrice.setTypeface(fontFace);
        addAppoResultContent.setTypeface(fontFace);

        if (addAppoByLitre.isChecked()) {
            switchToByLitre();
        } else {
            switchToByMoney();
        }

        addAppoSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mAuthTask != null)
                    return;
                if (TextUtils.isEmpty(addAppoDatetimeText.getText().toString().trim())) {
                    addAppoDatetimeText.setText("请选择预约时间");
                    return;
                }
                if (!TextUtils.isEmpty(phone)) {
                    if (addAppoValue.getProgress() > 0) {
                        oiltype = addAppoType.getSelectedItem().toString();
                        mAuthTask = new AddAppoTask();
                        mAuthTask.execute(phone, Long.toString(orderCalendar.getTimeInMillis()),
                                gasStation.getName(), gasStation.getBrandname(),
                                Double.toString(gasStation.getLat()), Double.toString(gasStation.getLon()),
                                oiltype, Float.toString(litre), Float.toString(money));
                        addAppoSave.setText("正在提交..");
                        addAppoSave.setClickable(false);
                    } else {
                        String str = "";
                        if (addAppoByLitre.isChecked()) {
                            str = "油量";
                        } else {
                            str = "油价";
                        }
                        Snackbar.make(addAppoValue, str + "不可为0", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(AppoActivity.this, "请先登录", Toast.LENGTH_LONG).show();
                }
            }
        });

        addAppoDatetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });

        addAppoByLitre.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    switchToByLitre();
                } else {
                    switchToByMoney();
                }
                addAppoValue.setProgress(0);
            }
        });

        addAppoType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                switch (addAppoType.getSelectedItem().toString()) {
                    case "93/92号汽油":
                        unitPrice = gasStation.getGasPrice_93();
                        break;
                    case "97/95号汽油":
                        unitPrice = gasStation.getGasPrice_97();
                        break;
                    case "90号汽油":
                        unitPrice = gasStation.getGasPrice_90();
                        break;
                    case "柴油":
                        unitPrice = gasStation.getGasPrice_0();
                        break;
                }
                String unitPriceStr = Float.toString(unitPrice);
                if (unitPriceStr.length() > 5) {
                    unitPriceStr = unitPriceStr.substring(0, 5);
                }
                addAppoUnitPrice.setText(unitPriceStr);
                addAppoValue.setProgress(0);
                addAppoResultContent.setText(Integer.toString(0));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addAppoValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                if (addAppoByLitre.isChecked()) {
                    String str = Float.toString(progress * unitPrice);
                    if (str.length() > 5) {
                        str = str.substring(0, 5);
                    }
                    addAppoValueName.setText(Integer.toString(progress));
                    addAppoResultContent.setText(str);
                    litre = progress;
                    money = progress * unitPrice;
                } else {
                    String str = Float.toString(progress / unitPrice);
                    if (str.length() > 5) {
                        str = str.substring(0, 5);
                    }
                    addAppoValueName.setText(Integer.toString(progress));
                    addAppoResultContent.setText(str);
                    money = progress;
                    litre = progress / unitPrice;
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    public void switchToByLitre() {
        addAppoValueTitle.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_text_litre), null, null, null);
        addAppoValue.setMax(130);
        addAppoValueTitle.setText("油量: ");
        addAppoValueName.setText("0");
        addAppoValueUnit.setText(" 升");
        addAppoResultTitle.setText("总价: ");
        addAppoResultContent.setText("0");
        addAppoResultUnit.setText(" 元");
    }

    public void switchToByMoney() {
        addAppoValueTitle.setCompoundDrawablesWithIntrinsicBounds(getResources().getDrawable(R.drawable.ic_text_money), null, null, null);
        addAppoValue.setMax(700);
        addAppoValueTitle.setText("油价: ");
        addAppoValueName.setText("0");
        addAppoValueUnit.setText(" 元");
        addAppoResultTitle.setText("总量: ");
        addAppoResultContent.setText("0");
        addAppoResultUnit.setText(" 升");
    }

    public void showDateDialog() {
        Calendar c = Calendar.getInstance();
        Dialog dialog = new DatePickerDialog(AppoActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        orderCalendar = Calendar.getInstance();
                        orderCalendar.set(Calendar.YEAR, year);
                        orderCalendar.set(Calendar.MONTH, monthOfYear);
                        orderCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        showTimeDialog();
                    }
                },
                c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)
        );
        dialog.show();
    }

    public void showTimeDialog() {
        Calendar c = Calendar.getInstance();
        Dialog dialog = new TimePickerDialog(AppoActivity.this,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        orderCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        orderCalendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat sf = new SimpleDateFormat("yyyy年MM月dd日HH时mm分");
                        addAppoDatetimeText.setText(sf.format(orderCalendar.getTime()));
                    }
                },
                c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true
        );
        dialog.show();
    }

    public class AddAppoTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Map<String, Object> map = new HashMap<>();
            map.put("phone", params[0]);
            map.put("date", params[1]);
            map.put("gasstation", params[2]);
            map.put("brandname", params[3]);
            map.put("gaslat", params[4]);
            map.put("gaslng", params[5]);
            map.put("type", params[6]);
            map.put("litre", params[7]);
            map.put("money", params[8]);

            Connection connection = new Connection();
            return connection.uploadParams("", "AddAppoTask", map);
        }

        @Override
        protected void onPostExecute(String result) {
            mAuthTask = null;
            addAppoSave.setText("提交订单");
            addAppoSave.setClickable(true);
            Order order;
            try {
                order = ResultJSONOperate.getOrderJson(result);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(AppoActivity.this, "未知错误，请重试", Toast.LENGTH_LONG).show();
                return;
            }
            if (order != null) {
                Toast.makeText(AppoActivity.this, "预约成功", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(AppoActivity.this, AppoDetailActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("orderInfo", order);
                intent.putExtras(bundle);
                startActivity(intent);
                AppoActivity.this.finish();
            } else {
                Toast.makeText(AppoActivity.this, "预约失败", Toast.LENGTH_LONG).show();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}

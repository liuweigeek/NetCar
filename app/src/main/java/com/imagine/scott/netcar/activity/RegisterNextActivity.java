package com.imagine.scott.netcar.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.imagine.scott.netcar.Constants;
import com.imagine.scott.netcar.R;
import com.imagine.scott.netcar.operation.Connection;
import com.imagine.scott.netcar.operation.ResultJSONOperate;

import java.util.HashMap;
import java.util.Map;

public class RegisterNextActivity extends AppCompatActivity {

    private UserRegisterNextTask mAuthTask = null;
    private ReSendCodeTask reSendCodeTask = null;
    private Handler handler = null;

    private String phone;

    private int reSendTime = 60;

    private EditText mCodeView;
    private Button mReSend;
    private EditText mUsernameView;
    private EditText mPasswordView;
    private EditText mRePasswordView;
    private View mProgressView;
    private View mRegisterNextFormView;
    private Button signUpButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_next);

        signUpButton = (Button) findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptNextRegister();
            }
        });

        Intent intent = getIntent();
        this.phone = intent.getStringExtra("phone");

        mRegisterNextFormView = findViewById(R.id.register_next_form);
        mProgressView = findViewById(R.id.register_next_progress);
        mCodeView = (EditText) findViewById(R.id.register_next_code);
        mReSend = (Button) findViewById(R.id.register_next_resend);
        mUsernameView = (EditText) findViewById(R.id.register_next_username);
        mPasswordView = (EditText) findViewById(R.id.register_next_password);
        mRePasswordView = (EditText) findViewById(R.id.register_next_confirm_password);

        handler = new Handler();
        new ReSendThread().start();

        mReSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (reSendTime == 60) {
                    if (reSendCodeTask == null) {
                        reSendCodeTask = new ReSendCodeTask();
                        reSendCodeTask.execute(phone);
                        mReSend.setClickable(false);
                        new ReSendThread().start();
                    }
                }
            }
        });
    }

    public class ReSendThread extends Thread {
        public void run(){
            try {
                while (reSendTime >= 0) {
                    handler.post(runnableUi);
                    if (reSendTime == 0) {
                        break;
                    }
                    Thread.sleep(1000);
                    reSendTime -= 1;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    Runnable runnableUi = new Runnable(){
        @Override
        public void run() {
            if (reSendTime > 0) {
                mReSend.setText("重新获取(" + reSendTime + "S)");
            } else {
                reSendTime = 60;
                mReSend.setClickable(true);
                mReSend.setText("重新获取");
            }
        }
    };

    private void attemptNextRegister() {

        if (mAuthTask != null) {
            return;
        }

        mCodeView.setError(null);
        mUsernameView.setError(null);
        mPasswordView.setError(null);
        mRePasswordView.setError(null);

        String code = mCodeView.getText().toString();
        String username = mUsernameView.getText().toString();
        String password = mPasswordView.getText().toString();
        String confirmPassword = mRePasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (TextUtils.isEmpty(code)) {
            mCodeView.setError("请输入短信验证码");
            focusView = mCodeView;
            cancel = true;
        } else if (TextUtils.isEmpty(username)) {
            mUsernameView.setError("不可为空");
            focusView = mUsernameView;
            cancel = true;
        } else if (TextUtils.isEmpty(password)) {
            mPasswordView.setError("不可为空");
            focusView = mPasswordView;
            cancel = true;
        }else if (!isPasswordValid(password)) {
            mPasswordView.setError("密码至少为8位");
            focusView = mPasswordView;
            cancel = true;
        } else if (TextUtils.isEmpty(confirmPassword) || !password.equals(confirmPassword)) {
            mRePasswordView.setError("两次密码不匹配");
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);
            mAuthTask = new UserRegisterNextTask();
            mAuthTask.execute(code, this.phone, username, password);
        }
    }

    private boolean isPasswordValid(String phone) {
        return  phone.length() >= 8;
    }

    public class UserRegisterNextTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Map<String, Object> map = new HashMap<>();
            map.put("msgcode", params[0]);
            map.put("phone", params[1]);
            map.put("username", params[2]);
            map.put("password", params[3]);

            Connection connection = new Connection();
            return connection.uploadParams("submitinfo", "RegisterServlet", map);
        }

        @Override
        protected void onPostExecute(final String result) {
            mAuthTask = null;
            showProgress(false);
            int resCode;
            try {
                resCode = Integer.parseInt(ResultJSONOperate.getRegisterCode(result));
            } catch (Exception e) {
                e.printStackTrace();
                mPasswordView.setError("未知错误，请重试");
                return;
            }
            switch (resCode) {
                case Constants.CODE_NOT_MATCH:
                    mCodeView.setError("验证码错误");
                    mCodeView.requestFocus();
                    break;
                case Constants.REGISTER_FAILED:
                    Toast.makeText(RegisterNextActivity.this, "注册失败，请重试", Toast.LENGTH_LONG).show();
                    break;
                case Constants.REGISTER_SUCCESS:
                    Intent intent = new Intent(RegisterNextActivity.this, AddInfoActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("phone", phone);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    RegisterNextActivity.this.finish();
                    break;
                default:
                    Toast.makeText(RegisterNextActivity.this, "注册异常，请重试", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    public class ReSendCodeTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {

            Map<String, Object> map = new HashMap<>();
            map.put("phone", params[0]);

            Connection connection = new Connection();
            return connection.uploadParams("", "ReSentMsgCode", map);
        }

        @Override
        protected void onPostExecute(String result) {
            reSendCodeTask = null;
        }
    }

    //region show progress
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mRegisterNextFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterNextFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterNextFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mRegisterNextFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
    //endregion
}

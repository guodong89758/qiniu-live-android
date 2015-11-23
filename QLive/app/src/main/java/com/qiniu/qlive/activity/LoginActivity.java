package com.qiniu.qlive.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.qiniu.qlive.config.APICode;
import com.qiniu.qlive.service.UserService;
import com.qiniu.qlive.service.result.LoginResult;
import com.qiniu.qlive.utils.Account;
import com.qiniu.qlive.utils.Tools;


public class LoginActivity extends AppCompatActivity implements APICode {
    private Context context;
    private EditText loginPhoneNumberEditText;
    private EditText loginPasswordEditText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_login);
        this.loginPhoneNumberEditText = (EditText) this.findViewById(R.id.loginPhoneNumberEditText);
        this.loginPasswordEditText = (EditText) this.findViewById(R.id.loginPasswordEditText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.signup_menu_item:
                Intent intent = new Intent(this.context, SignupActivity.class);
                this.startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void LoginAction(View view) {
        final String mobile = this.loginPhoneNumberEditText.getText().toString().trim();
        final String password = this.loginPasswordEditText.getText().toString().trim();
        if (mobile.isEmpty()) {
            Toast.makeText(context, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        new Thread() {

            @Override
            public void run() {
                LoginResult loginResult = UserService.login(mobile, password);
                if (loginResult != null) {
                    switch (loginResult.getCode()) {
                        case API_OK:
                            Account.save(context, mobile, password);
                            Tools.writeSession(context, loginResult.getSessionId(), loginResult.getUserName());
                            Intent intent = new Intent(context, MainActivity.class);
                            startActivity(intent);
                            finish();
                            break;
                        case API_USER_NOT_FOUND_ERROR:
                            Tools.showToast(context, "该用户不存在，请注册！");
                            break;
                        case API_USER_PWD_ERROR:
                            Tools.showToast(context, "密码输入错误！");
                            break;
                        case API_SERVER_ERROR:
                            Tools.showToast(context, "服务器内部错误，请稍后重试！");
                            break;
                        default:
                            Tools.showToast(context, String.format("未知错误，%d %s", loginResult.getCode(), loginResult.getDesc()));
                    }
                } else {
                    Tools.showToast(context, "请求失败，请检查网络状况！");
                }
            }
        }.start();
    }
}

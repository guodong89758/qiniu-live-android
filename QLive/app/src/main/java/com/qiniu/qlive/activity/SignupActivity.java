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
import com.qiniu.qlive.service.result.SignupResult;
import com.qiniu.qlive.utils.AsyncRun;
import com.qiniu.qlive.utils.Tools;

public class SignupActivity extends AppCompatActivity implements APICode {
    private Context context;
    private EditText signupPhoneNumberEditText;
    private EditText signupPasswordEditText;
    private EditText signupNameEditText;
    private EditText signupEmailEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = this;
        setContentView(R.layout.activity_signup);
        this.signupPhoneNumberEditText = (EditText) this.findViewById(R.id.signupPhoneNumberEditText);
        this.signupNameEditText = (EditText) this.findViewById(R.id.signupUserNameEditText);
        this.signupEmailEditText = (EditText) this.findViewById(R.id.signupEmailEditText);
        this.signupPasswordEditText = (EditText) this.findViewById(R.id.signupPasswordEditText);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_signup, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.login_menu_item:
                Intent intent = new Intent(this.context, LoginActivity.class);
                this.startActivity(intent);
                this.finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void SignupAction(View view) {
        final String mobile = this.signupPhoneNumberEditText.getText().toString().trim();
        final String password = this.signupPasswordEditText.getText().toString().trim();
        final String name = this.signupNameEditText.getText().toString().trim();
        final String email = this.signupEmailEditText.getText().toString().trim();

        if (mobile.isEmpty()) {
            Toast.makeText(context, "请输入手机号码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.isEmpty()) {
            Toast.makeText(context, "请输入密码", Toast.LENGTH_SHORT).show();
            return;
        }

        if (name.isEmpty()) {
            Toast.makeText(context, "请输入用户名", Toast.LENGTH_SHORT).show();
            return;
        }

        if (email.isEmpty()) {
            Toast.makeText(context, "请输入邮箱", Toast.LENGTH_SHORT).show();
            return;
        }


        new Thread() {

            @Override
            public void run() {
                SignupResult signupResult = UserService.signup(mobile, password, name, email);
                if (signupResult != null) {
                    switch (signupResult.getCode()) {
                        case API_OK:
                            Tools.showToast(context, "注册成功，请登录！");
                            AsyncRun.run(new Runnable() {
                                @Override
                                public void run() {
                                    switchToLogin();
                                }
                            });
                            break;
                        case API_PHONE_EXISTS_ERROR:
                            Tools.showToast(context, "注册失败，该手机已注册，请登录或使用其他手机号码注册！");
                            break;
                        case API_NAME_EXISTS_ERROR:
                            Tools.showToast(context, "注册失败，该用户名已被使用！");
                            break;
                        case API_EMAIL_EXISTS_ERROR:
                            Tools.showToast(context, "注册失败，该邮箱已被使用！");
                            break;
                        case API_SERVER_ERROR:
                            Tools.showToast(context, "服务器内部错误，请稍后重试！");
                            break;
                        default:
                            Tools.showToast(context, String.format("未知错误，%d %s", signupResult.getCode(), signupResult.getDesc()));
                            break;
                    }
                } else {
                    Tools.showToast(context, "请求失败，请检查网络状况！");
                }
            }
        }.start();
    }

    public void switchToLogin() {
        Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        this.finish();
    }
}

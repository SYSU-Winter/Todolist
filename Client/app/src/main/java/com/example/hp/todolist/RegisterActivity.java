package com.example.hp.todolist;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    private EditText id;
    private EditText password;
    private Button loginBtn;
    private EditText password2;
    private EditText username;
    private ProgressDialog progressDialog;
    private Handler handler;
    private static final int FAILED = 11;
    private static final int SUCCEEDED = 22;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);
        password2 = (EditText) findViewById(R.id.password2);
        username = (EditText) findViewById(R.id.username);
        loginBtn = (Button) findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String a = id.getText().toString();
                String b = username.getText().toString();
                String c = password.getText().toString();
                String d = password2.getText().toString();
                if (checkEdit(a, b, c, d)) {//检查注册信息
                    if (isConnectingToInternet()) { //检查网络
                        // 设置一个进度对话框
                        progressDialog = new ProgressDialog(RegisterActivity.this);
                        progressDialog.setTitle("注册中");
                        progressDialog.setMessage("正在注册，请稍候...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        //启动登录Thread
                        RegisterPostThread(a, b, c);
                    } else {
                        Toast.makeText(getApplicationContext(), "网络未连接", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Handle,Msg返回成功信息，跳转到其他Activity
        handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                progressDialog.dismiss();
                switch (msg.what) {
                    case SUCCEEDED:
                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(intent);
                        Toast.makeText(RegisterActivity.this, "注册成功！", Toast.LENGTH_SHORT).show();
                        break;
                    case FAILED:
                        Toast.makeText(RegisterActivity.this, "账号已被注册，注册失败", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }
        };
    }

    private void RegisterPostThread(final String id, final String username, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Sevice传回int

                if (!id.equals("")) {
                    // 要发送的数据
                    Map<String, String> map = new HashMap<>();
                    map.put("id", id);
                    map.put("username", username);
                    map.put("password", password);
                    // 发送数据，获取对象
                    RegisterPostService registerPostService = new RegisterPostService();
                    registerPostService.send(map, new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            // 发送消息
                            Message message = new Message();
                            if (result.equals("FAILED")) {
                                message.what = FAILED;
                            } else if (result.equals("SUCCEEDED")) {
                                message.what = SUCCEEDED;
                            } else {
                                message.what = 66;
                            }
                            handler.sendMessage(message);
                        }
                    });
                }
            }
        }).start();
    }

    //检查注册信息
    private boolean checkEdit(String a, String b, String c, String d) {
        if (TextUtils.isEmpty(a)) {
            Toast.makeText(RegisterActivity.this, "账户不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(c)) {
            Toast.makeText(RegisterActivity.this, "密码不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!c.equals(d)) {
            Toast.makeText(RegisterActivity.this, "两次输入的密码不同", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(b)) {
            Toast.makeText(RegisterActivity.this, "用户名不能为空", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    // 检测网络状态
    public boolean isConnectingToInternet() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}

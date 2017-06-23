package com.example.hp.todolist;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText id;
    private EditText password;
    private Button loginBtn;
    private Button registerBtn;
    private Handler handler;
    private static final int FAILED = 111;
    private static final int SUCCEEDED = 222;
    private ProgressDialog progressDialog;
    private TextView test;

    private String a = "";
    private String b = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        id = (EditText) findViewById(R.id.id);
        password = (EditText) findViewById(R.id.password);
        loginBtn = (Button) findViewById(R.id.loginBtn);
        registerBtn = (Button) findViewById(R.id.registerBtn);
        test = (TextView) findViewById(R.id.test);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isConnectingToInternet()) { //检查网络
                    a = id.getText().toString();
                    b = password.getText().toString();
                    if (TextUtils.isEmpty(a)) {
                        Toast.makeText(LoginActivity.this, "请输入账号", Toast.LENGTH_SHORT).show();
                    } else {
                        // 启动登录Thread
                        // 设置一个进度对话框
                        progressDialog = new ProgressDialog(LoginActivity.this);
                        progressDialog.setTitle("登录中");
                        progressDialog.setMessage("正在登录，请稍候...");
                        progressDialog.setCancelable(false);
                        progressDialog.show();
                        LoginPostThread(a, b);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), "网络未连接", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // 点击注册按钮跳转到注册页面
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到注册Activity
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        //Handle,Msg返回成功信息，跳转到其他Activity
        handler = new Handler() {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                progressDialog.dismiss();
                switch (msg.what) {
                    case FAILED:
                        Toast.makeText(LoginActivity.this, "账号和密码不匹配", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCEEDED:
                        Toast.makeText(LoginActivity.this, "跳转", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        String x = id.getText().toString();
                        int xx = Integer.parseInt(x);
                        intent.putExtra("ValueOfId", xx);
                        Log.i("id.getText().toString()", x);
                        //通过Intent对象返回结果，调用setResult方法
                        setResult(2, intent);
                        finish();//结束当前的activity的生命周期
//                        intent.putExtra("com.sysu.lwt.todolist", a);
//                        startActivity(intent);
                        break;
                    case 666:
                        Toast.makeText(LoginActivity.this, "请再试一次....", Toast.LENGTH_SHORT).show();
                    default:
                        break;
                }
            }
        };

    }

    private void LoginPostThread(final String id, final String password) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!id.equals("")) {
                    // 要发送的数据
                    Map<String, String> map = new HashMap<>();
                    map.put("id", id);
                    map.put("password", password);
                    Log.d("id", id);
                    Log.d("password", password);
                    // 发送数据，获取对象
                    LoginPostService loginPostService = new LoginPostService();
                    loginPostService.send(map, new VolleyCallback() {
                        @Override
                        public void onSuccess(String result) {
                            test.setText(result);

                            // 准备发送消息
                            Message message = new Message();
                            if (result.equals("FAILED")) {
                                message.what = FAILED;
                            } else if (result.equals("SUCCEEDED")) {
                                message.what = SUCCEEDED;
                            } else {
                                message.what = 666;
                            }
                            //Log.d("妈卖逼.......", "" + message.what);
                            handler.sendMessage(message);
                        }
                    });
                }
            }
        }).start();
    }

    // 检测网络状态
    public boolean isConnectingToInternet() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }
}

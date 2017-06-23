package com.example.hp.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Map;
import java.util.Vector;

public class MainActivity extends AppCompatActivity {
    MyDB myDB;
    Toolbar toolbar;
    DrawerLayout mDrawerLayout;
    MySensor mySensor;
    TextView head_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*// 初始化一个测试用数据库
        myDB = new MyDB(this);
        myDB.deleteAll();
        myDB.insert("test0", "dscpt0", "2016年12月20日，11:00");
        myDB.insert("test1", "dscpt1", "2016年12月4日，11:00");
        myDB.insert("test2", "dscpt2", "2016年12月25日，11:00");
        myDB.finishThis("test1");*/

        toolbar = (Toolbar) findViewById(R.id.toolbarInMain);
        setSupportActionBar(toolbar);
        myDB = new MyDB(this);
        mySensor = new MySensor(this);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.mainDrawerLayout);
        ActionBarDrawerToggle mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar,
                R.string.drawer_open, R.string.drawer_close);
        mDrawerToggle.syncState();//初始化状态
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        NavigationView mNavigationView = (NavigationView) findViewById(R.id.navigation_view);
        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new taskFragment()).commit();

        // 获取头部控件
        // 点击之后跳转到登陆注册界面
        View headview = mNavigationView.inflateHeaderView(R.layout.navigation_header);
        head_iv = (TextView) headview.findViewById(R.id.id_username);
        head_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(intent1, 1);
            }
        });

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                switch (menuItem.getItemId()) {
//                    case R.id.login:
//                        Intent intent1 = new Intent(MainActivity.this, LoginActivity.class);
//                        startActivity(intent1);
//                        break;
                    case R.id.search:
                        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                        startActivity(intent);
//                        overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
//                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new searchFragment()).commit();
//                        mToolbar.setVisibility(View.INVISIBLE);
                        break;
                    case R.id.collection:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new collectionFragment()).commit();
                        toolbar.setTitle(R.string.collect_box);
                        break;
                    case R.id.today_task:
                        getSupportFragmentManager().beginTransaction().replace(R.id.frame_content, new taskFragment()).commit();
                        toolbar.setTitle(R.string.today);
                        break;
                }
                menuItem.setChecked(true);
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    // 处理从LoginActivity中返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == 2){
            if(requestCode == 1){
                int request = data.getIntExtra("ValueOfId", 666);//接收返回数据
                head_iv.setText(String.valueOf(request));
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mySensor.registerSensor();
        sendBroadcast(new Intent(NewAppWidget.ACTION_UPDATE_ALL));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mySensor.unRegisterSensor();
        sendBroadcast(new Intent(NewAppWidget.ACTION_UPDATE_ALL));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                String toShare = "";
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                Map<String, Vector<String>> map = myDB.queryAll(1);
                Vector<String> titles = map.get("titles");
                int q = titles.size();
                Log.i("map_size", Integer.toString(q));
                if (q == 0) {
                    toShare = "任务全部完成啦！";
                } else {
                    toShare = "DDL复deadline，DDL何其多\n-----------------\n";
                    Vector<String> dates = map.get("dates");
                    for (int i = 0; i < titles.size(); i++) {
                        toShare = toShare + "Task" + Integer.toString(i + 1) + ": " + titles.get(i).toString() +
                                "\nDDL: " + dates.get(i).toString() + "\n";
                    }
                }
                intent.putExtra(Intent.EXTRA_TEXT, toShare);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(Intent.createChooser(intent, "share"));
                break;
            default:
                break;
        }
        return true;
    }
}

package com.example.hp.todolist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class SearchActivity extends AppCompatActivity {
    private EditText searchET;
    private ImageButton clearInputBtn;
    private MyDB myDB;
    private Map<String, Vector<String>> all;
    private ArrayList<Map<String, Object>> itemList;
    private SimpleAdapter simpleAdapter;
    private ListView searchResultLV;
    private TextView noResultTV;
    private AlertDialog.Builder deleteDialog;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String title = (String) msg.obj;
            itemList.clear();
            for (int i = 0; i < all.get("titles").size(); i++) {
                if (all.get("titles").get(i).contains(title)) {
                    Map<String, Object> map = new HashMap<String, Object>();
                    map.put("title", all.get("titles").get(i));
                    map.put("date", all.get("dates").get(i));
                    map.put("description", all.get("descriptions").get(i));
                    itemList.add(map);
                }
            }
            simpleAdapter.notifyDataSetChanged();
            if (itemList.size() == 0) {
                noResultTV.setVisibility(View.VISIBLE);
            } else {
                noResultTV.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarInSearch);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        myDB = new MyDB(this);
        all = myDB.queryAll(MyDB.ALL);

        searchResultLV = (ListView) findViewById(R.id.searchResultLV);
        searchET = (EditText) findViewById(R.id.searchET);
        clearInputBtn = (ImageButton) findViewById(R.id.clearInputBtn);
        noResultTV = (TextView) findViewById(R.id.noResultTV);

        itemList = new ArrayList<Map<String, Object>>();
        simpleAdapter = new SimpleAdapter(this, itemList, R.layout.item_in_search,
                new String[] {"title", "date", "description"},
                new int[] {R.id.title_in_search, R.id.date_in_search, R.id.description_in_search});
        searchResultLV.setAdapter(simpleAdapter);

        deleteDialog = new AlertDialog.Builder(this);
        deleteDialog.setTitle(R.string.delete).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });

        searchResultLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = (String) itemList.get(position).get("title");
                Intent intent = new Intent(SearchActivity.this, DetailsActivity.class);
                intent.putExtra("title", title);
                startActivity(intent);
            }
        });
        searchResultLV.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                deleteDialog.setMessage("您确定要删除任务 " + itemList.get(position).get("title") + " 吗？")
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDB.delete((String) itemList.get(position).get("title"));
                                itemList.remove(position);
                                simpleAdapter.notifyDataSetChanged();
                                sendBroadcast(new Intent(NewAppWidget.ACTION_UPDATE_ALL));
                            }
                        }).show();
                return true;
            }
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    clearInputBtn.setVisibility(View.GONE);
                    itemList.clear();
                    simpleAdapter.notifyDataSetChanged();
                }
                else {
                    clearInputBtn.setVisibility(View.VISIBLE);
                    Message message = new Message();
                    message.obj = s.toString();
                    handler.sendMessage(message);
                }
            }
        });

        clearInputBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchET.setText("");
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

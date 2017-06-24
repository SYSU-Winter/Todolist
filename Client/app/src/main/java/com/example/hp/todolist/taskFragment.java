package com.example.hp.todolist;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.getbase.floatingactionbutton.AddFloatingActionButton;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class taskFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ListView todolv;
    RelativeLayout divider;
    ListView finishlv;
    ImageView relaxImage;
    LinearLayout relaxText;
    AddFloatingActionButton addButton;
    MyDB myDB;
    AlertDialog.Builder deleteDialog;
    MyLVAdapter unfinishedAdapter;
    MyLVAdapter finishedAdapter;
    Map<String, Vector<String>> unfinished = new HashMap<String, Vector<String>>();
    Map<String, Vector<String>> finished = new HashMap<String, Vector<String>>();

    static boolean broadcast = false;

    public taskFragment() {
        // Required empty public constructor
    }


    public static taskFragment newInstance(String param1, String param2) {
        taskFragment fragment = new taskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_task, container, false);
        todolv = (ListView) view.findViewById(R.id.task_todo);
        divider = (RelativeLayout) view.findViewById(R.id.divider);
        finishlv = (ListView) view.findViewById(R.id.task_finished);
        relaxImage = (ImageView) view.findViewById(R.id.relaxDay);
        relaxText = (LinearLayout) view.findViewById(R.id.noTaskText);
        addButton = (AddFloatingActionButton) view.findViewById(R.id.addButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("title", "");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
            }
        });

        myDB = new MyDB(getActivity());

        deleteDialog = new AlertDialog.Builder(getActivity());
        deleteDialog.setTitle("删除").setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        unfinishedAdapter = new MyLVAdapter(getActivity(), handler, unfinished, false);
        todolv.setAdapter(unfinishedAdapter);
        finishedAdapter = new MyLVAdapter(getActivity(), handler, finished, true);
        finishlv.setAdapter(finishedAdapter);

        todolv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = (String) unfinished.get("titles").get(position);
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("title", title);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
            }
        });
        todolv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                deleteDialog.setMessage("您确定要删除任务 " + unfinished.get("titles").get(position) + " 吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDB.delete(unfinished.get("titles").get(position));
                                unfinished.get("titles").remove(position);
                                unfinished.get("dates").remove(position);
                                unfinishedAdapter.notifyDataSetChanged();
                                if (finished.get("titles").size() == 0 && unfinished.get("titles").size() == 0) {
                                    relaxImage.setVisibility(View.VISIBLE);
                                    relaxText.setVisibility(View.VISIBLE);
                                }
                            }
                        }).show();
                return true;
            }
        });

        finishlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String title = (String) finished.get("titles").get(position);
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                intent.putExtra("title", title);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_anim, R.anim.out_anim);
            }
        });
        finishlv.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                deleteDialog.setMessage("您确定要删除任务 " + finished.get("titles").get(position) + " 吗？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDB.delete(finished.get("titles").get(position));
                                finished.get("titles").remove(position);
                                finished.get("dates").remove(position);
                                finishedAdapter.notifyDataSetChanged();
                                if (finished.get("titles").size() == 0 && unfinished.get("titles").size() == 0) {
                                    relaxImage.setVisibility(View.VISIBLE);
                                    relaxText.setVisibility(View.VISIBLE);
                                }
                            }
                        }).show();
                return true;
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        Map<String, Vector<String>> newUnfinished = myDB.queryAll(MyDB.UNFINISHED);
        Map<String, Vector<String>> newFinished = myDB.queryAll(MyDB.FINISHED);

        Calendar calendar = Calendar.getInstance();
        String today = "2017年06月21日";
        if ((calendar.get(Calendar.MONTH) + 1) < 10) {
            today = calendar.get(Calendar.YEAR) + "年0" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日";
            Log.d("today = ", today);
        } else {
            today = calendar.get(Calendar.YEAR) + "年" + (calendar.get(Calendar.MONTH) + 1) + "月" + calendar.get(Calendar.DAY_OF_MONTH) + "日";
            Log.d("today = ", today);
        }
        //String today = calendar.get(Calendar.DAY_OF_MONTH) + " " + (calendar.get(Calendar.MONTH) + 1) + "月 " + calendar.get(Calendar.YEAR);
        for (int i = 0; i < newUnfinished.get("dates").size(); ) {
            if (!newUnfinished.get("dates").get(i).contains(today)) {
                int index = newUnfinished.get("dates").indexOf(newUnfinished.get("dates").get(i));
                newUnfinished.get("titles").remove(index);
                newUnfinished.get("dates").remove(index);
            } else {
                i++;
            }
        }
        for (int i = 0; i < newFinished.get("dates").size(); ) {
            if (!newFinished.get("dates").get(i).contains(today)) {
                int index = newFinished.get("dates").indexOf(newFinished.get("dates").get(i));
                newFinished.get("titles").remove(index);
                newFinished.get("dates").remove(index);
            } else {
                i++;
            }
        }

        unfinished.clear();
        unfinished.put("titles", newUnfinished.get("titles"));
        unfinished.put("dates", newUnfinished.get("dates"));
        finished.clear();
        finished.put("titles", newFinished.get("titles"));
        finished.put("dates", newFinished.get("dates"));

        unfinishedAdapter.notifyDataSetChanged();
        finishedAdapter.notifyDataSetChanged();

        if (!broadcast) {
            Bundle bundle = new Bundle();
            bundle.putInt("count", unfinishedAdapter.getCount());
            Intent intent = new Intent();
            intent.putExtras(bundle);
            intent.setAction("com.example.hp.staticreceiver");
            getActivity().sendBroadcast(intent);
            broadcast = true;
        }

        if (finishedAdapter.getCount() == 0) {
            divider.setVisibility(View.GONE);
        } else {
            divider.setVisibility(View.VISIBLE);
        }
        if (unfinishedAdapter.getCount() > 0 || finishedAdapter.getCount() > 0) {
            relaxImage.setVisibility(View.GONE);
            relaxText.setVisibility(View.GONE);
        } else {
            relaxImage.setVisibility(View.VISIBLE);
            relaxText.setVisibility(View.VISIBLE);
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Intent intent = (Intent) msg.obj;
            boolean isfinished = intent.getBooleanExtra("isfinished", true);
            String title = intent.getStringExtra("title");
            String date = intent.getStringExtra("date");
            if (isfinished) {
                myDB.unfinishThis(title);
                unfinished.get("titles").add(title);
                unfinished.get("dates").add(date);
                finished.get("titles").remove(finished.get("titles").indexOf(title));
                finished.get("dates").remove(finished.get("dates").indexOf(date));
            } else {
                myDB.finishThis(title);
                finished.get("titles").add(title);
                finished.get("dates").add(date);
                unfinished.get("titles").remove(unfinished.get("titles").indexOf(title));
                unfinished.get("dates").remove(unfinished.get("dates").indexOf(date));
            }
            finishedAdapter.notifyDataSetChanged();
            unfinishedAdapter.notifyDataSetChanged();

            if (finishedAdapter.getCount() == 0) {
                divider.setVisibility(View.GONE);
            } else {
                divider.setVisibility(View.VISIBLE);
            }
        }
    };

}

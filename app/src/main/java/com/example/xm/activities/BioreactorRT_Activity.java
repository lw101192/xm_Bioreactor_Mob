package com.example.xm.activities;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xm.adapter.MessageAdapter;
import com.example.xm.bean.MessageItem;
import com.example.xm.bean.StaticVar;
import com.example.xm.fragment.HistroyFragment;
import com.example.xm.util.DataBaseHelper;
import com.example.xm.callback.HistroyItemTouchHelperCallback;
import com.example.xm.widget.CustomadeDialog;
import com.example.xm.bioreactormobn.R;
import com.xm.Dao.OperationDao;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class BioreactorRT_Activity extends AppCompatActivity implements View.OnClickListener {
    Intent intent;
    public static Handler handler = null;
    Timer timer = null;
    TextView nicknanme;
    TextView id;
    TextView createtime;
    TextView online;
    TextView lastsynchronizetime;
    TextView state;
    TextView plantname;
    TextView inflatetime;
    TextView holdtime;
    TextView bleedtime;
    TextView cycletime;
    TextView times;
    TextView inflatecount;
    TextView holdcount;
    TextView bleedcount;
    TextView cyclecount;
    TextView count;
    ImageView pumpstate;
    ImageView valve1state;
    ImageView valve2state;

    ProgressDialog progressDialog;
    private String toID;            //目标设备ID
    private String nickName;        //目标设备别名
    private String onLine;          //目标设备在线状态
    private String createTime;      //目标设备添加日期
    DecimalFormat decimalFormat;

    NotificationCompat.Builder mBuilder;
    public NotificationManager mNotificationManager;
    private MessageAdapter adapter;
    private List<MessageItem> messagelist = new ArrayList();
    private RecyclerView messagelv;
    private ItemTouchHelper itemTouchHelper;
    String start_stop = "启动";

    ImageButton action;
    ImageButton messageaction;
    private Toolbar toolbar;
    private TextView title;
    private CheckBox cb_push;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bioreactor);
        initView();
        initHandler();

    }

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {
                    case 1:
                        if (timer != null)
                            timer.cancel();
                        if (msg.obj instanceof com.xm.Dao.OperationDao) {
                            if (progressDialog != null) {
                                progressDialog.setMessage("同步成功");
                                Message message = new Message();
                                message.what = 3;
                                handler.sendMessageDelayed(message, 2000);
                                String s = "";
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                s = simpleDateFormat.format(new Date(System.currentTimeMillis()));
                                lastsynchronizetime.setText(s);

                                DataBaseHelper dataBaseHelper = new DataBaseHelper(BioreactorRT_Activity.this, MainActivity.DB_NAME);
                                SQLiteDatabase sqliteDatabase = dataBaseHelper.getReadableDatabase();
                                ContentValues values;
                                values = new ContentValues();
                                values.put("lastsynchronizetime", s);
                                sqliteDatabase.update("mymachine", values, "id=?", new String[]{toID});
                                sqliteDatabase.close();
                            }

                            state.setText(((OperationDao) msg.obj).getState());
                            plantname.setText(((OperationDao) msg.obj).getPlantname());
                            inflatecount.setText(((OperationDao) msg.obj).getInflatetimesurplus());
                            inflatetime.setText(((OperationDao) msg.obj).getInflatetimetotal());
                            if (((OperationDao) msg.obj).getHoldtimesurplus() != null)
                                holdcount.setText(decimalFormat.format(Float.parseFloat(((OperationDao) msg.obj).getHoldtimesurplus()) / 60));
                            holdtime.setText(((OperationDao) msg.obj).getHoldtimetotal());
                            if (((OperationDao) msg.obj).getBleedtimesurplus() != null)
                                bleedcount.setText(decimalFormat.format(Float.parseFloat(((OperationDao) msg.obj).getBleedtimesurplus()) / 60));
                            bleedtime.setText(((OperationDao) msg.obj).getBleedtimetotal());
                            if (((OperationDao) msg.obj).getCycletimesurplus() != null)
                                cyclecount.setText(decimalFormat.format(Float.parseFloat(((OperationDao) msg.obj).getCycletimesurplus()) / 3600));
                            cycletime.setText(((OperationDao) msg.obj).getCycletimetotal());
                            if (((OperationDao) msg.obj).getTimestotal() != null)
                                if (((OperationDao) msg.obj).getTimestotal().equals("+∞")) {
                                    count.setText("+∞");
                                } else {
                                    count.setText(((OperationDao) msg.obj).getTimessurplus());
                                }

                            times.setText(((OperationDao) msg.obj).getTimestotal());
                            if (((OperationDao) msg.obj).getState() != null) {
                                switch (((OperationDao) msg.obj).getState()) {
                                    case "充气中":
                                        pumpstate.setImageResource(R.drawable.on);
                                        valve1state.setImageResource(R.drawable.on);
                                        valve2state.setImageResource(R.drawable.on);
                                        start_stop = "停止";
                                        break;
                                    case "保持中":
                                        pumpstate.setImageResource(R.drawable.off);
                                        valve1state.setImageResource(R.drawable.on);
                                        valve2state.setImageResource(R.drawable.off);
                                        start_stop = "停止";
                                        break;
                                    case "放气中":
                                        pumpstate.setImageResource(R.drawable.off);
                                        valve1state.setImageResource(R.drawable.off);
                                        valve2state.setImageResource(R.drawable.on);
                                        start_stop = "停止";
                                        break;
                                    case "等待中":
                                        pumpstate.setImageResource(R.drawable.off);
                                        valve1state.setImageResource(R.drawable.off);
                                        valve2state.setImageResource(R.drawable.off);
                                        start_stop = "停止";
                                        break;
                                    case "已停止":
                                        pumpstate.setImageResource(R.drawable.off);
                                        valve1state.setImageResource(R.drawable.off);
                                        valve2state.setImageResource(R.drawable.off);
                                        start_stop = "启动";
                                        break;
                                    default:
                                        break;
                                }
                            } else {
                                start_stop = "启动";
                            }
                        }

                        break;

                    case 2:
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }
                        if (timer != null)
                            timer.cancel();
                        Toast.makeText(BioreactorRT_Activity.this, "目标已下线", Toast.LENGTH_SHORT).show();
                        break;
                    case 3:
                        if (timer != null)
                            timer.cancel();
                        if (progressDialog != null) {
                            progressDialog.dismiss();
                            progressDialog = null;
                        }

                        break;
                    case StaticVar.REFRESH_MESSAGE:
                        getMessageList();
                        messagelv.smoothScrollToPosition(messagelist.size());
                        break;
                    case StaticVar.QUERY_CONFIG:
                        if (msg.obj.toString().equals("是"))
                            cb_push.setChecked(true);
                        break;
                    case StaticVar.QUERY_CONFIG_RESULT:
                        Toast.makeText(BioreactorRT_Activity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                        break;
//                    case 4:
//                        mBuilder.setContentTitle("生物反应器")
//                                .setWhen(System.currentTimeMillis())//通知产生的时间，会在通知信息里显示
//                                .setContentText(plantname.getText().toString() + "的仪器已完成所有循环")
////						.setNumber(number)//显示数量
//                                .setTicker("生物反应器通知来啦");//通知首次出现在通知栏，带上升动画效果的
//                        mNotificationManager.notify(100, mBuilder.build());
//                        break;


                }

            }
        };

    }

    private void initView() {
        intent = getIntent();
        decimalFormat = new DecimalFormat("0.00");//构造方法的字符格式这里如果小数不足2位,会以0补足.

        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("测试标题")
                .setContentText("测试内容")
                .setContentIntent(PendingIntent.getActivity(this, 1, new Intent(), PendingIntent.FLAG_CANCEL_CURRENT))
//		.setNumber(number)//显示数量
                .setTicker("测试通知来啦")//通知首次出现在通知栏，带上升动画效果的

                .setPriority(Notification.DEFAULT_ALL)//设置该通知优先级
//		.setAutoCancel(true)//设置这个标志当用户单击面板就可以让通知将自动取消
                .setOngoing(false)//ture，设置他为一个正在进行的通知。他们通常是用来表示一个后台任务,用户积极参与(如播放音乐)或以某种方式正在等待,因此占用设备(如一个文件下载,同步操作,主动网络连接)
                .setDefaults(Notification.DEFAULT_VIBRATE)//向通知添加声音、闪灯和振动效果的最简单、最一致的方式是使用当前的用户默认设置，使用defaults属性，可以组合：
                        //Notification.DEFAULT_ALL  Notification.DEFAULT_SOUND 添加声音 // requires VIBRATE permission
                .setSmallIcon(R.mipmap.ic_launcher);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        nicknanme = (TextView) findViewById(R.id.nickname);
        id = (TextView) findViewById(R.id.id);
        createtime = (TextView) findViewById(R.id.createtime);
        lastsynchronizetime = (TextView) findViewById(R.id.lastsynchronizetime);
        online = (TextView) findViewById(R.id.online);
        state = (TextView) findViewById(R.id.state);
        plantname = (TextView) findViewById(R.id.textview);
        inflatetime = (TextView) findViewById(R.id.inflatetime);
        holdtime = (TextView) findViewById(R.id.holdtime);
        bleedtime = (TextView) findViewById(R.id.bleedtime);
        cycletime = (TextView) findViewById(R.id.cycletime);
        times = (TextView) findViewById(R.id.times);
        inflatecount = (TextView) findViewById(R.id.inflatecount);
        holdcount = (TextView) findViewById(R.id.holdcount);
        bleedcount = (TextView) findViewById(R.id.bleedcount);
        cyclecount = (TextView) findViewById(R.id.cyclecount);
        count = (TextView) findViewById(R.id.count);
        pumpstate = (ImageView) findViewById(R.id.pumpstate);
        valve1state = (ImageView) findViewById(R.id.valve1state);
        valve2state = (ImageView) findViewById(R.id.valve2state);

        action = (ImageButton) findViewById(R.id.action);
        messageaction = (ImageButton) findViewById(R.id.messageaction);


//        infotitle.setOnClickListener(this);
//        processtitle.setOnClickListener(this);
//        messagetitle.setOnClickListener(this);

        action.setOnClickListener(this);
        messageaction.setOnClickListener(this);
//        start_stop.setOnClickListener(this);
//        synchronize.setOnClickListener(this);


        toID = intent.getStringExtra("toID");
        nickName = intent.getStringExtra("nicknname");
        onLine = getOnline();
        createTime = getCreateTime();

        nicknanme.setText(nickName);
        id.setText(toID);
        createtime.setText(createTime);
        if (onLine.equals("是")) {
            online.setText("在线");
        } else {
            online.setText("离线");
            Toast.makeText(this, "当前设备不在线，无法控制", Toast.LENGTH_SHORT).show();
        }

        getLastSynchronizeTime();

        initRecycleView();

        initToolBar();

    }

    /**
     * 获取最近一次同步时间
     */
    private void getLastSynchronizeTime() {
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this, MainActivity.DB_NAME);
        SQLiteDatabase sqliteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqliteDatabase.rawQuery("select lastsynchronizetime from mymachine where id=?", new String[]{toID});
        if (cursor.getCount() > 0) {
            cursor.moveToNext();
            lastsynchronizetime.setText(cursor.getString(cursor.getColumnIndex("lastsynchronizetime")));
        }
        cursor.close();
        sqliteDatabase.close();
    }

    private void initToolBar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        title = (TextView) findViewById(R.id.title);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        title.setText(nickName);
//        getSupportActionBar().setTitle(nickName);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.back_in_from_left, R.anim.back_out_to_right);
    }

    private void initRecycleView() {
        messagelv = (RecyclerView) findViewById(R.id.messge_lv);
        adapter = new MessageAdapter(toID, messagelist, this);
        messagelv.setLayoutManager(new LinearLayoutManager(this));
        messagelv.setAdapter(adapter);
//        messagelv.setItemAnimator(new SlideInOutLeftItemAnimator(messagelv));
        ItemTouchHelper.Callback callback = new HistroyItemTouchHelperCallback(adapter);
        itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(messagelv);
        getMessageList();
    }

    private List<MessageItem> getMessageList() {
        messagelist.clear();
        MessageItem item = null;
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this, MainActivity.DB_NAME);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select * from histroy where id=? order by createtime", new String[]{toID});
        while (cursor.moveToNext()) {
            item = new MessageItem(cursor.getString(cursor.getColumnIndex("content")), cursor.getString(cursor.getColumnIndex("createtime")));
            messagelist.add(item);
        }
        cursor.close();
        sqLiteDatabase.close();
        adapter.notifyDataSetChanged();
        return messagelist;
    }

    /**
     * 从本地数据库获取添加时间
     * @return
     */
    private String getCreateTime() {
        String createtime = "否";
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this, MainActivity.DB_NAME);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select createtime from mymachine where id=?", new String[]{toID});
        cursor.moveToNext();
        createtime = cursor.getString(0);
        cursor.close();
        sqLiteDatabase.close();
        return createtime;
    }

    /**
     *从本地数据库获取在线状态
     * @return
     */
    private String getOnline() {
        String online = "否";
        DataBaseHelper dataBaseHelper = new DataBaseHelper(this, MainActivity.DB_NAME);
        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getReadableDatabase();
        Cursor cursor = sqLiteDatabase.rawQuery("select online from mymachine where id=?", new String[]{toID});
        cursor.moveToNext();
        online = cursor.getString(0);
        cursor.close();
        sqLiteDatabase.close();
        return online;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.action:
//                synchornize();
                showActionPopupMenu(view);
                break;
            case R.id.messageaction:
                showMessageActionPopupMenu(view);
//                if(start_stop.getText().toString().equals("启动")){
//                    sendMsg("TR:Start\r\n");
//                    start_stop.setText("停止");
//                }else{
//                    sendMsg("TR:Stop\r\n");
//                    start_stop.setText("启动");
//                }

                break;


        }
    }

    private void showMessageActionPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(BioreactorRT_Activity.this, view);
        Menu menu = popupMenu.getMenu();
        menu.add("清空");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()) {
                    case "清空":
                        DataBaseHelper dataBaseHelper = new DataBaseHelper(BioreactorRT_Activity.this, MainActivity.DB_NAME);
                        SQLiteDatabase sqLiteDatabase = dataBaseHelper.getWritableDatabase();
                        sqLiteDatabase.delete("histroy", "id=?", new String[]{toID});
                        sqLiteDatabase.close();
                        getMessageList();
                        HistroyFragment.handler.sendEmptyMessage(StaticVar.REFRESH_HISTROY);
                        break;
                }
                return false;
            }
        });

    }

    private void showActionPopupMenu(View view) {
        PopupMenu popupMenu = new PopupMenu(BioreactorRT_Activity.this, view);
        Menu menu = popupMenu.getMenu();
        menu.add(start_stop);
        menu.add("同步");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getTitle().toString()) {
                    case "启动":
                        sendMsg("TR:Start\r\n");
                        start_stop = "停止";
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                synchornize();
                            }
                        }, 500);

                        break;
                    case "停止":
                        sendMsg("TR:Stop\r\n");
                        start_stop = "启动";
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                synchornize();
                            }
                        }, 500);
                        break;
                    case "同步":
                        synchornize();
                        break;
                }
                return false;
            }
        });
    }

//    private void update(Map<String, String> map) {
//        DataBaseHelper dataBaseHelper = new DataBaseHelper(this, MainActivity.DB_NAME);
//        SQLiteDatabase sqliteDatabase = dataBaseHelper.getReadableDatabase();
//        ContentValues values;
//        values = new ContentValues();
//        values.put("id", map.get("MachineID"));
//        values.put("nickname", map.get("MachineNickName"));
//        values.put("createtime", map.get("CreateTime"));
//        sqliteDatabase.update("mymachine", values, "id=?", new String[]{map.get("MachineID")});
//        sqliteDatabase.close();
//    }

    private void synchornize() {
        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (progressDialog != null) {
                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            // TODO Auto-generated method stub
                            progressDialog.setMessage("同步失败，请检查网络");
                            Message msg = new Message();
                            msg.what = 3;
                            handler.sendMessageDelayed(msg, 2000);
//                                    try {
//                                        Thread.sleep(2000);
//                                        progressDialog.dismiss();
//                                    } catch (InterruptedException e) {
//                                        // TODO Auto-generated catch block
//                                        e.printStackTrace();
//                                    }
                        }
                    });

                }
            }
        }, 8000);
        progressDialog = new ProgressDialog(BioreactorRT_Activity.this);
        progressDialog.setMessage("同步中...");
        progressDialog.show();
        progressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                if (timer != null)
                    timer.cancel();
            }
        });

        sendMsg("RE:SynchronousRequest\r\n");
    }


    public void sendMsg(final String content) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                // TODO Auto-generated method stub
                try {
                    Object[] msg = new Object[3];
                    msg[0] = MainActivity.USERNAME;
                    msg[1] = toID;
                    msg[2] = content;
                    MainActivity.client.sendFlag("SendCmd");
                    for (int i = 0; i < (int) msg.length; i++) {
                        try {
                            MainActivity.client.Client_out.writeObject(msg[i]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }).start();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        IntentFilter filter = new IntentFilter();
        filter.addAction(StaticVar.FINISH_ACTIVITY);
        registerReceiver(broadcastReceiver, filter);
        synchornize();
        MainActivity.bioreactorisshowing = true;
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        unregisterReceiver(broadcastReceiver);
        MainActivity.bioreactorisshowing = false;
        super.onDestroy();
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            BioreactorRT_Activity.this.finish();
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.biomenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.set:

                CustomadeDialog.Builder builder = new CustomadeDialog.Builder(BioreactorRT_Activity.this);
                View view = LayoutInflater.from(BioreactorRT_Activity.this).inflate(R.layout.layout_set, null);
//                final EditText edt_timer = (EditText)view.findViewById(R.id.edt_timer);
//                final CheckBox cb_timer = (CheckBox) view.findViewById(R.id.cb_timer);
                cb_push = (CheckBox) view.findViewById(R.id.cb_push);
                builder.setContentView(view).setTitle("设置").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String msg[] = new String[4];
                        msg[0] = MainActivity.USERNAME;
                        msg[1] = toID;
                        msg[2] = "push";

                        if (cb_push.isChecked()) {
                            msg[3] = "open";
                        } else {
                            msg[3] = "close";
                        }
                        MainActivity.client.sendFlag("config");
                        MainActivity.client.sendMsg(msg);
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).create().show();
                String msg[] = new String[3];
                msg[0] = MainActivity.USERNAME;
                msg[1] = toID;
                msg[2] = "push";

                MainActivity.client.sendFlag("queryconfig");
                MainActivity.client.sendMsg(msg);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        // TODO Auto-generated method stub
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            finish();
//            overridePendingTransition(R.anim.back_in_from_left,
//                    R.anim.back_out_to_right);
//            return true;
//        }
//        return super.onKeyDown(keyCode, event);
//    }

}

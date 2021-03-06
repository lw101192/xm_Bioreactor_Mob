package com.example.xm.activities;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.xm.bean.StaticVar;
import com.example.xm.bioreactormobn.R;
import com.example.xm.util.ConnctionServer;
import com.example.xm.thread.SocketServerThread;


public class Login_Activity extends Activity implements OnClickListener {

    public static Button userlogin;
    public static Button register;
    public static Button forget;
    private Button feedback;
    private static EditText tel;
    private static EditText password;
    private static CheckBox user_remember;
    private static CheckBox user_autologin;

    private static Toast toast;
    SharedPreferences sharepreferences;
    static Editor editor;
    public static LinearLayout passwordlayout;
    public static Handler loginhandler;
    public static ProgressDialog progressdialog;
    //    static boolean atThisActivity = false;
    public static Timer timer2;
    SocketServerThread serverThread;

    String uname;
    String pword;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        initView();
        initHandler();
        initVar();

//        userlogin.setText("登录");

    }


    private void initView() {
        // TODO Auto-generated method stub

//        atThisActivity = true;

        progressdialog = new ProgressDialog(Login_Activity.this);
        progressdialog.setCancelable(false);

        sharepreferences = getSharedPreferences("config", MODE_PRIVATE);
        editor = sharepreferences.edit();

        passwordlayout = (LinearLayout) findViewById(R.id.passwordlayout);

        userlogin = (Button) findViewById(R.id.Usernamelogin);
        register = (Button) findViewById(R.id.register);
        forget = (Button) findViewById(R.id.forget);
        userlogin.requestFocus();
        tel = (EditText) findViewById(R.id.tel);
        password = (EditText) findViewById(R.id.password);
        user_remember = (CheckBox) findViewById(R.id.user_remember);
        user_autologin = (CheckBox) findViewById(R.id.user_autologin);
        feedback = (Button) findViewById(R.id.feedback);

        userlogin.setOnClickListener(this);
        register.setOnClickListener(this);
        forget.setOnClickListener(this);
        feedback.setOnClickListener(this);

        tel.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                password.setText("");       //如果手机号发生了变化，清空密码
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        user_remember.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                // TODO Auto-generated method stub
                if (!isChecked) {       //如果“记住密码”被取消，同时取消“自动登录”
                    user_autologin.setChecked(false);
                }
            }
        });
        user_autologin
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        // TODO Auto-generated method stub
                        if (isChecked) {        //如果“自动登录”被选中，同时选中“记住密码”
                            user_remember.setChecked(true);
                        } else {
                            editor.putBoolean("user_autologin", false);
                        }
                    }
                });
    }

    private void initVar() {
        // TODO Auto-generated method stub
        tel.setText(sharepreferences.getString("username", null));
        password.setText(sharepreferences.getString("password", null));
        if (sharepreferences.getBoolean("user_remember", false))
            user_remember.setChecked(true);
        if (sharepreferences.getBoolean("user_autologin", false)) {
            user_autologin.setChecked(true);
        }
//        if (atThisActivity && MainActivity.log) {
//            register.setVisibility(View.GONE);
//            forget.setVisibility(View.GONE);
//            userlogin.setText("注销");
//            passwordlayout.setVisibility(View.GONE);
//            tel.setEnabled(false);
//            user_remember.setEnabled(false);
//            user_autologin.setEnabled(false);
//        }

        Intent intent = getIntent();
        if (intent.getAction() != null && intent.getAction().equals("offline")) {
//            if (userlogin.getText().toString().equals("注销"))
//                Login_Activity.userlogin.performClick();
            logOut();
            Toast.makeText(Login_Activity.this, "账号异地登录！", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 登录线程
     */
    private class LoginThread extends Thread {
        private String username;
        private String password;

        public LoginThread(String username, String password) {
            // TODO Auto-generated constructor stub
            this.username = username;
            this.password = password;
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub

            if (TextUtils.isEmpty(username) || TextUtils.isEmpty(password)) {
                loginhandler.sendEmptyMessage(4);
                return;
            }
            try {
                String msg[] = new String[2];
                ConnctionServer cs = new ConnctionServer(); // 连接服务器

                msg[0] = username;
                msg[1] = password;
                cs.sendFlag("login");

                cs.sendMsg(msg); // 发送登录信息
                String str = cs.inceptMsg();

                if (str != null) {      //返回的str若为空，则表示服务器无响应
                    if (str.equals(username)) {
                        MainActivity.log = true;
                        MainActivity.client = cs;
                        MainActivity.USERNAME = username;
                        loginhandler.sendEmptyMessage(StaticVar.LOGIN_SUCCEED);
                    } else {
                        cs.StreamClose();
                        MainActivity.log = false;
                        loginhandler.sendEmptyMessage(StaticVar.LOGIN_FAILED);
                    }
                } else {
                    cs.StreamClose();
                    loginhandler.sendEmptyMessage(StaticVar.SERVER_NOT_RESPONDING);
                }
            } catch (Exception e) {
                // TODO: handle exception

            }
        }
    }


    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {

            case R.id.Usernamelogin:


//                if (userlogin.getText().toString().equals("登录")) {
                logOn();
//                } else {
//                    logOut();
//                }
                break;
            case R.id.register:
                Intent intent = new Intent();
                intent.setClass(Login_Activity.this, Register_Activity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case R.id.forget:
                Intent intent1 = new Intent();
                intent1.setClass(Login_Activity.this, ChangePassword_Activity.class);
                intent1.setAction("forgetpwd");
                startActivity(intent1);
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            case R.id.feedback:
                startActivity(new Intent(Login_Activity.this, FeedBack_Activity.class));
                overridePendingTransition(R.anim.in_from_right, R.anim.out_to_left);
                break;
            default:
                break;
        }
    }

    /**
     * 登录
     */
    private void logOn() {
        editor.putString("username", tel.getText().toString());
        if (TextUtils.isEmpty(tel.getText())
                || TextUtils.isEmpty(password.getText())) {
            showTextToast("用户名和密码不能为空！");
            return;
        } else {
            uname = tel.getText().toString();
            pword = password.getText().toString();

        }

        LoginThread loginthread = new LoginThread(uname, pword);
        loginthread.start();
        if(progressdialog!=null){

        progressdialog.setTitle("请稍等");
        progressdialog.setMessage("登录中...");
        progressdialog.show();
        }
        timer2 = new Timer();
        timer2.schedule(new TimerTask() {       //如果超过10秒无响应，则表示网络异常

            @Override
            public void run() {
                // TODO Auto-generated method stub
                if (progressdialog != null && progressdialog.isShowing())
                    loginhandler.sendEmptyMessage(StaticVar.NETWORK_FAULT);

            }
        }, 10000);
    }

    /**
     * 登出
     */
    private void logOut() {
        if (serverThread != null)
            serverThread.stopSocketServerThread();
        MainActivity.log = false;
//        register.setVisibility(View.VISIBLE);
//        forget.setVisibility(View.VISIBLE);
//        userlogin.setText("登录");
        MainActivity.USERNAME = null;
//        passwordlayout.setVisibility(View.VISIBLE);
//        tel.setEnabled(true);
//        user_remember.setEnabled(true);
//        user_autologin.setEnabled(true);

        try {
            MainActivity.client.sendFlag("offline");
            MainActivity.client.StreamClose();
            MainActivity.client = null;
        } catch (Exception e) {
            // TODO: handle exception
        }
    }


    public void initHandler() { // 登录信息处理
        loginhandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // TODO Auto-generated method stub
                switch (msg.what) {
//					case StaticVar.LOGIN_SUCCEED:
//						if(progressDialog.isShowing()){
//							progressDialog.setMessage("登录成功");
//							try {
//								Thread.sleep(2000);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							progressDialog.dismiss();
//						}
//						SocketServerThread serverThread = new SocketServerThread(MainActivity.client.Client_Socket);
//						serverThread.start();
//						break;
//					case StaticVar.LOGIN_FAILED:
//						if(progressDialog.isShowing()){
//							progressDialog.setMessage("登录失败");
//							try {
//								Thread.sleep(2000);
//							} catch (InterruptedException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//							progressDialog.dismiss();
//						}
//						if(mtimer!=null){
//							mtimer.cancel();
//							mtimer=null;
//						}
//						break;
                    case StaticVar.LOGIN_SUCCEED:
                        progressdialog.setMessage("登录成功！");
                        if (timer2 != null)
                            timer2.cancel();
                        // passwordlayout.setVisibility(View.GONE);
//                        if (atThisActivity) {
                        if (user_remember.isChecked()) {
                            editor.putBoolean("user_remember", true);
                            editor.putString("password", password.getText()
                                    .toString());
                        } else {
                            editor.putBoolean("user_remember", false);
                            editor.putString("password", null);
                        }
                        if (user_autologin.isChecked()) {
                            editor.putBoolean("user_autologin", true);
                        } else {
                            editor.putBoolean("user_autologin", false);
                        }
                        editor.commit();

//                        register.setVisibility(View.GONE);
//                        forget.setVisibility(View.GONE);
//                        userlogin.setText("注销");
//                        passwordlayout.setVisibility(View.GONE);
//                        tel.setEnabled(false);
//                        user_remember.setEnabled(false);
//                        user_autologin.setEnabled(false);
                        Intent intent = new Intent(Login_Activity.this, MainActivity.class);
                        intent.setAction("loginsucceed");
                        startActivity(intent);
                        overridePendingTransition(R.anim.in_from_right,
                                R.anim.out_to_left);
                        finish();
//                        }


//                        if (atThisActivity && MainActivity.log) {
//                            register.setVisibility(View.GONE);
//                            forget.setVisibility(View.GONE);
//                            userlogin.setText("注销");
//                            passwordlayout.setVisibility(View.GONE);
//                            tel.setEnabled(false);
//                            user_remember.setEnabled(false);
//                            user_autologin.setEnabled(false);
//                            Intent intent = new Intent(Login_Activity.this, MainActivity.class);
//                            intent.setAction("loginsucceed");
//                            startActivity(intent);
//                            overridePendingTransition(R.anim.in_from_right,
//                                    R.anim.out_to_left);
//                            finish();
//                        }
                        // progressdialog.dismiss();
//                        serverThread = SocketServerThread.getInstance(MainActivity.client.Client_Socket);
//                        serverThread.startSocketServerThread();
//                        MainActivity.client.sendFlag("querymymachine");
//                        String[] mg = new String[1];
//                        mg[0] = MainActivity.USERNAME;
//                        MainActivity.client.sendMsg(mg);
                        break;
                    case StaticVar.NETWORK_FAULT:

                        if (progressdialog.isShowing()) {
                            progressdialog.setMessage("网络故障！");
                            loginhandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                            if (progressdialog != null && progressdialog.isShowing())
                                                progressdialog.dismiss();
                                }
                            }, 2000);
                        }
                        break;
                    case 4:

                        if (progressdialog.isShowing()) {
                            progressdialog.setMessage("用户名和密码不能为空！");
                            loginhandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (progressdialog != null && progressdialog.isShowing())
                                            progressdialog.dismiss();
                                }
                            }, 2000);
                        }
                        break;
                    case StaticVar.LOGIN_FAILED:
                        if (progressdialog.isShowing()) {
                            progressdialog.setMessage("用户名或密码错误！");
//                            try {
//                                Thread.sleep(2000);
//                            } catch (InterruptedException e) {
//                                // TODO Auto-generated catch block
//                                e.printStackTrace();
//                            }
                            loginhandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (progressdialog != null && progressdialog.isShowing())
                                            progressdialog.dismiss();
                                }
                            }, 2000);
                        }
                        break;
                    case StaticVar.SERVER_NOT_RESPONDING:
                        if (progressdialog.isShowing()) {
                            progressdialog.setMessage("服务器无响应");
                            loginhandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if (progressdialog != null && progressdialog.isShowing())
                                            progressdialog.dismiss();
                                }
                            }, 2000);
                        }
                        break;

                    default:
                        break;
                }


            }
        };
    }

    public void showTextToast(String msg) {
        if (toast == null) {
            toast = Toast.makeText(getApplicationContext(), msg,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(msg);
        }
        toast.show();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub

//        if (userlogin.getText().equals("注销")) {
//            editor.putString("userlogin", "注销");
//        } else
//            editor.putString("userlogin", "登录");
//        editor.commit();

//        atThisActivity = false;
        super.onPause();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
//        atThisActivity = true;
        super.onResume();
    }

    @Override
    protected void onStop() {
//        atThisActivity = false;

        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (progressdialog != null) {
            progressdialog.dismiss();
            progressdialog = null;
        }

        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            finish();
            overridePendingTransition(R.anim.back_in_from_left,
                    R.anim.back_out_to_right);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}

package com.example.xm.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xm.activities.WebView_Activity;
import com.example.xm.activities.ChangePassword_Activity;
import com.example.xm.activities.FeedBack_Activity;
import com.example.xm.activities.Login_Activity;
import com.example.xm.activities.MainActivity;
import com.example.xm.activities.Repair_Activity;
import com.example.xm.bean.StaticVar;
import com.example.xm.bioreactormobn.R;
import com.example.xm.thread.SocketServerThread;
import com.example.xm.util.Util;
import com.example.xm.widget.CustomadeDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link UserFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private Button user;
    private Button changepwd;
    private Button repair;
    private Button feedback;
    private Button about;
    private TextView version;
    private Button poweroff;
    private Button update;
    private Intent intent;
    public static Handler handler;
    private ProgressDialog progressDialog;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserFragment newInstance(String param1, String param2) {
        UserFragment fragment = new UserFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        init(view);
        iniHandler();
        return view;
    }

    private void iniHandler() {
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what){
                    case StaticVar.UPDATE_DOWNLOAD_PERCENT:
                        int percent = (int)((float)msg.arg1/msg.arg2*100);
                        if(progressDialog==null){
                            progressDialog = new ProgressDialog(getContext());
                            progressDialog.setCanceledOnTouchOutside(false);
                            progressDialog.show();
                        }else {
                            progressDialog.show();
                        }
                        progressDialog.setMessage("已下载"+percent+"%");
                        break;
                    case  StaticVar.COMPLETE_DOWNLOAD:
                        final String filename = msg.obj.toString();
                        if(progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        CustomadeDialog.Builder builder = new CustomadeDialog.Builder(getContext());
                        builder.setTitle("提示").setMessage("现在就要安装吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
//							AutoInstall autoInstall = new AutoInstall();
//							String path = Environment.getExternalStorageDirectory().toString()  
//			                        + "/"+msg.obj.toString();
//							autoInstall.setUrl(path);
//							autoInstall.install(Set.this);
//			                Tools.removeStatusBarAndNavigationBar(Set.this);

                                installApk(filename);
                            }
                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // TODO Auto-generated method stub
                            }
                        }).create().show();
                        break;
                    case StaticVar.UPDATE:
                        builder = new CustomadeDialog.Builder(getContext());
                        try {
                            int localVersionCode = getVersionCode();
                            JSONObject jsonObject = new JSONObject(msg.obj.toString());
                            int remotVersionCode = jsonObject.getInt("versioncode");
                            if(localVersionCode<remotVersionCode){
                                builder.setTitle("发现新版本 "+jsonObject.getString("versionname")).setMessage(jsonObject.getString("info"))
                                        .setPositiveButton("下载", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // TODO Auto-generated method stub
                                                MainActivity.client.sendFlag("update");
                                                MainActivity.client.sendFlag("machine");
                                                MainActivity.client.sendFlag("download");
                                            }
                                        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO Auto-generated method stub
                                    }
                                }).create().show();
                            }else{
                                builder.setTitle("提示").setMessage("当前为最新版本，无需更新")
                                        .setNegativeButton("确定", new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                // TODO Auto-generated method stub
                                            }
                                        }).create().show();
                            }
                        } catch (JSONException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        break;
                    case StaticVar.LOGIN_SUCCEED:
                        user.setText(MainActivity.USERNAME);
                        break;
                }
            }
        };
    }

    /**
     * 安装APK
     * @param filename APK文件名
     */
    private void installApk(String filename) {
        String path= Environment.getExternalStorageDirectory()+"/"+filename;
        File apkfile = new File(path);
        if (!apkfile.exists())      //判断文件是否存在，若不存在直接返回
        {
            return;
        }

        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.setDataAndType(Uri.fromFile(apkfile), "application/vnd.android.package-archive");
        getContext().startActivity(i);
    }

    private void init(View view) {
        user = (Button) view.findViewById(R.id.user);
        if (MainActivity.client == null) {
            user.setText("离线");
        } else {
            user.setText(MainActivity.USERNAME);
        }
        changepwd = (Button) view.findViewById(R.id.changepwd);
        repair = (Button) view.findViewById(R.id.repair);
        feedback = (Button) view.findViewById(R.id.feedback);
        about = (Button) view.findViewById(R.id.about);
        update = (Button) view.findViewById(R.id.update);
        version = (TextView) view.findViewById(R.id.version);
        version.setText(getVersionName());
        poweroff = (Button) view.findViewById(R.id.poweroff);

        user.setOnClickListener(this);
        changepwd.setOnClickListener(this);
        repair.setOnClickListener(this);
        feedback.setOnClickListener(this);
        about.setOnClickListener(this);
        update.setOnClickListener(this);
        version.setOnClickListener(this);
        poweroff.setOnClickListener(this);
    }

    /**
     * 获取APK VersionName
     * @return
     */
    private String getVersionName() {
        String versionName = null;
        try {
            versionName = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * 获取APK VersionCode
     * @return
     */
    private int getVersionCode() {
        int versionCode = 0;
        try {
            versionCode = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.user:
                if(user.getText().toString().equals("离线")){
                    MainActivity.handler.sendEmptyMessage(StaticVar.LOGIN);
                }
                break;
            case R.id.changepwd:
                intent = new Intent(getActivity(), ChangePassword_Activity.class);
                intent.setAction("changepwd");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                break;
            case R.id.repair:
                intent = new Intent(getActivity(), Repair_Activity.class);
                intent.setAction("repair");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                break;
            case R.id.feedback:
                intent = new Intent(getActivity(), FeedBack_Activity.class);
                intent.setAction("feedback");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                break;
            case R.id.about:
                intent = new Intent(getActivity(), WebView_Activity.class);
                intent.putExtra("url", "http://www.xm-biotech.com");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                break;
            case R.id.version:
            case R.id.update:
                    try {
                        MainActivity.client.sendFlag("update");
                        MainActivity.client.sendFlag("mob");
                        MainActivity.client.sendFlag("checkversion");
                    } catch (Exception e) {
                        // TODO: handle exception
                        Toast.makeText(getContext(), "未登录到服务器", Toast.LENGTH_LONG).show();
                    }

                break;
            case R.id.poweroff:
                if (SocketServerThread.getSocketServerThread() != null)
                    SocketServerThread.getSocketServerThread().stopSocketServerThread();
                MainActivity.log = false;
                MainActivity.USERNAME = null;

                try {
                    MainActivity.client.sendFlag("offline");
                    MainActivity.client.StreamClose();
                    MainActivity.client = null;
                } catch (Exception e) {
                    // TODO: handle exception
                }

                intent = new Intent(getActivity(), Login_Activity.class);
                intent.setAction("poweroff");
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.in_from_right,
                        R.anim.out_to_left);
                getActivity().finish();
                MainActivity.mainActivity.finish();
                break;
        }
    }

//    private boolean checkVersion() {
//        try {
//            MainActivity.client.sendFlag("update");
//            MainActivity.client.sendFlag("mob");
//            MainActivity.client.sendFlag("checkversion");
//        } catch (Exception e) {
//            // TODO: handle exception
//            Toast.makeText(getContext(), "未登录到服务器", Toast.LENGTH_SHORT).show();
//        }
//        return getLatestVersionCode()> getVersionCode();
//    }
//
//    private int getLatestVersionCode() {
//        int versioncode=0;
//        try {
//            String info = Util.ReadFromFile(getContext(),"MobAPPVersionInfo.txt");
//            JSONObject jsonObject = new JSONObject(info);
//            versioncode=jsonObject.getInt("versioncode");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return versioncode;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

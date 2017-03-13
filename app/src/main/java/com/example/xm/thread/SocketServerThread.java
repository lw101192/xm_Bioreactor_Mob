package com.example.xm.thread;

import android.os.Environment;
import android.os.Message;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import com.example.xm.bean.StaticVar;
import com.example.xm.activities.BioreactorRT_Activity;
import com.example.xm.activities.FeedBack_Activity;
import com.example.xm.activities.MainActivity;
import com.example.xm.activities.Repair_Activity;
import com.example.xm.fragment.MachineFragment;
import com.example.xm.fragment.UserFragment;

import org.json.JSONObject;

public class SocketServerThread extends Thread {

    String message;
    Socket socket;
    ObjectInputStream in;
    ObjectOutputStream out;
    boolean flag = true;
    public static SocketServerThread socketThread = null;

    public SocketServerThread(Socket socket) {
        this.socket = socket;
        try {
            in = MainActivity.client.Client_in;
            out = MainActivity.client.Client_out;
        } catch (Exception e) {
            // TODO: handle exception
        }

    }

    public static SocketServerThread getInstance(Socket socket) {

        if (socketThread == null) {
            socketThread = new SocketServerThread(socket);
        }
        return socketThread;
    }

    public static SocketServerThread getSocketServerThread() {
        return socketThread;
    }

    public void stopSocketServerThread() {
        flag = false;
        if (socketThread != null)
            socketThread = null;
    }

    public void startSocketServerThread() {
        if (socketThread != null && !socketThread.isAlive()) {
            flag = true;
            socketThread.start();
        }
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub
        try {
            while (flag) {
                message = in.readObject().toString();
//                System.out.println("接收到消息>>" + message);
                String[] msg = message.split("::");

                switch (msg[0]) {
//                    case "Query":
//                        switch (msg[1]) {
//                            case "IsOnline":
//
//                                break;
//
//                            default:
//                                break;
//                        }
//                        break;
                    case " ":       //服务器ACK
//                        System.out.println(MainActivity.USERNAME + "清除MainActivity.checkcount");
                        MainActivity.checkcount = 0;
                        break;
                    case "目标用户不在线":
                        Message.obtain(BioreactorRT_Activity.handler, 2).sendToTarget();
                        break;
//                    case "CHECK":
//                        MainActivity.client.sendFlag("HI");
//                        break;
                    case "异地登陆":
                        if (MainActivity.log) {
                            MainActivity.handler.sendEmptyMessage(StaticVar.LADNDING_IN_DIFFERENT_PLACES);
                        }
                        break;
                    case "querymymachine":
                        if (msg.length >= 1)
                            MachineFragment.handler.obtainMessage(StaticVar.REFRESH_MACHINE_LIST, msg[1]).sendToTarget();
                        break;
                    case "AddMachine":
                        MachineFragment.handler.obtainMessage(StaticVar.ADD_MACHINE, msg[1]).sendToTarget();
                        break;
                    case "FeedBack":
                        FeedBack_Activity.handler.obtainMessage(0, msg[1]).sendToTarget();
                        break;
                    case "Repaire":
                        Repair_Activity.handler.obtainMessage(0, msg[1]).sendToTarget();
                        break;
                    case "RemoveMachineFromList":
                        MachineFragment.handler.obtainMessage(StaticVar.REMOVE_MACHINE_FROM_LIST, msg[1]).sendToTarget();
                        break;
                    case "checkversion":
                        Message.obtain(UserFragment.handler, StaticVar.UPDATE, in.readObject().toString()).sendToTarget();
                        break;
                    case "queryconfig":
                        switch (in.readObject().toString()){
                            case "push":
                                Message.obtain(BioreactorRT_Activity.handler,StaticVar.QUERY_CONFIG,in.readObject().toString()).sendToTarget();
                                break;
                        }
                        break;
                    case "config":
                        Message.obtain(BioreactorRT_Activity.handler,StaticVar.QUERY_CONFIG_RESULT,in.readObject().toString()).sendToTarget();
                        break;
                    case "download":


//					try {
//						new YcApi().SendCmd("cp /data/pointercal/a1 /data/pointercal/2.txt");
//						new YcApi().SendCmd("chmod 777  /data/pointercal/2.txt");
//						File awakeTimeFile;
//				        FileWriter fr;
//						awakeTimeFile = new File("/data/pointercal/2.txt");
//						fr = new FileWriter(awakeTimeFile);
//			            fr.write("asdsasdasd");
//			            fr.write("\n");
//					} catch (Exception e) {
//						// TODO: handle exception
//						System.out.println("错误捕捉");
//						System.out.println(e);
//					}


                        DataInputStream inputStream = new DataInputStream(new BufferedInputStream(
                                MainActivity.client.Client_Socket.getInputStream()));
                        String filename = inputStream.readUTF();
                        File filePath = Environment.getExternalStorageDirectory();
//                        System.out.println("读" + new File("/data/pointercal/").canRead());
//                        System.out.println("写" + new File("/data/pointercal/").canWrite());

                        String savePath = filePath + "/" + filename;
                        int bufferSize = 8192;
                        byte[] buf = new byte[bufferSize];
                        int passedlen = 0;
                        long len = 0;

                        len = inputStream.readLong();
//                        System.out.println(savePath);
//                        System.out.println("文件的长度为:" + len + "\n");
//                        System.out.println("开始接收文件!" + "\n");

                        File file = new File(savePath);
//                        System.out.println("File" + (file == null));
//                        System.out.println("1");
                        FileOutputStream fos = null;
                        try {
                            fos = new FileOutputStream(file);
                        } catch (Exception e) {
                            // TODO: handle exception
//                            System.out.println(e);
                        }
//                        System.out.println("FileOutputStream" + (fos == null));
//                        System.out.println("2");
//	                while ((length = inputStream.read(buf, 0, buf.length)) > 0) {
//	                	passedlen += length;
//	                	System.out.println("长度:"+length+"文件接收了" +passedlen+"  "+ (passedlen * 100 / len) + "%\n");
//	                	Message.obtain(Set.handler, 1, passedlen, (int)len).sendToTarget();
//	                	fos.write(buf, 0, length);
//	                	fos.flush();
//	                	if((passedlen * 100 / len)==100)
//	                		break;
//	                }
                        while (true) {
                            int read = 0;
                            if (inputStream != null) {
                                read = inputStream.read(buf, 0, buf.length);
                            }
                            passedlen += read;

                            // 下面进度条本为图形界面的prograssBar做的，这里如果是打文件，可能会重复打印出一些相同的百分比
                            Message.obtain(UserFragment.handler, StaticVar.UPDATE_DOWNLOAD_PERCENT, passedlen, (int) len).sendToTarget();
//                            System.out.println("长度:" + read + "文件接收了" + passedlen + "  " + (passedlen * 100 / len) + "%\n");
                            fos.write(buf, 0, read);
                            if (passedlen >= len) {
                                Message.obtain(UserFragment.handler, StaticVar.COMPLETE_DOWNLOAD, filename).sendToTarget();
                                break;
                            }
                        }
                        //System.out.println("接收完成，文件存为" + savePath + "\n");
                        fos.close();
                        break;
                    case "SendCmd":
                        Object[] objmsg = new Object[3];
                        objmsg[0] = in.readObject();//FromID
                        objmsg[1] = in.readObject();//ToID
                        objmsg[2] = in.readObject();//Content
//                        System.out.println(objmsg[1]);
//                        JSONObject jsonObject = new JSONObject();
//                        jsonObject.put("ID",objmsg[1]);
//                        jsonObject.put("Content",objmsg[2]);
                        if (objmsg[2] instanceof String) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("ID", objmsg[0]);
                            jsonObject.put("Content", objmsg[2]);
                            Message.obtain(MainActivity.handler, StaticVar.SEND_MESSAGE, jsonObject).sendToTarget();
                        } else if (objmsg[2] instanceof com.xm.Dao.OperationDao) {
                            if (BioreactorRT_Activity.handler != null) {
                                Message.obtain(BioreactorRT_Activity.handler, 1, objmsg[2]).sendToTarget();
                            }
                        }
//                        if(BioreactorRT_Activity.handler!=null){
//                            if(objmsg[2] instanceof com.xm.Dao.OperationDao){
//                            Message.obtain(BioreactorRT_Activity.handler, 1, objmsg[2]).sendToTarget();
//                            }else{
//                                Message.obtain(MainActivity.handler, 1, jsonObject).sendToTarget();
//                            }
//                        }else{
//                            if(objmsg[2] instanceof com.xm.Dao.OperationDao){
//                            Message.obtain(MainActivity.handler, 1, jsonObject).sendToTarget();
//                        }else{
//
//                    }
//                        }
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
        }

    }
}

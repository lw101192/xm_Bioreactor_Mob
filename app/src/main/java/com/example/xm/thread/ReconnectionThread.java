package com.example.xm.thread;

import com.example.xm.activities.MainActivity;
import com.example.xm.bean.StaticVar;

/**
 * Created by liuwei on 2016/8/10.
 */
public class ReconnectionThread extends Thread {

    static ReconnectionThread reconnectionThread;
    private int waiting = 0;
    private static boolean flag = false;

    public static ReconnectionThread getInstance() {
        if (reconnectionThread == null) {
            reconnectionThread = new ReconnectionThread();
        }
        return reconnectionThread;
    }

    public static ReconnectionThread getReconnectionThread() {
        return reconnectionThread;
    }

    @Override
    public void run() {
        try {
            while (flag) {
                Thread.sleep((long) waiting() * 1000L);
//                xmppManager.connect();
                if (flag)
                    MainActivity.handler.sendEmptyMessage(StaticVar.RELOGIN);
                waiting++;
            }
        } catch (final InterruptedException e) {
//            xmppManager.getHandler().post(new Runnable() {
//                public void run() {
//                    xmppManager.getConnectionListener().reconnectionFailed(e);
//                }
//            });
        }
    }


    public static void startReconnectionThread() {
        synchronized (reconnectionThread) {
            if (reconnectionThread != null && !reconnectionThread.isAlive()) {
                flag = true;
                reconnectionThread.start();
            }
        }
    }

    public static void stopReconnectionThread() {
        flag = false;
        reconnectionThread = null;
    }

    private int waiting() {
        if (waiting > 20) {
            return 600;
        }
        if (waiting > 13) {
            return 300;
        }
        return waiting <= 7 ? 10 : 60;
    }
}

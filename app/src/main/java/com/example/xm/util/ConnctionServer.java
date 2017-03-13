package com.example.xm.util;

import com.example.xm.activities.MainActivity;
import com.example.xm.bean.StaticVar;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

public class ConnctionServer {

    /**
     * @param args
     */
    public Socket Client_Socket = null;
    public ObjectInputStream Client_in;
    public ObjectOutputStream Client_out;
    String[][] friend;

    public ConnctionServer() {
        InetAddress address;
        try {
            address = InetAddress.getByName("1512i317k4.iask.in");
            connction(address, 14457);
        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
        }

    }

    //建立服务器连接
    public void connction(InetAddress address, int port) {
        try {
            this.Client_Socket = new Socket("115.159.185.15",9999);
            this.Client_out = new ObjectOutputStream(Client_Socket.getOutputStream());
            this.Client_in = new ObjectInputStream(Client_Socket.getInputStream());
        } catch (UnknownHostException e) {
        } catch (IOException e) {

        }
    }


    //发送功能请求如注册，登陆等，发生一个字符串标志
    public void sendFlag(String stringFlag) {


        try {
            this.Client_out.writeObject(stringFlag);
        } catch (Exception e) {
            e.printStackTrace();
//			try{
//				MainActivity.handler.sendEmptyMessage(StaticVar.SEND_MESSAGE_FAIILED);
//			}catch (Exception e1){
//
//			}
        }

    }

    //发送注册信息
    public void sendMsg(String msg[]) {
        for (int i = 0; i < (int) msg.length; i++) {
            try {
                this.Client_out.writeObject(msg[i]);
            } catch (Exception e) {
                e.printStackTrace();
//				try{
//					MainActivity.handler.sendEmptyMessage(StaticVar.SEND_MESSAGE_FAIILED);
//				}catch (Exception e1){
//
//				}
            }
        }

    }

    //发送一个字符串
    public void sendString(String msg) {

        try {
            this.Client_out.writeObject(msg);
        } catch (Exception e) {
            e.printStackTrace();
//			try{
//				MainActivity.handler.sendEmptyMessage(StaticVar.SEND_MESSAGE_FAIILED);
//			}catch (Exception e1){
//
//			}
        }

    }


    //接收服务器传来的一个字符串
    public String inceptMsg() {
        String str = null;
        try {
            str = Client_in.readObject().toString();                //从服务器读取消息
        } catch (Exception e1) {
//			 try{
//				 MainActivity.handler.sendEmptyMessage(StaticVar.SEND_MESSAGE_FAIILED);
//			 }catch (Exception e){
//
//			 }

        }
       /* try {
			} catch (IOException e) {
			e.printStackTrace();
		}*/
        return str;
    }

    //接收一个整形的变量
    public int acceptInt() {
        int count = 0;
        try {
            count = Client_in.readInt();                //从服务器读取消息
        } catch (IOException e1) {
        }
        return count;
    }
	
	/*//接收服务器传来的好友列表函数
	public String[][] acceptFriendList(int rows,int cols)
	{
		String friendList[][]=new String[rows][cols];
		try {
			Client_in=new ObjectInputStream(Client_Socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		try{
			for(int rr=0;rr<rows;rr++){
				for(int cc=0;cc<cols;cc++){
					friendList[rr][cc]=Client_in.readObject();			//从服务器读取消息
				}
			}
	    }catch(IOException e1){
	    	e1.printStackTrace();
	    } 
		return friendList;
	}*/
	
	
	/*
	public String[][] inceptMsg_showFriend()
	{
		String str=null;
		try {
			Client_in=new DataInputStream(Client_Socket.getInputStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
		 try{
			 int len=Client_in.readInt();
			 friend=new String[len][4];
			 for(int j=0;j<=len;j++)
			 {
				 str=Client_in.readUTF();				//从服务器读取消息
				 StringTokenizer st=new StringTokenizer(str,",");
				 while(st.hasMoreTokens())
				 {		
					 for(int i=0;i<=st.countTokens();i++)
					 {
						 friend[j][i]=st.nextToken();
					 }
				 }
			 }
	        
	    }catch(IOException e1){
	    	e1.printStackTrace();
	    }	    
	    try {
			} catch (IOException e) {
			e.printStackTrace();
		}
		return friend;
	}
	*/

    //输入输出流的关闭
    public void StreamClose() {
        try {
            if (this.Client_out != null) {
                this.Client_out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (this.Client_in != null) {
                this.Client_in.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            if (Client_Socket != null) {
                Client_Socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


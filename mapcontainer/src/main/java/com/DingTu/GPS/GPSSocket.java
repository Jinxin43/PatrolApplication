package com.DingTu.GPS;

import com.DingTu.Base.PubVar;
import com.DingTu.Base.Tools;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Dingtu2 on 2017/6/1.
 */

public class GPSSocket {

    public String m_StatusMessage = "";
    public boolean m_Starting = false;
    public int m_AddCount = 0;
    private Timer m_Timer = null;
    public void Start(String ServerAddress,String Port,String DeviceID,String RefreshSecond)
    {
        this.m_ServerAddress = ServerAddress;
        this.m_ServerPort = Integer.valueOf(Port);
        this.m_Second = Integer.valueOf(RefreshSecond);
        this.m_DeviceID = DeviceID;
        if (this.m_Timer==null) this.m_Timer = new Timer();
        this.m_Timer.schedule(new TimerTask()
        {
            public void run()
            {
                if (m_AddCount>1000)m_AddCount=0;m_AddCount++;
                String GpsData = GetSendGpsData();
                if (GpsData!="")SendData(GpsData);
            }
        }, 100, this.m_Second*1000);
        this.m_Starting = true;
    }

    public void Stop()
    {
        this.m_Timer.cancel();this.m_Timer=null;
        this.m_Starting = false;
        this.m_StatusMessage = "无连接！";
    }

    //检查Socket状态，是否已经连接并可用
    private Socket m_Socket = null;    		//主通信SOCKET
    private String m_ServerAddress = "";    //服务器地址
    private int m_ServerPort = -1;			//服务器端口
    private int m_Second = 1;				//数据更新时间（秒）
    private String m_DeviceID = "";         //设备IP
    private void SendData(String GpsData)
    {
        try
        {
            if (!this.CheckSocketStatus()) return;
            this.m_StatusMessage = "正在发送数据...";
            PrintWriter out = new PrintWriter( new BufferedWriter(new OutputStreamWriter(this.m_Socket.getOutputStream())),true);
            out.println(GpsData);
        } catch (Exception e) {
            this.m_StatusMessage = "错误："+e.getMessage();
            e.printStackTrace();
        }
    }

    //获取GPS数据，组合成发送报文
    private String GetSendGpsData()
    {
        if (!Tools.ReadyGPS(false)) return "";
        String m_CoorString = PubVar.m_GPSLocate.getJWGPSCoordinate();  //JD,WD
        String m_DateString = PubVar.m_GPSLocate.getGPSDate();  //年月日  时分称
        String m_Speed = PubVar.m_GPSLocate.getGPSSpeed();  //速度

//		String m_CoorString = "126.553434,46.342323";  //JD,WD
//		String m_DateString = "2012-2-23 11:33:44"; //年月日  时分称
//		String m_Speed = "10";  //速度

        //格式：#GPSVIDEO#IP,经度,纬度，速度，日期，时间
        String DataStr = "#GPSVIDEO#"+this.m_DeviceID+","+m_CoorString+","+m_Speed+","+m_DateString.split(" ")[0]+","+m_DateString.split(" ")[1];
        return DataStr;
    }

    //检查SOCKET状态
    private boolean CheckSocketStatus()
    {
        if (this.m_Socket==null)
        {
            this.m_Socket = new Socket();
            return this.ConnectSocket(this.m_Socket);
        }

        try
        {
            this.m_Socket.sendUrgentData(0xFF);
        } catch (IOException e)
        {
            try
            {
                this.m_Socket.close();
                this.m_Socket=null;
                return false;
            } catch (IOException e1)
            {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }
        }
        if (this.m_Socket.isConnected()) return true;
        else return this.ConnectSocket(this.m_Socket);
    }

    private boolean ConnectSocket(Socket pSocket)
    {
        try
        {
            this.m_StatusMessage = "正在连接 ["+this.m_ServerAddress+","+this.m_ServerPort+"]...";
            SocketAddress pSA = new InetSocketAddress(InetAddress.getByName(this.m_ServerAddress),this.m_ServerPort);
            pSocket.connect(pSA, 5*1000);
            return true;
        } catch (IOException e) {
            this.m_StatusMessage = "错误："+e.getMessage();
            e.printStackTrace();
        } catch(IllegalArgumentException e)
        {
            this.m_StatusMessage = "错误："+e.getMessage();
            e.printStackTrace();
        }catch (Exception e) {
            // TODO Auto-generated catch block
            this.m_StatusMessage = "错误："+e.getMessage();
            e.printStackTrace();
        }
        return false;
    }
}

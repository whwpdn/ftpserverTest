package com.example.jewoo.ftpservertest;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.Inet4Address;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by jewoo on 2017. 7. 18..
 */

public class FTPServer {

    private final static int SOCKET_NUM = 5217;
    public static void main(String args[]) throws Exception
    {
        ServerSocket socket=new ServerSocket(SOCKET_NUM);
        System.out.println("FTP Server Started on Port Number "+SOCKET_NUM);
        //Log.i("log ftp","FTP Server Started on Port Number ");

        while(true)
        {
            System.out.println(Inet4Address.getLocalHost().getHostAddress());
            System.out.println("Waiting for Connection ...");
            Transferfile t=new Transferfile(socket.accept());

        }
    }
}

class Transferfile extends Thread
{
    Socket ClientSoc;

    DataInputStream din;
    DataOutputStream dout;

    Transferfile(Socket soc)
    {
        try
        {
            ClientSoc=soc;
            din=new DataInputStream(ClientSoc.getInputStream());
            dout=new DataOutputStream(ClientSoc.getOutputStream());
            System.out.println("FTP Client Connected ...");
            start();

        }
        catch(Exception ex)
        {
        }
    }
    void SendFile() throws Exception
    {
        String filename=din.readUTF();
        File f=new File(filename);
        if(!f.exists())
        {
            dout.writeUTF("File Not Found");
            return;
        }
        else
        {
            dout.writeUTF("READY");
            FileInputStream fin=new FileInputStream(f);
            int ch;
            do
            {
                ch=fin.read();
                dout.writeUTF(String.valueOf(ch));
            }
            while(ch!=-1);
            fin.close();
            dout.writeUTF("File Receive Successfully");
        }
    }

    void ReceiveFile() throws Exception
    {
        String filename=din.readUTF();
        if(filename.compareTo("File not found")==0)
        {
            return;
        }
        File f=new File(filename);
        String option;

        if(f.exists())
        {
            dout.writeUTF("File Already Exists");
            option=din.readUTF();
        }
        else
        {
            dout.writeUTF("SendFile");
            option="Y";
        }

        if(option.compareTo("Y")==0)
        {
            FileOutputStream fout=new FileOutputStream(f);
            int ch;
            String temp;
            do
            {
                temp=din.readUTF();
                ch=Integer.parseInt(temp);
                if(ch!=-1)
                {
                    fout.write(ch);
                }
            }while(ch!=-1);
            fout.close();
            dout.writeUTF("File Send Successfully");
        }
        else
        {
            return;
        }

    }


    public void run()
    {
        while(true)
        {
            try
            {
                System.out.println("Waiting for Command ...");
                String Command=din.readUTF();
                if(Command.compareTo("GET")==0)
                {
                    System.out.println("\tGET Command Received ...");
                    SendFile();
                    continue;
                }
                else if(Command.compareTo("SEND")==0)
                {
                    System.out.println("\tSEND Command Receiced ...");
                    ReceiveFile();
                    continue;
                }
                else if(Command.compareTo("DISCONNECT")==0)
                {
                    System.out.println("\tDisconnect Command Received ...");
                    System.exit(1);
                }
            }
            catch(Exception ex)
            {
            }
        }
    }
}
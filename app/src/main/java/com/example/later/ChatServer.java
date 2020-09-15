package com.example.later;
import java.io.*;
import java.util.*;
import java.net.*;
import com.example.later.MyMessage;

public class ChatServer
{
    static Hashtable<String,ClientHandler> ar = new Hashtable<>();

    public static void main(String[] args) throws IOException
    {
        ServerSocket ss = new ServerSocket(1234);
        System.out.println("Started");
        Socket s;
        while(true)
        {
            s = ss.accept();
            ObjectInputStream dis = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream dos = new ObjectOutputStream(s.getOutputStream());
            ClientHandler mtch = new ClientHandler(s, dis, dos);
            Thread t = new Thread(mtch);
            t.start();
        }
    }
}



// ClientHandler class
class ClientHandler implements Runnable
{
    Scanner scn = new Scanner(System.in);
    private final ObjectInputStream dis;
    final ObjectOutputStream dos;
    Socket s;
    static Vector<MyMessage> msgList = new Vector<MyMessage>();

    public ClientHandler(Socket s, ObjectInputStream dis, ObjectOutputStream dos) {
        this.dis = dis;
        this.dos = dos;
        this.s = s;
    }

    @Override
    public void run() {

        MyMessage received;
        String uid=null;
        try
        {
            received = (MyMessage)dis.readObject();
            if(received.type == MyMessage.Type.UID) {
                ChatServer.ar.put(received.msg,this);
                uid = received.msg;
                System.out.println(uid + " connected");
            }
            else
                throw new Exception("no uid");
        } catch(Exception e) {
            System.out.println(e);
        }

        for (MyMessage msg :msgList ) {
            if(msg.to.equals(uid)) {
                try {
                    dos.writeObject(msg);
                }catch(Exception e) {
                    System.out.println(e);
                }
            }
        }
        for (int i=msgList.size()-1;i>=0 ;i-- ) {
            if(msgList.get(i).to.equals(uid)) {
                msgList.remove(i);
            }
        }

        received = new MyMessage();
        try
        {
            boolean running = true;
            while (running)
            {

                // receive the string
                received = (MyMessage)dis.readObject();
                System.out.println("Recieved:" + received.from +received.msg+received.to);

                if(received.type ==  MyMessage.Type.EXIT) {
                    dos.writeObject(received);
                    this.s.close();
                    ChatServer.ar.remove(uid);
                    running = false;
                }
                else if(received.type ==  MyMessage.Type.CHAT) {
                    ClientHandler mc = ChatServer.ar.get(received.to);
                    if(mc==null) {
                        mc = ChatServer.ar.get("+91"+received.to);
                    }
                    if(mc != null) {
                        mc.dos.writeObject(received);
                        System.out.println("send:" + received.to);
                    }
                    else {
                        //store in locally
                        msgList.add(received);
                    }
                }

            }
        } catch (Exception e) {
            ClientHandler mc = ChatServer.ar.get(received.to);
            if(mc != null) {
                ChatServer.ar.remove(uid);
            }
            e.printStackTrace();
        }
        try
        {
            // closing resources
            this.dis.close();
            this.dos.close();

        }catch(IOException e){
            e.printStackTrace();
        }
    }
}

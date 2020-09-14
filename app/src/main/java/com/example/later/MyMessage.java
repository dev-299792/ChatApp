package com.example.later;


import java.io.Serializable;

public class MyMessage implements Serializable {
    public int id=0;
    boolean sender=false;
    public String msg="";
    boolean isSender()
    {
        return sender;
    }
    public enum Type {
        CHAT,
        RECIEVED_SERVER,
        RECIEVED_USER,
        UID,
        EXIT
    }

    public String from,to,other;
    public Type type;

}

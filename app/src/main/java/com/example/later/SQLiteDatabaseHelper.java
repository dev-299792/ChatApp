package com.example.later;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {

    private static final String db_name = "userchats";
    private static final  int db_version = 1;

    SQLiteDatabaseHelper(Context context) {
        super(context,db_name,null,db_version);
    }

    public static void addMessage(SQLiteDatabase db,MyMessage msg) {

    }

    public static ArrayList<MyMessage> getMessages(SQLiteDatabase database,String uid) {
        ArrayList<MyMessage> list = new ArrayList<>();
        return  list;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE CHATS ("
                +"_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                +"message TEXT ,"
                +"user_id TEXT NOT NULL,"
                +"is_sender INT ,"
                +"is_read INT,"
                +"CHECK( is_read IN (0,1) ),"
                +"CHECK( is_sender IN (0,1) ));");

        db.execSQL("CREATE TABLE USERS (_id INTEGER PRIMARY KEY AUTOINCREMENT,user_id text UNIQUE NOT NULL, username text NOT NULL)");



//        ContentValues values = new ContentValues();
//        values.put("message", "hi there");
//        values.put("is_sender", 0);
//        values.put("user_id", "BHNIYyVPNVSF7jsWVwdPbDN9m3r1");
//        db.insert("CHATS", null, values);
//        values = new ContentValues();
//        values.put("message", "hello");
//        values.put("is_sender", 1);
//        values.put("user_id", "BHNIYyVPNVSF7jsWVwdPbDN9m3r1");
//        db.insert("CHATS", null, values);
//        values = new ContentValues();
//        values.put("user_id", "BHNIYyVPNVSF7jsWVwdPbDN9m3r1");
//        values.put("username", "dev");
//        db.insert("USERS", null, values);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

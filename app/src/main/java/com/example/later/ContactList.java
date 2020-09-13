package com.example.later;


import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.HashSet;

public class ContactList {

    private ContentResolver cr;

    public ContactList(Context context) {
        this.cr = context.getContentResolver();
    }

    public static class Contact {

        public String name, phone;

        @Override
        public String toString() {
            return "Contact{" +
                    "name='" + name + '\'' +
                    ", phone='" + phone + '\'' +
                    '}';
        }

        public Contact(String name, String phone) {
            this.name = name;
            this.phone = phone;
        }
    }

    public  ArrayList<Contact> getNumbers()
    {
        Cursor phones = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,null,null, null);

        HashSet<String> nums = new HashSet<>();
        ArrayList<Contact> ContactList = new ArrayList<>();

        if (phones != null) {
            while (phones.moveToNext())
            {
                String name=phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                phoneNumber = phoneNumber.replaceAll("\\s", "");
                if(!nums.contains(phoneNumber)) {
                    ContactList.add(new Contact(name,phoneNumber));
                    nums.add(phoneNumber);
                }
            }
            phones.close();
        }
        return  ContactList;
    }
}
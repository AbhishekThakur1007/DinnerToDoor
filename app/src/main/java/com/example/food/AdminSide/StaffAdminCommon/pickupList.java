package com.example.food.AdminSide.StaffAdminCommon;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class pickupList extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_TABLE = "PickUpDetails.db";
    private static final String UserDetails = "UserDetails";
    public static final String ORDERID = "OrderID";
    public static final String PHONE = "Phone";
    public static final String NAME = "Name";
    public static final String ADDRESS = "Address";

    public pickupList (Context context){
        super(context,DB_TABLE,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String UserData = "CREATE TABLE " + UserDetails + "(" + ORDERID +  " TEXT, " + NAME + " TEXT, " + ADDRESS + " TEXT, " + PHONE + " TEXT )";
        db.execSQL(UserData);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public int getCount(){
        SQLiteDatabase db = this.getWritableDatabase();
        int count = 0;
        String Query = " SELECT * FROM " + UserDetails;
        Cursor cursor = db.rawQuery(Query,null);
        cursor.moveToFirst();
        if(cursor.moveToFirst()){
            count = cursor.getCount();
        }
        return count;
    }

    public int Check(String ID){
        SQLiteDatabase db = this.getWritableDatabase();
        int count;
        String Query = " SELECT " + ORDERID + " FROM " + UserDetails + " WHERE " + ORDERID + " =? " ;
        Cursor cursor = db.rawQuery(Query,new String[]{ID});
        count = cursor.getCount();
        cursor.close();
        db.close();
        return count;
    }

    public long putDetails(UserName_Address userName_address){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME,userName_address.getName());
        contentValues.put(ORDERID,userName_address.getOrderId());
        contentValues.put(PHONE,userName_address.getAdminPhone());
        contentValues.put(ADDRESS,userName_address.getAddress());
        long i = db.insert(UserDetails,null,contentValues);
        db.close();
        return i;
    }

    public List<UserName_Address> GetDetails(){
        SQLiteDatabase db = this.getReadableDatabase();
        String UName,UAddress,UID,UPhone;
        List<UserName_Address> userName_addresses = new ArrayList<>();
        String Query = " SELECT * FROM " + UserDetails;
        Cursor cursor = db.rawQuery(Query,null);
        cursor.moveToFirst();
        if(cursor.moveToFirst()){
            do{
                UID = cursor.getString(0);
                UName = cursor.getString(1);
                UAddress = cursor.getString(2);
                UPhone = cursor.getString(3);
                UserName_Address  userName_addresses1 = new UserName_Address(UID,UName,UPhone,UAddress);
                userName_addresses.add(userName_addresses1);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userName_addresses;
    }

    public long Delete(String phone){
        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause = PHONE + " =? " ;
        String[] whereArgs = {phone};
        long i = db.delete(UserDetails,whereClause,whereArgs);
        db.close();
        return i;
    }

}

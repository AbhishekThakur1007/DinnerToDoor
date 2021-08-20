package com.example.food.AdminSide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.food.AdminSide.AddNewFood.SizeAvailable;
import com.example.food.AdminSide.EmployeesDetails.Employees;

import java.util.ArrayList;
import java.util.List;

public class SQlDataAdminInformation extends SQLiteOpenHelper {

    //Database Name
    private static final String DB_NAME = "Upload.db";
    private static final int DV_VER = 1;

    //Spinner Inflate
    private static final String DB_CATEGORY = "CATEGORY";
    public static final String S_SIZE_CATEGORY = "S_Category";
    private static final String DB_QUANTITY = "Quantity";
    public static final String S_QUANTITY = "S_QUANTITY";

    // Quantity Table
    private static final String DB_QuantityDetails = "QuantityDetails";
    public static final String SizeCategory = "SizeCategory";
    public static final String QUANTITY = "Quantity";
    public static final String Price = "Price";
    public static final String Discount = "Discount";

    // Employees
    private static final String DB_EMPLOYEES = "EMPLOYEES";
    public static final String NAME = "Name";
    public static final String CONTACT = "Contact";
    public static final String AGE = "AGE";
    public static final String ADDRESS = "Address";


    public SQlDataAdminInformation(Context context) {
        super(context, DB_NAME, null, DV_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String Table1 = " CREATE TABLE " + DB_CATEGORY + "(" + S_SIZE_CATEGORY + " TEXT )" ;
        String Table2 = " CREATE TABLE " + DB_QUANTITY + "(" + S_QUANTITY + " TEXT )" ;
        String Table3 = " CREATE TABLE " + DB_QuantityDetails + "(" + SizeCategory + " TEXT, " + QUANTITY + " TEXT, " + Price + " TEXT, "  + Discount + " TEXT ) ";
        String Table4 = " CREATE TABLE " + DB_EMPLOYEES + "(" + NAME + " TEXT, " + CONTACT + " TEXT, " + AGE + " TEXT, " + ADDRESS + " TEXT ) ";

        db.execSQL(Table1);
        db.execSQL(Table2);
        db.execSQL(Table3);
        db.execSQL(Table4);
    }

    // Spinner

    //inflate
    public ArrayList<String> inflateSpinner(int check){
        SQLiteDatabase database = getReadableDatabase();
        ArrayList<String> list = new ArrayList<>();
        String ColumnName = null;
        String Table = null;
        if(check == 1){
            ColumnName = S_SIZE_CATEGORY;
            Table = DB_CATEGORY;
        }else if(check == 2){
            ColumnName = S_QUANTITY;
            Table = DB_QUANTITY;
        }

        String query = " SELECT " + ColumnName + " FROM " + Table;
        Cursor cursor;
        cursor = database.rawQuery(query,null);
        cursor.moveToFirst();
        if(cursor.getCount() != 0){
            if(cursor.moveToFirst()){
                do{
                    String Name = cursor.getString(0);
                    list.add(Name);
                }while (cursor.moveToNext());
            }
        }
        cursor.close();
        database.close();
        return list;
    }

    // Add new Category
    public long AddNewSpinnerInCategory(String value){

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(S_SIZE_CATEGORY,value);
        long inn = database.insert(DB_CATEGORY,null,contentValues);
        database.close();
        return inn;
    }

    //Add new Quantity
    public long AddNewSpinnerInQuantity(String value){

        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(S_QUANTITY,value);
        long inn = database.insert(DB_QUANTITY,null,contentValues);
        database.close();
        return inn;
    }

    // Edit Name
    public List<String> EditInSpinner(int check,String edit,String where){
        SQLiteDatabase database = this.getWritableDatabase();
        List<String> list = new ArrayList<>();
        String ColumnName = null;
        if(check == 1){
            ColumnName = S_SIZE_CATEGORY;
        }else if (check == 2) {
            ColumnName = S_QUANTITY;
        }
        String query = " SELECT " + ColumnName + " FROM " + DB_QUANTITY + " WHERE " + ColumnName + " =? " ;
        Cursor cursor;
        cursor = database.rawQuery(query,new String[]{ where });
        cursor.moveToFirst();
        if(cursor.getCount() >0 ){
            ContentValues contentValues = new ContentValues();
            contentValues.put(ColumnName,edit);
            String whereClause = ColumnName + " =? ";
            String[] whereArgs = { where };
            database.update(DB_QUANTITY,contentValues,whereClause,whereArgs);
        }
        cursor.close();
        database.close();
        return list;
    }

    //Delete From Spinner
    public long DeleteFromSpinner(int check,String delete){

        SQLiteDatabase database = this.getWritableDatabase();
        String ColumnName = null;
        String Table = null;
        long cursor = 0;
        if(check == 1){
            ColumnName = S_SIZE_CATEGORY;
            Table = DB_CATEGORY;
        }else if(check == 2){
            ColumnName = S_QUANTITY;
            Table = DB_QUANTITY;
        }
        String whereClaus = ColumnName + " =? ";
        String[] whereArgs = {delete};
        cursor = database.delete(Table,whereClaus,whereArgs);
        database.close();
        return cursor;
    }

    // Return Options and display on recycler view
    public ArrayList<SizeAvailable> SizeDetails(){
        ArrayList<SizeAvailable> sizeAvailable = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT * FROM " + DB_QuantityDetails;
        Cursor cursor;
        cursor = database.rawQuery(query,null);
        cursor.moveToFirst();
        if(cursor.moveToFirst()){
            do{
                String SizeCategory = cursor.getString(0);
                String Quantity = cursor.getString(1);
                String Price = cursor.getString(2);
                String Discount = cursor.getString(3);
                SizeAvailable sizeAvailable1 = new SizeAvailable(SizeCategory,Quantity,Price,Discount);
                sizeAvailable.add(sizeAvailable1);
            }while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return sizeAvailable;
    }

    // Insert In Table
    public long SizeInsert(SizeAvailable sizeAvailable){
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(QUANTITY,sizeAvailable.getQuantity());
        contentValues.put(SizeCategory,sizeAvailable.getSizeCategory());
        contentValues.put(Price,sizeAvailable.getPrice());
        contentValues.put(Discount,sizeAvailable.getDiscount());

        long Check3 = database.insert(DB_QuantityDetails,null,contentValues);
        database.close();
        return Check3;
    }

    //Check
    public int CheckIfAvail(SizeAvailable sizeAvailable){
        SQLiteDatabase database = this.getWritableDatabase();
        int i = 0;
        String query = "SELECT  " + Price + " , " + QUANTITY + " , " + SizeCategory + " FROM " + DB_QuantityDetails + " WHERE " +  Price + " =? " + " AND " +  QUANTITY + " =? "  + " AND " +  SizeCategory + " =? ";
        Cursor cursor = database.rawQuery(query,new String[]{sizeAvailable.getPrice(),sizeAvailable.getQuantity(),sizeAvailable.getSizeCategory()});
        if(cursor.getCount() != 0){
            i = 1;
        }
        cursor.close();
        database.close();
        return i;
    }

    public int GetSizeCount(){
       int u = 0;
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT * FROM " + DB_QuantityDetails;
        Cursor cursor = null;
        cursor = database.rawQuery(query,null);
        u = cursor.getCount();
        cursor.close();
        database.close();
        return u;
    }

    public void DeleteSize(boolean check,String PRICE){
        SQLiteDatabase database = this.getWritableDatabase();
        if(check){
            String query = "DELETE FROM " + DB_QuantityDetails;
            database.execSQL(query);
        }else{
            String whereClause = Price + " =? ";
            String[] whereArgs = { PRICE };
            database.delete(DB_QuantityDetails,whereClause,whereArgs);
        }

        database.close();
    }

    // Employee Info
    public long AddEmployee(Employees employees){
        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(NAME,employees.getEmployeeName());
        contentValues.put(AGE,employees.getAge());
        contentValues.put(ADDRESS,employees.getAddress());
        contentValues.put(CONTACT,employees.getNumber());
        long inn = database.insert(DB_EMPLOYEES,null,contentValues);
        database.close();
        return inn;

    }

    public List<String> Employees(){
        List<String> stringList = new ArrayList<>();
        SQLiteDatabase database = getReadableDatabase();
        String string = " SELECT " + NAME + " FROM " + DB_EMPLOYEES;
        Cursor cursor = database.rawQuery(string,null);
        cursor.moveToFirst();
        if(cursor.moveToFirst()){
            do{
                String Name = cursor.getString(0);
                stringList.add(Name);
            }while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return stringList;
    }

    public List<Employees> EmployeesDetails(){
        List<Employees> employees = null;
        Employees employees1;
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT * FROM " + DB_EMPLOYEES;
        Cursor cursor;
        cursor = database.rawQuery(query,null);
        cursor.moveToFirst();
        if(cursor.moveToFirst()){
            do{
                String Name = cursor.getString(0);
                String Number = cursor.getString(1);
                String Age = cursor.getString(2);
                String Address = cursor.getString(3);
                employees1 = new Employees(Name,Number,Age,Address);
                employees.add(employees1);
            }while (cursor.moveToNext());
        }
        cursor.close();
        database.close();
        return employees;
    }

    //Count Members
    public int EmployeesCount(){
        int employees1 = 0;
        SQLiteDatabase database = getReadableDatabase();
        String query = "SELECT * FROM " + DB_EMPLOYEES;
        Cursor cursor;
        cursor = database.rawQuery(query,null);
        cursor.moveToFirst();
        if(cursor.moveToFirst()) {
            employees1 = cursor.getCount();
        }
        cursor.close();
        database.close();
        return employees1;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}

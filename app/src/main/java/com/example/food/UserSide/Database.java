package com.example.food.UserSide;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;

import com.example.food.Common.UserDetails_Constructor;
import com.example.food.UserSide.UserPayment.Coordinates;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    //Database Name
    private static final String DB_NAME = "OrderDetails.db";
    private static final int DV_VER = 2;

    //  For Order Details
    private static final String DB_TABLE = "OrderDetails";
    public static final String ID = "ID";
    public static final String PRODUCT_NAME = "Product_Name";
    public static final String QUANTITY = "QUANTITY";
    public static final String PRICE = "Price";
    public static final String TOTAL = "TOTAL";
    public static final String PLATE = "Plate";

    // For Coordinates
    private static final String DB_Coordinates = "Coordinates";
    public static final String Latitude = "Latitude";
    public static final String Longitude = "Longitude";

    // For User Data
    private static final String DB_USERDATA = "User_Data";
    public static final String USER_ID = "User_Id";
    public static final String NAME= "Name";
    public static final String GENDER = "Gender";
    public static final String DATE_OF_BIRTH = "Birthday";
    public static final String PhoneNo= "Phone";
    public static final String ADDRESS = "Address";
    public static final String DLatitude= "DLatitude";
    public static final String DLongitude= "DLongitude";
    public static final String PASSWORD = "Password";
    public static final String TOKEN = "token";
    public static final String STAFF= "Staff";
    public static final String EMAIL = "Email";

    public Database(Context context) {
        super(context, DB_NAME, null, DV_VER);
    }

    @Override //Create Table
    public void onCreate(SQLiteDatabase db) {
        String CreateTable = "CREATE TABLE " + DB_TABLE + "(" + ID + " INT, " + PRODUCT_NAME + " TEXT, " + QUANTITY + " INT ," + PRICE + " INT," + TOTAL + " INT ," + PLATE + " TEXT )";
        String Coordinates = "CREATE TABLE " + DB_Coordinates + "(" + Latitude + " TEXT, " + Longitude + " TEXT)";
        String UserData = "CREATE TABLE " + DB_USERDATA + "(" + USER_ID + " TEXT," + NAME + " TEXT, " + PhoneNo + " TEXT, " + STAFF + " TEXT, " + EMAIL + " TEXT, " + ADDRESS + " TEXT, " + GENDER + " TEXT," + DATE_OF_BIRTH + " TEXT, " + PASSWORD + " TEXT, " + DLatitude + " TEXT, " + DLongitude + " TEXT, " + TOKEN + " TEXT )";
        db.execSQL(CreateTable);
        db.execSQL(Coordinates);
        db.execSQL(UserData);
    }

/*    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db, oldVersion, newVersion);
        db.setVersion(1);
    }*/

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
       /* String insert = " ALTER TABLE " + DB_USERDATA + " ADD " + TOKEN + " TEXT ";
        db.execSQL(insert);*/
    }

    // For User Data Upload to Db
    public long SetUserData(UserDetails_Constructor userDetailsConstructor) {

        SQLiteDatabase database = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(USER_ID, userDetailsConstructor.getUSER_ID());
        contentValues.put(NAME, userDetailsConstructor.getFullName());
        contentValues.put(PhoneNo, userDetailsConstructor.getPhoneNo());
        contentValues.put(STAFF, userDetailsConstructor.getIsstaff());
        contentValues.put(ADDRESS, userDetailsConstructor.getAddress());
        contentValues.put(GENDER, userDetailsConstructor.getGender());
        contentValues.put(DATE_OF_BIRTH, userDetailsConstructor.getDate());
        contentValues.put(PASSWORD, userDetailsConstructor.getPassword());
        contentValues.put(DLatitude, userDetailsConstructor.getDLatitude());
        contentValues.put(DLongitude, userDetailsConstructor.getDLongitude());

        long Check3 = database.insert(DB_USERDATA,null,contentValues);
        database.close();
        return Check3;
    }

    //Id
    public Bundle UserID(String phone){
        SQLiteDatabase db = this.getReadableDatabase();
        String ID = null;
        String Phone = null;
        String Password = null;
        Bundle bundle = new Bundle();
        String Query = " SELECT " + USER_ID + " , " + PhoneNo +  " , " + PASSWORD + " FROM " + DB_USERDATA + " WHERE " + PhoneNo +  " =? "  ;
        Cursor cursor = db.rawQuery(Query,new String[]{phone});
        cursor.moveToFirst();
        if(cursor.moveToFirst()){
            do{
                ID = cursor.getString(cursor.getColumnIndex(USER_ID));
                Phone = cursor.getString(cursor.getColumnIndex(PhoneNo));
                Password = cursor.getString(cursor.getColumnIndex(PASSWORD));
                bundle.putString("Id",ID);
                bundle.putString("Phone",Phone);
                bundle.putString("Password",Password);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return bundle;
    }

    public void Delete(String ID){
        SQLiteDatabase db = getWritableDatabase();
        String whereClause = USER_ID + " =? ";
        String[] whereArgs = {ID};
        db.delete(DB_USERDATA,whereClause,whereArgs);
        db.close();
    }

    //phone
    public String Phone(String ID){
        SQLiteDatabase db = this.getReadableDatabase();
        String Phone = null;
        String Query = " SELECT " + USER_ID + " , " + PhoneNo + " FROM " + DB_USERDATA + " WHERE " + USER_ID +  " =? "  ;
        Cursor cursor = db.rawQuery(Query,new String[]{ID});
        cursor.moveToFirst();
        if(cursor.moveToFirst()){
            do{
                Phone = cursor.getString(cursor.getColumnIndex(PhoneNo));
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return Phone;
    }

    //GetToken
    public String GetToken(String Id){
        SQLiteDatabase db = getReadableDatabase();
        String token = null;
        String Query = " SELECT * FROM " + DB_USERDATA + " WHERE " + USER_ID + " =? ";
        Cursor cursor = db.rawQuery(Query,new String[]{Id});
        cursor.moveToFirst();
        if(cursor.moveToFirst()){
            do{
                token = cursor.getString(cursor.getColumnIndex(TOKEN));
            }while (cursor.moveToLast());
        }
        cursor.close();
        db.close();
        return token;
    }

    //For Login
    public UserDetails_Constructor UserDetails(String ID){

        SQLiteDatabase db = getReadableDatabase();
        UserDetails_Constructor userDetailsConstructor = null;
        String Query = " SELECT * FROM " + DB_USERDATA + " WHERE " + USER_ID + " =? ";
        Cursor cursor = db.rawQuery(Query,new String[]{ID});
        cursor.moveToFirst();
        if(cursor.moveToFirst()){
            do{
                String GetId = cursor.getString(cursor.getColumnIndex(USER_ID));
                String Name = cursor.getString(cursor.getColumnIndex(NAME));
                String Staff = cursor.getString(cursor.getColumnIndex(STAFF));
                String Mail = cursor.getString(cursor.getColumnIndex(EMAIL));
                String phone = cursor.getString(cursor.getColumnIndex(PhoneNo));
                String Address = cursor.getString(cursor.getColumnIndex(ADDRESS));
                String Gender = cursor.getString(cursor.getColumnIndex(GENDER));
                String DOB = cursor.getString(cursor.getColumnIndex(DATE_OF_BIRTH));
                String Latitude = cursor.getString(cursor.getColumnIndex(DLatitude));
                String Longitude = cursor.getString(cursor.getColumnIndex(DLongitude));
                String password = cursor.getString(cursor.getColumnIndex(PASSWORD));
                userDetailsConstructor = new UserDetails_Constructor(GetId,Name,Mail,phone,password,DOB,Gender,Staff,Address,Latitude,Longitude,null);
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return userDetailsConstructor;
    }

    //Update Data
    // # 1 for Name,DOB,Gender Done
    // # 2 for Address,Lat,Long
    // # 3 for Phone Done
    // # 4 for Password
    // # 5 for Token
    public long UpdateUserData(String ID,int check,Bundle bundle1){

        SQLiteDatabase db = this.getReadableDatabase();
        String Query;
        long i = 0;
        Cursor cursor;
        Query = " SELECT * FROM " + DB_USERDATA + " WHERE " + USER_ID + " = " + ID ;
        cursor = db.rawQuery(Query,null);
        ContentValues cv = new ContentValues();
        String whereClause = USER_ID + " =? ";
        String[] whereArgs = {ID};
        if(check == 1){
            String Name = bundle1.getString("editName");
            String DATEOFBIRTH = bundle1.getString("editDOB");
            String Gender = bundle1.getString("editGen");
            String Mail = bundle1.getString("editMail");
            cursor.moveToFirst();
            if(cursor.moveToFirst()){
                    String Name1 = (cursor.getString(cursor.getColumnIndex(NAME)));
                    String Dob1 = (cursor.getString(cursor.getColumnIndex(DATE_OF_BIRTH)));
                    String Gender1 = (cursor.getString(cursor.getColumnIndex(GENDER)));
                    String Mail1 = (cursor.getString(cursor.getColumnIndex(EMAIL)));
                if(bundle1.getString("editName") != null){
                if(Name.length() > 0){
                    if(!Name1.equals(Name)){
                        cv.put(NAME,Name);
                        i =  db.update(DB_USERDATA,cv,whereClause,whereArgs);
                    }
                }
                }

                if(bundle1.getString("editMail") != null){

                    if(Mail1 == null) {
                        cv.put(EMAIL, Mail);
                        i = db.update(DB_USERDATA, cv, whereClause, whereArgs);
                    }else{
                        if(Mail.length() > 3){
                            if(!Mail.equals(Mail1)){
                                cv.put(EMAIL, Mail);
                                i = db.update(DB_USERDATA, cv, whereClause, whereArgs);
                            }
                        }else if (Mail.length() == 0){
                            cv.put(EMAIL, Mail);
                            i = db.update(DB_USERDATA, cv, whereClause, whereArgs);
                        }
                    }

                    }

                if(bundle1.getString("editDOB") != null) {
                    if (DATEOFBIRTH.length() > 0) {
                        if (!Dob1.equals(DATEOFBIRTH)) {
                            cv.put(DATE_OF_BIRTH, DATEOFBIRTH);
                            i = db.update(DB_USERDATA, cv, whereClause, whereArgs);
                        }
                    }
                }



                if(bundle1.getString("editGen") != null){
                if(Gender.length() > 0){
                    if(!Gender1.equals(Gender)){
                        cv.put(GENDER,Gender);
                       i = db.update(DB_USERDATA,cv,whereClause,whereArgs);
                    }
                }
                }
        }
        }else if(check == 2){
            String Add = bundle1.getString("Add");
            String Lon = bundle1.getString("Long");
            String Lat = bundle1.getString("Lat");
            cursor.moveToFirst();
            if(cursor.moveToFirst()){
                String Add1 = (cursor.getString(cursor.getColumnIndex(ADDRESS)));
                if(!Add1.equals(Add)){
                    cv.put(ADDRESS,Add);
                    cv.put(DLongitude,Lon);
                    cv.put(DLatitude,Lat);
                   i =  db.update(DB_USERDATA,cv,whereClause,whereArgs);
                }
            }
        }else if(check == 3){
            String Phone = bundle1.getString("Contact");
            cursor.moveToFirst();
            if(cursor.moveToFirst()){
                String Phone1 = (cursor.getString(cursor.getColumnIndex(PhoneNo)));
                if(!Phone1.equals(Phone)){
                    cv.put(PhoneNo,Phone);
                    i =  db.update(DB_USERDATA,cv,whereClause,whereArgs);
                }
            }
        }else if (check == 4) {
            String Password = bundle1.getString("Password");
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                cv.put(PASSWORD, Password);
                i = db.update(DB_USERDATA, cv, whereClause, whereArgs);
            }
        }else if (check == 5) {
            String token = bundle1.getString("token");
            cursor.moveToFirst();
            if (cursor.moveToFirst()) {
                cv.put(TOKEN, token);
                i = db.update(DB_USERDATA, cv, whereClause, whereArgs);
            }
        }
        cursor.close();
        db.close();
        return i;
    }

    // For Coordinates
    public void SetCoordinates(String latitude, String longitude){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Latitude,latitude);
        contentValues.put(Longitude,longitude);

        long insert = db.insert(DB_Coordinates, null, contentValues);
        if (insert == -1) {
            db.close();
        } else {
            db.close();
        }
        db.close();
    }

    // Get Coordinates
    public List<Coordinates> GetCoordinates() {
        Bundle Coordinate = new Bundle();
        final List<Coordinates> Directions = new ArrayList<>();
        String Coor = " SELECT " + DLatitude + " , "  + DLongitude + " FROM " + DB_USERDATA ;
        SQLiteDatabase database = getReadableDatabase();
        Cursor cursor = database.rawQuery(Coor,null);
        cursor.moveToPosition(1);
        if(cursor.moveToPosition(1)){
            Coordinate.putString("Lat",cursor.getString(cursor.getColumnIndex(DLatitude)));
            Coordinate.putString("Long",cursor.getString(cursor.getColumnIndex(DLongitude)));
            String Lat = cursor.getString(cursor.getColumnIndex(DLatitude));
            String Lon = cursor.getString(cursor.getColumnIndex(DLongitude));
            Coordinates coordinate1 = new Coordinates(Lon,Lat);
            Directions.add(coordinate1);
        }
        cursor.close();
        database.close();
        return Directions;
    }

    //  For Cart
    //Check Cart to add details for Multiple options
    public Bundle Check(Order_Constructor orderConstructor, boolean check1) {
        Bundle PlateTell = new Bundle();
        SQLiteDatabase database = getReadableDatabase();
        String check;
        Cursor cursor = null;
        if (check1) { //Second time
            check = " SELECT " + ID + " , " + PLATE + " , " + QUANTITY + " , " + TOTAL + " FROM " + DB_TABLE + " WHERE " + ID + " =? " + " AND " + PLATE + " =? ";
            cursor = database.rawQuery(check, new String[]{orderConstructor.getProductId(), orderConstructor.getPlate()});
            cursor.moveToFirst();
            if (cursor.getCount() > 0 && cursor.getString(cursor.getColumnIndex(PLATE)).equals(orderConstructor.getPlate())) {
                PlateTell.putString("Plate", cursor.getString(cursor.getColumnIndex(PLATE)));
                PlateTell.putInt("Quan", cursor.getInt(cursor.getColumnIndex(QUANTITY)));
                PlateTell.putInt("Total", cursor.getInt(cursor.getColumnIndex(TOTAL)));
            } else {
                PlateTell.putString("Plate", "diffPlate");
            }
        } else { // First time
            PlateTell.putString("Plate", "NOT");
        }
        assert cursor != null;
        cursor.close();
        return PlateTell;
    }

    // Edit Quantity for Multiple
    public void EditItem(Order_Constructor orderConstructor) {
        SQLiteDatabase db = this.getWritableDatabase();
        String check1 = " SELECT " + ID + " , " + PLATE + " , " + QUANTITY + " , " + TOTAL + " FROM " + DB_TABLE + " WHERE " + ID + " =? " + " AND " + PLATE + " =? ";
        Cursor cursor = db.rawQuery(check1, new String[]{orderConstructor.getProductId(), orderConstructor.getPlate()});
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            ContentValues contentValues = new ContentValues();
            contentValues.put(QUANTITY, Integer.parseInt(orderConstructor.getQuantity()));
            contentValues.put(TOTAL, Integer.parseInt(orderConstructor.getTotal()));
            String whereClause = ID + " =? " + " AND " + PLATE + " =? ";
            String[] whereArgs = {orderConstructor.getProductId(), orderConstructor.getPlate()};
            // Update (TableName, Content Value, Column name ID Which we which to change, Send ID of that column)
            db.update(DB_TABLE, contentValues, whereClause, whereArgs);
        }
        cursor.close();
        db.close();
    }

    //Check Cart to add details for Single options
    public Bundle SingleOption(Order_Constructor orderConstructor) {

        Bundle PlateTell1 = new Bundle();
        SQLiteDatabase database = getReadableDatabase();
        String check;
        Cursor cursor = null;

        check = " SELECT " + ID + " , " + QUANTITY + " , " + TOTAL + " FROM " + DB_TABLE + " WHERE " + ID + " = " + orderConstructor.getProductId();
        cursor = database.rawQuery(check, null);

        cursor.moveToFirst();
            // Second time
        if (cursor.moveToFirst()) {

            ContentValues contentValues = new ContentValues();
            contentValues.put(QUANTITY, Integer.parseInt(orderConstructor.getQuantity()));
            contentValues.put(TOTAL, Integer.parseInt(orderConstructor.getTotal()));
            String whereClause = ID + " =? ";
            String[] whereArgs = {orderConstructor.getProductId()};
            // Update (TableName, Content Value, Column name ID Which we which to change, Send ID of that column)
            database.update(DB_TABLE, contentValues, whereClause, whereArgs);
            PlateTell1.putString("Plate", "UPDATED");
        }

        else {  // First time
            PlateTell1.putString("Plate", "NOT");
        }
        cursor.close();
        database.close();
        return PlateTell1;
    }

    //Get Same Quantity Detail food Count
    public int GetTotalCount() {
        SQLiteDatabase database = this.getReadableDatabase();
        String check = "SELECT " + QUANTITY + " FROM " + DB_TABLE;
        Cursor cursor2 = database.rawQuery(check, null);
        List<Integer> Num = new ArrayList<>();
        int Number = 0;
        if (cursor2.getCount() == 0) {
            cursor2.close();
        } else {
            if (cursor2.moveToFirst()) {
                do {
                    Number = cursor2.getInt(cursor2.getColumnIndex(QUANTITY));
                    Num.add(Number);
                } while (cursor2.moveToNext());
            }
            cursor2.close();
            Number = 0;
            for (int i = 0; i < Num.size(); i++) {
                Number = Num.get(i) + Number;
            }
        }
        database.close();
        return Number;
    }

    //Get Count when Detail food opens
    public Bundle GetCount(String id) {
        SQLiteDatabase database = this.getReadableDatabase();
        List<Integer> Detail = new ArrayList<>();
        List<Integer> Detail1 = new ArrayList<>();
        Bundle bundle = new Bundle();

        String check = "SELECT * FROM " + DB_TABLE + " WHERE " + ID + " =? ";
        Cursor cursor2 = database.rawQuery(check, new String[]{id});
        int Number = 0;
        int Number1 = 0;
        if (cursor2.getCount() != 0) {
            cursor2.moveToFirst();
            if (cursor2.moveToFirst()) {
                do {
                    Number = cursor2.getInt(cursor2.getColumnIndex(QUANTITY));
                    Detail.add(Number);

                    Number1 = cursor2.getInt(cursor2.getColumnIndex(TOTAL));
                    Detail1.add(Number1);
                } while (cursor2.moveToNext());
            }
            // Quantity
            Number = 0;
            for (int i = 0; i < Detail.size(); i++) {
                Number = Detail.get(i) + Number;
            }
            // Total
            Number1 = 0;
            for (int j = 0; j < Detail.size(); j++) {
                Number1 = Detail1.get(j) + Number1;
            }
        }
        bundle.putInt("QuantitySent", Number);
        bundle.putInt("TotalSent", Number1);
        cursor2.close();
        database.close();
        return bundle;
    }

    // Add to cart
    public void addToCart(Order_Constructor orderConstructor) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(ID, orderConstructor.getProductId());
        cv.put(PRODUCT_NAME, orderConstructor.getProductName());
        cv.put(QUANTITY, orderConstructor.getQuantity());
        cv.put(PRICE, orderConstructor.getPrice());
        cv.put(TOTAL, orderConstructor.getTotal());
        cv.put(PLATE, orderConstructor.getPlate());

        long insert = db.insert(DB_TABLE, null, cv);
        if (insert == -1) {
            db.close();
        } else {
            db.close();
        }
    }

    // Get List to adapter
    public List<Order_Constructor> getCarts() {
        final List<Order_Constructor> result = new ArrayList<>();
        String query = "SELECT * FROM " + DB_TABLE;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int ID = cursor.getInt(0);
                String Name = cursor.getString(1);
                int Quan = cursor.getInt(2);
                int Paisa = cursor.getInt(3);
                int Total = cursor.getInt(4);
                String Plate = cursor.getString(5);
                Order_Constructor orderConstructor = new Order_Constructor(String.valueOf(ID), Name, String.valueOf(Quan), String.valueOf(Paisa), String.valueOf(Total), Plate);
                result.add(orderConstructor);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return result;
    }

    // Delete Item #true Plate
    public long DeleteItem(Order_Constructor orderConstructor, boolean from) {

        SQLiteDatabase db = this.getWritableDatabase();
        String whereClause;
        long Check;
        String[] whereArgs;
        if(from){
            whereClause = PRODUCT_NAME + " =? " + " AND " + PLATE + " =? ";
            whereArgs = new String[]{orderConstructor.getProductName(), orderConstructor.getPlate()};
        }else{
            whereClause = ID + " =? ";
            whereArgs = new String[]{orderConstructor.getProductId()};
        }
        Check =  db.delete(DB_TABLE,whereClause,whereArgs);
        return Check;
    }

    // Clear Cart
    public void clearCart() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + DB_TABLE;
        db.execSQL(query);
        db.close();
    }

}

package com.example.food.Common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class SessionManager {

    //Variables
    SharedPreferences usersSession;
    SharedPreferences.Editor editor;
    Context context;

    //Session Name
    public static final String SESSION_USERSESSION = "userLoginSession";
    public static final String SESSION_REMEMBERME = "rememberMe";

    //User Session variables
    static final String IS_LOGIN = "IsLoggedIn";
    public static final String KEY_USERID = "UserId";
    public static final String KEY_FULLNAME = "fullname";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_LAT = "Latitude";
    public static final String KEY_LONG = "Longitude";
    public static final String KEY_PASSWORD = "password";
    public static final String KEY_DATE = "date";
    public static final String KEY_GENDER = "gender";
    public static final String KEY_PHONENUMBER = "phonenumber";
    public static final String KEY_STAFF = "isstaff";

    //Remember Me Variables
    private static final String IS_REMEMBERME = "IsRememberMe";
    public static final String KEY_SESSIONPHONENUMBER = "phonenumber";
    public static final String KEY_SESSIONPASSWORD = "password";


    //Constructor make Session
    @SuppressLint("CommitPrefEdits")
    public SessionManager(Context _context, String sessionName) {
        // Type Session name to use various activities different

        context = _context;
        usersSession = _context.getSharedPreferences(sessionName, Context.MODE_PRIVATE);
        editor = usersSession.edit();
    }

    //What we want to pass in create session
    public void createLoginSession(String ID,String fullname, String email, String phonenumber, String password, String date, String gender, String staff, String address,String Latitude,String Longitude) {

        editor.putBoolean(IS_LOGIN,true);
        editor.putString(KEY_USERID,ID);
        editor.putString(KEY_FULLNAME, fullname);
        editor.putString(KEY_PASSWORD, password);
        editor.putString(KEY_GENDER, gender);
        editor.putString(KEY_DATE, date);
        editor.putString(KEY_PHONENUMBER, phonenumber);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_STAFF,staff);
        editor.putString(KEY_ADDRESS,address);
        editor.putString(KEY_LAT,Latitude);
        editor.putString(KEY_LONG,Longitude);

        //Store and edit data
        editor.commit();

    }

    //Put data in above session via hash map
    public HashMap<String, String> getUserDetailFromSession() {

        HashMap<String, String> userData = new HashMap<String, String>();
        userData.put(KEY_FULLNAME, usersSession.getString(KEY_FULLNAME, null));
        userData.put(KEY_LONG,usersSession.getString(KEY_LONG, null));
        userData.put(KEY_LAT, usersSession.getString(KEY_LAT, null));
        userData.put(KEY_PASSWORD, usersSession.getString(KEY_PASSWORD, null));
        userData.put(KEY_DATE, usersSession.getString(KEY_DATE, null));
        userData.put(KEY_GENDER, usersSession.getString(KEY_GENDER, null));
        userData.put(KEY_PHONENUMBER, usersSession.getString(KEY_PHONENUMBER, null));
        userData.put(KEY_STAFF, usersSession.getString(KEY_STAFF, null));
        userData.put(KEY_ADDRESS, usersSession.getString(KEY_ADDRESS, null));
        userData.put(KEY_USERID, usersSession.getString(KEY_USERID, null));
        userData.put(KEY_EMAIL, usersSession.getString(KEY_EMAIL, null));
        return userData;
    }

    //Check login
    public boolean checkLogin() {

        return usersSession.getBoolean(IS_LOGIN,false);
    }

    //Logout User
    public void logoutUserFromSession() {
        editor.putBoolean(IS_LOGIN,false);
        editor.clear();
        editor.commit();
    }

    //RememberMe
    public void createRememberMeSession(String phonenumber , String password/*,*//*String username*/) {

        editor.putBoolean(IS_REMEMBERME, true);

        editor.putString(KEY_SESSIONPHONENUMBER, phonenumber);
        editor.putString(KEY_SESSIONPASSWORD, password);

        //Store and edit data
        editor.commit();

    }

    public HashMap<String, String> getRememberMeDetailFromSession() {
        HashMap<String, String> userData = new HashMap<String, String>();

        userData.put(KEY_SESSIONPHONENUMBER, usersSession.getString(KEY_SESSIONPHONENUMBER, null));
        userData.put(KEY_SESSIONPASSWORD, usersSession.getString(KEY_SESSIONPASSWORD, null));
        return userData;
    }

    public boolean checkRememberME() {
        if (usersSession.getBoolean(IS_REMEMBERME, false)) {
            return true;
        } else {
            return false;
        }
    }


}

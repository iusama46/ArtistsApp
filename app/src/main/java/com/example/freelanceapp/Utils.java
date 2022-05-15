package com.example.freelanceapp;

import android.content.Context;
import android.content.SharedPreferences;

public class Utils {
    public static final int ARTIST=0;
    public static final int CONSUMER=1;

    public static  boolean IS_ARTIST=true ;


    public static void saveUserRole(Context context, int userType,String name){
        SharedPreferences sharedpreferences =context. getSharedPreferences("role", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();

        IS_ARTIST= userType==0;
        editor.putBoolean("user", IS_ARTIST);
        editor.putString("name", name);

        editor.apply();
    }

    public static boolean getUserIsArtist(Context context){
        SharedPreferences sharedpreferences =context. getSharedPreferences("role", Context.MODE_PRIVATE);
        return sharedpreferences.getBoolean("user", false);
    }

    public static String getUserName(Context context){
        SharedPreferences sharedpreferences =context. getSharedPreferences("role", Context.MODE_PRIVATE);
        return sharedpreferences.getString("name", "");
    }

    public static void clearDb(Context context){
        SharedPreferences sharedpreferences =context. getSharedPreferences("role", Context.MODE_PRIVATE);
        sharedpreferences.edit().clear().apply();
    }
}

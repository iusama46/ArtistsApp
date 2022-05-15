package com.example.freelanceapp.chat.helper;

import android.util.Base64;
/**
 * Created by Ussama Iftikhar on 13-Oct-2020.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
public class Base64Custom {

    public static String encodeBase64(String text){
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replaceAll("(\\n|\\r)", "");
    }

    public static  String decodeBase64(String encondedText){
        return new String(Base64.decode(encondedText, Base64.DEFAULT));
    }
}

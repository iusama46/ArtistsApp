package com.example.freelanceapp.chat.helper;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;
/**
 * Created by Ussama Iftikhar on 13-Oct-2020.
 * Email iusama46@gmail.com
 * Email iusama466@gmail.com
 * Github https://github.com/iusama46
 */
public class Permissions {

    public static boolean validatePermissions (int requestCode, Activity activity, String[] permissions){
        if(Build.VERSION.SDK_INT >= 23){
            List<String> listPermissions = new ArrayList<String>();

           //verifying every past permissions and checking if it's already released
            for(String permission: permissions){
               Boolean validatePermission = ContextCompat.checkSelfPermission(activity, permission) == PackageManager.PERMISSION_GRANTED;

               if(!validatePermission){
                   listPermissions.add(permission);
               }
            }

            //if list is empty, there's no need to request permission
            if(listPermissions.isEmpty()){
                return true;
            }

            String[] newPermissions = new String[listPermissions.size()];
            listPermissions.toArray(newPermissions);

            //request permission
            ActivityCompat.requestPermissions(activity, newPermissions, requestCode);

        }

        return true;
    }
}

package com.otec.koko.Running_service;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;
import com.otec.koko.R;
import com.otec.koko.utils.util;
import java.net.URL;
import me.pushy.sdk.Pushy;
public class RegisterUser extends AsyncTask<Void,Void,Object> {

    private Activity activity;
    private String TAG = "RegisterUser";

    public RegisterUser(Activity context) {
        this.activity = context;
    }

    @Override
    protected Object doInBackground(Void... voids) {
        try {
            String deviceToken = Pushy.register(activity.getApplicationContext());
            new URL("https://com.grelot.com/regsiter/device?token="+deviceToken).openConnection();
            Log.d(TAG, "USER TOKEN "+deviceToken);
            return  deviceToken;
          } catch (Exception e) {
            return e;
        }
    }


    @Override
    protected void onPostExecute(Object o) {
        if(o instanceof Exception){
            new util().init(activity.getApplicationContext()).edit().putString(activity.getString(R.string.DEVICE_TOKEN),null).apply();
            util.messageLong("Error Occurred while Registering Device token ! pls Ensure u have a good internet connection while relaunching app", activity);
        }
        else{
             new util().init(activity.getApplicationContext()).edit().putString(activity.getString(R.string.DEVICE_TOKEN),String.valueOf(o)).apply();
            Log.d(TAG, "onPostExecute: "+o);
        }


    }

}

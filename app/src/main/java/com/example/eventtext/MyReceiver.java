package com.example.eventtext;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static io.realm.Realm.getApplicationContext;

public class MyReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String result = intent.getExtras().getString("unique");
        String slumber = intent.getExtras().getString("number");
        slumber = slumber.replaceAll("-","");

        String sms = intent.getExtras().getString("sms");

        Toast.makeText(context, result +"\n"+ slumber +"\n"+sms,Toast.LENGTH_LONG).show();
        try {

            SmsManager mySmsManager = SmsManager.getDefault();
            mySmsManager.sendTextMessage(slumber, null, sms, null, null);
            Toast.makeText(context, "Text Send Successfully !", Toast.LENGTH_LONG).show();
        }catch (Exception e){
            Toast.makeText(context, "SMS Send permission required ", Toast.LENGTH_LONG).show();

        }

//        RealmResults<home_cardModel> results = realm.where(home_cardModel.class).equalTo("event_Id", result).findAll();
//        home_cardModel p = results.first();
//        realm.beginTransaction();
//        results.deleteAllFromRealm();
//        realm.commitTransaction();




    }
}
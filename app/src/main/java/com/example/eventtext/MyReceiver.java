package com.example.eventtext;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import io.realm.Realm;
import io.realm.RealmConfiguration;

import static io.realm.Realm.getApplicationContext;

public class MyReceiver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {

        String result = intent.getExtras().getString("unique");
        String slnumber = intent.getExtras().getString("number");
        String sms = intent.getExtras().getString("sms");

        Toast.makeText(context, result +"\n"+ slnumber +"\n"+sms,Toast.LENGTH_LONG).show();


    }
}
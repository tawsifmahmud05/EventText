package com.example.eventtext;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static android.Manifest.permission.READ_PHONE_NUMBERS;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.READ_SMS;
import static io.realm.Realm.getApplicationContext;


public class Home_Fragment extends Fragment {
    Realm realm;
    private RecyclerView eventListView;
    private ArrayList<home_cardModel> cardArrayList;
    home_card_adapter cardAdapter;

    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private TextView today,event_count,event_day;
    private Button today_btn,tomorrow_btn,week_btn;
    private String today_event,tomorrow_event,day_3,day_4,day_5,day_6,day_7;
    private int count=0;

    private static final int MY_PERMISSIONS_REQUEST_CODE = 123;

    Calendar cal = Calendar.getInstance();


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_home_, container, false);
        realm.init(getActivity());

        RealmConfiguration configuration = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(configuration);

        today = (TextView) view.findViewById(R.id.home_today);
        today_btn =(Button) view.findViewById(R.id.today_btn);
        tomorrow_btn =(Button) view.findViewById(R.id.tomorrow_btn);
        week_btn =(Button) view.findViewById(R.id.week_btn);
        event_day =(TextView) view.findViewById(R.id.home_event_day);
        event_count =(TextView) view.findViewById(R.id.home_event_num);


        cardArrayList = new ArrayList<>();

        // we are initializing our adapter class and passing our arraylist to it.
        cardAdapter = new home_card_adapter(getActivity(), cardArrayList);

        eventListView = (RecyclerView) view.findViewById(R.id.eventlistView);


        checkPermission();


        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        Date currentDate = new Date();
        LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        int next_day = localDateTime.plusDays(1).getDayOfMonth();
        int next_day_month = localDateTime.plusDays(1).getMonthValue();
        int third_day = localDateTime.plusDays(2).getDayOfMonth();
        int third_day_month = localDateTime.plusDays(2).getMonthValue();
        int four_day = localDateTime.plusDays(3).getDayOfMonth();
        int four_day_month = localDateTime.plusDays(3).getMonthValue();
        int five_day = localDateTime.plusDays(4).getDayOfMonth();
        int five_day_month = localDateTime.plusDays(5).getMonthValue();
        int six_day = localDateTime.plusDays(5).getDayOfMonth();
        int six_day_month = localDateTime.plusDays(6).getMonthValue();
        int seven_day = localDateTime.plusDays(6).getDayOfMonth();
        int seven_day_month = localDateTime.plusDays(6).getMonthValue();

        tomorrow_event = next_day+"-"+MONTHS[next_day_month-1]+"-"+year;
        day_3 = third_day+"-"+MONTHS[third_day_month-1]+"-"+year;
        day_4 = four_day+"-"+MONTHS[four_day_month-1]+"-"+year;
        day_5 = five_day+"-"+MONTHS[five_day_month-1]+"-"+year;
        day_6 = six_day+"-"+MONTHS[six_day_month-1]+"-"+year;
        day_7 = seven_day+"-"+MONTHS[seven_day_month-1]+"-"+year;



        today_event = day+"-"+MONTHS[month]+"-"+year;
        today.setText(today_event);


        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        eventListView.setLayoutManager(linearLayoutManager);
        eventListView.setAdapter(cardAdapter);


        RealmResults<home_cardModel> results = realm.where(home_cardModel.class).findAll();

        for (home_cardModel p : results) {
            cardArrayList.add(p);
            cardAdapter.notifyDataSetChanged();
        }
        RealmResults<home_cardModel> result = realm.where(home_cardModel.class).contains("event_Id",today_event).findAll();
        cardArrayList.clear();

        for(home_cardModel p:result){
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy-HH:mm");
            Calendar Alcal = Calendar.getInstance();
            try {
                Alcal.setTime(sdf.parse(p.getCard_date()+"-" +p.getCard_time()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            if(Alcal.getTimeInMillis()>=cal.getTimeInMillis()){
                cardArrayList.add(p);
                count++;

            }


        }
        cardAdapter.notifyDataSetChanged();

        event_count.setText(count + " events Today");


        today_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                event_day.setText("Today's Events");

                RealmResults<home_cardModel> results = realm.where(home_cardModel.class).contains("event_Id",today_event).findAll();
                cardArrayList.clear();

                for(home_cardModel p:results){
                    UpdateHomeData(p);
                }
                cardAdapter.notifyDataSetChanged();


            }
        });


        tomorrow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event_day.setText("Tomorrow's Events");
                RealmResults<home_cardModel> results = realm.where(home_cardModel.class).contains("event_Id",tomorrow_event).findAll();
                cardArrayList.clear();
                for(home_cardModel p:results){
                    UpdateHomeData(p);

                }
                cardAdapter.notifyDataSetChanged();


            }
        });
        week_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cardArrayList.clear();
                event_day.setText("This week's Events");
                RealmResults<home_cardModel> result1 = realm.where(home_cardModel.class).contains("event_Id",today_event).findAll();
                RealmResults<home_cardModel> result2 = realm.where(home_cardModel.class).contains("event_Id",tomorrow_event).findAll();
                RealmResults<home_cardModel> result3 = realm.where(home_cardModel.class).contains("event_Id",day_3).findAll();
                RealmResults<home_cardModel> result4 = realm.where(home_cardModel.class).contains("event_Id",day_4).findAll();
                RealmResults<home_cardModel> result5 = realm.where(home_cardModel.class).contains("event_Id",day_5).findAll();
                RealmResults<home_cardModel> result6 = realm.where(home_cardModel.class).contains("event_Id",day_6).findAll();
                RealmResults<home_cardModel> result7 = realm.where(home_cardModel.class).contains("event_Id",day_7).findAll();

                for(home_cardModel p:result1){
                    UpdateHomeData(p);

                }
                for(home_cardModel p:result2){
                    UpdateHomeData(p);

                }
                for(home_cardModel p:result3){
                    UpdateHomeData(p);

                }
                for(home_cardModel p:result4){
                    UpdateHomeData(p);

                }
                for(home_cardModel p:result5){
                    UpdateHomeData(p);

                }
                for(home_cardModel p:result6){
                    UpdateHomeData(p);

                }
                for(home_cardModel p:result7){
                    UpdateHomeData(p);

                }

                cardAdapter.notifyDataSetChanged();


            }
        });



        return view;
    }
    public void receiveMsg(ArrayList<home_cardModel> a) {
        cardArrayList = a;
    }


    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{READ_SMS, READ_PHONE_NUMBERS, READ_PHONE_STATE}, 100);
        }
    }

    protected void checkPermission(){
        if(ContextCompat.checkSelfPermission(getActivity(),Manifest.permission.SEND_SMS)
                + ContextCompat.checkSelfPermission(
                getActivity(),Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED){

            // Do something, when permissions not granted
            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(),Manifest.permission.SEND_SMS)
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(),Manifest.permission.READ_CONTACTS)){
                // If we should give explanation of requested permissions

                // Show an alert dialog here with request explanation
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage("Send SMS, Read Contacts " +
                        " Storage permissions are required to do the task.");
                builder.setTitle("Please grant those permissions");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ActivityCompat.requestPermissions(
                                requireActivity(),
                                new String[]{
                                        Manifest.permission.SEND_SMS,
                                        Manifest.permission.READ_CONTACTS
                                },
                                MY_PERMISSIONS_REQUEST_CODE
                        );
                    }
                });
                builder.setNeutralButton("Cancel",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }else{
                // Directly request for required permissions, without explanation
                ActivityCompat.requestPermissions(
                        getActivity(),
                        new String[]{
                                Manifest.permission.SEND_SMS,
                                Manifest.permission.READ_CONTACTS
                        },
                        MY_PERMISSIONS_REQUEST_CODE
                );
            }
        }else {
            // Do something, when permissions are already granted
//            Toast.makeText(getActivity(),"Permissions already granted",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults){
        switch (requestCode){
            case MY_PERMISSIONS_REQUEST_CODE:{
                // When request is cancelled, the results array are empty
                if(
                        (grantResults.length >0) &&
                                (grantResults[0]
                                        + grantResults[1]
                                        + grantResults[2]
                                        == PackageManager.PERMISSION_GRANTED
                                )
                ){
                    // Permissions are granted
                    Toast.makeText(getActivity(),"Permissions granted.",Toast.LENGTH_SHORT).show();
                }else {
                    // Permissions are denied
                    Toast.makeText(getActivity(),"Permissions denied.",Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    private void UpdateHomeData(home_cardModel p){
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy-HH:mm");
        Calendar Alcal = Calendar.getInstance();
        try {
            Alcal.setTime(sdf.parse(p.getCard_date()+"-" +p.getCard_time()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(Alcal.getTimeInMillis()>=cal.getTimeInMillis()) {
            cardArrayList.add(p);
        }
    }

}



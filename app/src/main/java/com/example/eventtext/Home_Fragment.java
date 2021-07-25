package com.example.eventtext;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;


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

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        Date currentDate = new Date();
        LocalDateTime localDateTime = currentDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        int next_day = localDateTime.plusDays(1).getDayOfMonth();
        int third_day = localDateTime.plusDays(2).getDayOfMonth();
        int four_day = localDateTime.plusDays(3).getDayOfMonth();
        int five_day = localDateTime.plusDays(4).getDayOfMonth();
        int six_day = localDateTime.plusDays(5).getDayOfMonth();
        int seven_day = localDateTime.plusDays(6).getDayOfMonth();

        tomorrow_event = next_day+"-"+MONTHS[month]+"-"+year;
        day_3 = third_day+"-"+MONTHS[month]+"-"+year;
        day_4 = four_day+"-"+MONTHS[month]+"-"+year;
        day_5 = five_day+"-"+MONTHS[month]+"-"+year;
        day_6 = six_day+"-"+MONTHS[month]+"-"+year;
        day_7 = seven_day+"-"+MONTHS[month]+"-"+year;



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
            cardArrayList.add(p);
            count++;
            cardAdapter.notifyDataSetChanged();

        }

        event_count.setText(count + " events Today");


        today_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                event_day.setText("Today's Events");

                RealmResults<home_cardModel> results = realm.where(home_cardModel.class).contains("event_Id",today_event).findAll();
                cardArrayList.clear();

                for(home_cardModel p:results){
                    cardArrayList.add(p);
                    cardAdapter.notifyDataSetChanged();

                }


            }
        });


        tomorrow_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                event_day.setText("Tomorrow's Events");
                RealmResults<home_cardModel> results = realm.where(home_cardModel.class).contains("event_Id",tomorrow_event).findAll();
                cardArrayList.clear();
                for(home_cardModel p:results){
                    cardArrayList.add(p);
                    cardAdapter.notifyDataSetChanged();

                }


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
                    cardArrayList.add(p);
                    cardAdapter.notifyDataSetChanged();

                }
                for(home_cardModel p:result2){
                    cardArrayList.add(p);
                    cardAdapter.notifyDataSetChanged();

                }
                for(home_cardModel p:result3){
                    cardArrayList.add(p);
                    cardAdapter.notifyDataSetChanged();

                }
                for(home_cardModel p:result4){
                    cardArrayList.add(p);
                    cardAdapter.notifyDataSetChanged();

                }
                for(home_cardModel p:result5){
                    cardArrayList.add(p);
                    cardAdapter.notifyDataSetChanged();

                }
                for(home_cardModel p:result6){
                    cardArrayList.add(p);
                    cardAdapter.notifyDataSetChanged();

                }
                for(home_cardModel p:result7){
                    cardArrayList.add(p);
                    cardAdapter.notifyDataSetChanged();

                }


            }
        });



        return view;
    }
    public void receiveMsg(ArrayList<home_cardModel> a) {
        cardArrayList = a;
    }


}
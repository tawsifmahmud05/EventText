package com.example.eventtext;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static io.realm.Realm.getApplicationContext;


public class Profile_Fragment extends Fragment {

    Realm realm;
    private RecyclerView eventListView;
    private ArrayList<home_cardModel> cardArrayList;
    private profile_card_adapter pro_adapter;

    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private TextView today;
    private ImageView edit,remove;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_profile_, container, false);

        realm.init(getActivity());

        RealmConfiguration configuration = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(configuration);


        eventListView = (RecyclerView) view.findViewById(R.id.AlleventlistView);
        today = (TextView) view.findViewById(R.id.prof_today);
        edit = view.findViewById(R.id.edit);
        remove = view.findViewById(R.id.remove);


        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        today.setText(day+"-"+MONTHS[month]+"-"+year);


        // here we have created new array list and added data to it.

        cardArrayList = new ArrayList<>();
        RealmResults<home_cardModel> results = realm.where(home_cardModel.class).findAll();

        for (home_cardModel p : results) {
            cardArrayList.add(p);
        }


        // we are initializing our adapter class and passing our arraylist to it.
        com.example.eventtext.profile_card_adapter cardAdapter = new com.example.eventtext.profile_card_adapter(getActivity(), cardArrayList);

        // below line is for setting a layout manager for our recycler view.
        // here we are creating vertical list so we will provide orientation as vertical
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);

        // in below two lines we are setting layoutmanager and adapter to our recycler view.
        eventListView.setLayoutManager(linearLayoutManager);
        eventListView.setAdapter(cardAdapter);

        cardAdapter.setOnItemClickListener(new profile_card_adapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
//                String c = cardArrayList.get(position).getCard_title();
//                Edit_Fragment frag = new Edit_Fragment();
//                FragmentManager fragmentManager = getFragmentManager();
//                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//                Bundle arguments = new Bundle();
//                arguments.putString("key",cardArrayList.get(position).getCard_date()+cardArrayList.get(position).getCard_time());
//                frag.setArguments(arguments);
//                fragmentTransaction.replace(R.id.pro_frag, frag);
//                fragmentTransaction.commit();

            }

            @Override
            public void onDeleteClick(int position) {

                deleteRecord(cardArrayList.get(position).getCard_date()+cardArrayList.get(position).getCard_time());
                cardArrayList.remove(position);
                cardAdapter.notifyItemRemoved(position);

            }

            @Override
            public void onEditClick(int position) {
                Edit_Fragment frag = new Edit_Fragment();
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                Bundle arguments = new Bundle();
                arguments.putString("key",cardArrayList.get(position).getCard_date()+cardArrayList.get(position).getCard_time());
                frag.setArguments(arguments);
                fragmentTransaction.replace(R.id.pro_frag, frag);
                fragmentTransaction.commit();
            }
        });



        return view;
    }
    public void deleteRecord(String eventId){
        RealmResults<home_cardModel> results = realm.where(home_cardModel.class).equalTo("event_Id", eventId).findAll();
        realm.beginTransaction();
        results.deleteAllFromRealm();
        realm.commitTransaction();


    }
}
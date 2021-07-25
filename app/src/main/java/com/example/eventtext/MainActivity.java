package com.example.eventtext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import com.ismaeldivita.chipnavigation.ChipNavigationBar;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    Realm realm;
    ArrayList<home_cardModel> mainRealm;
    ChipNavigationBar chipNavigationBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm.init(this);
        RealmConfiguration configuration = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(configuration);
        if( getApplicationContext().checkSelfPermission( Manifest.permission.READ_CONTACTS ) != PackageManager.PERMISSION_GRANTED )
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);



        home_cardModel savedEvent = (home_cardModel) getIntent().getSerializableExtra("event");


        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        chipNavigationBar.setItemSelected(R.id.bottom_nav_home,true);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new Home_Fragment()).commit();
        bottomMenu();
    }

    private void bottomMenu() {
        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i){
                    case R.id.bottom_nav_home:

                        fragment = new Home_Fragment();
                        break;
                    case R.id.bottom_nav_add:

                        fragment = new Add_Fragment();
                        break;
                    case R.id.bottom_nav_profile:
                        fragment = new Profile_Fragment();
                        break;


                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,fragment).commit();
            }
        });

    }






}
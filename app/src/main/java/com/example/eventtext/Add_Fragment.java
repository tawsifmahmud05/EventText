package com.example.eventtext;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.icu.lang.UProperty.NAME;
import static io.realm.Realm.getApplicationContext;


public class Add_Fragment extends Fragment {
    Realm realm;

    private TextView today, date, time;
    private EditText title, number, message;
    private ImageView contact;


    private Button save;

    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private DatePickerDialog.OnDateSetListener mnDataSetListener;


    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_add_, container, false);


        Realm.init(getActivity());
        RealmConfiguration configuration = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(configuration);


        today = (TextView) view.findViewById(R.id.add_today);

        title = (EditText) view.findViewById(R.id.add_title);
        date = (TextView) view.findViewById(R.id.add_date);
        time = (TextView) view.findViewById(R.id.add_time);
        number = (EditText) view.findViewById(R.id.add_number);
        message = (EditText) view.findViewById(R.id.add_message);
        contact = (ImageView) view.findViewById(R.id.add_contact);

        save = (Button) view.findViewById(R.id.add_save_btn);

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        today.setText(day+"-"+MONTHS[month]+"-"+year);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, mnDataSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
            }
        });

        mnDataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                date.setText(dayOfMonth + "-" + MONTHS[month] + "-" + year);
            }
        };

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        time.setText(selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Pick Time");
                mTimePicker.show();
            }


        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveEvent();
            }
        });




        return view;
    }

    public void SaveEvent() {

        if(TextUtils.isEmpty(title.getText()) || TextUtils.isEmpty(date.getText()) ||TextUtils.isEmpty(time.getText()) || TextUtils.isEmpty(number.getText()) || TextUtils.isEmpty(message.getText())) {

            if(TextUtils.isEmpty(title.getText())){
                Toast.makeText(getActivity(), "Title missing", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(date.getText())){
                Toast.makeText(getActivity(), "Choose a Date", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(time.getText())){
                Toast.makeText(getActivity(), "Pick a time", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(message.getText())){
                Toast.makeText(getActivity(), "No message given", Toast.LENGTH_SHORT).show();
            }



        }
        else{
            realm.beginTransaction();
            home_cardModel p = realm.createObject(home_cardModel.class);
            p.setCard_title(title.getText().toString());
            p.setCard_date(date.getText().toString());
            p.setCard_time(time.getText().toString());
            p.setCard_number(number.getText().toString());
            p.setCard_message(message.getText().toString());
            p.setEvent_Id(date.getText().toString() + time.getText().toString());
            realm.commitTransaction();



        }



    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_PICK_CONTACTS && resultCode == RESULT_OK) {
            Log.d(TAG, "Response: " + data.toString());
            uriContact = data.getData();

//            retrieveContactName();
            retrieveContactNumber();
//            retrieveContactPhoto();

        }
    }

    private void retrieveContactNumber() {
        String contactNumber = null;

        // getting contacts ID
        Cursor cursorID = getActivity().getContentResolver().query(uriContact,
                new String[]{ContactsContract.Contacts._ID},
                null, null, null);

        if (cursorID.moveToFirst()) {

            contactID = cursorID.getString(cursorID.getColumnIndex(ContactsContract.Contacts._ID));
        }

        cursorID.close();

        Log.d(TAG, "Contact ID: " + contactID);

        // Using the contact ID now we will get contact phone number
        Cursor cursorPhone = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},

                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ? AND " +
                        ContactsContract.CommonDataKinds.Phone.TYPE + " = " +
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE,

                new String[]{contactID},
                null);

        if (cursorPhone.moveToFirst()) {
            contactNumber = cursorPhone.getString(cursorPhone.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        }

        cursorPhone.close();

        number.setText(contactNumber);
    }
}

package com.example.eventtext;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;
import static io.realm.Realm.getApplicationContext;


public class Edit_Fragment extends Fragment {



    Realm realm;
    private String eventId ;

    //declare globally, this can be any int
    public final int PICK_CONTACT = 2015;

    public static final String[] MONTHS = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    private TextView today, date, time;
    private EditText title, number, message;
    private ImageView contact;

    private Button edit,delete;

    private DatePickerDialog.OnDateSetListener mnDataSetListener;

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;

    private int eventalarmid,dayid,timeid;

    Calendar cal = Calendar.getInstance();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_edit_, container, false);

        realm.init(getActivity());

        RealmConfiguration configuration = new RealmConfiguration
                .Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        realm = Realm.getInstance(configuration);


        today = (TextView) view.findViewById(R.id.edit_today);

        title = (EditText) view.findViewById(R.id.edit_title);
        date = (TextView) view.findViewById(R.id.edit_date);
        time = (TextView) view.findViewById(R.id.edit_time);
        number = (EditText) view.findViewById(R.id.edit_number);
        message = (EditText) view.findViewById(R.id.edit_message);
        contact = (ImageView) view.findViewById(R.id.edit_contact);

        edit = (Button) view.findViewById(R.id.edit_btn);


        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        today.setText(day+"-"+MONTHS[month]+"-"+year);



        Bundle bundle = getArguments();
        if (bundle != null) {
            eventId = bundle.getString("key");

        }

        loaded();

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar datecal = Calendar.getInstance();
                int year = datecal.get(Calendar.YEAR);
                int month = datecal.get(Calendar.MONTH);
                int day = datecal.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_MinWidth, mnDataSetListener, year, month, day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis()-10000);
                dialog.show();
            }
        });

        mnDataSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

                date.setText(dayOfMonth + "-" + MONTHS[month] + "-" + year);
                dayid = dayOfMonth+month+year;
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
                        if(TextUtils.isEmpty(date.getText().toString())){
                            Toast.makeText(getActivity(), "Set your day first", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy");
                            Calendar datetime = Calendar.getInstance();
                            try {
                                datetime.setTime(sdf.parse(date.getText().toString()));
                                datetime.set(Calendar.HOUR_OF_DAY, selectedHour);
                                datetime.set(Calendar.MINUTE, selectedMinute);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if(datetime.getTimeInMillis() > System.currentTimeMillis()+10000) {
                                time.setText(selectedHour + ":" + selectedMinute);
                                timeid = selectedHour + selectedMinute;
                            }
                            else{
                                Toast.makeText(getActivity(), "Invalid Time", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Pick Time");
                mTimePicker.show();
            }


        });



        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
                startActivityForResult(i, PICK_CONTACT);
            }

        });


        edit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                RealmResults<home_cardModel> search = realm.where(home_cardModel.class).equalTo("event_Id", date.getText().toString() + time.getText().toString()).findAll();
                if (search.isEmpty()) {
                    if (TextUtils.isEmpty(title.getText()) || TextUtils.isEmpty(date.getText()) || TextUtils.isEmpty(time.getText()) || TextUtils.isEmpty(number.getText()) || TextUtils.isEmpty(message.getText())) {
                        Toast.makeText(getApplicationContext(), "Content Missing", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        eventalarmid = dayid + timeid;
                        updateRecord();
                    }



                }
                else {
                    Toast.makeText(getApplicationContext(), "Activity time is occupied", Toast.LENGTH_SHORT).show();
                }

            }
        });





        return view;
    }

    private void loaded() {
        home_cardModel p = realm.where(home_cardModel.class).equalTo("event_Id", eventId).findFirst();

            title.setText(p.getCard_title());
            date.setText(p.getCard_date());
            time.setText(p.getCard_time());
            number.setText(p.getCard_number());
            message.setText(p.getCard_message());

    }

    public void updateRecord(){

            RealmResults<home_cardModel> results = realm.where(home_cardModel.class).equalTo("event_Id", eventId).findAll();
            if (results.isEmpty()) {
            } else {


                SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy-HH:mm");
                Calendar Alcal = Calendar.getInstance();
                try {
                    Alcal.setTime(sdf.parse(date.getText().toString() + "-" + time.getText().toString()));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                String unique = date.getText().toString() + time.getText().toString();
                String sms =    message.getText().toString();
                String slumber = number.getText().toString();

                home_cardModel p_id = realm.where(home_cardModel.class).equalTo("event_Id", eventId).findFirst();
                int id = p_id.getAlarm_id();

                updateAlarm(Alcal,id,unique,slumber,sms);


                realm.beginTransaction();

                for (home_cardModel p : results) {
                    p.setCard_title(title.getText().toString());
                    p.setCard_date(date.getText().toString());
                    p.setCard_time(time.getText().toString());
                    p.setCard_number(number.getText().toString());
                    p.setCard_message(message.getText().toString());
                    p.setEvent_Id(date.getText().toString() + time.getText().toString());
                    p.setAlarm_id(p.getAlarm_id());
                }
                realm.commitTransaction();


                Toast.makeText(getActivity(), "Successfully Updated Event", Toast.LENGTH_SHORT).show();


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

    private void updateAlarm(Calendar c, int Id,String unique,String smsnumber,String sms) {
        int requestcode = 0;

        cancelAlarm(Id);

        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), MyReceiver.class);
        Bundle b = new Bundle();
        b.putString("unique", unique);
        b.putString("number", smsnumber);
        b.putString("sms", sms);
        intent.putExtras(b);
//        startActivity(intent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestcode + eventalarmid , intent, PendingIntent.FLAG_ONE_SHOT);
//        alarmId++;
//        if (c.before(Calendar.getInstance())) {
//            c.add(Calendar.DATE, 1);
//        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);


    }
    private void cancelAlarm(int alarmId) {
        int requestId=0;
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), requestId+alarmId, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.cancel(pendingIntent);

    }
}
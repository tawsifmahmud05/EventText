package com.example.eventtext;

import android.Manifest;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ClipDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.os.Parcelable;
import android.provider.ContactsContract;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.telephony.TelephonyManager;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;
import static android.content.ContentValues.TAG;
import static android.content.Context.JOB_SCHEDULER_SERVICE;
import static android.icu.lang.UProperty.NAME;
import static io.realm.Realm.getApplicationContext;
import static java.lang.Integer.parseInt;


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
    private int eventalarmid,dayid,timeid;







    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        View view = inflater.inflate(R.layout.fragment_add_, container, false);

//        if( getApplicationContext().checkSelfPermission( Manifest.permission.READ_CONTACTS ) != PackageManager.PERMISSION_GRANTED ) {
//            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_CONTACTS}, 1);
//        }
//        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.SEND_SMS)) {
//            } else {
//                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.SEND_SMS}, MY_PERMISSIONS_REQUEST_SEND_SMS);
//            }
//        }





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

        today.setText(day + "-" + MONTHS[month] + "-" + year);

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance();
                int year = cal.get(Calendar.YEAR);
                int month = cal.get(Calendar.MONTH);
                int day = cal.get(Calendar.DAY_OF_MONTH);

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
                time.setText("");
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
                startActivityForResult(new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI), REQUEST_CODE_PICK_CONTACTS);
            }
        });


        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RealmResults<home_cardModel> search = realm.where(home_cardModel.class).equalTo("event_Id", date.getText().toString() + time.getText().toString()).findAll();
                if (search.isEmpty()) {
                    if (TextUtils.isEmpty(title.getText()) || TextUtils.isEmpty(date.getText()) || TextUtils.isEmpty(time.getText()) || TextUtils.isEmpty(number.getText()) || TextUtils.isEmpty(message.getText())) {
                        Toast.makeText(getApplicationContext(), "Content Missing", Toast.LENGTH_SHORT).show();
                    }
                    else{
                            eventalarmid = dayid + timeid;
                            SaveEvent();
                        }



                }
                else {
                    Toast.makeText(getApplicationContext(), "Event time is occupied", Toast.LENGTH_SHORT).show();
                }

            }


        });


        return view;
    }

    public void SaveEvent() {


            realm.beginTransaction();
            home_cardModel p = realm.createObject(home_cardModel.class);
            p.setCard_title(title.getText().toString());
            p.setCard_date(date.getText().toString());
            p.setCard_time(time.getText().toString());
            p.setCard_number(number.getText().toString());
            p.setCard_message(message.getText().toString());
            p.setEvent_Id(date.getText().toString() + time.getText().toString());
            p.setAlarm_id(eventalarmid);
            realm.commitTransaction();



            SimpleDateFormat sdf = new SimpleDateFormat("dd-MMM-yy-HH:mm");
            Calendar Alcal = Calendar.getInstance();
            try {
                Alcal.setTime(sdf.parse(date.getText().toString() + "-" + time.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String unique = date.getText().toString() + time.getText().toString();
            String sms = message.getText().toString();
            String slumber = number.getText().toString();

            setAlarm(Alcal, eventalarmid, unique, slumber, sms);
            Toast.makeText(getActivity(), "Event added", Toast.LENGTH_SHORT).show();





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

    private void setAlarm(Calendar c, int Id,String unique,String smsnumber,String sms) {
        int requestcode = 0;
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), MyReceiver.class);
        Bundle b = new Bundle();
        b.putString("unique", unique);
        b.putString("number", smsnumber);
        b.putString("sms", sms);
        intent.putExtras(b);
//        startActivity(intent);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), requestcode + Id, intent, PendingIntent.FLAG_ONE_SHOT);

//        if (c.before(Calendar.getInstance())) {
//            c.add(Calendar.DATE, 1);
//        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);


    }



}





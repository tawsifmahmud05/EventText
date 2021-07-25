package com.example.eventtext;

import android.content.Intent;
import android.database.Cursor;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static android.app.Activity.RESULT_OK;


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


    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int REQUEST_CODE_PICK_CONTACTS = 1;
    private Uri uriContact;
    private String contactID;

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
        delete = (Button) view.findViewById(R.id.dlt_btn);

        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH);
        int day = cal.get(Calendar.DAY_OF_MONTH);

        today.setText(day+"-"+MONTHS[month]+"-"+year);



        Bundle bundle = getArguments();
        if (bundle != null) {
            eventId = bundle.getString("key");

        }

        loaded();



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
                updateRecord();
            }
        });

        delete.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                deleteRecord();
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
        else {
            RealmResults<home_cardModel> results = realm.where(home_cardModel.class).equalTo("event_Id", eventId).findAll();
            if (results.isEmpty()) {
            } else {
                realm.beginTransaction();

                for (home_cardModel p : results) {
                    p.setCard_title(title.getText().toString());
                    p.setCard_date(date.getText().toString());
                    p.setCard_time(time.getText().toString());
                    p.setCard_number(number.getText().toString());
                    p.setCard_message(message.getText().toString());
                    p.setEvent_Id(date.getText().toString() + time.getText().toString());
                }
                realm.commitTransaction();
                Toast.makeText(getActivity(), "Successfully Updated", Toast.LENGTH_SHORT).show();


            }
        }

    }



    public void deleteRecord(){
            RealmResults<home_cardModel> results = realm.where(home_cardModel.class).equalTo("event_Id", eventId).findAll();
            realm.beginTransaction();
            results.deleteAllFromRealm();
            realm.commitTransaction();
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
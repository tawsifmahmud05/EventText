package com.example.eventtext;

import java.util.Date;
import java.util.GregorianCalendar;

import io.realm.RealmObject;

public class home_cardModel extends RealmObject {


    private String card_title;
    private String card_number;
    private String card_message;
    private String card_date;
    private String card_time;
    private String event_Id;
    private int alarm_id;


    public home_cardModel(){

    }

    public home_cardModel(String card_title, String card_number, String card_message, String card_date, String card_time, String event_Id,int alarm_id) {
        this.card_title = card_title;
        this.card_number = card_number;
        this.card_message = card_message;
        this.card_date = card_date;
        this.card_time = card_time;
        this.event_Id = event_Id;
        this.alarm_id = alarm_id;
    }

    public int getAlarm_id() {
        return alarm_id;
    }

    public void setAlarm_id(int alarm_id) {
        this.alarm_id = alarm_id;
    }

    public String getEvent_Id() {
        return event_Id;
    }

    public void setEvent_Id(String event_Id) {
        this.event_Id = event_Id;
    }

    public String getCard_date() {
        return card_date;
    }

    public void setCard_date(String card_date) {
        this.card_date = card_date;
    }

    public String getCard_time() {
        return card_time;
    }

    public void setCard_time(String card_time) {
        this.card_time = card_time;
    }

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getCard_title() {
        return card_title;
    }

    public void setCard_title(String card_title) {
        this.card_title = card_title;
    }


    public String getCard_message() {
        return card_message;
    }

    public void setCard_message(String card_message) {
        this.card_message = card_message;
    }


}
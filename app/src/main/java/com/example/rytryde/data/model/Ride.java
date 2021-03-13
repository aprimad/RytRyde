package com.example.rytryde.data.model;

import java.util.ArrayList;

public class Ride {
    private int id;
    private int parent_id;
    private int user_id;
    private String start_time;
    private String pick_up_address;
    private String drop_off_address;
    private String pick_up_latitude;
    private String pick_up_longitude;
    private String drop_off_latitude;
    private String drop_off_longitude;
    private String waiting_time;
    private String ride_km;
    private int ride_min;
    private String cancel_reason;
    private String ride_type;
    private String status;
    private String is_broadcast;
    private int ride_members_count;
    private int ride_members_request_count;
    private String ride_date;
    private String ride_time;
    private String ride_types;
    private String type;
    private ArrayList<String> ride_members;
    private ArrayList<String> ride_members_request;

    public int getId() {
        return id;
    }

    public int getParent_id() {
        return parent_id;
    }

    public int getUser_id() {
        return user_id;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getPick_up_address() {
        return pick_up_address;
    }

    public String getDrop_off_address() {
        return drop_off_address;
    }

    public String getPick_up_latitude() {
        return pick_up_latitude;
    }

    public String getPick_up_longitude() {
        return pick_up_longitude;
    }

    public String getDrop_off_latitude() {
        return drop_off_latitude;
    }

    public String getDrop_off_longitude() {
        return drop_off_longitude;
    }

    public String getWaiting_time() {
        return waiting_time;
    }

    public String getRide_km() {
        return ride_km;
    }

    public int getRide_min() {
        return ride_min;
    }

    public String getCancel_reason() {
        return cancel_reason;
    }

    public String getRide_type() {
        return ride_type;
    }

    public String getStatus() {
        return status;
    }

    public String getIs_broadcast() {
        return is_broadcast;
    }

    public int getRide_members_count() {
        return ride_members_count;
    }

    public int getRide_members_request_count() {
        return ride_members_request_count;
    }

    public String getRide_date() {
        return ride_date;
    }

    public String getRide_time() {
        return ride_time;
    }

    public String getRide_types() {
        return ride_types;
    }

    public String getType() {
        return type;
    }

    public ArrayList<String> getRide_members() {
        return ride_members;
    }

    public ArrayList<String> getRide_members_request() {
        return ride_members_request;
    }


}

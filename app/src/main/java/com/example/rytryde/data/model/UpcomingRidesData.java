package com.example.rytryde.data.model;

import java.util.ArrayList;
import java.util.List;

public class UpcomingRidesData {
    private boolean success;
    private List<Ride> data;
    private String carbon_saving;
    private int unread_notification;
    private String terms_status;
    private String privacy_status;
    private String message;

    public List<Ride> getData() {
        return data;
    }

    public void setData(ArrayList<Ride> data) {
        this.data = data;
    }

    public String getCarbon_saving() {
        return carbon_saving;
    }

    public void setCarbon_saving(String carbon_saving) {
        this.carbon_saving = carbon_saving;
    }

    public int getUnread_notification() {
        return unread_notification;
    }

    public void setUnread_notification(int unread_notification) {
        this.unread_notification = unread_notification;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}

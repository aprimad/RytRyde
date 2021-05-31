package com.example.rytryde.data.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyContacts {

    private String id;
    private String user_id;
    private String last_name;
    private String number;
    private String userId;
    private String contactId;
    private String first_name;
    private String status;
    private String app_user_id;
    private String photoURI;

    public MyContacts(String _id, String mfullName, String mnumber, String _photoURI) {
        this.id = _id;
        this.fullName = mfullName;
        this.number = mnumber;
        this.photoURI = _photoURI;
    }

    public static List<MyContacts> reaarangeList(List<MyContacts> rearrangecontactsList) {
        List<MyContacts> contactsList = new ArrayList<>(rearrangecontactsList);
        Collections.sort(contactsList, (o1, o2) -> {
            if (o1.getAppUserId() == null) {
                return (o2.getAppUserId() == null) ? 0 : 1;
            }
            if (o2.getAppUserId() == null) {
                return -1;
            }
            return o2.getAppUserId().compareToIgnoreCase(o1.getAppUserId());
        });
        return contactsList;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    private String fullName;

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    private String isTrustedContact;
    private String contactType;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getApp_user_id() {
        return app_user_id;
    }

    public void setApp_user_id(String app_user_id) {
        this.app_user_id = app_user_id;
    }

    public String getPhotoURI() {
        return photoURI;
    }

    public void setPhotoURI(String photoURI) {
        this.photoURI = photoURI;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getFullName() {
        return first_name + " " + last_name;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getIsTrustedContact() {
        return isTrustedContact;
    }

    public void setIsTrustedContact(String isTrustedContact) {
        this.isTrustedContact = isTrustedContact;
    }

    public String getContactType() {
        return contactType;
    }

    public void setContactType(String contactType) {
        this.contactType = contactType;
    }

    public String getAppUserId() {
        return app_user_id;
    }

    public void setAppUserId(String appUserId) {
        this.app_user_id = appUserId;
    }


}

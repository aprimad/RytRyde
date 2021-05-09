package com.example.rytryde.data.model;

public class PlaceAutocomplete {
    String placeID;
    String primaryText;
    String secondaryText;

    public PlaceAutocomplete(String mPlaceID, String mPrimaryText, String mFullText) {
        placeID = mPlaceID;
        primaryText = mPrimaryText;
        secondaryText = mFullText;
    }

    public String getPlaceID() {
        return placeID;
    }

    public String getPrimaryText() {
        return primaryText;
    }

    public String getSecondaryText() {
        return secondaryText;
    }
}

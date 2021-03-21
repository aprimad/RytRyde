package com.example.rytryde.service.http.cms;

import okhttp3.Response;

public interface ICmsService {
    Response termsAndConditions();

    Response privacyPolicy();

    Response aboutUs();
}

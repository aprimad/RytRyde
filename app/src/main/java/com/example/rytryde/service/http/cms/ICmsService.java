package com.example.rytryde.service.http.cms;

import okhttp3.Response;

public interface ICmsService {
    Response termsAndConditions();

    Response privacyPolicy();

    Response aboutUs();

    Response faq(int page, int limit);

    Response loadNextFAQPage(String nextURL);

    Response getSubjectList();

    Response contactUs(int subjectID, String message);
}

package com.example.rytryde.service.http.account;

import okhttp3.Response;

public interface IAccountService {

    Response login(String email, String password);

    Response sendOTP(String firstName, String lastName, String email, String dialCode, String phoneNumber);

    Response verifyOTP(String otpType, String OTP, String phoneNumber);

    Response resendOTP(String otpType, String phoneNumber);

    Response signup(String firstName, String lastName, String otpCode, String email, String password, String dialCode, String phoneNumber);

    Response changePassword(String password, String confirmPassword);

    Response updateProfile(String firstName, String lastName, String email, String country_code, String phoneNumber, String gender, String dob, String searchRadius, String mediaID);

    //Response uploadMedia()

}

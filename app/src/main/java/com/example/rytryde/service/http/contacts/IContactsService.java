package com.example.rytryde.service.http.contacts;

import com.example.rytryde.data.model.Contacts;
import com.example.rytryde.data.model.SyncContact;

import java.util.List;

import okhttp3.Response;

public interface IContactsService {

    Response syncContacts(List<SyncContact> contactsList);

    Response myContacts();

    Response addContacts(List<Contacts> contactsList);
}

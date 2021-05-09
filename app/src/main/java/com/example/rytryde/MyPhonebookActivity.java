package com.example.rytryde;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cursoradapter.widget.SimpleCursorAdapter;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.rytryde.service.app.AppService;

public class MyPhonebookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    // Defines the id of the loader for later reference
    public static final int CONTACT_LOADER_ID = 78;
    private static final int PERMISSIONS_REQUEST_ACCESS_READ_CONTACTS = 1;
    private SimpleCursorAdapter adapter;
    private ListView contactRV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_phonebook);

        contactRV = findViewById(R.id.rv_contacts);

        if (AppService.getContactPermission()) {
            setupCursorAdapter();

            contactRV.setAdapter(adapter);

            LoaderManager.getInstance(this).initLoader(0, null, this);
        }


    }


    // Create simple cursor adapter to connect the cursor dataset we load with a ListView
    private void setupCursorAdapter() {

        // Column data from cursor to bind views from
        String[] uiBindFrom = {ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI};
        // View IDs which will have the respective column data inserted
        int[] uiBindTo = {R.id.tv_contact_title, R.id.contact_image};
        // Create the simple cursor adapter to use for our list
        // specifying the template to inflate (item_contact),
        adapter = new SimpleCursorAdapter(
                this, R.layout.my_phonebook_items,
                null, uiBindFrom, uiBindTo,
                0);
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id,
                                         @Nullable Bundle args) {
        // Define the columns to retrieve
        String[] projectionFields = new String[]{ContactsContract.Contacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_URI};
        // Construct the loader
        CursorLoader cursorLoader = new CursorLoader(MyPhonebookActivity.this,
                ContactsContract.Contacts.CONTENT_URI, // URI
                projectionFields, // projection fields
                null, // the selection criteria
                null, // the selection args
                null // the sort order
        );
        // Return the loader for use
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader,
                               Cursor data) {
        adapter.swapCursor(data);
    }

    // This method is triggered when the loader is being reset
    // and the loader data is no longer available. Called if the data
    // in the provider changes and the Cursor becomes stale.
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        // Clear the Cursor we were using with another call to the swapCursor()
        adapter.swapCursor(null);

    }
}
package com.example.contactsbackendless;

import android.app.Application;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;

import java.util.List;


public class ApplicationClass extends Application {

    public static final String APPLICATION_ID = "DAF80BD9-EF6F-6D2D-FFFC-004C3AC42200";
    public static final String API_KEY = "84CAC6D2-88F2-4ACF-933C-34FAD74A417E";
    public static final String SERVER_URL = "https://api.backendless.com";

    public static BackendlessUser user;    //for every contact created a user can be connected to that contact created by his own account.

    public static List<Contact> contacts;

    @Override
    public void onCreate() {
        super.onCreate();

        //initialisation.
        Backendless.setUrl( SERVER_URL );
        Backendless.initApp( getApplicationContext(),
                APPLICATION_ID,
                API_KEY );

    }
}

package com.example.zhy_horizontalscrollview;

import android.app.Application;

import com.parse.Parse;

/**
 * Created by GelicaNONO on 2015/10/29.
 */
public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "0ImTnQre1fBacLJpBcc7SmRHfkq3t4RrchbzNY8b"
                , "TF0Hgv3vMHQbxg9uEUcRnspf4keRMkmljnxCxhqA");
    }
}

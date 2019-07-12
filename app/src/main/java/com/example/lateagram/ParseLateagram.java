package com.example.lateagram;

import android.app.Application;

import com.example.lateagram.models.LateComment;
import com.example.lateagram.models.LatePost;
import com.parse.Parse;
import com.parse.ParseObject;

public class ParseLateagram extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(LatePost.class);
        ParseObject.registerSubclass(LateComment.class);
        final Parse.Configuration configuration = new Parse.Configuration.Builder(this)
                .applicationId("farfromhome-great") // should correspond to APP_ID env variable
                .clientKey("strangerthins3wasok")  // set explicitly unless clientKey is explicitly configured on Parse server
                .server("https://ayyolmao-fbu-lateagram.herokuapp.com/parse/").build();
        Parse.initialize(configuration);


    }
}

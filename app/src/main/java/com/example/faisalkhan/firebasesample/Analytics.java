package com.example.faisalkhan.firebasesample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;

/**
 * Activity class for Event Analytics.
 *
 * For triggering event to firebase first you need to configure firebase in your project.
 * To know how you can configure Firebase follow link :- https://firebase.google.com/docs/android/setup
 * Or you can flow my doc as well link :- http://firebasesample.blogspot.in/
 *
 * In firebase you can trigger two kinds of events Custom and predefine.
 * There are more then 500 predefine events in firebase that you can use to track events
 * And Custom are those those are defines by user.
 *
 * For understanding how we can send custom and predefine events please have a look on
 * sendPredefinedEvent and sendCustomEvent  methods.
 *
 * For more details for Events follow this link :- https://firebase.google.com/docs/analytics/android/start/
 *
 * Note :- Value of event will only visible n console when more then 10 user trigger that event. else it will remain empty.
 * And events will be update after 4 to 5 hours.
 *
 * @author Faisal Khan
 */
public class Analytics extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        //setting action bar
        getActionBar().setTitle("Authentication");

        //getting firebase analytics instance to triggering events
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }

    /**
     * Method to send predefine method
     * @param view root view
     */
    public void sendPredefinedEvent(View view) {
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.LEVEL, "level 2");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LEVEL_UP, bundle);
    }

    /**
     * Method to send Custom event method
     * @param view root view
     */
    public void sendCustomEvent(View view) {
        Bundle bundle = new Bundle();
        bundle.putString("custom_param", "custom value");
        mFirebaseAnalytics.logEvent("custom_event", bundle);

        Bundle bundle1 = new Bundle();
        bundle1.putString("name", "faisal");
        mFirebaseAnalytics.logEvent("faisal", bundle);
    }
}

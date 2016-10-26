package com.example.faisalkhan.firebasesample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.analytics.FirebaseAnalytics;

public class Analytics extends AppCompatActivity {

    private FirebaseAnalytics mFirebaseAnalytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }

    public void sendPredefinedEvent(View view){
        Bundle bundle = new Bundle();
        bundle.putString(FirebaseAnalytics.Param.LEVEL, "level 2");
        mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.LEVEL_UP, bundle);
    }

    public void sendCustomEvent(View view){
        Bundle bundle = new Bundle();
        bundle.putString("custom_param", "custom value");
        mFirebaseAnalytics.logEvent("custom_event", bundle);

        Bundle bundle1 = new Bundle();
        bundle1.putString("name", "faisal");
        mFirebaseAnalytics.logEvent("faisal", bundle);
    }
}

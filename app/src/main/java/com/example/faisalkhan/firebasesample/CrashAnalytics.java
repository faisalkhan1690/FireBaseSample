package com.example.faisalkhan.firebasesample;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

/**
 * Activity class for Firebase Crash analytics
 *
 * For using Firebase Crash analytics you just need to setup firebase in your project. this will start working automatically.
 * To know how you can configure Firebase follow link :- https://firebase.google.com/docs/android/setup
 * Or you can flow my doc as well link :- http://firebasesample.blogspot.in/
 *
 * @author Faisal Khan
 */
public class CrashAnalytics extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crash_analytics);

        //setting action bar
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        toolbar.setTitle("CrashAnalytics");

        Button btnCrash=(Button)findViewById(R.id.btn_crash);
        btnCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              throw new NullPointerException("Test null pointer");
            }
        });
    }
}

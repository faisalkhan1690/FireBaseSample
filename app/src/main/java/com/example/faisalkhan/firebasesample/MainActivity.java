package com.example.faisalkhan.firebasesample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * MainActivity class is a activity class for displaying list of Firebase features.
 *
 * For more details go on activity class
 *
 * @author Faisal Khan
 */
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        List<String> listData=new ArrayList<>();
        listData.add("Analytics");
        listData.add("Authentication");
        listData.add("DataBase");
        listData.add("Storage");
        listData.add("Notifications");
        ListView lvFireBase=(ListView)findViewById(R.id.lv_fire_base);
        lvFireBase.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listData));

        lvFireBase.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity(new Intent(MainActivity.this,Analytics.class));
                        break;
                    case 1:
                        startActivity(new Intent(MainActivity.this,Authentication.class));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this,DataBase.class));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this,StorageActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(MainActivity.this,Notifications.class));
                        break;
                }
            }
        });
    }

}

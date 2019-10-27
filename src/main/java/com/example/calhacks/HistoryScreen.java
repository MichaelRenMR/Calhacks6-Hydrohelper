package com.example.calhacks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class HistoryScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_screen);
        Intent intent = getIntent();
        int[] daysv = intent.getIntArrayExtra("days_values");
        String[] days = intent.getStringArrayExtra("days");
        ((TextView) findViewById(R.id.d1)).setText(days[0]);
        ((TextView) findViewById(R.id.d2)).setText(days[1]);
        ((TextView) findViewById(R.id.d3)).setText(days[2]);
        ((TextView) findViewById(R.id.d4)).setText(days[3]);
        ((TextView) findViewById(R.id.d5)).setText(days[4]);
        ((TextView) findViewById(R.id.d6)).setText(days[5]);
        ((TextView) findViewById(R.id.d7)).setText(days[6]);
        ((TextView) findViewById(R.id.dv1)).setText(daysv[0]+" fl oz");
        ((TextView) findViewById(R.id.dv2)).setText(daysv[1]+" fl oz");
        ((TextView) findViewById(R.id.dv3)).setText(daysv[2]+" fl oz");
        ((TextView) findViewById(R.id.dv4)).setText(daysv[3]+" fl oz");
        ((TextView) findViewById(R.id.dv5)).setText(daysv[4]+" fl oz");
        ((TextView) findViewById(R.id.dv6)).setText(daysv[5]+" fl oz");
        ((TextView) findViewById(R.id.dv7)).setText(daysv[6]+" fl oz");
    }

    public void returnMain(View view) {
        finish();
    }
}

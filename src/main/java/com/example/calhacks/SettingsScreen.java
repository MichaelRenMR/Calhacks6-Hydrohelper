package com.example.calhacks;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SettingsScreen extends AppCompatActivity {
    static final int num_attr = 4;
    static final TextView[] inputs = new TextView[num_attr];
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_screen);
        Intent intent = getIntent();
        int[] saved_pref = intent.getIntArrayExtra("values");
        inputs[0] = ((TextView) findViewById(R.id.b0));
        inputs[1] = ((TextView) findViewById(R.id.b1));
        inputs[2] = ((TextView) findViewById(R.id.b2));
        inputs[3] = ((TextView) findViewById(R.id.b3));
        for(int i = 0; i < num_attr; i++) {
            inputs[i].setText(""+saved_pref[i]);
        }
    }

    public void returnMain(View view) {
        Intent output = new Intent();
        for(int i=0;i<num_attr;i++) {
            output.putExtra("b" + i, inputs[i].getText().toString());
        }
        setResult(RESULT_OK, output);
        finish();
    }

}

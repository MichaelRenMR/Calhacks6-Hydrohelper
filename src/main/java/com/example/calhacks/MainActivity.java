package com.example.calhacks;
//credits to https://stackoverflow.com/questions/13450406/how-to-receive-serial-data-using-android-bluetooth
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    TextView myLabel;
    static int[] user_info = new int[4], last_seven = new int[7];
    static String[] last_seven_days = new String[7];
    static final int num_attr = 4, num_days = 7;
    static final int UPDATE_SETTINGS = 69;
    static final int DISPLAY_HISTORY = 420;
    static int goal = -1, curr = 0, v1 = -1, v2 = -1;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mmSocket;
    BluetoothDevice mmDevice;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Thread workerThread;
    static boolean isConnected = false;
    byte[] readBuffer;
    int readBufferPosition;
    volatile boolean stopWorker;
    //weight, feet, inches, exercise
    TextView curramt, goalamt, progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        curramt = (TextView) findViewById(R.id.curramt);
        goalamt = (TextView) findViewById(R.id.goalamt);
        progress = (TextView) findViewById(R.id.progress);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        for (int i = 0; i < num_attr; i++) {
            user_info[i] = sharedPref.getInt("b" + i, 0);
        }
        for (int i = 0; i < num_days; i++) {
            Calendar c = Calendar.getInstance();
            c.add(Calendar.DATE, -(i+1));
            last_seven_days[i] = new SimpleDateFormat("MM/dd/yyyy").format(c.getTime());
            last_seven[i] =  sharedPref.getInt(new SimpleDateFormat("MM/dd/yyyy").format(c.getTime()), 0);
        }
        goal = sharedPref.getInt("goal", -1);
        curr = sharedPref.getInt(new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()), 0);
        update_progress();

        myLabel = (TextView) findViewById(R.id.label);
        TextView bt1 = (TextView) findViewById(R.id.bt1);
        TextView bt2 = (TextView) findViewById(R.id.bt2);

        bt1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myLabel.setText("Loading...");
                try {
                    openBT();
                } catch (IOException ex) {

                }
            }
        });
        bt2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                myLabel.setText("Loading...");
                try {
                    closeBT();
                } catch (IOException ex) {

                }
            }
        });
    }

    void openBT() throws IOException{
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mBluetoothAdapter == null){
            myLabel.setText("No bluetooth adapter available");
        }

        if(!mBluetoothAdapter.isEnabled()){
            Intent enableBluetooth = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBluetooth, 0);
        }

        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if(pairedDevices.size() > 0){
            for (BluetoothDevice device : pairedDevices){
                if (device.getName().equals("HC-06")){
                    mmDevice = device;
                    break;
                }
            }
            myLabel.setText("Bluetooth Device Paired but not Connected");
        }else{
            myLabel.setText("Pair Device First");
        }
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); //Standard SerialPortService ID
        if(mmDevice == null){
            myLabel.setText("Cannot find BT device");
        }else{
            mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
            mmSocket.connect();
            mmOutputStream = mmSocket.getOutputStream();
            mmInputStream = mmSocket.getInputStream();
            myLabel.setText("Bluetooth Opened");
            isConnected = true;
            beginListenForData();
        }
    }

    void beginListenForData(){
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable(){
            public void run(){
                while(!Thread.currentThread().isInterrupted() && !stopWorker){
                    try{
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0){
                            System.out.println("GOING");
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII").trim();

                                    readBufferPosition = 0;
                                    handler.post(new Runnable() {
                                        public void run() {
                                          //  System.out.println("VALUE" + data);
                                            try {
                                                int save = Integer.parseInt(data);
                                                if(save <= 25 && save != 0) {
                                                    v1 = v2;
                                                    v2 = Integer.parseInt(data);
                                                    //   System.out.println("START" + data + " " + v1 + " " + v2);
                                                    myLabel.setText(data);
                                                    if(v1 != -1) {
                                                        calc_amt_drank(v1, v2);
                                                    }
                                                }
                                            } catch (NumberFormatException e) {
                                              //  System.out.println(data+"ERROR");
                                            }
                                        }
                                    });
                                }else{
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex){
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();
    }

    void closeBT() throws IOException {
        myLabel.setText("Loading...");
        stopWorker = true;
        if (mmInputStream != null) {
            mmInputStream.close();
            mmSocket.close();
            myLabel.setText("Bluetooth Closed");
            isConnected = false;
        } else {
            myLabel.setText("Bluetooth Device was not connected");
        }
    }

    public void settings(View view){
        Intent intent = new Intent(this, SettingsScreen.class);
        intent.putExtra("values", user_info);
        startActivityForResult(intent, UPDATE_SETTINGS);
    }

    public void history(View view){
        Intent intent = new Intent(this, HistoryScreen.class);
        intent.putExtra("days", last_seven_days);
        intent.putExtra("days_values", last_seven);
        startActivityForResult(intent, DISPLAY_HISTORY);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        if (requestCode == UPDATE_SETTINGS){
            if (resultCode == RESULT_OK){
                SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                for(int i = 0; i < num_attr; i++){
                    user_info[i] = Integer.parseInt(data.getStringExtra("b"+i));
                    editor.putInt("b"+i, user_info[i]);
                }
                goal = ((user_info[0] * 2 / 3) + (user_info[3] * 2 / 5)) + 1;
                editor.putInt("goal", goal);
                editor.commit();
                update_progress();

            }
        }
    }

    void update_progress(){
        curramt.setText(curr + " fluid ounces");

        if(goal < 0){
            goalamt.setText("Update your settings");
            progress.setText("0%");
        } else {
            goalamt.setText("" + goal + " fluid ounces");
            progress.setText((curr * 100 / goal) + "%");
         //   ((ProgressBar) findViewById(R.id.progressBar)).setProgress((curr*100/goal), false);
            if(isConnected) {
                send_lcd();
            }
            if(curr >= goal){
                progress.setText("100%");
                SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
            }
        }
    }

    public void commit_curr(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(new SimpleDateFormat("MM/dd/yyyy").format(Calendar.getInstance().getTime()), curr);
        editor.commit();
    }

    public void update_curr2(int n){
        curr += n;
        commit_curr();
        update_progress();
    }

    public void calc_amt_drank(int v1, int v2){
        if(v2 > v1){
            update_curr2(2*v2 - 2*v1);
        }
    }

    public void send_lcd(){
        int i = Math.min(16, (int)(16.0 * curr / goal));
        try {
            mmOutputStream.write(i + 'a');
           // mmOutputStream.write(Integer.toString(5).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reset_curr(View view){
        curr  = 0;
        commit_curr();
        update_progress();
    }


    /*DEBUGGING STUFF
    activity_main.xml:
        <Button
        android:id="@+id/button2"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginBottom="16dp"
        android:layout_marginStart="220dp"
        android:onClick="update_curr"
        android:text="Increment"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toStartOf="parent" />

        <Button
        android:id="@+id/button3"
        android:layout_width="wrap_content"
        android:layout_height="51dp"
        android:layout_marginBottom="8dp"
        android:layout_marginStart="8dp"
        android:onClick="reset_curr"
        android:text="Reset"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintStart_toEndOf="@+id/button2" />



    MainActivity.java


        public void update_curr(View view){
            curr += 1;
            commit_curr();
            update_progress();
        }

     */


}

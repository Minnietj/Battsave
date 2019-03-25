package com.battsave;

//Uses Library! https://github.com/OmarAflak/Arduino-Library
//Library downloaded through Gradle

import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import java.util.concurrent.TimeUnit;

import android.view.View;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;


public class MainActivity extends AppCompatActivity {
    Arduino arduino;
    Handler handler;
    TextView myTextView;
    int Temperature = 50;
    double Voltage = 2.7;
    double Current = 1.2;
    double Percentage = 70;


    @Override
    protected void onStart() {
        super.onStart();
        //Code below acts as listener or interrupt handler. Runs in parallel with main loop.
        arduino.setArduinoListener(new ArduinoListener() {
            String buffer = new String();

            @Override
            public void onArduinoAttached(UsbDevice device) {
                arduino.open(device);
            }

            @Override
            public void onArduinoDetached() {
                // arduino detached from phone
            }

            @Override
            public void onArduinoMessage(byte[] bytes) {
                //This Code runs whenever phone receives a message from Arduino

                //Stores whatever was received in string "message"
                String message = new String(bytes);

                //Concatenate new message string into existing buffer
                buffer = buffer + message;


                //This code extracts data from buffer by detecting null characters (\0). When data is extracted, The buffer is cleared, then printed to a textview, or you can do whatever you want with it.
                if (buffer.contains("\0") && buffer.indexOf("\0") > 0 && buffer.length() > 0) {
                    //extract string
                    Log.d("on recieve", buffer); //checkpoint print to logcat
                    final String print = buffer.substring(0, buffer.indexOf("\0"));
                    Log.d("this is print", print); //checkpoint print to logcat
                    //clear buffer
                    buffer = "";
                    Log.d("buffer after deleted", buffer); //checkpoint print to logcat
                    //Print to textview
                    runOnUiThread(new Runnable() {
                        //this is in a new runnable because the Arduino listener does not have access to textviews.
                        @Override
                        public void run() {

                            myTextView.setText(print);
                            // Stuff that updates the UI

                        }
                    });
                }
            }

            @Override
            public void onArduinoOpened() {
                // you can start the communication
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //This is just a message to indicate that comms have been established between arduino and phone
                        myTextView.setText("yoo my dude its half working");
                        // Stuff that updates the UI

                    }
                });
                String str = "Hello Arduino !";
                arduino.send(str.getBytes());
            }

            @Override
            public void onUsbPermissionDenied() {
                // Permission denied, display popup then
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        //Sad message.
                        myTextView.setText("Permission DENIED bitchhhh");
                        // Stuff that updates the UI

                    }
                });
                arduino.reopen();
            }
        });


    }


    public void percentMe() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        //Are we charging/charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        //How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        //BATTERY PERCENTAGE
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        //Percentage = (level /  scale) * 100;
        Percentage = (level*100) / (scale);

        TextView randomView = (TextView)
                findViewById(R.id.textViewPERCENTAGE);

        String mytext = Double.toString(Percentage);
        randomView.setText(mytext);
    }

    public void voltMe() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        //Are we charging/charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        //How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        Voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        TextView randomView = (TextView)
                findViewById(R.id.textViewVOLTAGE);

        randomView.setText(Double.toString(Voltage));

    }

    public void ampMe() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        //Are we charging/charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        //How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        TextView randomView = (TextView)
                findViewById(R.id.textViewCURRENT);

        Current = BatteryManager.BATTERY_PROPERTY_CURRENT_NOW;

        randomView.setText(Double.toString(Current));
    }

    public void tempMe() {
        IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getApplicationContext().registerReceiver(null, ifilter);
        //Are we charging/charged?
        int status = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                status == BatteryManager.BATTERY_STATUS_FULL;
        //How are we charging?
        int chargePlug = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        boolean usbCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_USB;
        boolean acCharge = chargePlug == BatteryManager.BATTERY_PLUGGED_AC;

        TextView randomView = (TextView)
                findViewById(R.id.textViewTEMPERATURE);

        Temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        Temperature = Temperature/10;
        randomView.setText(Integer.toString(Temperature));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Do this stuff when the app is initially launched
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arduino = new Arduino(this); //make a new arduino object
        arduino.addVendorId(6790); //Vendor id lets phone know that connected thing is indeed an arduino
        myTextView = (TextView) findViewById(R.id.Textbox1); //associate myTextView object with actuial textview in layout
        handler = new Handler(); //idk


        //Code below acts as main loop
        final Runnable r = new Runnable() {
            public void run() {
                percentMe();
                voltMe();
                ampMe();
                tempMe();

                if (arduino.isOpened()) { //Only send a message if comms with the arduino have been established
                    //Format Message to send
                    String str = Temperature + "\n" + Voltage + "\n" + Current + "\n" + Percentage + "\n" + "ABCDEFG" + "\0";
                    arduino.send(str.getBytes());
                } else { //if arduino is not connected
                    myTextView.setText("I cant find an arduino :(");
                }

                handler.postDelayed(this, 2000);
            }
        };

        handler.postDelayed(r, 2000);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arduino.unsetArduinoListener();
        arduino.close();
    }
}

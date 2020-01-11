package com.battsave;

//Uses Library! https://github.com/OmarAflak/Arduino-Library
//Library downloaded through Gradle
import android.hardware.usb.UsbDevice;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

public class MainActivity extends AppCompatActivity {
    Arduino arduino;
    Handler handler;
    TextView myTextView;
    int Temperature = 50;
    double Voltage = 2.7;
    double Current = 1.2;
    int Percentage = 70;


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
                if (buffer.contains("\0")&& buffer.indexOf("\0") > 0 && buffer.length() > 0) {
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
                        myTextView.setText("yoo its half working");
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
                        myTextView.setText("Permission DENIED");
                        // Stuff that updates the UI

                    }
                });
                arduino.reopen();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Do this stuff when the app is initially launched
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arduino = new Arduino(this); //make a new arduino object
        arduino.addVendorId(6790); //Vendor id lets phone know that connected thing is indeed an arduino
        myTextView = (TextView)findViewById(R.id.Textbox1); //associate myTextView object with actuial textview in layout
        handler = new Handler(); //idk


        //Code below acts as main loop
        final Runnable r = new Runnable() {
            public void run() {

                if (arduino.isOpened()){ //Only send a message if comms with the arduino have been established
                    //Format Message to send
                    String str = Temperature + "\n" + Voltage + "\n" + Current + "\n" + Percentage + "\0";
                    arduino.send(str.getBytes());
                }
                else{ //if arduino is not connected
                    myTextView.setText("I cant find an arduino :(");
                }

                handler.postDelayed(this, 500);
            }
        };

        handler.postDelayed(r, 500);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        arduino.unsetArduinoListener();
        arduino.close();
    }
}

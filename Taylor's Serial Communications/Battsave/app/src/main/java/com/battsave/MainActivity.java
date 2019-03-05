package com.battsave;

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



    @Override
    protected void onStart() {
        super.onStart();
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
                String message = new String(bytes);
                // new message received from arduino

                //Concatenate
                buffer = buffer + message;

                if (buffer.contains("\0")&& buffer.indexOf("\0") > 0 && buffer.length() > 0) {
                    //extract string
                    Log.d("on recieve", buffer);
                    final String print = buffer.substring(0, buffer.indexOf("\0"));
                    Log.d("this is print", print);
                    //delete extracted bit
                    buffer = "";
                    Log.d("buffer after deleted", buffer);
                    //Print to textview
                    runOnUiThread(new Runnable() {

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

                        myTextView.setText("yoo bitch its half working");
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

                        myTextView.setText("Permission DENIED bitchhhh");
                        // Stuff that updates the UI

                    }
                });
                arduino.reopen();
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        arduino = new Arduino(this);
        arduino.addVendorId(6790);
        myTextView = (TextView)findViewById(R.id.Textbox1);

        handler = new Handler();

        final Runnable r = new Runnable() {
            public void run() {

                if (arduino.isOpened()){
                    String str = "Fifty\0";
                    arduino.send(str.getBytes());
                }
                else{
                    //
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

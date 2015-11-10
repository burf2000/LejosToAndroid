package com.burfdevelopment.lejostoandroid;

import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    static final int PORT = 5678;
    ServerSocket cmdSock;
    Socket s;
    TextView dataTextView;

    int data = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        dataTextView = (TextView) findViewById(R.id.dataTextView);

        Intent intent = new Intent(BluetoothDevice.ACTION_PAIRING_REQUEST);
        intent.putExtra(BluetoothDevice.EXTRA_DEVICE, "left");
        int PAIRING_VARIANT_PIN = 1234;
        intent.putExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, PAIRING_VARIANT_PIN);
        sendBroadcast(intent);

//        Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
//        startActivityForResult(intent, REQUEST_PAIR_DEVICE);

        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... params) {
                try {

                    startServer();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                return null;
            }
        }.execute(1);
    }

        //runs without a timer by reposting this handler at the end of the runnable
        Handler timerHandler = new Handler();
        Runnable timerRunnable = new Runnable() {

            @Override
            public void run() {

                try {

                    byte[] messageByte = new byte[1000];
                    boolean end = false;
                    String messageString = "";

                    DataInputStream in = new DataInputStream(s.getInputStream());
                    int bytesRead = 0;

                    while(!end)
                    {

                        bytesRead = in.read(messageByte);
                        messageString += new String(messageByte, 0, bytesRead);
                        if (messageString.length() == 100)
                        {
                            end = true;
                        }
                    }
                    System.out.println("MESSAGE: " + messageString);

                    final String finalMessageString = messageString;
                    runOnUiThread(new Runnable() {
                        public void run() {
                            dataTextView.setText(finalMessageString);
                        }
                    });

//                BufferedReader br = new BufferedReader(new InputStreamReader(s.getInputStream()));
//                String str = br.readLine();
//                Log.i("TAG", " " + str);

//                    data = s.getInputStream().read();
//                    Log.i("TAG", " " + data);
//
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            dataTextView.setText(data);
//                        }
//                    });


//                data = s.getInputStream().read()
//                //dataTextView.setText(String.format("%d", data));
//                Log.i("TAG", " " + data);
//
//                while (data > 0)
//                {
//                    data = s.getInputStream().read();
//
//                    runOnUiThread(new Runnable() {
//                        public void run() {
//                            Log.i("TAG", " " + data);
//                            dataTextView.setText("Score " + data);
//                        }
//                    });
//                }

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    try {
                        s.close();
                        cmdSock.close();

                        cmdSock = new ServerSocket(PORT);
                        s = cmdSock.accept();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }

                }

                timerHandler.postDelayed(this, 1000);
            }
        };

    private void startServer() {

        try {
            cmdSock = new ServerSocket(PORT);
            s = cmdSock.accept();

            timerHandler.postDelayed(timerRunnable, 1000);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


    }

    public void pairDevice(BluetoothDevice device)
    {
        String ACTION_PAIRING_REQUEST = "android.bluetooth.device.action.PAIRING_REQUEST";
        Intent intent = new Intent(ACTION_PAIRING_REQUEST);
        String EXTRA_DEVICE = "android.bluetooth.device.extra.DEVICE";
        intent.putExtra(EXTRA_DEVICE, device);
        String EXTRA_PAIRING_VARIANT = "android.bluetooth.device.extra.PAIRING_VARIANT";
        int PAIRING_VARIANT_PIN = 0;
        intent.putExtra(EXTRA_PAIRING_VARIANT, PAIRING_VARIANT_PIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        this.startActivity(intent);
    }
}

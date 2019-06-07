package bib.CeBiTecLEDpanel;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "iGEM MAIN_ACTIVITY";

    private static final String bluetoothDongleName_1 = "RNBT-DE1A";
    //private static final String bluetoothDongleName_2 = "HC-05";
    // HC-05 UUID  "00001101-0000-1000-8000-00805F9B34FB"
    // UUID is a combination of the Serial Port Value and the Base UUID Value
    private static final UUID MY_UUID =
            UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    //AppController appController = (AppController) AppController.getInstance();
    private static final BluetoothAdapter tooth = BluetoothAdapter.getDefaultAdapter();
    //private MyBluetoothService toothService = appController.getService();

    private ProgressBar spinner;
    private Button btn_retry;
    private TextView txt_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // create an IntentFilter that filters for action of type BluetoothDevice.ACTION_FOUND
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        // connect the filter with the main activity thread. The receiver will be called every time
        // a broadcast intent is generated that matches the filter
        registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);

        spinner = (ProgressBar)findViewById(R.id.spinner);
        spinner.setVisibility(View.VISIBLE);

        txt_info = (TextView) findViewById(R.id.txt_info);


        btn_retry = (Button) findViewById(R.id.retry);
        btn_retry.setVisibility(View.GONE);
        btn_retry.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                btn_retry.setVisibility(View.GONE);
                spinner.setVisibility(View.VISIBLE);
                txt_info.setText("Scanning Bluetooth environment...");

                // start a new discovery
                if (tooth.isDiscovering()) {
                    tooth.cancelDiscovery();
                }
                tooth.startDiscovery();
            }
        });

        if (tooth != null) {
            // for some android versions, these permissions have to be requested on runtime for
            // bluetooth to work
            int MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION = 1;
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_COARSE_LOCATION);
            //check if bluetooth adapter is already discovering. If so, stop and restart
            // check for isDiscovering not neccessary, cancelDiscovery always terminates
            if (tooth.isDiscovering()) {
                tooth.cancelDiscovery();
            }
            // start scanning for bluetooth devices. PARALLEL PROCESS!
            tooth.startDiscovery();

        } else {
            System.err.println("shit");
        }
    }

    // Create a BroadcastReceiver for ACTION_FOUND.
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            System.out.println(action);
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Discovery has found a device. Get the BluetoothDevice
                // object and its info from the Intent.
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if ((device.getName() != null) && (device.getName().length() > 0)) {
                    String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    System.out.println(deviceName);
                    System.out.println(deviceHardwareAddress);

                    if (bluetoothDongleName_1.equals(deviceName)){// || (bluetoothDongleName_2.equals(deviceName))) {
                        //Log.i(TAG, "LED-Panel found!");
                        ConnectThread toothDeviceThread = new ConnectThread(device);
                        toothDeviceThread.start();
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                if (tooth.isDiscovering()){
                    tooth.cancelDiscovery();
                }
                //Log.i(TAG, "Discovery stopped.");
                txt_info.setText("Discovery stopped.");
                spinner.setVisibility(View.GONE);
                btn_retry.setVisibility(View.VISIBLE);
            }
        }
    };

    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                //Log.e(TAG, "Socket's create() method failed", e);
                ;
            }
            mmSocket = tmp;
        }

        public void run() {
            //Log.i(TAG, "starting to connect");
            // Cancel discovery because it otherwise slows down the connection.
            tooth.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    //Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            AppController.getInstance().getService().manageMyConnectedSockets(mmSocket);

            // change Activity
            //Log.i(TAG, "changing to Activity ProgramSequence");
            Intent i = new Intent(getApplicationContext(), ProgramSequence.class);

            startActivity(i);
            finish();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                //Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //unregister the ACTION_FOUND receiver.
        unregisterReceiver(mReceiver);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

}
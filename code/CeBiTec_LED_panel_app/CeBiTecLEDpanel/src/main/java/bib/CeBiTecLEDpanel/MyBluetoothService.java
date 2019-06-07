package bib.CeBiTecLEDpanel;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

/**
 * Created by markushaak on 20.07.17.
 */

public class MyBluetoothService {
    private static final String TAG = "BT_SERVICE";
    private ConnectedThread serviceThread;
    private final Context context;
    private IntentFilter filter = new IntentFilter();

    public MyBluetoothService(Context c){
        context = c;
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        c.registerReceiver(mReceiver, filter);
    }


    // Defines several constants used when transmitting messages between the
    // service and the UI.
    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
    }

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        private static final String TAG = "BT_BROADCAST_REC";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

            if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(action)) {
                //Log.v(TAG, "DISCONNCET_REQ");
                Intent disconnectIntent = new Intent();
                disconnectIntent.putExtra("ID", View.generateViewId());

                disconnectIntent.setAction("com.example.broadcast.DISCONNECT");
                LocalBroadcastManager.getInstance(context).sendBroadcast(disconnectIntent);
            }
            else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                //Log.v(TAG, " DISCONNCET");
                Intent disconnectIntent = new Intent();
                disconnectIntent.putExtra("ID", View.generateViewId());

                disconnectIntent.setAction("com.example.broadcast.DISCONNECT");
                LocalBroadcastManager.getInstance(context).sendBroadcast(disconnectIntent);
            }
        }
    };

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg){
            if(msg.what == MessageConstants.MESSAGE_READ){
                String readMessage = null;
                try {
                    int numBytes = msg.arg1;
                    readMessage = new String(Arrays.copyOfRange((byte[]) msg.obj, 0, numBytes), "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                // mReadBuffer.setText(readMessage);
                //Log.i(TAG, "received:" + readMessage);

                if(readMessage.startsWith("status")){
                    // split into temp, accel
                    Log.i(TAG, "received:" + readMessage);
                    String[] status = readMessage.split(";");
                    update_status(status);
                }
                else if(readMessage.replaceAll("(\\r|\\n)", "").trim().equals("program started")){
                    // inform ProgramSequence Activity
                    //Log.i(TAG,"received confirmation for program start");
                    Intent intent = new Intent();
                    intent.putExtra("ID", View.generateViewId());

                    intent.setAction("com.example.broadcast.START_CONFIRMED");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
                else if(readMessage.replaceAll("(\\r|\\n)", "").trim().equals("program terminated")) {
                    //Log.i(TAG, "initiating StatusActivity termination");
                    Intent intent = new Intent();
                    intent.putExtra("ID", View.generateViewId());

                    intent.setAction("com.example.broadcast.PROGRAM_TERMINATED");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
                else if(readMessage.replaceAll("(\\r|\\n)", "").trim().equals("program corrupted")){
                    Intent intent = new Intent();
                    intent.putExtra("ID", View.generateViewId());

                    intent.setAction("com.example.broadcast.SEQUENCE_CORRUPTED");
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                }
            }
        }
    };

    public void update_status(String[] status){
        if (status.length == 7) {
            Intent intent = new Intent();
            intent.putExtra("ID", View.generateViewId());
            intent.setAction("com.example.broadcast.STATUS_UPDATES");
            intent.putExtra("tmp_top", status[1].replaceAll("(\\r|\\n)", ""));
            intent.putExtra("tmp_btm", status[2].replaceAll("(\\r|\\n)", ""));
            intent.putExtra("acc_x", status[3].replaceAll("(\\r|\\n|-)", ""));
            intent.putExtra("acc_y", status[4].replaceAll("(\\r|\\n|-)", ""));
            intent.putExtra("acc_z", status[5].replaceAll("(\\r|\\n|-)", ""));
            intent.putExtra("acc_max", status[6].replaceAll("(\\r|\\n|-)", ""));
            LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(context);
            localBroadcastManager.sendBroadcast(intent);
        }
    }

    public void manageMyConnectedSockets(BluetoothSocket socket){
        serviceThread = new ConnectedThread(socket);
        serviceThread.start();
    }

    public void write(byte[] bytes){
        //Log.i(TAG, "sending " + Arrays.toString(bytes));
        serviceThread.write(bytes);
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        private byte[] mmBuffer; // mmBuffer store for the stream

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams; using temp objects because
            // member streams are final.
            try {
                tmpIn = socket.getInputStream();
            } catch (IOException e) {
                //Log.e(TAG, "Error occurred when creating input stream", e);
            }
            try {
                tmpOut = socket.getOutputStream();
            } catch (IOException e) {
                //Log.e(TAG, "Error occurred when creating output stream", e);
            }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            mmBuffer = new byte[1024];
            int inBytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs.
            while (true) {
                try {
                    // Read from the InputStream.
                    inBytes = mmInStream.read(mmBuffer);
                    if(inBytes != 0) {
                        //Log.i(TAG, "receiving...");
                        mmBuffer = new byte[1024];
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        inBytes = mmInStream.available(); // how many bytes are ready to be read?
                        inBytes = mmInStream.read(mmBuffer, 0, inBytes); // record how many bytes we actually read
                        Message receivedMessage = mHandler.obtainMessage(MessageConstants.MESSAGE_READ, inBytes, -1, mmBuffer);
                        receivedMessage.sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    //Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(byte[] bytes) {
            //Log.i(TAG, "sending...");
            try {
                mmOutStream.write(bytes);

                // Share the sent message with the UI activity.
                Message writtenMsg = mHandler.obtainMessage(MessageConstants.MESSAGE_WRITE, -1, -1);
                writtenMsg.sendToTarget();
            } catch (IOException e) {
                //Log.e(TAG, "Error occurred when sending data", e);

                // Send a failure message back to the activity.
                Message writeErrorMsg =
                        mHandler.obtainMessage(MessageConstants.MESSAGE_TOAST);
                Bundle bundle = new Bundle();
                bundle.putString("toast",
                        "Couldn't send data to the other device");
                writeErrorMsg.setData(bundle);
                mHandler.sendMessage(writeErrorMsg);
            }
        }

        // Call this method from the main activity to shut down the connection.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                //Log.e(TAG, "Could not close the connect socket", e);
            }
        }
    }
}

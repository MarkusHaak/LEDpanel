package bib.CeBiTecLEDpanel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.nio.charset.Charset;

public class StatusActivity extends AppCompatActivity {

    private static final String TAG = "StatusActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BroadcastReceiver statusBR = new StatusBroadcastReceiver();
        IntentFilter filter = new IntentFilter("com.example.broadcast.STATUS_UPDATES");
        LocalBroadcastManager.getInstance(this).registerReceiver(statusBR, filter);
        IntentFilter filter2 = new IntentFilter("com.example.broadcast.PROGRAM_TERMINATED");
        LocalBroadcastManager.getInstance(this).registerReceiver(statusBR, filter2);
        IntentFilter filter3 = new IntentFilter("com.example.broadcast.DISCONNECT");
        LocalBroadcastManager.getInstance(this).registerReceiver(statusBR, filter3);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Sent order to cancel program", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                cancel_illumination();
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
    }

    public void returnAsResult(int i, android.content.Intent intent){
        setResult(i, intent);
        finish();
    }

    protected void cancel_illumination(){
        String toReturn = "X";
        //Log.i(TAG, toReturn);
        final byte[] bytes = toReturn.getBytes(Charset.forName("UTF-8"));
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after some time
                AppController.getInstance().getService().write(bytes);
            }
        }, 20);
    }

    private class StatusBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = "StatusBroadcastReceiver";
        private int last_id;

        @Override
        public void onReceive(Context context, Intent intent) {
            int id = intent.getIntExtra("ID", 0);
            if ((last_id == id) || (id == 0)){
                return;
            }

            if (intent.getAction() == "com.example.broadcast.STATUS_UPDATES"){
                //Log.i(TAG, intent.getStringExtra("tmp_top"));
                //Log.i(TAG, intent.getStringExtra("tmp_btm"));
                TextView tmp_top = (TextView) findViewById(R.id.temp_top);
                tmp_top.setText(intent.getStringExtra("tmp_top") + " °C");
                TextView tmp_btm = (TextView) findViewById(R.id.temp_btm);
                tmp_btm.setText(intent.getStringExtra("tmp_btm") + " °C");
                TextView acc_x = (TextView) findViewById(R.id.acc_x);
                acc_x.setText(intent.getStringExtra("acc_x") + " m/s^2");
                TextView acc_y = (TextView) findViewById(R.id.acc_y);
                acc_y.setText(intent.getStringExtra("acc_y") + " m/s^2");
                TextView acc_z = (TextView) findViewById(R.id.acc_z);
                acc_z.setText(intent.getStringExtra("acc_z") + " m/s^2");
                TextView acc_max = (TextView) findViewById(R.id.acc_max);
                acc_max.setText(intent.getStringExtra("acc_max") + " m/s^2");
            }
            else if (intent.getAction() == "com.example.broadcast.PROGRAM_TERMINATED"){
                // create result
                Intent result = new Intent();
                result.putExtra("reason", "program terminated");

                // return result and change Activity
                StatusActivity.this.returnAsResult(RESULT_OK, result);
            }
            else if (intent.getAction() == "com.example.broadcast.DISCONNECT"){
                //Log.i(TAG, "Bluetooth connection lost");
                //finish();
                Intent i=new Intent(getApplicationContext(), MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
            }

            last_id = id;
        }
    }

}
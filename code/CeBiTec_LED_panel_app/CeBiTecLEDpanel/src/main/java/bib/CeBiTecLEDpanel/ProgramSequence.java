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
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.nio.charset.Charset;
import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class ProgramSequence extends AppCompatActivity {
    private LinearLayout ll;
    private ProgramSequence self;
    private int counter = -1;
    private static final String TAG = "iGEM PROGRAM_ACTIVITY";
    private static final int STAT_REQ_CODE = 9;
    private boolean statusActivityRunning = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_program_sequence);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        ll = (LinearLayout) findViewById(R.id.vLinearLayout);
        self = this;

        final LinearLayout add_button = (LinearLayout) findViewById(R.id.add_button);
        add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                counter++;
                final View custom = inflater.inflate(R.layout.custom, null);
                custom.setId(counter);
                custom.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        // change Activity
                        Intent i = new Intent(getApplicationContext(), EditAcitvity.class);
                        // get data to transmit preset settings
                        Bundle data = createBundleFromEntry(custom);
                        // attach bundle to intent
                        i.putExtras(data);
                        startActivityForResult(i, custom.getId());
                    }
                });


                TextView tv_index = (TextView) custom.findViewById(R.id.tv_index);
                tv_index.setText("" + counter);

                if (counter > 0){
                    View previous = (View) findViewById((counter - 1));
                    TextView prev_tv_slope = (TextView) previous.findViewById(R.id.tv_slope);
                    // if previous entry is a jump entry, there are no settings that should be passed on
                    if(!prev_tv_slope.getText().toString().equals("Jump")) {
                        TextView prev_tv_1_left = (TextView) previous.findViewById(R.id.tv_1_left);
                        TextView prev_tv_1_right = (TextView) previous.findViewById(R.id.tv_1_right);
                        TextView tv_0_left = (TextView) custom.findViewById((R.id.tv_0_left));
                        tv_0_left.setText(prev_tv_1_left.getText());
                        TextView tv_0_right = (TextView) custom.findViewById((R.id.tv_0_right));
                        tv_0_right.setText(prev_tv_1_right.getText());
                        TextView tv_1_left = (TextView) custom.findViewById((R.id.tv_1_left));
                        tv_1_left.setText(prev_tv_1_left.getText());
                        TextView tv_1_right = (TextView) custom.findViewById((R.id.tv_1_right));
                        tv_1_right.setText(prev_tv_1_right.getText());

                        //TextView prev_tv_slope = (TextView) previous.findViewById(R.id.tv_slope);
                        if (prev_tv_slope.getText().toString().contains("*")) {
                            TextView slope = (TextView) custom.findViewById(R.id.tv_slope);
                            slope.setText(slope.getText().toString() + "*");

                        }
                        TextView prev_tv_accel = (TextView) previous.findViewById(R.id.tv_accel);
                        TextView prev_tv_accel_time = (TextView) previous.findViewById(R.id.tv_accel_time);
                        TextView tv_accel = (TextView) custom.findViewById(R.id.tv_accel);
                        TextView tv_accel_time = (TextView) custom.findViewById(R.id.tv_accel_time);
                        tv_accel.setText(prev_tv_accel.getText().toString());
                        tv_accel_time.setText(prev_tv_accel_time.getText().toString());
                    }
                }

                ImageButton delete_button = (ImageButton) custom.findViewById(R.id.deleteButton);
                delete_button.setOnClickListener(new View.OnClickListener() {
                    public void onClick( View v) {
                        // change id of every following entry
                        for(int i = custom.getId() + 1; i <= counter ; i++){
                            View toChange = (View) findViewById(i);
                            TextView tv_index = (TextView) toChange.findViewById(R.id.tv_index);
                            tv_index.setText(""+ (i - 1));
                            toChange.setId(i - 1);
                            TextView toChangeSlope = (TextView) toChange.findViewById(R.id.tv_slope);
                            if( toChangeSlope.getText().toString().equals("Jump") ){
                                TextView toChangeTvJumpIndex = (TextView) toChange.findViewById(R.id.tv_1_left);
                                int prevJumpIndex = Integer.parseInt(toChangeTvJumpIndex.getText().toString());
                                if(prevJumpIndex >= custom.getId()){
                                    int newJumpIndex = prevJumpIndex - 1;
                                    if(newJumpIndex < 0) {
                                        newJumpIndex = 0;
                                    }
                                    toChangeTvJumpIndex.setText("" + newJumpIndex);
                                }
                            }
                        }
                        counter--;

                        // delete from program list
                        ll.removeView(custom);
                    }
                });
                ll.addView(custom);
            }
        });


        BroadcastReceiver btBR = new ProgramSequence.BtBroadcastReceiver();
        IntentFilter filter = new IntentFilter("com.example.broadcast.START_CONFIRMED");
        LocalBroadcastManager.getInstance(this).registerReceiver(btBR, filter);
        IntentFilter filter2 = new IntentFilter("com.example.broadcast.SEQUENCE_CORRUPTED");
        LocalBroadcastManager.getInstance(this).registerReceiver(btBR, filter2);
        IntentFilter filter3 = new IntentFilter("com.example.broadcast.PROGRAM_TERMINATED");
        LocalBroadcastManager.getInstance(this).registerReceiver(btBR, filter3);
        IntentFilter filter4 = new IntentFilter("com.example.broadcast.DISCONNECT");
        LocalBroadcastManager.getInstance(this).registerReceiver(btBR, filter4);
        IntentFilter filter5 = new IntentFilter("com.example.broadcast.STATUS_UPDATES");
        LocalBroadcastManager.getInstance(this).registerReceiver(btBR, filter5);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(counter >= 0){
                    Snackbar.make(view, "program sent...", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                    send_program_sequence();
                }
                else{
                    Snackbar.make(view, "program sequence empty", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            }
        });
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data == null){
            //Log.i(TAG, "Cancelled - " + "by user pressing return");
            // cancel illumination
            //Log.i(TAG, "Sending order to cancel illumination...");
            cancel_illumination();
        }
        else{
            Bundle extras = data.getExtras();
            //Log.i(TAG, "received ActivityResult with requestCode: "+ requestCode);
            // Check which request it is that we're responding to
            // case StatusActivity request code
            if (requestCode == STAT_REQ_CODE){
                //Log.i(TAG, "Received result from StatusActivity");


                if (resultCode == RESULT_CANCELED) {
                    // retrieve data
                    //Log.i(TAG, "Cancelled - " + extras.getString("reason"));
                    if (!(extras.getString("reason").equals("program terminated"))) {
                        // cancel illumination
                        if (extras.getString("reason").equals("program terminated")){
                            Snackbar.make(findViewById(R.id.ProgramSequence_CoordinatiorLayout), "termination confirmed", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                }
                else if (resultCode == RESULT_OK) {
                    // retrieve data
                    //Log.i(TAG, "OK - " + extras.getString("reason"));
                }

            }
            // case EditActivity request code
            else {
                if (resultCode == RESULT_OK) {
                    // search View and its children to edit
                    View custom = findViewById(requestCode);
                    TextView time = (TextView) custom.findViewById(R.id.time);
                    ImageView image = (ImageView) custom.findViewById(R.id.slope_img);
                    TextView tv_0_left = (TextView) custom.findViewById(R.id.tv_0_left);
                    TextView tv_0_right = (TextView) custom.findViewById(R.id.tv_0_right);
                    TextView tv_1_left = (TextView) custom.findViewById(R.id.tv_1_left);
                    TextView tv_1_right = (TextView) custom.findViewById(R.id.tv_1_right);
                    LinearLayout ll_0_right = (LinearLayout) custom.findViewById(R.id.ll_0_right);
                    LinearLayout ll_1_right = (LinearLayout) custom.findViewById(R.id.ll_1_right);
                    TextView tv_slope = (TextView) custom.findViewById(R.id.tv_slope);

                    TextView tv_accel = (TextView) custom.findViewById(R.id.tv_accel);
                    TextView tv_accel_time = (TextView) custom.findViewById(R.id.tv_accel_time);

                    ImageView img_plate_left = (ImageView) custom.findViewById(R.id.img_plate_left);
                    ImageView img_plate_right = (ImageView) custom.findViewById(R.id.img_plate_right);
                    ImageView img_jumps = (ImageView) custom.findViewById(R.id.img_jump);
                    ImageView img_index = (ImageView) custom.findViewById(R.id.img_index);

                    // retrieve data
                    if ((extras.getInt("minutes") < 10) && (extras.getInt("seconds") < 10)){
                        time.setText("0" + extras.getInt("minutes") + ":0" + extras.getInt("seconds"));
                    }else if (extras.getInt("minutes") < 10){
                        time.setText("0" + extras.getInt("minutes") + ":" + extras.getInt("seconds"));
                    }else if (extras.getInt("seconds") < 10){
                        time.setText("" + extras.getInt("minutes") + ":0" + extras.getInt("seconds"));
                    }else{
                        time.setText("" + extras.getInt("minutes") + ":" + extras.getInt("seconds"));
                    }

                    tv_0_left.setText("" + extras.getInt("0_left") + " %");
                    tv_0_right.setText("" + extras.getInt("0_right") + " %");
                    tv_1_left.setText("" + extras.getInt("1_left") + " %");
                    tv_1_right.setText("" + extras.getInt("1_right") + " %");

                    tv_accel.setText(String.format("%.2f", extras.getDouble("accel")));

                    if ((extras.getInt("accel_minutes") < 10) && (extras.getInt("accel_seconds") < 10)){
                        tv_accel_time.setText("0" + extras.getInt("accel_minutes") + ":0" + extras.getInt("accel_seconds"));
                    }else if (extras.getInt("accel_minutes") < 10){
                        tv_accel_time.setText("0" + extras.getInt("accel_minutes") + ":" + extras.getInt("accel_seconds"));
                    }else if (extras.getInt("accel_seconds") < 10){
                        tv_accel_time.setText("" + extras.getInt("accel_minutes") + ":0" + extras.getInt("accel_seconds"));
                    }else{
                        tv_accel_time.setText("" + extras.getInt("accel_minutes") + ":" + extras.getInt("accel_seconds"));
                    }

                    switch (extras.getString("slope")){
                        case "Constant":
                            image.setImageResource(R.drawable.constant);
                            if(extras.getBoolean("acceleration")){
                                tv_slope.setText("Constant*");
                            }
                            else{
                                tv_slope.setText("Constant");
                            }
                            tv_1_left.setText("" + extras.getInt("0_left") + " %");
                            tv_1_right.setText("" + extras.getInt("0_right") + " %");
                            break;
                        case "Linear":
                            image.setImageResource(R.drawable.lin_increase);
                            if(extras.getBoolean("acceleration")){
                                tv_slope.setText("Linear*");
                            }
                            else{
                                tv_slope.setText("Linear");
                            }
                            break;
                        case "Exponential":
                            image.setImageResource(R.drawable.exp_increase);
                            if(extras.getBoolean("acceleration")){
                                tv_slope.setText("Exponential*");
                            }
                            else{
                                tv_slope.setText("Exponential");
                            }
                            break;
                        case "Jump":
                            tv_slope.setText("Jump");
                            tv_0_left.setText("" + extras.getInt("jumps"));
                            tv_1_left.setText("" + extras.getInt("jump_index"));
                            break;
                        /*
                        case "Logarithmic":
                            image.setImageResource(R.drawable.log_increase);
                            if(extras.getBoolean("acceleration")){
                                tv_slope.setText("Logarithmic*");
                            }
                            else{
                                tv_slope.setText("Logarithmic");
                            }
                            break;
                        */
                        default:
                            //Log.i(TAG, "slope not interpretable.");
                            break;
                    }

                    if(extras.getString("slope").equals("Jump")){
                        img_plate_left.setVisibility(View.GONE);
                        img_plate_right.setVisibility(View.GONE);
                        img_index.setVisibility(View.VISIBLE);
                        img_jumps.setVisibility(View.VISIBLE);
                        ll_0_right.setVisibility(GONE);
                        ll_1_right.setVisibility(GONE);
                        time.setVisibility(View.GONE);
                    }
                    else{
                        img_plate_left.setVisibility(View.VISIBLE);
                        img_plate_right.setVisibility(View.VISIBLE);
                        img_index.setVisibility(View.GONE);
                        img_jumps.setVisibility(View.GONE);
                        ll_0_right.setVisibility(VISIBLE);
                        ll_1_right.setVisibility(VISIBLE);
                        time.setVisibility(View.VISIBLE);
                    }

                    if(extras.getBoolean("gradient")){
                        ll_0_right.setVisibility(VISIBLE);
                        ll_1_right.setVisibility(VISIBLE);
                    }else{
                        ll_0_right.setVisibility(GONE);
                        ll_1_right.setVisibility(GONE);
                    }

                }else{
                    //Log.i(TAG, "Something went wrong...");
                }
            }
        }
    }

    protected Bundle createBundleFromEntry(View custom){
        TextView time = (TextView) custom.findViewById(R.id.time);
        TextView tv_0_left = (TextView) custom.findViewById(R.id.tv_0_left);
        TextView tv_0_right = (TextView) custom.findViewById(R.id.tv_0_right);
        TextView tv_1_left = (TextView) custom.findViewById(R.id.tv_1_left);
        TextView tv_1_right = (TextView) custom.findViewById(R.id.tv_1_right);
        TextView tv_slope = (TextView) custom.findViewById(R.id.tv_slope);
        TextView tv_accel = (TextView) custom.findViewById(R.id.tv_accel);
        TextView tv_accel_time = (TextView) custom.findViewById(R.id.tv_accel_time);
        TextView tv_index = (TextView) custom.findViewById(R.id.tv_index);

        Intent result = new Intent();
        if(tv_slope.getText().toString().contains("*")){
            result.putExtra("slope", tv_slope.getText().toString().replace("*", ""));
            result.putExtra("acceleration", true);
        }
        else{
            result.putExtra("slope", tv_slope.getText().toString());
            result.putExtra("acceleration", false);
        }
        result.putExtra("gradient", tv_0_right.isShown());
        result.putExtra("0_left", Integer.parseInt(tv_0_left.getText().toString().split(" ")[0]));
        result.putExtra("0_right", Integer.parseInt(tv_0_right.getText().toString().split(" ")[0]));
        result.putExtra("1_left", Integer.parseInt(tv_1_left.getText().toString().split(" ")[0]));
        result.putExtra("1_right", Integer.parseInt(tv_1_right.getText().toString().split(" ")[0]));
        String str_time = time.getText().toString();
        result.putExtra("minutes", Integer.parseInt(str_time.split(":")[0]));
        result.putExtra("seconds", Integer.parseInt(str_time.split(":")[1]));
        String str_accel_time = tv_accel_time.getText().toString();
        result.putExtra("accel_minutes", Integer.parseInt(str_accel_time.split(":")[0]));
        result.putExtra("accel_seconds", Integer.parseInt(str_accel_time.split(":")[1]));
        result.putExtra("accel", Double.parseDouble(tv_accel.getText().toString().replace(",",".")));
        result.putExtra("index", Integer.parseInt(tv_index.getText().toString()));
        return result.getExtras();
    }

    protected void send_program_sequence(){
        /*
         - entry seperator: /
            --> must be at beginning of first entry too!
         - value seperator: _
         - beginning of Sequence: >
         - end of Sequence: <

         - entry_length: ALWAYS 38 characters

         - constants:
            - slope:
                0: Constant
                1: Linear
                2: Exponential
            - gradient:
                0: false
                1: true

          - order of entry-elements:
            0: slope
            1: gradient boolean
            2: total seconds
            3: 0_left
            4: 0_right
            5: 1_left
            6: 1_right
            7: acceleration control boolean
            8: acceleration threshold
            9: acceleration timeout

         Examples:

        >/0_0_0030_010_000_010_000_1_02.00_00.05<
        --> turns on all wells to 10 percent for 30 seconds, stopping illumination
        for 5 seconds every time the panel is accelerated more than 2.0 m/s^2 compared
        to the previous measurement (measurements every 50 ms).
         */
        StringBuilder glob_sb = new StringBuilder();
        int i;
        for(i=0; i<=counter; i++){
            StringBuilder sb = new StringBuilder();
            if(i == 0){
                sb.append('>');
            }
            View custom = findViewById(i);
            Bundle data = createBundleFromEntry(custom);

            //leading "/"
            sb.append('/');
            //slope
            if (data.getString("slope").startsWith("Constant")){
                sb.append(0);
            }else if (data.getString("slope").startsWith("Linear")){
                sb.append(1);
            }else if (data.getString("slope").startsWith("Exponential")){
                sb.append(2);
            }else if (data.getString("slope").startsWith("Jump")){
                sb.append(4);
            }
            //else if (data.getString("slope").startsWith("Logarithmic")){
            //    sb.append(3);
            //}
            sb.append('_');
            //gradient
            if(data.getBoolean("gradient")) {
                sb.append(1);
            }else{
                sb.append(0);
            }
            sb.append('_');
            //total seconds
            int tot_sec = data.getInt("minutes")*60+data.getInt("seconds");
            sb.append(String.format("%04d", tot_sec));
            sb.append('_');
            //0_left
            sb.append(String.format("%03d", data.getInt("0_left")));
            sb.append('_');
            //0_right
            sb.append(String.format("%03d", data.getInt("0_right")));
            sb.append('_');
            //1_left
            sb.append(String.format("%03d", data.getInt("1_left")));
            sb.append('_');
            //1_right
            sb.append(String.format("%03d", data.getInt("1_right")));
            sb.append('_');
            //acceleration
            if(data.getBoolean("acceleration")) {
                sb.append(1);
            }else{
                sb.append(0);
            }
            sb.append('_');
            //accel
            if(data.getDouble("accel") >= 10.0){
                sb.append(String.format("%.2f", data.getDouble("accel")).replace(",", "."));
            }
            else{
                sb.append("0" + String.format("%.2f", data.getDouble("accel")).replace(",", "."));
            }
            sb.append('_');
            //total accel seconds
            int tot_accel_sec = data.getInt("accel_minutes")*60+data.getInt("accel_seconds");
            sb.append(String.format("%04d", tot_accel_sec));

            // build string and send
            String toReturn = sb.toString();
            //Log.i(TAG, toReturn);
            glob_sb.append(toReturn);
            final byte[] bytes = toReturn.getBytes(Charset.forName("UTF-8"));
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppController.getInstance().getService().write(bytes);
                }
            }, i*10);
        }

        StringBuilder sb = new StringBuilder();
        glob_sb.append('_');
        sb.append('_');
        //Log.i(TAG, "hashing:");
        //Log.i(TAG, glob_sb.toString());
        //Log.i(TAG, "" + hash(glob_sb.toString()));
        //Log.i(TAG, "--------");
        sb.append(hash(glob_sb.toString()));
        sb.append('<');
        String toReturn = sb.toString();
        //Log.i(TAG, toReturn);
        final byte[] bytes = toReturn.getBytes(Charset.forName("UTF-8"));
        final Handler handler = new Handler();
        i++;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                AppController.getInstance().getService().write(bytes);
            }
        }, i*10);

    }

    protected int hash(String str){
        int hash = 5381;
        int c;
        int i = 0;

        while (str.length() != 0){
            i++;
            c = str.charAt(0);
            str = str.substring(1);
            hash = ((hash << 5) + hash) + c; /* hash * 33 + c */
        }

        //Log.i(TAG, ""+i);
        return hash;
    }

    private class BtBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = "ProgramSequence_BR";
        private int last_id;

        @Override
        public void onReceive(Context context, Intent intent) {
            int id = intent.getIntExtra("ID", 0);
            String action = intent.getAction();

            if ((last_id == id) || (id == 0)){
                return;
            }
            if (action == "com.example.broadcast.START_CONFIRMED" || action == "com.example.broadcast.STATUS_UPDATES"){
                //Log.i(TAG, "confirmation received, starting StatusActivity");
                if(!statusActivityRunning){
                    statusActivityRunning = true;
                    Intent i = new Intent(getApplicationContext(), StatusActivity.class);
                    startActivityForResult(i, STAT_REQ_CODE);
                }
            }
            else if (action == "com.example.broadcast.SEQUENCE_CORRUPTED"){
                //Log.i(TAG, "Bluetooth transmission failed");
                Snackbar.make(findViewById(R.id.ProgramSequence_CoordinatiorLayout), "transmission failed, please retry", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
            else if (action == "com.example.broadcast.PROGRAM_TERMINATED"){
                //Log.i(TAG, "Bluetooth transmission failed");
                Snackbar.make(findViewById(R.id.ProgramSequence_CoordinatiorLayout), "termination confirmed", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                statusActivityRunning = false;
            }
            else if (action == "com.example.broadcast.DISCONNECT"){
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

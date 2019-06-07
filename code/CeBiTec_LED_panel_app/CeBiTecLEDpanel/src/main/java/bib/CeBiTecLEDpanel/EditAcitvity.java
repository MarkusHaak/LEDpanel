package bib.CeBiTecLEDpanel;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

public class EditAcitvity extends AppCompatActivity {

    private LinearLayout ll_0_left;
    private LinearLayout ll_0_right;
    private LinearLayout ll_1_left;
    private LinearLayout ll_1_right;

    private TextView left_0;
    private TextView left_1;
    private TextView slope;
    private TextView tv_0_left;
    private TextView tv_0_right;
    private TextView tv_1_left;
    private TextView tv_1_right;

    private ImageButton const_btn;
    private ImageButton lin_btn;
    private ImageButton exp_btn;
    //private ImageButton log_btn;
    private ImageButton jmp_btn;

    private SeekBar seek_0_left;
    private SeekBar seek_0_right;
    private SeekBar seek_1_left;
    private SeekBar seek_1_right;

    private CheckBox gradient;

    private NumberPicker np_minutes;
    private NumberPicker np_seconds;

    private CheckBox acceleration;
    private SeekBar seek_accel;
    private RelativeLayout accel_time_layout;
    private LinearLayout accel_layout;
    private TextView tv_accel;
    private NumberPicker np_accel_sec;
    private NumberPicker np_accel_min;

    private NumberPicker np_jumps;
    private NumberPicker np_index;
    private LinearLayout ll_illumination;
    private LinearLayout ll_jump;

    private static final String TAG = "iGEM EDIT_ACTIVITY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_acitvity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        gradient = (CheckBox) findViewById(R.id.gradient);

        ll_0_left = (LinearLayout) findViewById(R.id.ll_0_left);
        ll_0_right = (LinearLayout) findViewById(R.id.ll_0_right);
        ll_1_left = (LinearLayout) findViewById(R.id.ll_1_left);
        ll_1_right = (LinearLayout) findViewById(R.id.ll_1_right);

        left_0 = (TextView) findViewById(R.id.left_0);
        left_1 = (TextView) findViewById(R.id.left_1);

        slope = (TextView) findViewById(R.id.slope);

        const_btn = (ImageButton) findViewById(R.id.const_btn);
        lin_btn = (ImageButton) findViewById(R.id.lin_btn);
        exp_btn = (ImageButton) findViewById(R.id.exp_btn);
        //log_btn = (ImageButton) findViewById(R.id.log_btn);
        jmp_btn = (ImageButton) findViewById(R.id.jmp_btn);

        seek_0_left = (SeekBar) findViewById(R.id.left_0_seek);
        seek_0_right = (SeekBar) findViewById(R.id.right_0_seek);
        seek_1_left = (SeekBar) findViewById(R.id.left_1_seek);
        seek_1_right = (SeekBar) findViewById(R.id.right_1_seek);

        tv_0_left = (TextView) findViewById(R.id.left_0_percent);
        tv_0_right = (TextView) findViewById(R.id.right_0_percent);
        tv_1_left = (TextView) findViewById(R.id.left_1_percent);
        tv_1_right = (TextView) findViewById(R.id.right_1_percent);

        np_minutes = (NumberPicker) findViewById(R.id.minutes);
        np_seconds = (NumberPicker) findViewById(R.id.seconds);

        acceleration = (CheckBox) findViewById(R.id.accel_control);
        seek_accel = (SeekBar) findViewById(R.id.accel_seek);
        accel_time_layout = (RelativeLayout) findViewById(R.id.accel_time_layout);
        accel_layout = (LinearLayout) findViewById(R.id.accel_layout);
        tv_accel = (TextView) findViewById(R.id.accel_unit);
        np_accel_sec = (NumberPicker) findViewById(R.id.accel_seconds);
        np_accel_min = (NumberPicker) findViewById(R.id.accel_minutes);

        np_jumps = (NumberPicker) findViewById(R.id.np_jumps);
        np_index = (NumberPicker) findViewById(R.id.np_index);
        ll_illumination = (LinearLayout) findViewById(R.id.ll_illumination);
        ll_jump = (LinearLayout) findViewById(R.id.ll_jump);

        const_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!const_btn.isSelected()){
                    ll_illumination.setVisibility(View.VISIBLE);
                    ll_jump.setVisibility(View.GONE);
                    const_btn.setSelected(true);
                    lin_btn.setSelected(false);
                    exp_btn.setSelected(false);
                    //log_btn.setSelected(false);
                    jmp_btn.setSelected(false);
                    slope.setText("Constant");
                    ll_1_left.setVisibility(View.INVISIBLE);
                    ll_1_right.setVisibility(View.INVISIBLE);
                }
            }
        });

        lin_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!lin_btn.isSelected()){
                    ll_illumination.setVisibility(View.VISIBLE);
                    ll_jump.setVisibility(View.GONE);
                    const_btn.setSelected(false);
                    lin_btn.setSelected(true);
                    exp_btn.setSelected(false);
                    //log_btn.setSelected(false);
                    jmp_btn.setSelected(false);
                    slope.setText("Linear");
                    ll_1_left.setVisibility(View.VISIBLE);
                    if (gradient.isChecked()) {
                        ll_1_right.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        exp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!exp_btn.isSelected()){
                    ll_illumination.setVisibility(View.VISIBLE);
                    ll_jump.setVisibility(View.GONE);
                    const_btn.setSelected(false);
                    lin_btn.setSelected(false);
                    exp_btn.setSelected(true);
                    //log_btn.setSelected(false);
                    jmp_btn.setSelected(false);
                    slope.setText("Exponential");
                    ll_1_left.setVisibility(View.VISIBLE);
                    if (gradient.isChecked()) {
                        ll_1_right.setVisibility(View.VISIBLE);
                    }
                }
            }
        });


        jmp_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!jmp_btn.isSelected()){
                    ll_illumination.setVisibility(View.GONE);
                    ll_jump.setVisibility(View.VISIBLE);
                    const_btn.setSelected(false);
                    lin_btn.setSelected(false);
                    exp_btn.setSelected(false);
                    jmp_btn.setSelected(true);
                    slope.setText("Jump");
                }
            }
        });


        seek_0_left.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                tv_0_left.setText("" + progress + " %");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){

            }
        });

        seek_0_right.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                tv_0_right.setText("" + progress + " %");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){

            }
        });

        seek_1_left.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                tv_1_left.setText("" + progress + " %");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){

            }
        });

        seek_1_right.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                tv_1_right.setText("" + progress + " %");
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){

            }
        });

        seek_accel.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                double value = getAccelerationFromProgress(progress);
                tv_accel.setText(Html.fromHtml(String.format("%.2f ms<sup>-2</sup>", value)));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar){

            }
            @Override
            public void onStopTrackingTouch(SeekBar seekBar){

            }
        });


        //Populate NumberPickers values from minimum and maximum value range
        //Set the minimum value of NumberPickers
        np_minutes.setMinValue(0);
        np_seconds.setMinValue(0);
        np_accel_min.setMinValue(0);
        np_accel_sec.setMinValue(0);
        //Specify the maximum value/number of NumberPicker
        np_minutes.setMaxValue(60);
        np_seconds.setMaxValue(59);
        np_accel_min.setMaxValue(60);
        np_accel_sec.setMaxValue(59);

        np_jumps.setMaxValue(999);




        //Gets whether the selector wheel wraps when reaching the min/max value.
        np_minutes.setWrapSelectorWheel(true);
        np_seconds.setWrapSelectorWheel(true);
        np_accel_sec.setWrapSelectorWheel(true);
        np_accel_min.setWrapSelectorWheel(true);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                // create result
                Intent result = new Intent();
                result.putExtra("slope", slope.getText());
                result.putExtra("gradient", gradient.isChecked());
                result.putExtra("0_left", seek_0_left.getProgress());
                result.putExtra("0_right", seek_0_right.getProgress());
                result.putExtra("1_left", seek_1_left.getProgress());
                result.putExtra("1_right", seek_1_right.getProgress());
                result.putExtra("minutes", np_minutes.getValue());
                result.putExtra("seconds", np_seconds.getValue());
                result.putExtra("acceleration", acceleration.isChecked());
                result.putExtra("accel", getAccelerationFromProgress(seek_accel.getProgress()));
                result.putExtra("accel_minutes", np_accel_min.getValue());
                result.putExtra("accel_seconds", np_accel_sec.getValue());

                result.putExtra("jumps", np_jumps.getValue());
                result.putExtra("jump_index", np_index.getValue());

                // return result and change Activity
                setResult(RESULT_OK, result);
                finish();
            }
        });



        // initiate with preset state
        Bundle data = getIntent().getExtras();

        // slope
        if (data.getString("slope").equals("Constant")) {
            ll_1_left.setVisibility(View.INVISIBLE);
        }

        // gradient
        gradient.setChecked(data.getBoolean("gradient"));
        onCheckboxClicked(gradient);
        if(!data.getString("slope").equals("Jump")){
            // seeks
            seek_0_left.setProgress(data.getInt("0_left"));
            tv_0_left.setText("" + data.getInt("0_left") + " %");
            seek_0_right.setProgress(data.getInt("0_right"));
            tv_0_right.setText("" + data.getInt("0_right") + " %");
            seek_1_left.setProgress(data.getInt("1_left"));
            tv_1_left.setText("" + data.getInt("1_left") + " %");
            seek_1_right.setProgress(data.getInt("1_right"));
            tv_1_right.setText("" + data.getInt("1_right") + " %");
        }else{
            seek_0_left.setProgress(0);
            tv_0_left.setText("" + 0 + " %");
            seek_0_right.setProgress(0);
            tv_0_right.setText("" + 0 + " %");
            seek_1_left.setProgress(0);
            tv_1_left.setText("" + 0 + " %");
            seek_1_right.setProgress(0);
            tv_1_right.setText("" + 0 + " %");
        }

        // time
        np_minutes.setValue(data.getInt("minutes"));
        np_seconds.setValue(data.getInt("seconds"));
        // acceleration
        acceleration.setChecked(data.getBoolean("acceleration"));
        onCheckboxClicked(acceleration);
        seek_accel.setProgress(getProgressFromAcceleration(data.getDouble("accel")));
        tv_accel.setText(Html.fromHtml(String.format("%.2f ms<sup>-2</sup>", data.getDouble("accel"))));
        np_accel_min.setValue(data.getInt("accel_minutes"));
        np_accel_sec.setValue(data.getInt("accel_seconds"));

        if (data.getInt("index") >= 1){
            np_index.setMaxValue(data.getInt("index")-1);
        }
        else{
            np_index.setMaxValue(data.getInt("index"));
        }

        switch (data.getString("slope").replace("*", "")){
            case "Constant":
                const_btn.callOnClick();
                break;
            case "Linear":
                lin_btn.callOnClick();
                break;
            case "Exponential":
                exp_btn.callOnClick();
                break;
            case "Jump":
                jmp_btn.callOnClick();
                np_jumps.setValue(data.getInt("0_left"));
                np_index.setValue(data.getInt("1_left"));
                break;
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        BroadcastReceiver editBR = new EditBroadcastReceiver();
        IntentFilter filter = new IntentFilter("com.example.broadcast.DISCONNECT");
        LocalBroadcastManager.getInstance(this).registerReceiver(editBR, filter);
    }

    public double getAccelerationFromProgress(int progress){
        return (((double) progress) / 100.0) * 15.0 + 1.0;
    }

    public int getProgressFromAcceleration(double acceleration){
        int progress = (int)(((acceleration - 1.0) / 15.0) * 100.0);
        if(progress < 0){
            progress = 0;
        }
        else if (progress > 100){
            progress = 100;
        }
        return progress;
    }

    @Override
    public void onBackPressed() {
        // create result
        Intent result = new Intent();
        // return result and change Activity
        setResult(RESULT_CANCELED, result);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // create result
        Intent result = new Intent();
        // return result and change Activity
        setResult(RESULT_CANCELED, result);
        finish();
        return true;
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        if (view.getId() == gradient.getId()){
            boolean checked = ((CheckBox) view).isChecked();

            if (checked) {
                ll_0_right.setVisibility(View.VISIBLE);
                left_0.setText("Leftmost");
                left_1.setText("Leftmost");
                if (slope.getText().equals("Constant")) {
                    ll_1_right.setVisibility(View.INVISIBLE);
                } else{
                    ll_1_right.setVisibility(View.VISIBLE);
                }
            }else {
                ll_0_right.setVisibility(View.INVISIBLE);
                left_0.setText("All wells");
                left_1.setText("All wells");
                ll_1_right.setVisibility(View.INVISIBLE);
            }
        }
        else if (view.getId() == acceleration.getId()){
            boolean checked = ((CheckBox) view).isChecked();

            if (checked) {
                accel_layout.setVisibility(View.VISIBLE);
                accel_time_layout.setVisibility(View.VISIBLE);
            }else {
                accel_layout.setVisibility(View.INVISIBLE);
                accel_time_layout.setVisibility(View.INVISIBLE);
            }
        }
    }

    private class EditBroadcastReceiver extends BroadcastReceiver {
        private static final String TAG = "EditBroadcastReceiver";
        private int last_id;

        @Override
        public void onReceive(Context context, Intent intent) {
            int id = intent.getIntExtra("ID", 0);
            if ((last_id == id) || (id == 0)){
                return;
            }

            if (intent.getAction() == "com.example.broadcast.DISCONNECT"){
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

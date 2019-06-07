package bib.CeBiTecLEDpanel;

import android.app.Application;

/**
 * Created by markushaak on 23.07.17.
 */

public class AppController extends Application {
    private static AppController mInstance = null;
    private static MyBluetoothService service;

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        mInstance = this;
        startService();
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public void startService(){
        service = new MyBluetoothService(getApplicationContext());
        //start your service

    }
    public void stopService(){
        //stop service
    }
    public MyBluetoothService getService(){
        return service;
    }
}


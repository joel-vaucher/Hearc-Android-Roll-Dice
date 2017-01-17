package ch.hearc.rollanddice;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.content.Context;
import android.content.pm.ConfigurationInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class OpenGLActivity extends Activity {

    /** Hold a reference to our GLSurfaceView */
    private GLSurfaceView mGLSV;
    private MyGLRenderer mGLR;


    public float mAccel = 0.0f; // acceleration apart from gravity
    public float mAccelCurrent = SensorManager.GRAVITY_EARTH; // current acceleration including gravity
    public float mAccelLast = SensorManager.GRAVITY_EARTH; // last acceleration including gravity

    public Handler accelHandler;
    public Handler[] pointerHandler = new Handler[] {accelHandler};


    private Map<Integer, Integer> listRolledDices = new HashMap<Integer, Integer>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mGLSV = new MyGLSurfaceView(this);

        // Check if the system supports OpenGL ES 2.0.
        final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        final ConfigurationInfo configurationInfo = activityManager.getDeviceConfigurationInfo();
        final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000;

        if (supportsEs2)
        {
            mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);


            // Request an OpenGL ES 2.0 compatible context.
            mGLSV.setEGLContextClientVersion(2);

            // Set the renderer to our demo renderer, defined below.
            updateListRolledDices();

            mGLR = new MyGLRenderer(this, pointerHandler, listRolledDices);
            mGLSV.setRenderer(mGLR);
        }
        else
        {
            // This is where you could create an OpenGL ES 1.x compatible
            // renderer if you wanted to support both ES 1 and ES 2.
            return;
        }

        setContentView(mGLSV);
    }

    protected void updateListRolledDices(){
        ArrayList<String> textFaceDices = getIntent().getStringArrayListExtra("textFaceDices");
        ArrayList<String> textNbDices = getIntent().getStringArrayListExtra("textNbDices");

        listRolledDices.clear();
        for(int i = 0; i < textFaceDices.size(); i++){
            int nbFaces = Integer.parseInt(textFaceDices.get(i));
            int nbDices = Integer.parseInt(textNbDices.get(i));
            if(listRolledDices.containsKey(nbFaces)){
                listRolledDices.put(nbFaces, listRolledDices.get(nbFaces) + nbDices);
            }else{
                listRolledDices.put(nbFaces, nbDices);
            }
        }
    }


    @Override
    protected void onResume()
    {
        // The activity must call the GL surface view's onResume() on activity onResume().
        super.onResume();
        mSensorManager.unregisterListener(mSensorListener);
        mGLSV.onResume();
    }

    @Override
    protected void onPause()
    {
        // The activity must call the GL surface view's onPause() on activity onPause().
        super.onPause();
        mSensorManager.registerListener(mSensorListener, mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
        mGLSV.onPause();
    }

    private SensorManager mSensorManager;

    private final SensorEventListener mSensorListener = new SensorEventListener() {

        public void onSensorChanged(SensorEvent se) {
            float x = se.values[0];
            float y = se.values[1];
            float z = se.values[2];
            mAccelLast = mAccelCurrent;
            mAccelCurrent = (float) Math.sqrt((double) (x*x + y*y + z*z));
            float delta = mAccelCurrent - mAccelLast;
            mAccel = (delta/5) + mAccel; // perform low-cut filter
            Log.i("trukkk",Float.toString(x));
            Log.i("trukkk",Float.toString(y));
            Log.i("trukkk",Float.toString(z));
            if(pointerHandler[0] != null) {
                Log.i("trukk",pointerHandler[0].toString());
                Message msg = pointerHandler[0].obtainMessage();
                msg.arg1 = (int)(mAccel*1000);

                pointerHandler[0].sendMessage(msg);
            }
        }

        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
    };
}

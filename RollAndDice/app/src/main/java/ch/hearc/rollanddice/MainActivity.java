package ch.hearc.rollanddice;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.pm.ConfigurationInfo;
import android.content.Context;
import android.content.Intent;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ch.hearc.rollanddice.common.*;
import ch.hearc.rollanddice.MyGLRenderer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends Activity implements LocationListener {
    private TextView textViewResult;
    private EditText nbD4;
    private EditText nbD6;
    private EditText nbD10;
    private EditText nbD20;
    private EditText nbD100;
    private String textSave;

    private LocationManager locationManager;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

      /*  ActionBar actionBar = getActionBar();
        actionBar.show();*/


        textViewResult = (TextView)findViewById(R.id.textViewResult);
        nbD4 = (EditText)findViewById(R.id.editTextD4);
        nbD6 = (EditText)findViewById(R.id.editTextD6);
        nbD10 = (EditText)findViewById(R.id.editTextD10);
        nbD20 = (EditText)findViewById(R.id.editTextD20);
        nbD100 = (EditText)findViewById(R.id.editTextD100);
        Button btnRoll = (Button)findViewById(R.id.buttonRoll);
        btnRoll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                //rollDices();
                createOpenGlView();
            }
        });
        Button btnStats = (Button)findViewById(R.id.buttonStats);
        btnStats.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent demarre = new Intent(MainActivity.this, StatsActivity.class);
                startActivity(demarre);
            }
        });

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        try{
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 2000, 1, this);
            Log.v("Location", "Attempt location");
            Location lastKnownLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            Log.v("Location", lastKnownLocation.toString());
        }catch(SecurityException se){
            Log.v("Location :", "Security Exception");
        }catch(NullPointerException ne){
            //rien
        }




    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("Location", "Permission granted");

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.v("Location", "Permission denied");
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }

    }
    public void createOpenGlView(){
        Intent intent = new Intent(this, OpenGLActivity.class);
        EditText D6 = (EditText)findViewById(R.id.editTextD6);
        intent.putExtra("D6", Integer.parseInt(D6.getText().toString()));
        EditText DX = (EditText)findViewById(R.id.editTextD100);
        intent.putExtra("DX", Integer.parseInt(DX.getText().toString()));
        startActivity(intent);
    }



    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.test:
                return true;
/*
            case R.id.action_search :
                return true;
            case R.id.action_share :
                return true; */
  /*          default:
                return super.onOptionsItemSelected(item);
        }
    }*/

    protected int readNumber(EditText editText){
        if(editText.getText().toString() == "")
            return 0;
        else
            return Integer.parseInt(editText.getText().toString());
    }

    protected void rollDices(){
        textViewResult.setText("");
        textSave = "";
        int total = 0;

        int nbDices = readNumber(nbD4);
        if(nbDices > 0) {
            total += rollOneTypeOfDice(4, nbDices);
        }
        nbDices = readNumber(nbD6);
        if(nbDices > 0) {
            total += rollOneTypeOfDice(6, nbDices);
        }
        nbDices = readNumber(nbD10);
        if(nbDices > 0) {
            total += rollOneTypeOfDice(10, nbDices);
        }
        nbDices = readNumber(nbD20);
        if(nbDices > 0) {
            total += rollOneTypeOfDice(20, nbDices);
        }
        nbDices = readNumber(nbD100);
        if(nbDices > 0) {
            total += rollOneTypeOfDice(100, nbDices);
        }
        textSave += total+ "\n";

        textViewResult.setText(textViewResult.getText() + "\nTotal: " + total);
        saveFile();
    }

    protected int rollOneTypeOfDice(int nbFaces, int nbDices){
        String txt = "D" + nbFaces + ": ";
        textSave += nbFaces + "/";
        int total = 0;
        for (int i = 0; i < nbDices; i++) {
            int result = rollOneDice(nbFaces);
            total += result;
            if(i > 0) {
                txt += ", ";
                textSave += ",";
            }
            textSave += result;
            txt += result;
        }
        textSave += "/" + total + ";";

        txt += "   Total: " + total + "\n";
        textViewResult.setText(textViewResult.getText() + txt);
        return total;
    }

    protected int rollOneDice(int nbFaces){
        return (int)(Math.random()*nbFaces+1);
    }

    protected void saveFile(){
        String filename = "RollAndDiceData";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_APPEND);
            outputStream.write(textSave.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onProviderEnabled(String provider){
        Log.v("Location", "Provider Enable" + provider);
    }

    @Override
    public void onLocationChanged(Location location){
        String msg = "Latitude : " + location.getLatitude() + "Longitude : " + location.getLongitude();
        Log.v("Location" , msg);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
        Log.v("Location" , "Status Changed");
    }

    @Override
    public void onProviderDisabled(String provider){
        Log.v("Location", "Provider Disabled " + provider);
    }

}

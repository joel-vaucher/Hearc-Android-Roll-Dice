/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.hearc.rollanddice;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;

public class MainActivity extends Activity {

    private GLSurfaceView mGLView;
    private TextView textViewResult;
    private EditText nbD4;
    private EditText nbD6;
    private EditText nbD10;
    private EditText nbD20;
    private EditText nbD100;
    private String textSave;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create a GLSurfaceView instance and set it
        // as the ContentView for this Activity.
        //mGLView = new MyGLSurfaceView(this);
       // setContentView(mGLView);
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
                rollDices();
            }
        });

        Button btnStats = (Button)findViewById(R.id.buttonStats);
        btnStats.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent demarre= new Intent(MainActivity.this, StatsActivity.class);
                startActivity(demarre);
            }
        });
    }

    @Override
    protected void onPause() {
        // The following call pauses the rendering thread.
        // If your OpenGL application is memory intensive,
        // you should consider de-allocating objects that
        // consume significant memory here.
        super.onPause();
      //  mGLView.onPause();
    }

    @Override
    protected void onResume() {
        // The following call resumes a paused rendering thread.
        // If you de-allocated graphic objects for onPause()
        // this is a good place to re-allocate them.
        super.onResume();
      //  mGLView.onResume();
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
}
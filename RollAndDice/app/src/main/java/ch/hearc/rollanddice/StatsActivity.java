package ch.hearc.rollanddice;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class StatsActivity extends AppCompatActivity {


    ArrayList<String> stats = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stats);

        readSave();

        ArrayAdapter adapter = new ArrayAdapter<String>(this,
                R.layout.activity_list, stats);

        ListView listView = (ListView) findViewById(R.id.listViewStats);
        listView.setAdapter(adapter);

        Button btnReturn = (Button)findViewById(R.id.buttonReturn);
        btnReturn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                finish();
            }
        });

        Button btnReset = (Button)findViewById(R.id.buttonReset);
        btnReset.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                resetFile();
                finish();
            }
        });
    }

    protected void resetFile(){
        String filename = "RollAndDiceData";
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write("".getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void readSave(){
        File saveFile = new File(getFilesDir(), "RollAndDiceData");

        try {
            BufferedReader br = new BufferedReader(new FileReader(saveFile));
            String line;
            while ((line = br.readLine()) != null) {
                addLine(line);
            }
            br.close();
        } catch (IOException e) {
            Log.i("readSaveError", e.getMessage());
        }
    }

    protected void addLine(String line){
        String renderedLine = "";
        for(String dices : line.split(";")){
            String[] result = dices.split("/");
            if(result.length == 1){
                renderedLine += "\nTotal: " + result[0];
            }else {
                renderedLine += "Dé(s) à " + result[0] + " faces: " + result[1] + ", Total: " + result[2] + "\n";
            }
        }
        stats.add(renderedLine);
    }
}

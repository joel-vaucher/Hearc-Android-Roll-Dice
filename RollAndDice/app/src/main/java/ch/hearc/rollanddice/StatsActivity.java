package ch.hearc.rollanddice;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.File;
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
    }



    protected void readSave(){
        File saveFile = new File(getFilesDir(), "RollAndDiceData");

        // if(saveFile.exists()) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(saveFile));
            String line;
          //  Log.i("readSave", "ok");
            while ((line = br.readLine()) != null) {
               // Log.i("readSave", line);
                addLine(line);
            }
            br.close();
        } catch (IOException e) {
            Log.i("readSaveError", e.getMessage());
        }
        // }
    }

    protected void addLine(String line){
        String renderedLine = "";
        for(String dices : line.split(";")){
            String[] result = dices.split("/");
            if(result.length == 1){
                renderedLine += "\nTotal: " + result[0];
            }else {
                renderedLine += "Dé(s) à " + result[0] + " face(s): " + result[1] + ", Total: " + result[2] + "\n";
            }
        }
        stats.add(renderedLine);
    }
}

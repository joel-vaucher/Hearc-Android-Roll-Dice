package ch.hearc.rollanddice;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ch.hearc.rollanddice.common.*;
import ch.hearc.rollanddice.MyGLRenderer;

public class MainActivity extends Activity
{
    private TextView textViewResult;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        textViewResult = (TextView)findViewById(R.id.textViewResult);
        Button btnA = (Button)findViewById(R.id.buttonRoll);
        btnA.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                createOpenGlView();
            }
        });
    }
    public void createOpenGlView(){
        Intent intent = new Intent(this, OpenGLActivity.class);
        EditText D6 = (EditText)findViewById(R.id.editTextD6);
        intent.putExtra("D6", Integer.parseInt(D6.getText().toString()));
        startActivity(intent);
    }

}
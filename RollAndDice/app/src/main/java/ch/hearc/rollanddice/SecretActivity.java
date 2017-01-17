package ch.hearc.rollanddice;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class SecretActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secret);

        Button btnValidate = (Button)findViewById(R.id.buttonValidate);
        btnValidate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                TextView mail = (TextView)findViewById(R.id.editTextMail);
                TextView name = (TextView)findViewById(R.id.editTextName);

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("mail", mail.getText().toString());
                editor.putString("name", name.getText().toString());

                editor.commit();

                finish();
            }
        });
    }
}

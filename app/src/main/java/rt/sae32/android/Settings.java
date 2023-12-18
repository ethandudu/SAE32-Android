package rt.sae32.android;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class Settings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);

        if (sharedPreferences.getBoolean("macResolution", false)){
            SwitchMaterial macResolution = findViewById(R.id.macresolution);
            macResolution.setChecked(true);
        }
        EditText serverUrl = findViewById(R.id.serverUrl);
        serverUrl.setText(sharedPreferences.getString("serverUrl", ""));

    }
    public void returnToMainActivity(View view){
        super.finish();
    }

    public void saveSettings(View view){
        SwitchMaterial macResolution = findViewById(R.id.macresolution);
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("macResolution", macResolution.isChecked());
        EditText serverUrl = findViewById(R.id.serverUrl);
        editor.putString("serverUrl", serverUrl.getText().toString());
        editor.apply();
        super.finish();
    }


}
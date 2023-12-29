package rt.sae32.android;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

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

        SwitchMaterial darkSwitch = findViewById(R.id.darktheme);
        if (sharedPreferences.getBoolean("darkMode", false)){
            darkSwitch.setChecked(true);
        }
        EditText serverUrl = findViewById(R.id.serverUrl);
        serverUrl.setText(sharedPreferences.getString("serverUrl", ""));

        EditText token = findViewById(R.id.token);
        token.setText(sharedPreferences.getString("authorizedToken", ""));

        darkSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

    }
    public void returnToMainActivity(View view){
        super.finish();
    }

    public void saveSettings(View view){
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        SwitchMaterial macResolution = findViewById(R.id.macresolution);
        editor.putBoolean("macResolution", macResolution.isChecked());

        EditText serverUrl = findViewById(R.id.serverUrl);
        editor.putString("serverUrl", serverUrl.getText().toString());

        EditText token = findViewById(R.id.token);
        editor.putString("authorizedToken", token.getText().toString());

        SwitchMaterial darkSwitch = findViewById(R.id.darktheme);
        editor.putBoolean("darkMode", darkSwitch.isChecked());

        editor.apply();
        super.finish();
    }
}
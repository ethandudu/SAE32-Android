package rt.sae32.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

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
    }
    public void returnToMainActivity(View view){
        super.finish();
    }

    public void saveSettings(View view){
        SwitchMaterial macResolution = findViewById(R.id.macresolution);
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("macResolution", macResolution.isChecked());
        editor.apply();
        System.out.println("macResolutionSwitch : " + macResolution.isChecked());
        System.out.println("macResolution : " + sharedPreferences.getBoolean("macResolution", false));
        super.finish();
    }
}
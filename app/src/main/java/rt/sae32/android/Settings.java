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
        System.out.println(sharedPreferences.getBoolean("ouiDownloaded", false));

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

    public void macSwitchChange(View v){
        SwitchMaterial macResolution = findViewById(R.id.macresolution);
        if (macResolution.isChecked()){
            SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
            if (!sharedPreferences.getBoolean("ouiDownloaded", false)) {
                System.out.println("oui");
                //show a dialog to ask if the user wants to download the oui file
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Pour utiliser la résolution du constructeur de l'adresse MAC, le téléchargement de la base de données est requis. Voulez-vous le faire maintenant ?")
                        .setPositiveButton("Oui", (dialog, id) -> {
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putBoolean("ouiDownloaded", true);
                            editor.apply();
                            DownloadOuiFile();
                        })
                        .setNegativeButton("Non", (dialog, id) -> {
                            macResolution.setChecked(false);
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
    }
    public void DownloadOuiFile(){
        DownloadFile downloadFile = new DownloadFile();
        downloadFile.execute("https://api.sae32.ethanduault.fr/oui.json", getFilesDir().getAbsolutePath() + "/oui.json");

    }
}
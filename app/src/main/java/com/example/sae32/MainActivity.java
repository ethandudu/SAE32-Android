package com.example.sae32;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.example.sae32.HttpRequest;

public class MainActivity extends AppCompatActivity {

    private ListView laListe;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        laListe= findViewById(R.id.idListeView);
        laListe.setOnItemClickListener(this::onItemClick);//onItemClick);

        String url = "https://api.sae32.ethanduault.fr/tests.php";
        HttpRequest request = new HttpRequest();
        String response = "";
        try {
            response = request.execute(url).get();
        } catch (Exception e) { e.printStackTrace(); }
        System.out.println("Response get " + response);
    }

    public void onItemClick (AdapterView<?> p, View v, int pos, long id){
        Intent intent= new Intent(this, ProtocolInfo.class);
        startActivity(intent);
    }
}
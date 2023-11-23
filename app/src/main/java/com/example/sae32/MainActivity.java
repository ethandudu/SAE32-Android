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

public class MainActivity extends AppCompatActivity {

    private ListView laListe;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        laListe= findViewById(R.id.idListeView);
        laListe.setOnItemClickListener(this::onItemClick);//onItemClick);
    }

    public void onItemClick (AdapterView<?> p, View v, int pos, long id){
        Intent intent= new Intent(this, ProtocolInfo.class);
        startActivity(intent);
    }

}
package com.example.sae32;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;

import android.content.Intent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView laListe = findViewById(R.id.idListeView);
        laListe.setOnItemClickListener(this::onItemClick);
        refreshData(findViewById(R.id.refresh));
    }

    public void onItemClick (AdapterView<?> p, View v, int pos, long id){
        Intent intent= new Intent(this, ProtocolInfo.class);
        intent.putExtra("id", pos);
        startActivity(intent);
    }


    public void refreshData(View view) {
        Toast.makeText(this, "Actualisation ...", Toast.LENGTH_SHORT).show();

        String url = "https://api.sae32.ethanduault.fr/tests.php";
        HttpRequest request = new HttpRequest();
        String response = "";

        try {
            response = request.execute(url).get();
        } catch (Exception e) { e.printStackTrace(); }

        ArrayList<String> items = new ArrayList<>();
        ArrayAdapter<String> adapter;

        try {
            JSONObject json = new JSONObject(response);
            JSONArray array = json.getJSONArray("data");
            String[] tests = new String[array.length()];
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                String id = item.getString("ID");
                String name = item.getString("name");
                //add to array
                tests[i] = id + " - " + name;
            }

            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tests);
            ListView laListe = findViewById(R.id.idListeView);
            laListe.setAdapter(adapter);
        } catch (JSONException e) { e.printStackTrace(); }
    }
}
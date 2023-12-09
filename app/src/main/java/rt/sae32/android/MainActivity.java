package rt.sae32.android;

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

    private String[] tests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView laListe = findViewById(R.id.idListeView);
        laListe.setOnItemClickListener(this::onItemClick);
        refreshData(findViewById(R.id.refresh));
    }

    public void onItemClick (AdapterView<?> p, View v, int pos, long id){
        //parsing the string to get the id of the test
        String[] parts = tests[pos].split(" - ");
        String idTest = parts[0];
        Intent intent= new Intent(this, ListePaquets.class);
        intent.putExtra("idtest", idTest);
        intent.putExtra("testfullname", tests[pos]);
        startActivity(intent);
    }


    /**
     * @param view the view that called the method
     */
    public void refreshData(View view) {
        Toast.makeText(this, "Actualisation ...", Toast.LENGTH_SHORT).show();

        //prepare the request
        String url = getString(R.string.testUrl);
        HttpRequest request = new HttpRequest();
        String response = "";

        try {
            response = request.execute(url, "GET").get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response.startsWith("Error")) {
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            return;
        }

        ArrayList<String> items = new ArrayList<>();
        ArrayAdapter<String> adapter;

        try {
            //read the json
            JSONObject json = new JSONObject(response);
            JSONArray array = json.getJSONArray("data");
            tests = new String[array.length()];
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                String id = item.getString("ID");
                String name = item.getString("name");
                tests[i] = id + " - " + name;
            }

            //add the data to the list and display it
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tests);
            ListView laListe = findViewById(R.id.idListeView);
            laListe.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
package rt.sae32.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListePaquets extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liste_paquets);

        TextView testName = findViewById(R.id.textView2);
        testName.setText(String.format("Test n°%s", getIntent().getStringExtra("testfullname")));
        getData();
    }

    public void returnToMainActivity(View view){
        super.finish();
    }
    private void getData(){
        //prepare the request
        String url = getString(R.string.packetsUrl) + "?fileid=" + getIntent().getStringExtra("idtest");
        System.out.println(url);
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
            String[] packets = new String[array.length()];
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                String id = item.getString("ID");
                String protocols = item.getString("protocols");
                System.out.println(id + " - " + protocols);
                packets[i] = id + " - " + protocols;
            }

            //add the data to the list and display it
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, packets);
            ListView laListe = findViewById(R.id.listview);
            laListe.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
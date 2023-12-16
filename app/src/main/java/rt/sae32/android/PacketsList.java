package rt.sae32.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.Future;


public class PacketsList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packetslist);

        TextView testName = findViewById(R.id.textView2);
        testName.setText(String.format("Test n°%s", getIntent().getStringExtra("testFullname")));
        getData();

        ListView laListe = findViewById(R.id.listview);
        laListe.setOnItemClickListener(this::onItemClick);
    }

    public void onItemClick (AdapterView<?> p, View v, int pos, long id){
        //parsing the string to get the id of the test
        String[] parts = p.getItemAtPosition(pos).toString().split(" - ");
        String idPacket = parts[0];
        Intent intent = new Intent(this, PacketDetails.class);
        intent.putExtra("idPacket", idPacket);
        intent.putExtra("idTest", getIntent().getStringExtra("idTest"));
        startActivity(intent);
    }

    public void returnToMainActivity(View view){
        super.finish();
    }

    private void getData(){
        //prepare the request
        String url = getString(R.string.packetsUrl) + "?fileid=" + getIntent().getStringExtra("idTest");
        Future<String> request = HttpRequest.execute(url,"GET");
        String response = "";

        try {
            response = request.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response.startsWith("Error")) {
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            return;
        }

        readResponse(response);

    }

    private void readResponse(String response){
        ArrayAdapter<String> adapter;

        try {
            //read the json
            JSONObject json = new JSONObject(response);
            JSONArray array = json.getJSONArray("data");
            String[] packets = new String[array.length()];
            for (int i = 0; i < array.length(); i++) {
                JSONObject item = array.getJSONObject(i);
                String id = item.getString("packetid");
                StringBuilder protocolString = new StringBuilder();
                JSONArray protocols = new JSONArray(array.getJSONObject(0).getString("protocols"));
                for (int i2 = 0; i2 < protocols.length(); i2++) {
                    protocolString.append(protocols.getString(i2)).append(" ");
                }
                packets[i] = id + " - " + protocolString;
            }

            //add the data to the list and display it
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, packets);
            ListView laListe = findViewById(R.id.listview);
            laListe.setAdapter(adapter);

        } catch (JSONException e) {
            Toast.makeText(this, "Une erreur est survenue ...", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }
}
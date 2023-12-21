package rt.sae32.android;

import static android.app.usage.UsageEvents.Event.NONE;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.content.Intent;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class MainActivity extends AppCompatActivity {

    private String[] tests;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView laListe = findViewById(R.id.idListeView);
        laListe.setOnItemClickListener(this::onItemClick);
        checkSettings();
        refreshData(true);
        registerForContextMenu(laListe);
    }

    private void onItemClick (AdapterView<?> p, View v, int pos, long id){
        //parsing the string to get the id of the test
        String[] parts = tests[pos].split(" - ");
        String idTest = parts[0];
        Intent intent= new Intent(this, PacketsList.class);
        intent.putExtra("idTest", idTest);
        intent.putExtra("testFullname", tests[pos]);
        startActivity(intent);
    }


    public void openSettings(View v){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
    public void refreshData(Boolean init) {
        if (!init) {
            Toast.makeText(this, "Actualisation ...", Toast.LENGTH_SHORT).show();
        }
        String server = ServerUrl.getServerUrl(this);
        //prepare the request
        String url = server + "/tests.php";
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

    private void readResponse(String response) {
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
        } catch (JSONException e){
            Toast.makeText(this, "Une erreur est survenue ...", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    public void onCreateContextMenu(ContextMenu m, View v, ContextMenu.ContextMenuInfo i) {
        super.onCreateContextMenu(m, v, i);
        m.setHeaderTitle("Options");
        m.add(NONE, v.getId(), 1, "Modifier");
        m.add(NONE, v.getId(), 2, "Supprimer");
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        if (item.getTitle()=="Modifier"){
            assert info != null;
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Modifier le nom du test");
            builder.setMessage("Entrez le nouveau nom du test");
            final EditText input = new EditText(this);
            builder.setView(input);
            builder.setCancelable(true);
            builder.setPositiveButton("Valider", (dialog, which) -> {
                String[] parts = tests[info.position].split(" - ");
                String idTest = parts[0];
                String url = ServerUrl.getServerUrl(MainActivity.this) + getString(R.string.modifyUrl) + "?fileid=" + idTest + "&name=" + input.getText().toString();
                Future<String> request = HttpRequest.execute(url,"GET");
                String response;
                try {
                    response = request.get();
                    //read the json
                    System.out.println(response);
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("responsecode");
                    if (status.equals("200")) {
                        Toast.makeText(getApplicationContext(),"Modification réussie",Toast.LENGTH_LONG).show();
                        refreshData(false);
                    } else {
                        Toast.makeText(getApplicationContext(),"Erreur lors de la modification",Toast.LENGTH_LONG).show();
                    }
                } catch (ExecutionException | InterruptedException | JSONException e) {
                    throw new RuntimeException(e);
                }
            });
            builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
            builder.show();

        }
        else if (item.getTitle()=="Supprimer") {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Supprimer le test");
            builder.setMessage("Êtes-vous sûr de vouloir supprimer ce test ?");
            builder.setCancelable(true);
            builder.setPositiveButton("Valider", (dialog, which) -> {
                assert info != null;
                String[] parts = tests[info.position].split(" - ");
                String idTest = parts[0];
                String url = ServerUrl.getServerUrl(MainActivity.this) + getString(R.string.deleteTestUrl) + "?fileid=" + idTest;
                Future<String> request = HttpRequest.execute(url,"GET");
                String response;
                try {
                    response = request.get();
                    //read the json
                    JSONObject json = new JSONObject(response);
                    String status = json.getString("responsecode");
                    if (status.equals("200")) {
                        Toast.makeText(getApplicationContext(),"Suppression réussie",Toast.LENGTH_LONG).show();
                        refreshData(false);
                    } else {
                        Toast.makeText(getApplicationContext(),"Erreur lors de la suppression",Toast.LENGTH_LONG).show();
                    }
                } catch (ExecutionException | InterruptedException | JSONException e) {
                    throw new RuntimeException(e);
                }
            });
            builder.setNegativeButton("Annuler", (dialog, which) -> dialog.cancel());
            builder.show();
        } else {
            return false;
        }
        return false;
    }

    private void checkSettings() {
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        if (!sharedPreferences.contains("firstStart")) {
            Intent intent = new Intent(this, FirstStart.class);
            startActivity(intent);
            finish();
        }

    }

    public void refreshData(View view) {
        refreshData(false);
    }
}
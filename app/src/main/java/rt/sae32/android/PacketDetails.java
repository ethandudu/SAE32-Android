package rt.sae32.android;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;

public class PacketDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packetdetails);
        getData();
        TextView paquetName = findViewById(R.id.packet);
        paquetName.setText(String.format("Paquet n°%s", getIntent().getStringExtra("idPacket")));
        TextView editText = findViewById(R.id.editTextTextMultiLine);
        editText.setEnabled(false);
    }

    public void returnToPreviousActivity(View view){
        super.finish();
    }

    public void getData() {
        //prepare the request
        String url = getString(R.string.packetsUrl) + "?fileid=" + getIntent().getStringExtra("idTest") + "&packetid=" + getIntent().getStringExtra("idPacket");
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
        try {
            //read the json
            JSONObject json = new JSONObject(response);
            JSONArray array = json.getJSONArray("data");
            TextView protocol = findViewById(R.id.protocol);
            TextView macsrc = findViewById(R.id.macSource);
            TextView macdst = findViewById(R.id.macDest);


            StringBuilder protocolString = new StringBuilder();
            JSONArray protocols = new JSONArray(array.getJSONObject(0).getString("protocols"));
            boolean isIP = false;
            for (int i = 0; i < protocols.length(); i++) {
                protocolString.append(protocols.getString(i)).append(" ");
                if (protocols.getString(i).equals("ip") || protocols.getString(i).equals("ipv6")) {
                    isIP = true;
                }
            }

            protocol.setText(MessageFormat.format("{0}{1}", getString(R.string.protocols), protocolString.toString()));

            if (checkSettings()) {
                String macsrchex = array.getJSONObject(0).getString("macsrc");
                macsrchex = macsrchex.replace(":", "");
                macsrchex = macsrchex.substring(0, 6);
                String macdsthex = array.getJSONObject(0).getString("macdst");
                macdsthex = macdsthex.replace(":", "");
                macdsthex = macdsthex.substring(0, 6);
                System.out.println(macsrchex);
                System.out.println(macdsthex);

                Map<String, String> values = macVendorResolution(macsrchex, macdsthex);

                if (Objects.equals(values.get("vendorsrc"), "null")){
                    macsrc.setText(MessageFormat.format("{0}{1}", getString(R.string.mac_src), array.getJSONObject(0).getString("macsrc")));
                } else {
                    macsrc.setText(MessageFormat.format("{0}{1} ({2})", getString(R.string.mac_src), array.getJSONObject(0).getString("macsrc"), values.get("vendorsrc")));
                }
                if (Objects.equals(values.get("vendordst"), "null")){
                    macdst.setText(MessageFormat.format("{0}{1}", getString(R.string.mac_dest), array.getJSONObject(0).getString("macdst")));
                } else {
                    macdst.setText(MessageFormat.format("{0}{1} ({2})", getString(R.string.mac_dest), array.getJSONObject(0).getString("macdst"), values.get("vendordst")));
                }

            } else{
                macsrc.setText(MessageFormat.format("{0}{1}", getString(R.string.mac_src), array.getJSONObject(0).getString("macsrc")));
                macdst.setText(MessageFormat.format("{0}{1}", getString(R.string.mac_dest), array.getJSONObject(0).getString("macdst")));
            }

            TextView ipsrc = findViewById(R.id.ipSource);
            TextView ipdst = findViewById(R.id.ipDest);

            if (isIP) {
                ipsrc.setText(MessageFormat.format("{0}{1}", getString(R.string.ip_src), array.getJSONObject(0).getString("ipsrc")));
                ipdst.setText(MessageFormat.format("{0}{1}", getString(R.string.ip_dest), array.getJSONObject(0).getString("ipdst")));
            } else {
                ipsrc.setVisibility(View.GONE);
                ipdst.setVisibility(View.GONE);
            }

        } catch (JSONException e){
            Toast.makeText(this, "Une erreur est survenue ...", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private boolean checkSettings(){
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        return sharedPreferences.getBoolean("macResolution", false);
    }
    private Map<String, String> macVendorResolution(String macsrc, String macdst){
        //read the oui json file from the internal storage
        macsrc = macsrc.toUpperCase();
        macdst = macdst.toUpperCase();
        String vendorsrc = "null";
        String vendordst = "null";
        try {
            FileInputStream fis = openFileInput("oui.json");
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader br = new BufferedReader(isr);
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
            isr.close();
            fis.close();

            String json = sb.toString();
            JSONArray array = new JSONArray(json);
            Map<String, String> macToVendorMap = new HashMap<>();
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                macToVendorMap.put(object.getString("Assignment"), object.getString("Organization Name"));
            }
            vendorsrc = macToVendorMap.getOrDefault(macsrc, "null");
            vendordst = macToVendorMap.getOrDefault(macdst, "null");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Map.of("vendorsrc", vendorsrc, "vendordst", vendordst);
    }
}
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

import java.text.MessageFormat;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;

public class PacketDetails extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_packetdetails);

        String response = getData(getIntent().getStringExtra("idTest"), getIntent().getStringExtra("idPacket"));
        readResponse(response);

        TextView packetName = findViewById(R.id.packet);
        packetName.setText(String.format("Paquet n°%s", getIntent().getStringExtra("idPacket")));

        TextView editText = findViewById(R.id.editTextTextMultiLine);
        editText.setEnabled(false);
    }

    /**
     * Return to the previous activity
     * @param view the view of the button
     */
    public void returnToPreviousActivity(View view){
        super.finish();
    }

    /**
     * Get the data from the API
     * @param idTest the id of the test
     * @param idPacket the id of the packet
     * @return the response of the API
     */
    public String getData(String idTest, String idPacket) {
        //prepare the request
        String server = ServerUrl.getServerUrl(this);
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        String token = sharedPreferences.getString("authorizedToken", "");
        String url = server + getString(R.string.packetsUrl) + "?fileid=" + idTest + "&packetid=" + idPacket;
        Future<String> request = HttpRequest.execute(url,"GET", token);
        String response = "";

        try {
            response = request.get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (response.startsWith("Error")) {
            Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
            return null;
        }

        return response;
    }

    /**
     * Read the response and set the text of the textviews
     * @param response the response of the API
     */
    private void readResponse(String response) {
        try {
            //read the json
            JSONObject json = new JSONObject(response);
            JSONArray array = json.getJSONArray("data");
            TextView protocol = findViewById(R.id.protocol);
            TextView macsrc = findViewById(R.id.macSource);
            TextView macdst = findViewById(R.id.macDest);
            TextView data = findViewById(R.id.editTextTextMultiLine);


            StringBuilder protocolString = new StringBuilder();
            JSONArray protocols = new JSONArray(array.getJSONObject(0).getString("protocols"));
            boolean isIP = false;

            for (int i = 0; i < protocols.length(); i++) {
                protocolString.append(protocols.getString(i)).append(" ");
                if (protocols.getString(i).equals("ip") || protocols.getString(i).equals("ipv6")) {
                    isIP = true;
                }
            }

            if (array.getJSONObject(0).getString("data").length() > 0) {
                data.setText(formatData(array.getJSONObject(0).getString("data")));
            } else {
                data.setVisibility(View.GONE);
            }

            protocol.setText(MessageFormat.format("{0}{1}", getString(R.string.protocols), protocolString.toString()));

            if (checkSettings()) {
                String macsrchex = array.getJSONObject(0).getString("macsrc");
                macsrchex = macsrchex.replace(":", "");
                macsrchex = macsrchex.substring(0, 6);
                String macdsthex = array.getJSONObject(0).getString("macdst");
                macdsthex = macdsthex.replace(":", "");
                macdsthex = macdsthex.substring(0, 6);

                Map<String, String> values = macVendorResolution(macsrchex, macdsthex);

                if (Objects.equals(values.get("vendorsrc"), "Unknown")){
                    macsrc.setText(MessageFormat.format("{0}{1}", getString(R.string.mac_src), array.getJSONObject(0).getString("macsrc")));
                } else {
                    macsrc.setText(MessageFormat.format("{0}{1} ({2})", getString(R.string.mac_src), array.getJSONObject(0).getString("macsrc"), values.get("vendorsrc")));
                }
                if (Objects.equals(values.get("vendordst"), "Unknown")){
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

    /**
     * Format the JSON data to be more readable
     * @param data the data to format from the JSON object
     * @return the formatted data as human readable string
     */
    private String formatData(String data) throws JSONException {
        JSONObject json = new JSONObject(data);
        return json.toString(4);
    }


    /**
     * Check if the mac resolution is enabled in the settings
     * @return true if the mac resolution is enabled, false otherwise
     */
    private boolean checkSettings(){
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        return sharedPreferences.getBoolean("macResolution", false);
    }

    /**
     * Resolve the mac addresses to get the vendor via the API
     * @param macsrc the source mac address
     * @param macdst the destination mac address
     * @return a map with the vendors of the source and destination mac addresses. If the vendor is unknown, the value is "Unknown"
     */
    private Map<String, String> macVendorResolution(String macsrc, String macdst){
        macsrc = macsrc.toUpperCase();
        macdst = macdst.toUpperCase();
        String vendorsrc = "Unknown";
        String vendordst = "Unknown";

        //prepare the request
        String server = ServerUrl.getServerUrl(this);
        String url = server + getString(R.string.macVendorUrl) + "?macsrc=" + macsrc + "&macdst=" + macdst;
        SharedPreferences sharedPreferences = getSharedPreferences("settings", MODE_PRIVATE);
        String token = sharedPreferences.getString("authorizedToken", "");
        Future<String> request = HttpRequest.execute(url,"GET", token);
        String response;

        try {
            response = request.get();

            if (response.startsWith("Error")) {
                Toast.makeText(this, response, Toast.LENGTH_SHORT).show();
                return Map.of("vendorsrc", vendorsrc, "vendordst", vendordst);
            }

            JSONObject json = new JSONObject(response);
            JSONObject object = json.getJSONObject("data");
            vendorsrc = object.getString("vendorsrc");
            vendordst = object.getString("vendordst");

        } catch (Exception e) {
            e.printStackTrace();
        }
        return Map.of("vendorsrc", vendorsrc, "vendordst", vendordst);
    }
}
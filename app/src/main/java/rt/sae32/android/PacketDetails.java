package rt.sae32.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
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
                if (protocols.getString(i).equals("ip") || protocols.getString(i).equals("ipv6")){
                    isIP = true;
                }
            }


            protocol.setText(MessageFormat.format("{0}{1}", getString(R.string.protocols), protocolString.toString()));
            macsrc.setText(MessageFormat.format("{0}{1}", getString(R.string.mac_src), array.getJSONObject(0).getString("macsrc")));
            macdst.setText(MessageFormat.format("{0}{1}", getString(R.string.mac_dest), array.getJSONObject(0).getString("macdst")));


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
}
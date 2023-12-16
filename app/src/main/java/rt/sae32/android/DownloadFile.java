package rt.sae32.android;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DownloadFile extends AsyncTask<String, Void, String> {
    private static final String TAG = "DownloadFileTask";

    @Override
    protected String doInBackground(String... strings) {
        String fileUrl = strings[0];
        String destinationFilePath = strings[1];

        try {
            URL url = new URL(fileUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            // Vérifie si la connexion est réussie
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                Log.e(TAG, "Erreur de connexion : " + connection.getResponseCode() +
                        " " + connection.getResponseMessage());
                return null;
            }

            // Obtient la taille du fichier
            connection.getContentLength();

            // Téléchargement du fichier
            InputStream input = new BufferedInputStream(url.openStream());
            FileOutputStream output = new FileOutputStream(destinationFilePath);

            byte[] data = new byte[1024];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
            return "success";

        } catch (IOException e) {
            return "error";
        }
    }
}
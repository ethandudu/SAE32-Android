package com.example.sae32;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * Do a HTTP request to the server, and return the response.
 * Takes as parameter the url and the method
 */
public class HttpRequest extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String ... params){
        String url = params[0];
        String method = params[1];
        String response = "";
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod(method);
            con.setRequestProperty("Authorization", "wJb~9E@ttr#{%?K7");
            con.setRequestProperty("User-Agent", "Android/SAE32-App");
            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                //read the response
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String inputLine;
                StringBuilder responseBuffer = new StringBuilder();
                while ((inputLine = in.readLine()) != null) {
                    responseBuffer.append(inputLine);
                }
                in.close();
                response = responseBuffer.toString();
            } else {
                response = "Error: " + responseCode;
            }



        } catch (IOException e) {
            e.printStackTrace();
            response = "Error: " + e.getMessage();
        }
        return response;
    }
}
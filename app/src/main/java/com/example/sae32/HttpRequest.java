package com.example.sae32;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

//make a GET request to the specified URL
//return the response as a string
public class HttpRequest extends AsyncTask<String, Void, String> {
    @Override
    protected String doInBackground(String... params) {
        String url = params[0];
        System.out.println("URL get " + url);
        String response = "";
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("Authorization", "wJb~9E@ttr#{%?K7");
            int responseCode = con.getResponseCode();
            System.out.println("Response code get " + responseCode);

            //read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            StringBuffer responseBuffer = new StringBuffer();
            while ((inputLine = in.readLine()) != null) {
                responseBuffer.append(inputLine);
            }
            in.close();
            response = responseBuffer.toString();


        } catch (IOException e) { e.printStackTrace(); }
        System.out.println("Response send to front");
        return response;
    }
}
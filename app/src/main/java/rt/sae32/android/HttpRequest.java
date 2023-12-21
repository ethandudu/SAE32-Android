package rt.sae32.android;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Do a HTTP request to the server, and return the response.
 * Takes as parameter the url and the method
 */
public class HttpRequest implements Callable<String> {
    private final String url;
    private final String method;
    private final String token;

    public HttpRequest(String url, String method, String token) {
        this.url = url;
        this.method = method;
        this.token = token;
    }

    @Override
    public String call() {
        String response;
        try {
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();
            con.setRequestMethod(method);
            con.setRequestProperty("Authorization", token);
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

    public static Future<String> execute(String url, String method, String token) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<String> future = executor.submit(new HttpRequest(url, method, token));
        executor.shutdown(); // it is very important to shutdown your non-singleton ExecutorService.
        return future;
    }
}
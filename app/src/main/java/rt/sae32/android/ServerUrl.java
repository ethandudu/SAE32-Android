package rt.sae32.android;

import android.content.Context;
import android.content.SharedPreferences;

public class ServerUrl {

    /**
     * Get the url of the server from the settings
     * @param context the context of the application
     * @return the url of the server
     */
    public static String getServerUrl(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return prefs.getString("serverUrl", "https://api.sae32.ethanduault.fr");
    }
}
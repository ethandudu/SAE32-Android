package rt.sae32.android;

import android.content.Context;
import android.content.SharedPreferences;

public class ServerUrl {
    public static String getServerUrl(Context context) {
        return urlsettings(context);
    }

    private static String urlsettings(Context context){
        SharedPreferences prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE);
        return prefs.getString("serverUrl", "https://api.sae32.ethanduault.fr");
    }
}
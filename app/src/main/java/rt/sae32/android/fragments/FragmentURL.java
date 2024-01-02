package rt.sae32.android.fragments;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import rt.sae32.android.R;

public class FragmentURL extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_u_r_l, container, false);
        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("settings", MODE_PRIVATE);


        Button buttonNext = view.findViewById(R.id.next);
        Button buttonBack = view.findViewById(R.id.previous);

        EditText urlText = view.findViewById(R.id.url);
        EditText tokenText = view.findViewById(R.id.token);


        // check if the serverUrl and authorizedToken was already set but the setup was not finished
        if (sharedPreferences.contains("serverUrl")) {
            urlText.setText(sharedPreferences.getString("serverUrl", ""));
        }

        if (sharedPreferences.contains("authorizedToken")) {
            tokenText.setText(sharedPreferences.getString("authorizedToken", ""));
        }

        // listen for the click on the next button
        buttonNext.setOnClickListener(v -> {
            if (!checkEmpty(urlText.getText().toString(),tokenText.getText().toString())) {
                Toast.makeText(requireActivity(), "Merci de remplir tous les éléments", Toast.LENGTH_SHORT).show();
                return;
            } else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("serverUrl", urlText.getText().toString());
                editor.putString("authorizedToken", tokenText.getText().toString());
                editor.apply();
            }

            FragmentSettings fragmentSettings = new FragmentSettings();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragmentSettings);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        // listen for the click on the back button
        buttonBack.setOnClickListener(v -> {
            FragmentWelcome fragmentWelcome = new FragmentWelcome();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragmentWelcome);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    /**
     * Check if the url and the token are not empty
     * @param url the url to check
     * @param token the token to check
     * @return true if the url and the token are not empty
     */
    private Boolean checkEmpty(String url, String token) {
        boolean emptyURL = url.isEmpty();
        boolean emptyToken = token.isEmpty();

        return !emptyURL && !emptyToken;
    }
}
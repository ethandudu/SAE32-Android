package rt.sae32.android.Fragments;

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


        Button buttonnext = view.findViewById(R.id.next);
        Button buttonback = view.findViewById(R.id.previous);

        EditText editText = view.findViewById(R.id.url);

        if (sharedPreferences.contains("serverUrl")) {
            editText.setText(sharedPreferences.getString("serverUrl", ""));
        }

        buttonnext.setOnClickListener(v -> {
            if (!checkUrl(editText.getText().toString())) {
                Toast.makeText(requireActivity(), "URL Invalide", Toast.LENGTH_SHORT).show();
                return;
            } else {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("serverUrl", editText.getText().toString());
                editor.apply();
            }


            FragmentSettings fragmentSettings = new FragmentSettings();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragmentSettings);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        buttonback.setOnClickListener(v -> {
            FragmentWelcome fragmentWelcome = new FragmentWelcome();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragmentWelcome);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }

    private boolean checkUrl(String url) {
        return url != null && !url.isEmpty();
    }
}
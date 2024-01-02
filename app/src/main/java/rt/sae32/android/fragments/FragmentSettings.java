package rt.sae32.android.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.switchmaterial.SwitchMaterial;

import rt.sae32.android.R;

public class FragmentSettings extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button buttonNext = view.findViewById(R.id.next);
        Button buttonBack = view.findViewById(R.id.previous);

        SwitchMaterial macResolution = view.findViewById(R.id.macresolution);
        SwitchMaterial darkSwitch = view.findViewById(R.id.darktheme);

        // check if the OS is in dark mode
        if ((getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES) {
            darkSwitch.setChecked(true);
        }

        // listen the changes on the dark mode switch
        darkSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        });

        // listen for the click on the next button
        buttonNext.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("settings", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("darkMode", darkSwitch.isChecked());
            editor.putBoolean("macResolution", macResolution.isChecked());
            editor.putBoolean("firstStart", true);
            editor.apply();

            Intent intent = new Intent(requireActivity(), rt.sae32.android.MainActivity.class);
            startActivity(intent);
            requireActivity().finish();
        });

        // listen for the click on the back button
        buttonBack.setOnClickListener(v -> {
            FragmentURL fragmentURL = new FragmentURL();
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragment_container, fragmentURL);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }
}
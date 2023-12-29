package rt.sae32.android.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import rt.sae32.android.R;

public class FragmentWelcome extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_welcome, container, false);

        Button button = view.findViewById(R.id.next);
        button.setOnClickListener(v -> {
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
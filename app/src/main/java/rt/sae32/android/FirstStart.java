package rt.sae32.android;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import rt.sae32.android.fragments.FragmentWelcome;

public class FirstStart extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_start);

        //check if the activity is restarted due to theme change, if not, add the welcome fragment
        if (savedInstanceState == null){
        FragmentWelcome fragmentWelcome = new FragmentWelcome();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragmentWelcome)
                .commit();
        }
    }
}
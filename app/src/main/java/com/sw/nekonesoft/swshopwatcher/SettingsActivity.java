package com.sw.nekonesoft.swshopwatcher;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by kishimoto on 2016/12/02.
 */

public class SettingsActivity extends AppCompatActivity implements SettingsFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
    }

}

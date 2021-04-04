package com.example.lab_net;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import java.io.Serializable;
import java.util.ArrayList;

public class plotLocActivity extends AppCompatActivity implements Serializable {

    private ArrayList<Double> locations = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot_loc);

        locations.add(0.696969);


        Fragment fragment = new plotLocFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("locations",locations);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.map_layout_PL,fragment).commit();
    }
}
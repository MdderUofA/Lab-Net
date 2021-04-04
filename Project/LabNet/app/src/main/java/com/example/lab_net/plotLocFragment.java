package com.example.lab_net;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class plotLocFragment extends Fragment {

    private static final String TAG = "TESTING";
    private ArrayList<Double> latitude;
    private ArrayList<Double> longitude;
    private ArrayList<String> trialName;

    public void putLocationOnMap(double latitude, double longitude, GoogleMap googleMap, String trialName){
        //Help from 'How to Implement Google Map Inside Fragment in Android Studio | GoogleMap | Android Coding'
        //by Android Coding (09/12/2020, YouTube) - https://www.youtube.com/watch?v=YCFPClPjDIQ

        LatLng location = new LatLng(latitude, longitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(location);
        markerOptions.title(trialName);
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                location, 12
        ));
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                location, 12
        ));
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_plot_loc, container, false);

        latitude = (ArrayList<Double>) this.getArguments().getSerializable("latitude");
        longitude = (ArrayList<Double>) this.getArguments().getSerializable("longitude");
        trialName = (ArrayList<String>) this.getArguments().getSerializable("trialName");

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);


        supportMapFragment.getMapAsync(new OnMapReadyCallback() {

            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {

               for (int i = 0; i < (trialName.size()); i++ ){
                   Log.d(TAG, "onMapReady: PLOTTING" + latitude.get(i) + longitude.get(i));
                   putLocationOnMap(latitude.get(i), longitude.get(i), googleMap, trialName.get(i));
               }
            }
        });
        return view;
    }
}
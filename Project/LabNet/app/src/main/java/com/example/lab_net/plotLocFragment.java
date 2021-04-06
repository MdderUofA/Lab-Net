package com.example.lab_net;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class plotLocFragment extends Fragment {

    private static final String TAG = "TESTING";
    private ArrayList<Double> latitude = new ArrayList<>();
    private ArrayList<Double> longitude = new ArrayList<>();
    private ArrayList<String> trialName = new ArrayList<>();
    private FirebaseFirestore db;
    private String experimentId;

    public void putLocationOnMap(double latitude, double longitude, GoogleMap googleMap, String trialName) {
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
        View view = inflater.inflate(R.layout.fragment_plot_loc, container, false);

        experimentId = this.getArguments().getString("experimentId");

        db = FirebaseFirestore.getInstance();

        CollectionReference collectionReference = db.collection("Trials");

        collectionReference.whereEqualTo("ExperimentId", experimentId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    private static final String TAG = "Error";

                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                latitude.add((Double) document.getData().get("Lat"));
                                longitude.add((Double) document.getData().get("Long"));
                                trialName.add(document.getData().get("Title").toString());
                                Log.d(TAG, "onComplete: " + document.getData().get("Lat"));
                                Log.d(TAG, "onComplete: " + document.getData().get("Long"));
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);

        Toast.makeText(getActivity(), "Getting locations. Please wait for 5 seconds.", Toast.LENGTH_LONG).show();
        //CITE THIS
        //https://stackoverflow.com/questions/23430839/how-to-make-app-wait-and-then-start-activity-or-go-back
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (latitude.isEmpty() || longitude.isEmpty()) {
                    Toast.makeText(getActivity(), "No GeoLocations available. Please go back.", Toast.LENGTH_LONG).show();
                } else {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {

                        @SuppressLint("MissingPermission")
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            int nullValues = 0;
                            for (int i = 0; i < (latitude.size()); i++) {
                                Log.d(TAG, "onMapReady: PLOTTING" + latitude.get(i) + longitude.get(i));
                                if (latitude.get(i) == null || longitude.get(i) == null){
                                    nullValues++;
                                    continue;
                                } else {
                                    putLocationOnMap(latitude.get(i), longitude.get(i), googleMap, trialName.get(i));
                                }
                             if (nullValues == latitude.size()){
                                 Toast.makeText(getActivity(), "No GeoLocations available. Please go back.", Toast.LENGTH_LONG).show();
                             }

                            }
                        }
                    });
                }
            }
        }, 5000);

        return view;
    }
}


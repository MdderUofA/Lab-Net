package com.example.lab_net;

import android.Manifest;
import android.annotation.SuppressLint;;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;


public class MapFragment extends Fragment {
    private static final String TAG = "TESTING" ;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private double mainLongitude;
    private double mainLatitude;
    private Task<Location> locationResult;
    private Location lastKnownLocation;
    private double[] coordinates = new double[2];

    public void putLocationOnMap(Location lastKnownLocation, GoogleMap googleMap){

        mainLatitude = lastKnownLocation.getLatitude();
        mainLongitude = lastKnownLocation.getLongitude();
        Log.d(TAG, "onComplete: " + lastKnownLocation.getLatitude());
        coordinates[0] = mainLatitude;
        coordinates[1] = mainLongitude;
        Log.d(TAG, "onComplete: " + mainLatitude);
        sendLocation(coordinates);
        LatLng currentLocation = new LatLng(mainLatitude, mainLongitude);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLocation);
        markerOptions.title(currentLocation.latitude + " : " + currentLocation.longitude);
        googleMap.clear();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                currentLocation, 10
        ));
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                currentLocation, 10
        ));

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        SupportMapFragment supportMapFragment = (SupportMapFragment)
                getChildFragmentManager().findFragmentById(R.id.google_map);


        supportMapFragment.getMapAsync(new OnMapReadyCallback() {


            @SuppressLint("MissingPermission")
            @Override
            public void onMapReady(GoogleMap googleMap) {

                if (isPermissionGiven()) {
                    locationResult = fusedLocationProviderClient.getLastLocation();
                    locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful()) {
                                lastKnownLocation = locationResult.getResult();
                                if (lastKnownLocation != null) {
                                    putLocationOnMap(lastKnownLocation, googleMap);
                                } else {
                                    Toast.makeText(getContext(), "Can't get location, please try again", Toast.LENGTH_LONG).show();
                                }
                            }
                        }});

                } else {
                    askPermission();
                    locationResult = fusedLocationProviderClient.getLastLocation();
                    locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if (task.isSuccessful()) {
                                lastKnownLocation = locationResult.getResult();
                                if (lastKnownLocation != null) {
                                    putLocationOnMap(lastKnownLocation, googleMap);
                                } else {
                                    Toast.makeText(getContext(), "Can't get location, please try again", Toast.LENGTH_LONG).show();
                                }
                            }
                        }});
                }
                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {

                            MarkerOptions markerOptions = new MarkerOptions();
                            markerOptions.position(latLng);
                            markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                            googleMap.clear();
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                    latLng, 10
                            ));
                            googleMap.addMarker(markerOptions);

                            mainLatitude = latLng.latitude;
                            mainLongitude = latLng.longitude;
                            coordinates[0] = mainLatitude;
                            coordinates[1] = mainLongitude;
                            sendLocation(coordinates);
                    }
                });
            }
        });
        return view;
    }

    public boolean isPermissionGiven(){
        return ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED;
    }

    public void askPermission(){
        Dexter.withContext(getContext()).withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        locationResult = fusedLocationProviderClient.getLastLocation();
                        lastKnownLocation = locationResult.getResult();
                        mainLatitude = lastKnownLocation.getLatitude();
                        mainLongitude = lastKnownLocation.getLongitude();
                        Toast.makeText(getContext(), "Thank you, location is now shown", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        Toast toast = new Toast(getContext());
                        Toast.makeText(getContext(), "Permission is required to show your location", Toast.LENGTH_LONG).show();
                    }
                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                });

    }

    public void sendLocation(double[] coordinates){
        CoordinateListener coordinateListener = (CoordinateListener) getActivity();
        coordinateListener.getCoordinates(coordinates[0], coordinates[1]);
        Log.d(TAG, "sendLocation: " + coordinates[0] + coordinates[1]);

    }

}
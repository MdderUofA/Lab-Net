package com.example.lab_net;

import android.Manifest;
import android.annotation.SuppressLint;;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
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
    @SuppressLint("MissingPermission")
    public void getLocation(FusedLocationProviderClient fusedLocationProviderClient, GoogleMap googleMap){
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

                    getLocation(fusedLocationProviderClient, googleMap);

                googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                    @Override
                    public void onMapClick(LatLng latLng) {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(latLng.latitude + " : " + latLng.longitude);
                        googleMap.clear();
                        googleMap.addMarker(markerOptions);
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                                latLng, 15
                        ));


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

    public void sendLocation(double[] coordinates){
        CoordinateListener coordinateListener = (CoordinateListener) getActivity();
        coordinateListener.getCoordinates(coordinates[0], coordinates[1]);
        Log.d(TAG, "sendLocation: " + coordinates[0] + coordinates[1]);

    }


    public FusedLocationProviderClient getFusedLocationProviderClient() {
        return fusedLocationProviderClient;
    }

    public void setFusedLocationProviderClient(FusedLocationProviderClient fusedLocationProviderClient) {
        this.fusedLocationProviderClient = fusedLocationProviderClient;
    }

    public double getMainLongitude() {
        return mainLongitude;
    }

    public void setMainLongitude(double mainLongitude) {
        this.mainLongitude = mainLongitude;
    }

    public double getMainLatitude() {
        return mainLatitude;
    }

    public void setMainLatitude(double mainLatitude) {
        this.mainLatitude = mainLatitude;
    }

    public Task<Location> getLocationResult() {
        return locationResult;
    }

    public void setLocationResult(Task<Location> locationResult) {
        this.locationResult = locationResult;
    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    public void setLastKnownLocation(Location lastKnownLocation) {
        this.lastKnownLocation = lastKnownLocation;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[] coordinates) {
        this.coordinates = coordinates;
    }

}
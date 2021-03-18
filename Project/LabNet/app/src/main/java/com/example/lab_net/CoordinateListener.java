package com.example.lab_net;

/**
 * Used to send location coordinates from MapFragment to MapActivity continously, keeping MapActivity
 * updated at all times
 * @author gurjogsingh
 * @version 1.0
 * @see MapFragment
 * @see MapActivity
 */
public interface CoordinateListener {
    /**
     * Gets coordinates. Implementation in MapActivity.
     * @param latitude
     * @param longitude
     * @return void
     */
    void getCoordinates(double latitude, double longitude);
}

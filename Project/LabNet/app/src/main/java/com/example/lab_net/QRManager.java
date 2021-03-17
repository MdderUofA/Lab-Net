package com.example.lab_net;

import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

public class QRManager {

    private QRManager() {}; // static class

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static void scanQR(AppCompatActivity source) { // convenience
        Intent intent = new Intent(source, QRScanner.class);
        source.startActivity(intent);
    }

    public static void processQRResult(AppCompatActivity caller, String result) {
        if(result == null)
            return;
        QRManager.findTarget(result);
    }

    private static void findTarget(String result) {
        // query database and get a document, then
    }
}

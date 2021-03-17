package com.example.lab_net;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class QRManager {

    private QRManager() {}; // static class

    private static FirebaseFirestore db = FirebaseFirestore.getInstance();

    public static Bitmap generateQRFromString(String name) throws IOException {
        Map<EncodeHintType, ErrorCorrectionLevel> map = new HashMap<>();
        map.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.L);

        BitMatrix bm = null;
        try {
            bm = new MultiFormatWriter().encode(
                    name, BarcodeFormat.QR_CODE, 256, 256);
        } catch (WriterException e) {
            throw new IOException(e);
        }
        // code written by ρяσѕρєя K https://stackoverflow.com/users/1202025/%cf%81%d1%8f%cf%83%d1%95%cf%81%d1%94%d1%8f-k
        // source: https://stackoverflow.com/a/19337524
        Bitmap ret = Bitmap.createBitmap(256,256,Bitmap.Config.RGB_565);
        for(int yy = 0;yy<256;yy++) {
            for (int xx = 0; xx < 256; xx++) {
                ret.setPixel(xx, yy, bm.get(xx, yy) ? Color.BLACK : Color.WHITE);
            }
        }
        return ret;
    }

    public static void registerBarcode(Object barcode) {
        // take the barcode and turn it into something that can be stored in the DB
        // then store the same as a QR (reference a document)
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

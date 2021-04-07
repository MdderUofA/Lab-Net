package com.example.lab_net;

import android.graphics.Bitmap;
import android.graphics.Color;

import androidx.appcompat.app.AppCompatActivity;
import androidx.print.PrintHelper;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A static class which holds various utility methods related to generation of QR codes.
 * @author Marcus
 */
public class QRManager {

    public static final String LAB_NET_PREPEND = "<!--lab-net--!>";
    public static final String LAB_NET_JOIN = "/";

    private QRManager(){}; // static class.

    /**
     * Converts a List of strings into a QR code string.
     * @param list The list of strings to convert
     * @return The non null QR version of the list.
     */
    public static String toQRString(List<String> list) {
        String string = "";
        for(int i =0;i<list.size()-1;i++) {
            string+=list.get(i)+"/";
        }
        string+=list.get(list.size()-1);
        return string;
    }

    /**
     * Attempts to generate and then prompts to print a new QR code from the specified activity.
     * This method may silently fail if the generated QR is invalid. If you must know of failures,
     * use generateQRFromString() and then printQRFromBitmap().
     *
     * @param parent The calling activity
     * @param qr The string to write into the QR
     */
    public static void printQRFromString(AppCompatActivity parent, String qr) {
        try {
            Bitmap bitmap = QRManager.generateQRFromString(qr);
            QRManager.printQRFromBitmap(parent,bitmap);
        } catch (IOException e) { }
    }

    /**
     * Prints the specified QR code from a bitmap image.
     * @param parent The AppCompatActivity that wants to print
     * @param bitmap The bitmap to print
     */
    public static void printQRFromBitmap(AppCompatActivity parent, Bitmap bitmap) {
        PrintHelper photoPrinter = new PrintHelper(parent);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        photoPrinter.printBitmap("Lab-Net QR Print", bitmap);
    }

    /**
     * Generates a Bitmap from the specified String as a QR code image.
     *
     * @param qr The String to encode into the QR code
     * @return The Bitmap representing the QR code.
     * @throws IOException
     */
    public static Bitmap generateQRFromString(String qr) throws IOException {
        Map<EncodeHintType, ErrorCorrectionLevel> map = new HashMap<>();
        map.put(EncodeHintType.ERROR_CORRECTION,ErrorCorrectionLevel.L);

        BitMatrix bm = null;
        try {
            bm = new MultiFormatWriter().encode(
                    qr, BarcodeFormat.QR_CODE, 512, 512);
        } catch (WriterException e) {
            throw new IOException(e);
        }

        // code written by ρяσѕρєя K https://stackoverflow.com/users/1202025/%cf%81%d1%8f%cf%83%d1%95%cf%81%d1%94%d1%8f-k
        // source: https://stackoverflow.com/a/19337524
        Bitmap ret = Bitmap.createBitmap(512,512,Bitmap.Config.RGB_565);
        for(int yy = 0;yy<512;yy++) {
            for (int xx = 0; xx < 512; xx++) {
                ret.setPixel(xx, yy, bm.get(xx, yy) ? Color.BLACK : Color.WHITE);
            }
        }
        return ret;
    }
}

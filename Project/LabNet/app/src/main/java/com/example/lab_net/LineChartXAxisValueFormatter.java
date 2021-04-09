// code from
// https://stackoverflow.com/questions/41426021/how-to-add-x-axis-as-datetime-label-in-mpandroidchart

package com.example.lab_net;

import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Formatter for the Line chart
 */
public class LineChartXAxisValueFormatter extends IndexAxisValueFormatter {

    @Override
    public String getFormattedValue(float value) {

        // Convert float value to date string
        // Convert from seconds back to milliseconds to format time  to show to the user
        long emissionsMilliSince1970Time = ((long) value) * 1000;

        // Show time in local version
        Date timeMilliseconds = new Date(emissionsMilliSince1970Time);
        DateFormat dateTimeFormat = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

        return dateTimeFormat.format(timeMilliseconds);
    }
}

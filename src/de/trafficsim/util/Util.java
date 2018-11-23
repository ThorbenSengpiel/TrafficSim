package de.trafficsim.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Util {
    public static final DecimalFormat DOUBLE_FORMAT_0_0000;
    public static final DecimalFormat DOUBLE_FORMAT_0_00;
    static {
        DOUBLE_FORMAT_0_0000 = new DecimalFormat("0.0000");
        DOUBLE_FORMAT_0_0000.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

        DOUBLE_FORMAT_0_00 = new DecimalFormat("0.00");
        DOUBLE_FORMAT_0_00.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
    }


    public static final double CAR_SIZE = 3;
    public static final double VEHICLE_LENGTH = CAR_SIZE*2+2;

    public static double map(double value, double istart, double inTo, double outFrom, double outTo) {
        return outFrom + (outTo - outFrom) * ((value - istart) / (inTo - istart));
    }

    public static double interpolate(double value, double v0, double v1) {
        double dist = v1-v0;
        return v0 + dist*value;
    }

    public static double kmhToMs(double kmh) {
        return kmh / 3.6;
    }



}

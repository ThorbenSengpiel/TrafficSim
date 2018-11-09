package de.trafficsim.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Util {
    public static DecimalFormat DOUBLE_FORMAT_0_0000;
    public static DecimalFormat DOUBLE_FORMAT_0_00;
    static {
        DOUBLE_FORMAT_0_0000 = new DecimalFormat("0.0000");
        DOUBLE_FORMAT_0_0000.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));

        DOUBLE_FORMAT_0_00 = new DecimalFormat("0.00");
        DOUBLE_FORMAT_0_00.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
    }
}

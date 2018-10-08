package de.trafficsim.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Util {
    public static DecimalFormat DOUBLE_FORMAT_0_0000;
    static {
        DOUBLE_FORMAT_0_0000 = new DecimalFormat("0.0000");
        DOUBLE_FORMAT_0_0000.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
    }
}

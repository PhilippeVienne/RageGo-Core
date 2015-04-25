package com.ragego.engine;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Value of komi.
 * This class is immutable.
 */
public final class Komi {
    private final double value;

    /**
     * Constructor.
     *
     * @param komi The value for the komi.
     */
    public Komi(double komi) {
        value = komi;
    }

    /**
     * Parse komi from string.
     *
     * @param s The string (null not allowed), empty string means unknown
     *          komi.
     * @return The komi or null if unknown komi.
     */
    public static Komi parseKomi(String s) {
        assert s != null;
        if (s.trim().equals(""))
            return null;
        // Also accept , instead of .
        double komi = Double.parseDouble(s.replace(',', '.'));
        return new Komi(komi);
    }

    public boolean equals(Object object) {
        if (object == null || object.getClass() != getClass())
            return false;
        Komi komi = (Komi) object;
        return (komi.value == value);
    }

    public int hashCode() {
        long bits = Double.doubleToLongBits(value);
        return (int) (bits ^ (bits >>> 32));
    }

    public boolean isMultipleOf(double multiple) {
        return Math.IEEEremainder(value, multiple) == 0;
    }

    public double toDouble() {
        return value;
    }

    public String toString() {
        DecimalFormat format =
                (DecimalFormat) (NumberFormat.getInstance(Locale.ENGLISH));
        format.setGroupingUsed(false);
        format.setDecimalSeparatorAlwaysShown(false);
        return format.format(value);
    }
}
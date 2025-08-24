package com.sing.util;

import java.util.ArrayList;
import java.util.List;

public class MathUtil {
    public static double hzToMidi(double hz) {
        return 69.0 + 12.0 * (Math.log(hz / 440.0) / Math.log(2.0));
    }

    public static List<Double> resampleToLength(List<Double> src, int targetLen) {
        List<Double> out = new ArrayList<>(targetLen);
        if (src.isEmpty() || targetLen <= 0) return out;
        for (int i = 0; i < targetLen; i++) {
            double t = (double) i / (targetLen - 1);
            double x = t * (src.size() - 1);
            int x0 = (int) Math.floor(x);
            int x1 = Math.min(x0 + 1, src.size() - 1);
            double frac = x - x0;
            Double v0 = src.get(x0);
            Double v1 = src.get(x1);
            double v = (isNaN(v0) || isNaN(v1)) ? Double.NaN : v0 * (1 - frac) + v1 * frac;
            out.add(v);
        }
        return out;
    }

    public static boolean isNaN(Double d) { return d == null || d.isNaN(); }

    public static double median(List<Double> values) {
        List<Double> v = new ArrayList<>();
        for (Double d : values) if (!isNaN(d)) v.add(d);
        if (v.isEmpty()) return Double.NaN;
        v.sort(Double::compareTo);
        int n = v.size();
        return (n % 2 == 1) ? v.get(n/2) : (v.get(n/2 - 1) + v.get(n/2)) / 2.0;
    }

    public static double stddev(List<Double> values) {
        List<Double> v = new ArrayList<>();
        for (Double d : values) if (!isNaN(d)) v.add(d);
        if (v.size() < 2) return 0.0;
        double mean = v.stream().mapToDouble(d -> d).average().orElse(0);
        double var = v.stream().mapToDouble(d -> (d - mean)*(d - mean)).sum() / (v.size()-1);
        return Math.sqrt(var);
    }
}

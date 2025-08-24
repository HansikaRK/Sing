package com.sing.dto;

import java.util.List;

public class AnalysisResult {
    public double pitchAccuracyPercent;
    public double timingAccuracyPercent;
    public double averageCentsError;

    public String emotion;

    public List<Double> timeSec;
    public List<Double> refMidi;
    public List<Double> userMidi;

    public List<Double> refOnsetsSec;
    public List<Double> userOnsetsSec;
}

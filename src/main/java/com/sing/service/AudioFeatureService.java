package com.sing.service;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.onsets.ComplexOnsetDetector;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioInputStream;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.UniversalAudioInputStream;
import org.springframework.stereotype.Service;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.sing.util.MathUtil.hzToMidi;

@Service
public class AudioFeatureService {

    public static class PitchSeries {
        public List<Double> timeSec = new ArrayList<>();
        public List<Double> midi = new ArrayList<>();
    }

    public static class Onsets {
        public List<Double> timesSec = new ArrayList<>();
    }

    public static class RmsSeries {
        public List<Double> timeSec = new ArrayList<>();
        public List<Double> rms = new ArrayList<>();
    }

    private AudioDispatcher createDispatcher(File wavFile, int bufferSize, int overlap) throws Exception {
        AudioInputStream stream = AudioSystem.getAudioInputStream(wavFile);

        // Convert Java AudioFormat → TarsosDSPAudioFormat
        TarsosDSPAudioFormat tarsosFormat = new TarsosDSPAudioFormat(
                (float) stream.getFormat().getSampleRate(),
                stream.getFormat().getSampleSizeInBits(),
                stream.getFormat().getChannels(),
                true, // signed PCM
                false // little endian
        );

        TarsosDSPAudioInputStream tarsosStream = new UniversalAudioInputStream(stream, tarsosFormat);

        return new AudioDispatcher(tarsosStream, bufferSize, overlap);
    }

    public PitchSeries extractPitchMidi(File wavFile, int bufferSize, int overlap) throws Exception {
        AudioDispatcher dispatcher = createDispatcher(wavFile, bufferSize, overlap);
        float sampleRate = dispatcher.getFormat().getSampleRate();

        PitchSeries out = new PitchSeries();

        PitchDetectionHandler handler = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult res, AudioEvent e) {
                float hz = res.getPitch();
                double t = e.getTimeStamp();
                out.timeSec.add(t);
                if (hz > 0) {
                    out.midi.add(hzToMidi(hz));
                } else {
                    out.midi.add(Double.NaN); // unvoiced
                }
            }
        };

        dispatcher.addAudioProcessor(new PitchProcessor(
                PitchProcessor.PitchEstimationAlgorithm.YIN,
                sampleRate, bufferSize, handler));

        dispatcher.run();
        return out;
    }

    public Onsets detectOnsets(File wavFile, int bufferSize, int overlap, double threshold, double sensitivity) throws Exception {
        AudioDispatcher dispatcher = createDispatcher(wavFile, bufferSize, overlap);

        Onsets on = new Onsets();
        OnsetHandler onsetHandler = (time, salience) -> on.timesSec.add(time);

        // ComplexOnsetDetector constructor: (int bufferSize, double threshold, double sensitivity)
        ComplexOnsetDetector onsetDetector = new ComplexOnsetDetector(bufferSize, threshold, sensitivity);
        onsetDetector.setHandler(onsetHandler); // attach your handler

        dispatcher.addAudioProcessor(onsetDetector);

        dispatcher.run();
        return on;
    }

    public RmsSeries extractRms(File wavFile, int bufferSize, int overlap) throws Exception {
        AudioDispatcher dispatcher = createDispatcher(wavFile, bufferSize, overlap);
        RmsSeries rs = new RmsSeries();

        dispatcher.addAudioProcessor(new AudioProcessor() {
            @Override
            public boolean process(AudioEvent e) {
                float[] buf = e.getFloatBuffer();
                double sum = 0;
                for (float v : buf) sum += v * v;
                double rms = Math.sqrt(sum / buf.length);
                rs.timeSec.add(e.getTimeStamp());
                rs.rms.add(rms);
                return true;
            }

            @Override
            public void processingFinished() {}
        });

        dispatcher.run();
        return rs;
    }
}

package com.sing.controller;

import com.sing.service.AudioFeatureService;
import com.sing.service.AudioFeatureService.PitchSeries;
import com.sing.service.AudioFeatureService.RmsSeries;
import com.sing.service.AudioFeatureService.Onsets;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;

@RestController
@RequestMapping("/api/audio")
public class AudioFeatureController {

    @Autowired
    private AudioFeatureService audioFeatureService;

    @PostMapping("/analyze")
    public ResponseEntity<?> analyzeAudio(@RequestParam("file") MultipartFile file) {
        try {
            // Save the uploaded file temporarily
            File tempFile = File.createTempFile("upload-", ".wav");
            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                fos.write(file.getBytes());
            }

            int bufferSize = 1024;
            int overlap = 512;
            double threshold = 0.3;
            double sensitivity = 1.5;

            // Extract features
            PitchSeries pitch = audioFeatureService.extractPitchMidi(tempFile, bufferSize, overlap);
            RmsSeries rms = audioFeatureService.extractRms(tempFile, bufferSize, overlap);
            Onsets onsets = audioFeatureService.detectOnsets(tempFile, bufferSize, overlap, threshold, sensitivity);

            // Delete temp file
            tempFile.delete();

            // Return all features as JSON
            return ResponseEntity.ok(new Object() {
                public final PitchSeries pitchSeries = pitch;
                public final RmsSeries rmsSeries = rms;
                public final Onsets onsetsSeries = onsets;
            });

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error processing audio: " + e.getMessage());
        }
    }
}

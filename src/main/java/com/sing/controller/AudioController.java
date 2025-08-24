package com.sing.controller;

import com.sing.service.AudioFeatureService;
import com.sing.service.AudioFeatureService.PitchSeries;
import com.sing.service.AudioFeatureService.Onsets;
import com.sing.service.AudioFeatureService.RmsSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/audio")
public class AudioController {

    @Autowired
    private AudioFeatureService audioFeatureService;

    @PostMapping("/analyze")
    public String analyzeAudio(@RequestParam("file") MultipartFile file) {
        try {
            // Save file temporarily
            File tempFile = File.createTempFile("upload", ".wav");
            file.transferTo(tempFile);

            // Example: extract pitch
            PitchSeries pitch = audioFeatureService.extractPitchMidi(tempFile, 1024, 512);

            // Clean up
            tempFile.delete();

            return "Analysis done. Number of pitch points: " + pitch.midi.size();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}


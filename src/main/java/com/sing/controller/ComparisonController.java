package com.sing.controller;

import com.sing.service.AudioFeatureService;
import com.sing.service.AudioFeatureService.PitchSeries;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

@RestController
@RequestMapping("/api/compare")
public class ComparisonController {

    @Autowired
    private AudioFeatureService audioFeatureService;

    @PostMapping
    public ResponseEntity<?> compare(@RequestParam("file1") MultipartFile file1,
                                     @RequestParam("file2") MultipartFile file2) {
        try {
            // Save both files temporarily
            File temp1 = File.createTempFile("f1-", ".wav");
            File temp2 = File.createTempFile("f2-", ".wav");
            file1.transferTo(temp1);
            file2.transferTo(temp2);

            // Extract pitch series
            PitchSeries s1 = audioFeatureService.extractPitchMidi(temp1, 1024, 512);
            PitchSeries s2 = audioFeatureService.extractPitchMidi(temp2, 1024, 512);

            // Clean up
            temp1.delete();
            temp2.delete();

            // TODO: implement similarity function (e.g., DTW)
            double similarity = Math.random(); // placeholder

            return ResponseEntity.ok(new Object() {
                public final double score = similarity;
                public final PitchSeries series1 = s1;
                public final PitchSeries series2 = s2;
            });

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error comparing: " + e.getMessage());
        }
    }
}

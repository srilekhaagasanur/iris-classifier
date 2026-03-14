package com.irisclassifier.service;

import com.irisclassifier.ml.IrisClassifierEngine;
import com.irisclassifier.model.ClassificationResult;
import com.irisclassifier.model.IrisSample;
import com.irisclassifier.model.ModelMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class IrisClassificationService {

    private final IrisClassifierEngine classifierEngine;

    /**
     * Classify a single Iris flower sample.
     */
    public ClassificationResult classify(IrisSample sample) {
        log.info("Classifying sample: sepalLength={}, sepalWidth={}, petalLength={}, petalWidth={}",
                sample.getSepalLength(), sample.getSepalWidth(),
                sample.getPetalLength(), sample.getPetalWidth());

        ClassificationResult result = classifierEngine.classify(sample);
        log.info("Classification result: species={}, confidence={}",
                result.getPredictedSpecies(), result.getConfidence());
        return result;
    }

    /**
     * Classify a batch of Iris flower samples.
     */
    public List<ClassificationResult> classifyBatch(List<IrisSample> samples) {
        log.info("Classifying batch of {} samples", samples.size());
        return samples.stream()
                .map(this::classify)
                .collect(Collectors.toList());
    }

    /**
     * Retrieve current model metrics and training stats.
     */
    public ModelMetrics getModelMetrics() {
        return classifierEngine.getMetrics();
    }
}
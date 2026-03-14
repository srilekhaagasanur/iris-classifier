package com.irisclassifier.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Model training and evaluation metrics")
public class ModelMetrics {

    @Schema(description = "Algorithm used", example = "K-Nearest Neighbours (K=5)")
    private String algorithm;

    @Schema(description = "Number of training samples", example = "120")
    private int trainingSamples;

    @Schema(description = "Number of test samples", example = "30")
    private int testSamples;

    @Schema(description = "Model accuracy on test set", example = "0.967")
    private Double accuracy;

    @Schema(description = "K value", example = "5")
    private int numberOfTrees;

    @Schema(description = "Number of features", example = "4")
    private int numberOfFeatures;

    @Schema(description = "Number of classes", example = "3")
    private int numberOfClasses;

    @Schema(description = "Model status", example = "TRAINED")
    private String status;
}

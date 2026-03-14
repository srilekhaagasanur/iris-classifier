package com.irisclassifier.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Result of an Iris species classification")
public class ClassificationResult {

    @Schema(description = "Predicted Iris species", example = "Iris-setosa")
    private String predictedSpecies;

    @Schema(description = "Confidence score (0.0 - 1.0)", example = "0.97")
    private Double confidence;

    @Schema(description = "Probability distribution across all species")
    private Map<String, Double> probabilities;

    @Schema(description = "Input features used for prediction")
    private IrisSample inputFeatures;

    @Schema(description = "Model algorithm used", example = "K-Nearest Neighbours")
    private String modelAlgorithm;
}

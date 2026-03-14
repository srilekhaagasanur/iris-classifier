package com.irisclassifier.controller;

import com.irisclassifier.model.ClassificationResult;
import com.irisclassifier.model.IrisSample;
import com.irisclassifier.model.ModelMetrics;
import com.irisclassifier.service.IrisClassificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/iris")
@RequiredArgsConstructor
@Tag(name = "Iris Classification", description = "ML-powered Iris flower species classification API")
public class IrisClassificationController {

    private final IrisClassificationService classificationService;

    @PostMapping("/classify")
    @Operation(
            summary = "Classify a single Iris flower",
            description = "Predicts the species of an Iris flower based on sepal and petal measurements."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Classification successful",
                    content = @Content(schema = @Schema(implementation = ClassificationResult.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input measurements")
    })
    public ResponseEntity<ClassificationResult> classify(
            @Valid @RequestBody IrisSample sample) {
        return ResponseEntity.ok(classificationService.classify(sample));
    }

    @PostMapping("/classify/batch")
    @Operation(
            summary = "Classify a batch of Iris flowers",
            description = "Predicts the species for multiple Iris flowers in a single request."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Batch classification successful"),
            @ApiResponse(responseCode = "400", description = "Invalid input in batch")
    })
    public ResponseEntity<List<ClassificationResult>> classifyBatch(
            @Valid @RequestBody List<IrisSample> samples) {
        return ResponseEntity.ok(classificationService.classifyBatch(samples));
    }

    @GetMapping("/model/metrics")
    @Operation(
            summary = "Get model metrics",
            description = "Returns training metadata, accuracy and configuration of the ML model."
    )
    @ApiResponse(responseCode = "200", description = "Model metrics retrieved successfully",
            content = @Content(schema = @Schema(implementation = ModelMetrics.class)))
    public ResponseEntity<ModelMetrics> getModelMetrics() {
        return ResponseEntity.ok(classificationService.getModelMetrics());
    }

    @GetMapping("/species")
    @Operation(
            summary = "List all Iris species",
            description = "Returns the list of Iris species this model can classify."
    )
    public ResponseEntity<List<String>> getSpecies() {
        return ResponseEntity.ok(List.of(
                "Iris-setosa",
                "Iris-versicolor",
                "Iris-virginica"
        ));
    }

    @GetMapping("/sample/{species}")
    @Operation(
            summary = "Get a sample measurement for a species",
            description = "Returns example measurements for a given Iris species — handy for testing."
    )
    @ApiResponse(responseCode = "200", description = "Sample returned")
    @ApiResponse(responseCode = "404", description = "Species not found")
    public ResponseEntity<IrisSample> getSampleMeasurement(
            @PathVariable String species) {
        return switch (species.toLowerCase()) {
            case "iris-setosa", "setosa" -> ResponseEntity.ok(
                    IrisSample.builder()
                            .sepalLength(5.1).sepalWidth(3.5)
                            .petalLength(1.4).petalWidth(0.2).build());
            case "iris-versicolor", "versicolor" -> ResponseEntity.ok(
                    IrisSample.builder()
                            .sepalLength(7.0).sepalWidth(3.2)
                            .petalLength(4.7).petalWidth(1.4).build());
            case "iris-virginica", "virginica" -> ResponseEntity.ok(
                    IrisSample.builder()
                            .sepalLength(6.3).sepalWidth(3.3)
                            .petalLength(6.0).petalWidth(2.5).build());
            default -> ResponseEntity.notFound().build();
        };
    }
}
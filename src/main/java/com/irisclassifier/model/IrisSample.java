package com.irisclassifier.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Iris flower measurements for classification")
public class IrisSample {

    @NotNull(message = "Sepal length is required")
    @DecimalMin(value = "0.1", message = "Sepal length must be positive")
    @Schema(description = "Sepal length in cm", example = "5.1")
    private Double sepalLength;

    @NotNull(message = "Sepal width is required")
    @DecimalMin(value = "0.1", message = "Sepal width must be positive")
    @Schema(description = "Sepal width in cm", example = "3.5")
    private Double sepalWidth;

    @NotNull(message = "Petal length is required")
    @DecimalMin(value = "0.1", message = "Petal length must be positive")
    @Schema(description = "Petal length in cm", example = "1.4")
    private Double petalLength;

    @NotNull(message = "Petal width is required")
    @DecimalMin(value = "0.1", message = "Petal width must be positive")
    @Schema(description = "Petal width in cm", example = "0.2")
    private Double petalWidth;
}

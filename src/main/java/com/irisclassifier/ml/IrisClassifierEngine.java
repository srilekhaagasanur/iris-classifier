package com.irisclassifier.ml;

import com.irisclassifier.model.ClassificationResult;
import com.irisclassifier.model.IrisSample;
import com.irisclassifier.model.ModelMetrics;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

/**
 * Iris classifier using a K-Nearest Neighbours (KNN) algorithm.
 * Built from scratch to avoid external ML library version conflicts.
 * Trained on the classic UCI Iris dataset (150 samples, 3 classes).
 */
@Slf4j
@Component
public class IrisClassifierEngine {

    private static final String[] CLASS_LABELS = {
            "Iris-setosa", "Iris-versicolor", "Iris-virginica"
    };
    private static final int K = 5;

    private double[][] trainX;
    private int[] trainY;

    // Normalisation params
    private double[] means;
    private double[] stds;

    @Getter
    private ModelMetrics metrics;

    @PostConstruct
    public void init() {
        log.info("Loading Iris dataset and training KNN classifier...");
        List<double[]> features = new ArrayList<>();
        List<Integer> labels = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                Objects.requireNonNull(getClass().getResourceAsStream("/iris.csv"))))) {
            String line;
            boolean header = true;
            while ((line = br.readLine()) != null) {
                if (header) { header = false; continue; }
                String[] parts = line.split(",");
                features.add(new double[]{
                        Double.parseDouble(parts[0]),
                        Double.parseDouble(parts[1]),
                        Double.parseDouble(parts[2]),
                        Double.parseDouble(parts[3])
                });
                labels.add(labelIndex(parts[4].trim()));
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to load iris.csv", e);
        }

        int n = features.size();
        int numFeatures = 4;
        double[][] X = features.toArray(new double[0][]);
        int[] y = labels.stream().mapToInt(Integer::intValue).toArray();

        // Compute normalisation (z-score)
        means = new double[numFeatures];
        stds  = new double[numFeatures];
        for (int f = 0; f < numFeatures; f++) {
            double sum = 0, sq = 0;
            for (double[] row : X) sum += row[f];
            means[f] = sum / n;
            for (double[] row : X) sq += Math.pow(row[f] - means[f], 2);
            stds[f] = Math.sqrt(sq / n);
            if (stds[f] == 0) stds[f] = 1;
        }

        // Normalise
        double[][] Xnorm = new double[n][numFeatures];
        for (int i = 0; i < n; i++)
            for (int f = 0; f < numFeatures; f++)
                Xnorm[i][f] = (X[i][f] - means[f]) / stds[f];

        // 80/20 train-test split
        int trainSize = 120;
        trainX = Arrays.copyOfRange(Xnorm, 0, trainSize);
        trainY = Arrays.copyOfRange(y, 0, trainSize);

        // Evaluate accuracy on test set
        int correct = 0;
        for (int i = trainSize; i < n; i++) {
            int pred = knnPredict(Xnorm[i]);
            if (pred == y[i]) correct++;
        }
        double accuracy = (double) correct / (n - trainSize);

        metrics = ModelMetrics.builder()
                .algorithm("K-Nearest Neighbours (K=" + K + ")")
                .trainingSamples(trainSize)
                .testSamples(n - trainSize)
                .accuracy(Math.round(accuracy * 10000.0) / 10000.0)
                .numberOfTrees(K)
                .numberOfFeatures(numFeatures)
                .numberOfClasses(3)
                .status("TRAINED")
                .build();

        log.info("Model trained successfully. Test accuracy: {}", accuracy);
    }

    public ClassificationResult classify(IrisSample sample) {
        double[] raw = {
                sample.getSepalLength(), sample.getSepalWidth(),
                sample.getPetalLength(), sample.getPetalWidth()
        };
        double[] norm = new double[4];
        for (int f = 0; f < 4; f++)
            norm[f] = (raw[f] - means[f]) / stds[f];

        double[] distances = new double[trainX.length];
        for (int i = 0; i < trainX.length; i++)
            distances[i] = euclidean(norm, trainX[i]);

        int[] indices = argsort(distances);
        int[] votes = new int[3];
        for (int i = 0; i < K; i++)
            votes[trainY[indices[i]]]++;

        Map<String, Double> probs = new LinkedHashMap<>();
        int best = 0;
        for (int c = 0; c < 3; c++) {
            probs.put(CLASS_LABELS[c], (double) votes[c] / K);
            if (votes[c] > votes[best]) best = c;
        }

        return ClassificationResult.builder()
                .predictedSpecies(CLASS_LABELS[best])
                .confidence(probs.get(CLASS_LABELS[best]))
                .probabilities(probs)
                .inputFeatures(sample)
                .modelAlgorithm("K-Nearest Neighbours (K=" + K + ")")
                .build();
    }

    // ── helpers ──────────────────────────────────────────────────────────────

    private int labelIndex(String label) {
        for (int i = 0; i < CLASS_LABELS.length; i++)
            if (CLASS_LABELS[i].equals(label)) return i;
        throw new IllegalArgumentException("Unknown label: " + label);
    }

    private int knnPredict(double[] x) {
        double[] distances = new double[trainX.length];
        for (int i = 0; i < trainX.length; i++)
            distances[i] = euclidean(x, trainX[i]);
        int[] indices = argsort(distances);
        int[] votes = new int[3];
        for (int i = 0; i < K; i++) votes[trainY[indices[i]]]++;
        int best = 0;
        for (int c = 1; c < 3; c++) if (votes[c] > votes[best]) best = c;
        return best;
    }

    private double euclidean(double[] a, double[] b) {
        double sum = 0;
        for (int i = 0; i < a.length; i++) sum += Math.pow(a[i] - b[i], 2);
        return Math.sqrt(sum);
    }

    private int[] argsort(double[] arr) {
        Integer[] idx = new Integer[arr.length];
        for (int i = 0; i < arr.length; i++) idx[i] = i;
        Arrays.sort(idx, Comparator.comparingDouble(i -> arr[i]));
        return Arrays.stream(idx).mapToInt(Integer::intValue).toArray();
    }
}
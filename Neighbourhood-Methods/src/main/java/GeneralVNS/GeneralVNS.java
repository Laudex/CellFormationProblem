package GeneralVNS;

import com.rits.cloning.Cloner;
import entities.Matrix;
import entities.Result;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.*;


public class GeneralVNS {

    private RealMatrix inputMatrix;
    private Result finalResult = new Result();

    public GeneralVNS(RealMatrix inputMatrix) {
        this.inputMatrix = inputMatrix;
    }

    public Result generalVNS(int kMax, int lMax) {

        for (int i = 1; i <= kMax; i++) {
            List<Matrix> clusters = shaking(i);
            if (clusters != null) {
                List<Matrix> clustersAfterVND = vnd(clusters, lMax);

                double objectiveValue = objectiveFunction(clustersAfterVND);
                if (objectiveValue > finalResult.getObjectiveValue()) {
                    finalResult.setObjectiveValue(objectiveValue);
                    finalResult.setCluster(clustersAfterVND);
                    finalResult.setNumberOfCluster(i);
                }
            } else {
                break;
            }
        }
        printFinalResult(finalResult);
        return finalResult;
    }

    private List<Matrix> shaking(int amountOfParts) {

        int numberOfRows = inputMatrix.getRowDimension();
        int numberOfColumns = inputMatrix.getColumnDimension();
        int sizeOfPart;

        if (amountOfParts <= Math.min(numberOfRows, numberOfColumns)) {
            List<Matrix> newClusters = new ArrayList<>();
            sizeOfPart = numberOfRows / amountOfParts;

            for (int i = 0; i < amountOfParts; i++) {
                int startNextCluster = i * sizeOfPart + 1;
                if (i != amountOfParts - 1) {
                    int endNextCluster = i * sizeOfPart + sizeOfPart;
                    newClusters.add(new Matrix(getClusterMatrix(startNextCluster, endNextCluster, endNextCluster)));
                } else {
                    int endRow = inputMatrix.getRowDimension();
                    int endColumn = inputMatrix.getColumnDimension();
                    newClusters.add(new Matrix(getClusterMatrix(startNextCluster, endRow, endColumn)));
                }
            }
            return newClusters;
        }
        return null;
    }

    private List<Matrix> vnd(List<Matrix> clusters, int lMax) {

        if (clusters != null && clusters.size() <= 1) {
            return clusters;
        } else {
            double currentObjectiveValue = -1;
            List<Matrix> bestClusterAfterVND = clusters;

            for (int l = 0; l < lMax; l++) {
                List<Matrix> iteratedCluster = bestClusterAfterVND;
                Result rowResult = moveRows(iteratedCluster);
                Result columnResult = moveColumns(iteratedCluster);

                double maxObjectiveValue = Math.max(rowResult.getObjectiveValue(), columnResult.getObjectiveValue());
                if (maxObjectiveValue > currentObjectiveValue) {
                    currentObjectiveValue = maxObjectiveValue;
                    if (maxObjectiveValue == rowResult.getObjectiveValue()){
                        bestClusterAfterVND = rowResult.getCluster();
                    } else {
                        bestClusterAfterVND = columnResult.getCluster();
                    }
                    l = 0;
                } else {
                    break;
                }
            }
            return bestClusterAfterVND;
        }
    }

    private double objectiveFunction(List<Matrix> clusters) {

        int totalNumberOfOneS = 0;
        for (int i = 0; i < inputMatrix.getRowDimension(); i++) {
            totalNumberOfOneS += Arrays.stream(inputMatrix.getRow(i)).filter(value -> value == 1.0).count();
        }

        final int[] numberOfOnesInClusters = {0};
        final int[] numberOfZerosInClusters = {0};

        clusters.forEach(matrix -> {
            matrix.getMatrix().keySet().forEach(key -> {
                matrix.getRow(key).forEach(index -> {
                    if (inputMatrix.getEntry(key - 1, index - 1) == 1.0)
                        numberOfOnesInClusters[0] += 1;
                    else
                        numberOfZerosInClusters[0] += 1;
                });
            });
        });

        return (double) numberOfOnesInClusters[0] / (totalNumberOfOneS + numberOfZerosInClusters[0]);
    }


    private Map<Integer, List<Integer>> getClusterMatrix(int startIndex, int endRowIndex, int endColumnIndex) {
        List<Integer> sequenceNumbers = new ArrayList<>();
        Map<Integer, List<Integer>> cluster = new HashMap<>();

        for (int i = startIndex; i <= endColumnIndex; i++) {
            sequenceNumbers.add(i);
        }
        for (int i = startIndex; i <= endRowIndex; i++) {
            cluster.put(i, new ArrayList<>(sequenceNumbers));
        }
        return cluster;
    }

    private List<Matrix> addRow(List<Matrix> clusters, int rowCount) {

        double currentObjective = objectiveFunction(clusters);
        List<Matrix> bestCluster = clusters;

        final int[] indexOfMatrix = {0};
        clusters.stream()
                .filter(matrix -> matrix.getMatrix().keySet().contains(rowCount))
                .findFirst().ifPresent(matrix -> indexOfMatrix[0] = clusters.indexOf(matrix));

        if (clusters.get(indexOfMatrix[0]).getMatrix().size() > 1) {

            for (int clusterCount = 0; clusterCount < clusters.size(); clusterCount++) {
                if (clusterCount != indexOfMatrix[0]) {
                    List<Matrix> copyClusters = getClone(clusters);
                    copyClusters.stream().filter(matrix -> matrix.getMatrix().keySet().contains(rowCount))
                            .forEach(matrix -> matrix.getMatrix().remove(rowCount));

                    Matrix cluster = copyClusters.get(clusterCount);
                    cluster.getMatrix().put(rowCount, new ArrayList<>(cluster.getColumnIndexes()));
                    double tempObjective = objectiveFunction(copyClusters);
                    if (tempObjective > currentObjective) {
                        currentObjective = tempObjective;
                        bestCluster = copyClusters;
                    }
                }
            }
        }
        return bestCluster;
    }

    private List<Matrix> addColumn(List<Matrix> clusters, int columnCount) {
        double currentObjective = objectiveFunction(clusters);
        List<Matrix> bestCluster = clusters;

        final int[] indexOfMatrix = {0};
        clusters.stream()
                .filter(matrix -> matrix.getColumnIndexes().contains(columnCount))
                .findFirst().ifPresent(matrix -> indexOfMatrix[0] = clusters.indexOf(matrix));


        if (clusters.get(indexOfMatrix[0]).getColumnIndexes().size() > 1) {

            for (int clusterCount = 0; clusterCount < clusters.size(); clusterCount++) {
                if (clusterCount != indexOfMatrix[0]) {
                    List<Matrix> copyClusters = getClone(clusters);
                    copyClusters.stream()
                            .filter(matrix -> matrix.getColumnIndexes().contains(columnCount))
                            .forEach(matrix -> matrix.getMatrix().keySet()
                                    .forEach(key -> matrix.getRow(key).remove((Integer) columnCount)));

                    Matrix cluster = copyClusters.get(clusterCount);
                    for (Integer index : cluster.getMatrix().keySet()) {
                        cluster.getMatrix().get(index).add(columnCount);
                    }

                    double tempObjective = objectiveFunction(copyClusters);
                    if (tempObjective > currentObjective) {
                        currentObjective = tempObjective;
                        bestCluster = copyClusters;
                    }
                }
            }
        }
        return bestCluster;
    }

    

    private void printFinalResult(Result result) {
        System.out.println("Max objective value: " + result.getObjectiveValue());
        System.out.println("Number of clusters: " + result.getNumberOfCluster());
    }


    private <T> T getClone(T o) {
        Cloner cloner = new Cloner();
        return cloner.deepClone(o);
    }

    private Result moveRows(List<Matrix> iteratedCluster) {

        Result result = new Result(iteratedCluster);
        List<Matrix> rowChangedClusters;

        for (int rowCount = 1; rowCount <= inputMatrix.getRowDimension(); rowCount++) {
            rowChangedClusters = addRow(iteratedCluster, rowCount);
            double objectiveValue = objectiveFunction(rowChangedClusters);
            if (objectiveValue > result.getObjectiveValue()) {
                result.setObjectiveValue(objectiveValue);
                result.setCluster(rowChangedClusters);
            }
        }
        return result;
    }

    private Result moveColumns(List<Matrix> iteratedCluster) {

        Result result = new Result(iteratedCluster);
        List<Matrix> columnChangedClusters;

        for (int columnCount = 1; columnCount <= inputMatrix.getColumnDimension(); columnCount++) {
            columnChangedClusters = addColumn(iteratedCluster, columnCount);
            double objectiveValue = objectiveFunction(columnChangedClusters);
            if (objectiveValue > result.getObjectiveValue()) {
                result.setObjectiveValue(objectiveValue);
                result.setCluster(columnChangedClusters);
            }
        }
        return result;
    }
}

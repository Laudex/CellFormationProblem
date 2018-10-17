package entities;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


public class Result {

    private double objectiveValue = -1.0;
    private List<Matrix> cluster;
    private int numberOfCluster = 0;

    public Result() {
        cluster = new ArrayList<>();
    }

    public Result(List<Matrix> cluster) {
        this.cluster = cluster;
    }

    public Result(double objectiveValue, List<Matrix> cluster) {
        this.objectiveValue = objectiveValue;
        this.cluster = cluster;
    }

    @Override
    public String toString() {
        return "Result{" +
                "objectiveValue=" + objectiveValue +
                ", cluster=" + cluster +
                ", numberOfCluster=" + numberOfCluster +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Result result = (Result) o;

        if (Double.compare(result.objectiveValue, objectiveValue) != 0) return false;
        if (numberOfCluster != result.numberOfCluster) return false;
        return cluster != null ? cluster.equals(result.cluster) : result.cluster == null;

    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(objectiveValue);
        result = (int) (temp ^ (temp >>> 32));
        result = 31 * result + (cluster != null ? cluster.hashCode() : 0);
        result = 31 * result + numberOfCluster;
        return result;
    }

    public int getNumberOfCluster() {
        return numberOfCluster;
    }

    public void setNumberOfCluster(int numberOfCluster) {
        this.numberOfCluster = numberOfCluster;
    }

    public double getObjectiveValue() {
        return objectiveValue;
    }

    public void setObjectiveValue(double objectiveValue) {
        this.objectiveValue = objectiveValue;
    }

    public List<Matrix> getCluster() {
        return cluster;
    }

    public void setCluster(List<Matrix> cluster) {
        this.cluster = cluster;
    }

}

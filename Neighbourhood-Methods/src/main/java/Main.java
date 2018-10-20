import GeneralVNS.GeneralVNSSecond;
import entities.Cluster;
import entities.FileService;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;


public class Main {
    private final static String outputPath = "C:\\vns\\out\\";

    public static void main(String[] args) {

        List<String> fileNames = new ArrayList<>();
        fileNames.add("30x50.txt");

        for (int i = 1; i <= fileNames.size(); i++) {
            String fileName = fileNames.get(i-1);
            String path = "C:\\vns\\in\\" + fileName;

            FileService fileService = new FileService();
            RealMatrix matrix = fileService.readMatrixFromTxt(path);
            int totalNumberOfOnes = calculateTotalNumberOfOnes(matrix);

            Cluster.setMatrix(matrix);
            GeneralVNSSecond generalVNSSecond = new GeneralVNSSecond(matrix, totalNumberOfOnes);

            ArrayList<Cluster> startClusters = findInitialSolution(matrix);
            double startObjectiveValue = generalVNSSecond.objectiveFunction(startClusters);
            generalVNSSecond.generalVNS(startClusters, startObjectiveValue);
            ArrayList<Cluster> results = generalVNSSecond.getFinalResult();
            fileService.writeToFile(outputPath + "30x50.sol.txt", results);
        }
    }

    public static ArrayList<Cluster> findInitialSolution(RealMatrix matrix){
        int machinesCount = matrix.getRowDimension();
        int partsCount = matrix.getColumnDimension();
        int middleMachines = machinesCount / 2;
        int middleParts = partsCount / 2;
        ArrayList<Cluster> clusters = new ArrayList<>();
        ArrayList<Integer> firstMachines = new ArrayList<>();
        ArrayList<Integer> secondMachines = new ArrayList<>();
        ArrayList<Integer> firstParts = new ArrayList<>();
        ArrayList<Integer> secondParts = new ArrayList<>();
        for (int i = 1; i <= middleMachines; i++){
            firstMachines.add(i);
        }
        for (int i = middleMachines + 1; i <= machinesCount; i++){
            secondMachines.add(i);
        }
        for (int j = 1; j <= middleParts; j++){
            firstParts.add(j);
        }
        for (int j = middleParts + 1; j <= partsCount; j++){
            secondParts.add(j);
        }
        Cluster firstCluster = new Cluster(firstMachines, firstParts);
        Cluster secondCluster = new Cluster(secondMachines, secondParts);
        firstCluster.countOnesAndZeros();
        secondCluster.countOnesAndZeros();
        clusters.add(firstCluster);
        clusters.add(secondCluster);
        return clusters;
    }

    public static int calculateTotalNumberOfOnes(RealMatrix matrix){
        int totalNumberOfOnes = 0;
        for (int i = 0; i < matrix.getColumnDimension(); i++){
            for (int j = 0; j < matrix.getRowDimension(); j++){
                try {
                    if (matrix.getEntry(j, i) == 1.0) {
                        totalNumberOfOnes++;
                    }
                } catch (OutOfRangeException e){
                    System.out.println();
                }
            }
        }
        return totalNumberOfOnes;
    }
}

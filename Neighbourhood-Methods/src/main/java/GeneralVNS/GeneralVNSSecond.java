package GeneralVNS;

import com.rits.cloning.Cloner;
import entities.Cluster;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;

public class GeneralVNSSecond {

    private RealMatrix matrix;
    private int totalNumberOfOnes;

    private ArrayList<Cluster> finalResult = new ArrayList<>();

    public ArrayList<Cluster> getFinalResult() {
        return finalResult;
    }

    public GeneralVNSSecond(RealMatrix matrix, int totalNumberOfOnes) {
        this.matrix = matrix;
        this.totalNumberOfOnes = totalNumberOfOnes;
    }

    public double objectiveFunction(ArrayList<Cluster> clusters) {
        int numberOfZerosInsideCells = 0;
        int numberOfOnesInsideCells = 0;
        for (Cluster cluster : clusters) {
            numberOfOnesInsideCells += cluster.getNumberOfOnes();
            numberOfZerosInsideCells += cluster.getNumberOfZeros();
        }
        return (double) numberOfOnesInsideCells / (totalNumberOfOnes + numberOfZerosInsideCells);
    }

    public void generalVNS(ArrayList<Cluster> clusters, double objectiveValue) {
        double bestObjectiveValue = objectiveValue;
        finalResult = getClone(clusters);

       // int kMax = Math.min(matrix.getRowDimension(), matrix.getColumnDimension());
        int kMax = 70;
        for (int k = 2; k <= kMax; k++) {
            ArrayList<Cluster> shakedClusters = shaking(k);
            //double shakedObjectiveValue = objectiveFunction(shakedClusters);
            ArrayList<Cluster> vndResult = vnd(shakedClusters);
            double vndObjectiveValue = objectiveFunction(vndResult);
            if (vndObjectiveValue > bestObjectiveValue) {
                bestObjectiveValue = vndObjectiveValue;
                finalResult = vndResult;
            }

        }

        System.out.println(bestObjectiveValue);
        for (Cluster cluster : finalResult) {
            ArrayList<Integer> machines = cluster.getMachines();
            ArrayList<Integer> parts = cluster.getParts();
            System.out.println("Cluster: " + machines.toString() + " " + parts.toString());
        }
    }

    public ArrayList<Cluster> shaking(int amountOfParts) {
        ArrayList<Cluster> clusters = new ArrayList<>();
        int machinesInCluster = matrix.getRowDimension() / amountOfParts;
        int partsInCluster = matrix.getColumnDimension() / amountOfParts;
        if (amountOfParts > Math.min(matrix.getRowDimension(), matrix.getColumnDimension())){
            int size = finalResult.size();
            int firstClusterIndex = (int)(Math.random() * (size -1));
            int secondClusterIndex = (int)(Math.random() * (size -1));
            while (firstClusterIndex == secondClusterIndex){
                secondClusterIndex = (int)(Math.random() * (size -1));
            }
            clusters = merge(firstClusterIndex, secondClusterIndex, getClone(finalResult));

        } else {
            for (int i = 0; i < amountOfParts; i++) {
                Cluster newCluster;
                int startMachine = i * machinesInCluster + 1;
                int startPart = i * partsInCluster + 1;
                if (amountOfParts - 1 != i) {
                    int finishMachine = i * machinesInCluster + machinesInCluster;
                    int finishPart = i * partsInCluster + partsInCluster;
                    newCluster = formNewCluster(startMachine, finishMachine, startPart, finishPart);
                } else {
                    int finishMachine = matrix.getRowDimension();
                    int finishPart = matrix.getColumnDimension();
                    newCluster = formNewCluster(startMachine, finishMachine, startPart, finishPart);
                }
                clusters.add(newCluster);

            }
        }
        return clusters;
    }

    public Cluster formNewCluster(int startMachine, int finishMachine, int startPart, int finishPart) {
        ArrayList<Integer> machines = new ArrayList<>();
        ArrayList<Integer> parts = new ArrayList<>();
        for (int i = startMachine; i <= finishMachine; i++) {
            machines.add(i);
        }
        for (int j = startPart; j <= finishPart; j++) {
            parts.add(j);
        }

        Cluster cluster = new Cluster(machines, parts);
        cluster.countOnesAndZeros();
        return cluster;
    }

    public ArrayList<Cluster> vnd(ArrayList<Cluster> clusters) {
        ArrayList<Cluster> afterMachineMove = getClone(clusters);
        double bestObjective = objectiveFunction(afterMachineMove);
        for (int l = 0; l < 150; l++) {
            ArrayList<Cluster> nextMove = machineMove(afterMachineMove);
            ArrayList<Cluster> partMove = partMove(nextMove);
            double newObjective = objectiveFunction(partMove);
            if (newObjective > bestObjective) {
                bestObjective = newObjective;
                afterMachineMove = partMove;
            }

        }
        return afterMachineMove;
        //ArrayList<Cluster> afterPartMove = partMove(clusters);
    }

    public ArrayList<Cluster> partMove(ArrayList<Cluster> clusters) {
        ArrayList<Cluster> bestClusters = getClone(clusters);
        double bestObjectiveValue = objectiveFunction(bestClusters);
        int currentCluster = -1;
        for (Cluster cluster : clusters) {
            currentCluster++;
            ArrayList<Integer> parts = cluster.getParts();
            if (parts.size() > 1) {
                for (Integer part : parts) {
                    for (int clustersCount = 0; clustersCount < clusters.size(); clustersCount++) {
                        if (clustersCount != currentCluster) {
                            ArrayList<Cluster> copyClusters = getClone(clusters);
                            Cluster from = copyClusters.get(currentCluster);
                            Cluster to = copyClusters.get(clustersCount);
                            from.getParts().remove(part);
                            ArrayList<Integer> toParts = to.getParts();
                            toParts.add(part);
                            from.countOnesAndZeros();
                            to.countOnesAndZeros();

                            double newOjectiveValue = objectiveFunction(copyClusters);
                            if (newOjectiveValue > bestObjectiveValue) {
                                bestObjectiveValue = newOjectiveValue;
                                bestClusters = copyClusters;
                            }
                        }
                    }
                }
            }
        }
        return bestClusters;
    }

    public ArrayList<Cluster> machineMove(ArrayList<Cluster> clusters) {
        ArrayList<Cluster> bestClusters = getClone(clusters);
        double bestObjectiveValue = objectiveFunction(bestClusters);
        int currentCluster = -1;
        for (Cluster cluster : clusters) {
            currentCluster++;
            ArrayList<Integer> machines = cluster.getMachines();
            if (machines.size() > 1) {
                for (Integer machine : machines) {
                    for (int clustersCount = 0; clustersCount < clusters.size(); clustersCount++) {
                        if (clustersCount != currentCluster) {
                            ArrayList<Cluster> copyClusters = getClone(clusters);
                            Cluster from = copyClusters.get(currentCluster);
                            Cluster to = copyClusters.get(clustersCount);
                            from.getMachines().remove(machine);
                            ArrayList<Integer> toMachines = to.getMachines();
                            toMachines.add(machine);
                            from.countOnesAndZeros();
                            to.countOnesAndZeros();

                            double newOjectiveValue = objectiveFunction(copyClusters);
                            if (newOjectiveValue > bestObjectiveValue) {
                                bestObjectiveValue = newOjectiveValue;
                                bestClusters = copyClusters;
                            }
                        }
                    }
                }
            }
        }
        return bestClusters;
    }

    public static ArrayList<Cluster> merge(int first, int second, ArrayList<Cluster> clusters){
        Cluster firstCluster = clusters.get(first);
        Cluster secondCluster = clusters.remove(second);
        ArrayList<Integer> machines = firstCluster.getMachines();
        ArrayList<Integer> parts = firstCluster.getParts();
        machines.addAll(secondCluster.getMachines());
        parts.addAll(secondCluster.getParts());
        return clusters;

    }

    private <T> T getClone(T o) {
        Cloner cloner = new Cloner();
        return cloner.deepClone(o);
    }
}

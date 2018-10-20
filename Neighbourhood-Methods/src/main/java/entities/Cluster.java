package entities;

import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;

public class Cluster {
    private ArrayList<Integer> machines = new ArrayList<>();
    private ArrayList<Integer> parts = new ArrayList<>();

    private static RealMatrix matrix;

    public static void setMatrix(RealMatrix matrix) {
        Cluster.matrix = matrix;
    }

    private int numberOfZeros;
    private int numberOfOnes;

    public Cluster() {
    }

    public Cluster(ArrayList<Integer> machines, ArrayList<Integer> parts) {
        this.machines = machines;
        this.parts = parts;
    }

    public ArrayList<Integer> getMachines() {
        return machines;
    }

    public void setMachines(ArrayList<Integer> machines) {
        this.machines = machines;
    }

    public ArrayList<Integer> getParts() {
        return parts;
    }

    public void setParts(ArrayList<Integer> parts) {
        this.parts = parts;
    }

    public int getNumberOfZeros() {
        return numberOfZeros;
    }

    public void setNumberOfZeros(int numberOfZeros) {
        this.numberOfZeros = numberOfZeros;
    }

    public int getNumberOfOnes() {
        return numberOfOnes;
    }

    public void setNumberOfOnes(int numberOfOnes) {
        this.numberOfOnes = numberOfOnes;
    }

    public void countOnesAndZeros(){
        int numberOfZeros = 0;
        int numberOfOnes = 0;
        for (Integer machine : machines){
            for (Integer part : parts){
                if (matrix.getEntry(machine - 1, part -1 ) == 1.0){
                    numberOfOnes++;
                } else {
                    numberOfZeros++;
                }
            }
        }
        setNumberOfOnes(numberOfOnes);
        setNumberOfZeros(numberOfZeros);
    }
}

package entities;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;


public class FileService {

    private RealMatrix inputMatrix;

    public RealMatrix readMatrixFromTxt(String path) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(path));

            String str = in.readLine().trim();
            int space = str.indexOf(" ");
            inputMatrix = new BlockRealMatrix(Integer.parseInt(str.substring(0, space)),
                    Integer.parseInt(str.substring(space + 1, str.length())));

            int count = 0;
            while ((str = in.readLine()) != null) {
                List<String> indexes = new ArrayList<>(Arrays.asList(str.trim().split(" ")));
                indexes.remove(0);

                double[] indexList = new double[inputMatrix.getColumnDimension()];
                for (int i = 0; i < inputMatrix.getColumnDimension(); i++) {
                    indexList[i] = 0;
                }
                indexes.forEach(index -> indexList[Integer.parseInt(index) - 1] = 1);
                inputMatrix.setRow(count++, indexList);
            }
            in.close();
            return inputMatrix;
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return null;
        }
    }
    public void writeToFile(String path, ArrayList<Cluster> clusters){
        TreeMap<Integer, Integer> machinesMapping = new TreeMap<>();
        TreeMap<Integer, Integer> partsMapping = new TreeMap<>();
        int clusterCount = 0;
        for (Cluster cluster : clusters){
            clusterCount++;
            for (Integer machine : cluster.getMachines()){
                machinesMapping.put(machine, clusterCount);
            }
            for (Integer part : cluster.getParts()){
                partsMapping.put(part, clusterCount);
            }

        }
        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(path));
            StringBuilder stringBuilder = new StringBuilder();
            for (Map.Entry<Integer, Integer> entry : machinesMapping.entrySet()){
                stringBuilder.append(entry.getKey() + "_" + entry.getValue() + " ");
            }
            stringBuilder.append("\n");
            for (Map.Entry<Integer, Integer> entry : partsMapping.entrySet()){
                stringBuilder.append(entry.getKey() + "_" + entry.getValue() + " ");
            }
            writer.write(stringBuilder.toString());
            writer.close();
        } catch (IOException e){
            System.out.println(e.getMessage());
        }
    }
}

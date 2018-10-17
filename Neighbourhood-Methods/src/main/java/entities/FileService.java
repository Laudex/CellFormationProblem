package entities;

import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


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
    public void writeToFile(String pathToFile, Result finalResult) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(Paths.get(pathToFile));
            StringBuilder stringBuilder = new StringBuilder();
            int clusterId = 1;
            for (Matrix matrix : finalResult.getCluster()) {
                for (Integer machine : matrix.getMatrix().keySet()){
                    stringBuilder.append(machine + "_" + clusterId + " ");
                }
                clusterId++;
            }
            clusterId = 1;
            stringBuilder.append("\n");
            for (Matrix matrix : finalResult.getCluster()){
                List<Integer> parts = matrix.getColumnIndexes();
                for (Integer part : parts){
                    stringBuilder.append(part + "_" + clusterId + " ");
                }
                clusterId++;
            }
            writer.write(stringBuilder.toString());
            writer.close();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}

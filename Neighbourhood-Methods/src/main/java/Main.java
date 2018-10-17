import GeneralVNS.GeneralVNS;
import entities.FileService;
import entities.Result;
import org.apache.commons.math3.linear.RealMatrix;

import java.util.ArrayList;
import java.util.List;


public class Main {
    private final static int K_MAX = 40;
    private final static int L_MAX = 150;
    private final static String outputPath = "C:\\vns\\out\\";

    public static void main(String[] args) {

        List<String> fileNames = new ArrayList<>();
        fileNames.add("20x20.txt");

        for (int i = 1; i <= fileNames.size(); i++) {
            String fileName = fileNames.get(i-1);
            String path = "C:\\vns\\in\\" + fileName;

            FileService fileService = new FileService();
            RealMatrix matrix = fileService.readMatrixFromTxt(path);
            GeneralVNS generalVNS = new GeneralVNS(matrix);
            Result result = generalVNS.generalVNS(K_MAX, L_MAX);
            String out = outputPath + fileName;
            fileService.writeToFile(out, result);
        }
    }
}

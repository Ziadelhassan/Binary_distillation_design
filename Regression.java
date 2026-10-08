import java.io.*;
import java.util.*;
public class Regression {

    /**
     * Reads equilibrium data points from a given text file and stores them in a 2D array.
     * @param filePath the location of the VLE data.
     * @return double[][] array where column 0 is x and column 1 is y.
     */
   public double[][] readVLEData(String filePath) {
        
        ArrayList<Double> xList = new ArrayList<>();
        ArrayList<Double> yList = new ArrayList<>();
        
        try (Scanner scan = new Scanner(new File(filePath))) {
            while (scan.hasNextDouble()) {
                xList.add(scan.nextDouble()); 
                yList.add(scan.nextDouble()); 
            }
        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found at " + filePath);
           
            return new double[0][2]; 
        }

        int numDataPoints = xList.size();
        double[][] vleData = new double[numDataPoints][2];

        for (int i = 0; i < numDataPoints; i++) {
            vleData[i][0] = xList.get(i); 
            vleData[i][1] = yList.get(i); 
        }

        return vleData;
    }
    
    /**
     * Performs generalized linear least squares regression using the double[][] array from readVLEData to fit the data. 
     * @param x liquid compositions x stored in an array.
     * @param y vapour compositions y stored i an array.
     * @return QuadraticRelationship object containing the fitted coefficients a, b and c. 
     */
    public QuadraticRelationship leastSquaresRegression(double [] x, double[] y){

        int n = x.length;

        //Creates array of Y (nx1) and fills it
        double[][] Y = new double[n][1];
        for(int i = 0; i < n; i++){
            Y[i][0] = y[i];
        }

        //creates an array of X
        double[][] X = new double[n][3];
        for(int i = 0; i < n; i++){
            X[i][0] = 1;
            X[i][1] = x[i];
            X[i][2] = x[i] * x[i];
        }

        MatrixManipulation manip = new MatrixManipulation();

        //X^T
        double[][] XT = manip.transpose(X);

        //X^T * X
        double[][] XT_X = manip.mMult(XT, X);

        //(X^T * X)^-1
        double [][] XT_X_inverse = manip.inverse(XT_X);

        //(X^T * Y)
        double[][] XT_Y = manip.mMult(XT, Y);

        //B = (X^T * X)^-1 *(X^T * Y)
        double B[][] = manip.mMult(XT_X_inverse, XT_Y);

        double c = B[0][0];
        double b = B[1][0];
        double a = B[2][0];

        return new QuadraticRelationship(a, b, c);
    }

}

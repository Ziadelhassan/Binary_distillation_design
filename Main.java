import java.util.*;
import java.io.*;
public class Main {

    //The heart of the program. reads inputs, initilizes objects, runs regression and distillation calculations.
    public static void main(String[] args) {
        
        Scanner console = new Scanner(System.in);
        String inputFilePath = "input.txt";
        double q = 0, zF = 0, xD = 0, xB = 0, F = 0; 
        
        boolean validInputs = false; 

        while(!validInputs){
            System.out.println("\nHave you inputted your desired initial conditions in " + inputFilePath + "?");
            System.out.print("Press 'Y' for Yes, 'N' for No: ");
            String response = console.next();

            if (response.equalsIgnoreCase("N")) {
            // If no, ask user for inputs and overwrite the file
            updateInputFile(console, inputFilePath);
            } else if (!response.equalsIgnoreCase("Y")) {
            System.out.println("Invalid input. Assuming 'Y' and proceeding...");
            }
        
        
            try (Scanner fileSc = new Scanner(new File(inputFilePath))) {
            
                q  = parseValue(fileSc.nextLine());
                zF = parseValue(fileSc.nextLine());
                xD = parseValue(fileSc.nextLine());
                xB = parseValue(fileSc.nextLine());
                F  = parseValue(fileSc.nextLine());
                System.out.println("Successfully read inputs from " + inputFilePath);
                validInputs = true;

            } catch (Exception e) {
            System.out.println("\n--- The input file contains invalid or missing values. ---");
            System.out.println("Ensure file format is: 'Label = Value'.");
            }
        }

        Regression regression = new Regression();
        String vleFilePath = "Equilibrium Data.txt";
        double[][] vleData = regression.readVLEData(vleFilePath);

        if (vleData.length == 0) {
            System.out.println("TEST FAILED: VLE data file not found or empty.");
            return;
        }

        int n = vleData.length;
        double[] x = new double[n];
        double[] y = new double[n];
        for (int i = 0; i < n; i++) {
            x[i] = vleData[i][0];
            y[i] = vleData[i][1];
        }

        
        QuadraticRelationship vleCurve = regression.leastSquaresRegression(x, y);
        System.out.println("VLE Curve: " + vleCurve);

        try {
            
            DistillationCalculations simulator = new DistillationCalculations(q, zF, xD, xB);

            simulator.calculateRmin(vleCurve);
            
            
            double rMin = simulator.getRMin();
            double R = rMin * 1.5;
            simulator.setR(R);
            
            simulator.buildOperatingLines();
            
            double Stages = simulator.mccabeThiele();

            double feedStage = simulator.getFeedStage();

            double D = F * (zF - xB) / (xD - xB);
            double B = F - D;
            double sse = 0.0;

            for(int i = 0; i < x.length; i++){
                double predictedY = vleCurve.evaluate(x[i]);
                double error = y[i] - predictedY;
                sse += error * error;
            }

            double mse = sse / x.length;

            LinearRelationship qLine = simulator.getQLine();
            double pinchX;
            double pinchY;

            if (qLine != null) {
                pinchX = solveQuadraticIntersection(vleCurve, qLine);
                pinchY = qLine.evaluate(pinchX);
            } else {
                pinchX = zF;
                pinchY = vleCurve.evaluate(zF);
            }

            LinearRelationship rectLine = simulator.getRectifyingLine();
            LinearRelationship stripLine = simulator.getStrippingLine();

            double y1 = xD;
            double x1 = solveQuadraticIntersection(vleCurve, y1);
            //prints to console.
            System.out.println("\n--- Simulation Results ---");
            System.out.printf("Minimum Reflux (Rmin):   %8.2f\n", rMin);
            System.out.printf("Actual Reflux (R):       %8.2f\n", R);
            System.out.printf("Number of Stages:        %8.0f\n", Stages);
            System.out.printf("Feed Stage:              %8.0f\n", feedStage);
            System.out.printf("Distillate Flow (D):     %8.0f kmol/h\n", D);
            System.out.printf("Bottoms Flow (B):        %8.0f kmol/h\n", B);
           

            String outputFilePath = "output.txt";
            try (PrintWriter writer = new PrintWriter(new FileWriter(outputFilePath))) {
                writer.println("--- Simulation Results ---");
                writer.println("VLE Curve: " + vleCurve);
                writer.println();
                writer.printf("Minimum Reflux (Rmin):   %8.2f\n", rMin);
                writer.printf("Actual Reflux (R):       %8.2f\n", R);
                writer.printf("Number of Stages:        %8.0f\n", Stages);
                writer.printf("Feed Stage:              %8.0f\n", feedStage);
                writer.printf("Distillate Flow (D):     %8.0f kmol/h\n", D);
                writer.printf("Bottoms Flow (B):        %8.0f kmol/h\n", B);

                writer.println("--- Validation Data ---");
                writer.println("\n--- 2. VLE REGRESSION VALIDATION ---");
                writer.println("Model: Quadratic (y = ax^2 + bx + c)");
                writer.printf("Coeff a:                %10.6f\n", vleCurve.getA());
                writer.printf("Coeff b:                %10.6f\n", vleCurve.getB());
                writer.printf("Coeff c:                %10.6f\n", vleCurve.getC());
                writer.printf("Fit Accuracy (MSE):     %10.8f\n", mse);

                writer.println("\n--- 3. OPERATING LINE VALIDATION ---");
                writer.println("Format: y = mx + b");
                writer.println("q-Line:");
                if (qLine != null) {
                    writer.printf("  Slope (m):            %10.4f\n", qLine.getA());
                    writer.printf("  Intercept (b):        %10.4f\n", qLine.getB());
                } else {
                    // Handle the q=1 case explicitly
                    writer.printf("  Vertical Line (q=1):  x = %10.4f\n", zF);
                }
                writer.println("Rectifying Line:");
                writer.printf("  Slope (m):            %10.4f\n", rectLine.getA());
                writer.printf("  Intercept (b):        %10.4f\n", rectLine.getB());
                writer.println("Stripping Line:");
                writer.printf("  Slope (m):            %10.4f\n", stripLine.getA());
                writer.printf("  Intercept (b):        %10.4f\n", stripLine.getB());

                writer.println("\n--- 4. PINCH POINT VALIDATION ---");
                writer.println("Intersection of q-Line and VLE Curve used for Rmin:");
                writer.printf("  x_pinch:              %10.4f\n", pinchX);
                writer.printf("  y_pinch:              %10.4f\n", pinchY);
                writer.println("  (Check: Rmin = (xD - y_p) / (y_p - x_p) )");

                writer.println("\n--- 5. Validation of Composition at Each Stage ---");
                writer.println();
            
                // Retrieve and print the data
                java.util.List<String> history = simulator.getStageData();
            
                for (String row : history) {
                    writer.println(row);
                }
            

            }
            System.out.println("\nResults successfully written to " + outputFilePath);
            System.out.printf("Please read the file 'output.txt' for a detailed report.");

        } catch (Exception e) {
            System.out.println("\n--- TEST FAILED ---");
            e.printStackTrace();
        }
    }

    /**
     * General case: VLE: y = ax² + bx + c, line: y = m x + b
     * @param quad the quadratic VLE curve
     * @param lin the linear q-line
     * @return the x-coordinate, or the pinch point of the intersection.
     */
    private static double solveQuadraticIntersection(QuadraticRelationship quad, LinearRelationship lin) {
    double a = quad.getA();
    double b = quad.getB() - lin.getA(); // Combine linear terms (b - m)
    double c = quad.getC() - lin.getB(); // Combine constants (c - b_line)
    
    double discrim = b*b - 4*a*c;
    
    // Robustness check for negative discriminant
    if (discrim < 0) return 0; 
    
    // Return the smaller positive root
    double x1 = (-b + Math.sqrt(discrim)) / (2*a);
    double x2 = (-b - Math.sqrt(discrim)) / (2*a);
    
    
    if (x1 >= 0 && x1 <= 1) return x1;
    return x2;
    }

    /**
     * Validation helper method that solves for x given a target y. This is the overloaded form of solveQuadraticEquation that finds the value of x corresponding to a given y.
     * @param quad Quadratic VLE curve
     * @param yTarget The known y value.
     * @return The calculated x value.
     */
    private static double solveQuadraticIntersection(QuadraticRelationship quad, double yTarget) {
    // Create a line: y = 0*x + yTarget
    LinearRelationship horizontalLine = new LinearRelationship(0.0, yTarget);
    
    return solveQuadraticIntersection(quad, horizontalLine);
    }

    /**
     * Helper to prompt user for inputs and overwrite input.txt, takes q, zF, xD, xB and F as input from the user.
     * updates filePath
     */
    private static void updateInputFile(Scanner console, String filePath) {
        System.out.println("\n--- Enter New Simulation Parameters ---");
        
        System.out.print("Feed Quality (q): ");
        double q = console.nextDouble();
        
        System.out.print("Feed Concentration (zF): ");
        double zF = console.nextDouble();
        
        System.out.print("Distillate Concentration (xD): ");
        double xD = console.nextDouble();
        
        System.out.print("Bottoms Concentration (xB): ");
        double xB = console.nextDouble();
        
        System.out.print("Feed Flow Rate (F): ");
        double F = console.nextDouble();

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            writer.println("Feed quality (q) = " + q);
            writer.println("Feed concentration (zF) = " + zF);
            writer.println("Distillate concentration (xD) = " + xD);
            writer.println("Bottoms concentration (xB) = " + xB);
            writer.println("Feed flow rate (F) = " + F);
            System.out.println("New parameters saved to " + filePath);
        } catch (IOException e) {
            System.out.println("Error writing to input file.");
            e.printStackTrace();
        }
    }

    /**
     * 
     * Extracts numerical part of the user input from the input.txt file.
     * @param line the line of text from the input file
     * @return The double value parsed from each line.
     */
    private static double parseValue(String line) {
        // Splits the line at the "=" sign and takes the second part
        String[] parts = line.split("=");
       if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new IllegalArgumentException("Missing value in line: " + line + "in input.txt");
        }
        return Double.parseDouble(parts[1].trim());
    }
}
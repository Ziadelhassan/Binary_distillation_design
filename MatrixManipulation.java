public class MatrixManipulation {

    public MatrixManipulation(){
    }

    public MatrixManipulation(MatrixManipulation src){
    }

    public boolean equals(Object comparator){
        if(comparator == null){
            return false;
        }
        if(this.getClass() != comparator.getClass()){
            return false;
        }
        return true;
    }

    public MatrixManipulation clone(){
        return this;
    }

    /**
     * Transposes a matrix
     * @param A the matrix that will be transposed
     * @return this method will return a new matrix T[i][j] = A[j][i] as an Array
     */
    public double[][] transpose(double[][] A){
        //null check
        if(A == null){
            throw new IllegalArgumentException("Matrix cannot be null");
        }

        if(A.length == 0){
            return new double[0][0];
        }
        
        int rows = A.length;
        int cols = A[0].length;

        double T[][] = new double[cols][rows];

        for(int i = 0; i < rows; i++){
            for(int j = 0; j < cols; j++){
                T[j][i] = A[i][j];
            }
        }

        return T;
    }

    /**
     * Multiplies matrices with valid dimensions i.e. the number of columns of the first matrix must match the number of rows in the second matrix.
     * @param A the first matrix involved in the multiplication.
     * @param B the second matrix involved in the multiplication.
     * @return the resulting matrix as an Array.
     */
    public double[][] mMult(double[][] A, double B[][]){
      
        if (A == null || B == null) {
            throw new IllegalArgumentException("Matrices cannot be null.");
        }

        int rowsA = A.length;
        int rowsB = B.length; 

        if (rowsA == 0) {
            return new double[0][0]; 
        }

        if (rowsB == 0) {
             throw new IllegalArgumentException("Matrix B is empty, cannot multiply.");
        }
        
        int colsB = B[0].length;
        int colsA = A[0].length;
    
        if (colsA != rowsB) {
            throw new IllegalArgumentException("Matrix A columns (" + colsA + 
                                               ") must equal Matrix B rows (" + rowsB + ").");
        }

        double[][] C = new double[rowsA][colsB];
        for(int i = 0; i < rowsA; i++){
            for(int j = 0; j < colsB; j++){

                double sum = 0;
                for(int k = 0; k < colsA; k++){
                    sum += A[i][k] * B[k][j];
                }

                C[i][j] = sum;
            }
        }
        
        return C;
    }

    /**
     * Calculates the inverse of a square matrix using Gauss-Jordan elimination.
     * @param A the matrix array to be inverted.
     * @return an array called inverse that is the inverse of input array A.
     */
    public double[][] inverse(double[][] A){
        
        if (A == null) {
            throw new IllegalArgumentException("Matrix cannot be null.");
        }
        if (A.length == 0) {
            return new double[0][0]; 
        }

        int n = A.length; 

        if (n != A[0].length) {
            throw new IllegalArgumentException("Matrix must be square to calculate an inverse.");
        }

        int augmentedCols = n * 2;
        double[][] aug = new double[n][augmentedCols];
        
        double[][] I = new double[n][n];
        for (int i = 0; i < n; i++) {
            I[i][i] = 1.0;
        }

        for (int i = 0; i < n; i++) { //identity matrix
            for (int j = 0; j < n; j++) {
                aug[i][j] = A[i][j];     
                aug[i][j + n] = I[i][j]; 
            }
        }
        
        //Gauss Jordan Elimination
        for (int j = 0; j < n; j++) { 

            int pivotRow = j;
            for (int i = j + 1; i < n; i++) {
                if (Math.abs(aug[i][j]) > Math.abs(aug[pivotRow][j])) {
                    pivotRow = i;
                }
            }

            double[] tempRow = aug[j];
            aug[j] = aug[pivotRow];
            aug[pivotRow] = tempRow;

           
            if (Math.abs(aug[j][j]) < 1e-10) { //if any element on the diagonal i.e pivot is close enough to 0 then the matrix is singular.
                throw new IllegalArgumentException("Matrix is singular and has no inverse.");
            }

            double pivotVal = aug[j][j];
            for (int k = 0; k < augmentedCols; k++) {
                aug[j][k] /= pivotVal;
            }

            //elimination
            for (int i = 0; i < n; i++) {
                if (i != j) {
                    double factor = aug[i][j];
                    for (int k = 0; k < augmentedCols; k++) {
                        aug[i][k] -= factor * aug[j][k];
                    }
                }
            }
        }
        
        //Taking the inverse matrix out of the composite matrix
        double[][] inverse = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                inverse[i][j] = aug[i][j + n];
            }
        }

        return inverse;
    }
    
}

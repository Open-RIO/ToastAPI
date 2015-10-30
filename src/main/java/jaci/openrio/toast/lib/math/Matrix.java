package jaci.openrio.toast.lib.math;

/**
 * The Matrix Class is a math object that exists in the form of a 2 Dimensional Matrix. These matrices follow
 * standard mathematical matrix rules.
 *
 * @author Jaci
 */
public class Matrix {

    int rows, columns;
    double[][] values;

    public static Matrix IDENTITY = new Matrix(2,
            1, 0,
            0, 1);

    /**
     * Create a new, empty matrix with a specified number of rows and columns
     */
    public Matrix(int rows, int columns) {
        this.rows = rows; this.columns = columns;
        values = new double[rows][columns];
    }

    /**
     * Create a new matrix with a specified dataset.
     * @param data      The data to insert into the matrix
     * @param columns   How many columns for each row (used to split the dataset into a 2d matrix)
     */
    public Matrix(double[] data, int columns) {
        this(data.length / columns, columns);
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                values[i][j] = data[(i * columns) + j];
    }

    /**
     * Create a new matrix with a specified dataset.
     * @param data      The data to insert into the matrix
     * @param columns   How many columns for each row (used to split the dataset into a 2d matrix)
     */
    public Matrix(int columns, double... data) {
        this(data, columns);
    }

    /**
     * Create a new matrix with the specified dataset.
     * @param data      The data to use for the matrix. This 2d array is the data of the 2d matrix
     */
    public Matrix(double[][] data) {
        values = data;
        rows = data.length; columns = data[0].length;
    }

    /**
     * Get the values inside of the matrix in the form of a 2d double matrix
     */
    public double[][] getValues() {
        return values;
    }

    /**
     * Get the amount of rows in the matrix
     */
    public int getRows() {
        return rows;
    }

    /**
     * Get the amount of columns per row in the matrix
     */
    public int getColumns() {
        return columns;
    }

    /**
     * Set a value in the matrix to a specified value
     * @param rowID     The row number to update
     * @param columnID  The column number to update
     * @param value     The value to update to
     */
    public void set(int rowID, int columnID, double value) {
        values[rowID][columnID] = value;
    }

    /**
     * Get a value in the matrix
     * @param rowID     The row number to get
     * @param columnID  The column number to get
     */
    public double get(int rowID, int columnID) {
        return values[rowID][columnID];
    }

    /**
     * Returns true if this matrix equals another matrix or a 2d double array
     */
    public boolean equals(Object o) {
        double[][] va;
        if (o instanceof Matrix) {
            va = ((Matrix) o).values;
        } else if (o instanceof double[][]) {
            va = (double[][]) o;
        } else return false;
        if (va == null) return false;

        if (va.length != values.length || va[0].length != values[0].length) return false;
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++)
                if (get(i,j) != va[i][j]) return false;
        }
        return true;
    }

    /**
     * Returns true if the matrix is square (the row count and column count are equal)
     */
    public boolean isSquare() {
        return rows == columns;
    }

    /**
     * Clone the 2d double array for manipulation
     */
    private double[][] cloneValues() {
        double[][] newDoubles = new double[values.length][];
        for(int i = 0; i < values.length; i++)
            newDoubles[i] = values[i].clone();
        return newDoubles;
    }

    /**
     * Get the determinant of the matrix. For a 2x2 matrix, this is 'ad-bc'. Determinants can only be
     * found if the matrix is square.
     */
    public double determinant() {
        if (!isSquare())
            throw new IllegalArgumentException("Determinant Matrices must be Square");
        return determinant(cloneValues(), rows);
    }

    /**
     * Returns true if the matrix is singular (the determinant is 0). Singular matrices cannot be inverted.
     */
    public boolean isSingular() {
        return determinant() == 0;
    }

    /**
     * Convert the matrix into a nicely formatted string
     */
    public String asString() {
        String s = rows + " * " + columns + " matrix\n";
        for (int i = 0; i < rows; i++) {
            String build = "";
            for (int j = 0; j < columns; j++) {
                build += "\t " + MathHelper.round(values[i][j], 2);
            }
            s += build;
            if (i != rows - 1) s += "\n";
        }
        return s;
    }

    /**
     * Invert the matrix. This cannot occur if the matrix is singular (determinant is 0)
     */
    public Matrix invert() {
        if (isSingular())
            throw new IllegalStateException("Cannot invert singular matrix!");
        return new Matrix(invert(cloneValues()));
    }

    /**
     * Multiply this matrix by another matrix.
     * The object this method is called on is treated as the pre-multiplied matrix, and the parameter is the
     * post-multiplied.
     * e.g.
     *  A.multiply(B) => AB
     * @param mx The matrix to multiply with
     */
    public Matrix multiply(Matrix mx) {
        return new Matrix(multiply(values, mx.values));
    }

    /**
     * Multiply this matrix element-wise with a scalar quantity
     */
    public Matrix multiply(double scalar) {
        double[][] newMatrix = new double[rows][columns];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                newMatrix[i][j] = values[i][j] * scalar;
        return new Matrix(newMatrix);
    }

    /**
     * Multiply this matrix by another matrix.
     * The object this method is called on is treated as the post-multiplied matrix, and the parameter is the
     * pre-multiplied.
     * e.g.
     *  A.postmultiplyBy(B) => BA
     * @param mx The matrix to multiply with
     */
    public Matrix premultiplyBy(Matrix mx) {
        return mx.multiply(this);
    }

    /**
     * Alias for {@link #multiply(Matrix)}
     */
    public Matrix postmultiplyBy(Matrix mx) {
        return this.multiply(mx);
    }

    /**
     * Add this matrix element-wise with another matrix
     */
    public Matrix add(Matrix mx) {
        double[][] newMatrix = new double[rows][columns];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                newMatrix[i][j] = values[i][j] + mx.get(i, j);
        return new Matrix(newMatrix);
    }

    /**
     * Subtract the parameter matrix from this matrix
     */
    public Matrix subtract(Matrix mx) {
        double[][] newMatrix = new double[rows][columns];
        for (int i = 0; i < rows; i++)
            for (int j = 0; j < columns; j++)
                newMatrix[i][j] = values[i][j] - mx.get(i, j);
        return new Matrix(newMatrix);
    }

    // -- STATICS -- //

    public static double[][] multiply(double[][] a, double[][] b) {
        int aRows = a.length;
        int aColumns = a[0].length;
        int bRows = b.length;
        int bColumns = b[0].length;

        if (aColumns != bRows)
            throw new IllegalArgumentException("Matrices of size " + aRows+"*"+aColumns +
                    " and " + bRows+"*"+bColumns +" cannot be multiplied!");

        double[][] result = new double[aRows][bColumns];

        for (int i = 0; i < aRows; i++)
            for (int j = 0; j < bColumns; j++)
                for (int k = 0; k < aColumns; k++)
                    result[i][j] += a[i][k] * b[k][j];

        return result;
    }

    public static double determinant(double[][] a, int n){
        double det = 0;
        int p = 0, q = 0, sign = 1;

        if(n==1) det = a[0][0];
        else {
            double b[][] = new double[n-1][n-1];
            for(int x = 0 ; x < n ; x++){
                p=0; q=0;
                for(int i = 1;i < n; i++){
                    for(int j = 0; j < n;j++){
                        if(j != x){
                            b[p][q++] = a[i][j];
                            if(q % (n-1) == 0){
                                p++;
                                q=0;
                            }
                        }
                    }
                }
                det = det + a[0][x] * determinant(b, n-1) * sign;
                sign = -sign;
            }
        }
        return det;
    }

    // Thanks to http://www.sanfoundry.com/java-program-find-inverse-matrix/ for inversion algorithm

    public static double[][] invert(double[][] a) {
        int n = a.length;
        double x[][] = new double[n][n];
        double b[][] = new double[n][n];
        int index[] = new int[n];
        for (int i=0; i<n; ++i)
            b[i][i] = 1;

        gaussian(a, index);

        for (int i=0; i<n-1; ++i)
            for (int j=i+1; j<n; ++j)
                for (int k=0; k<n; ++k)
                    b[index[j]][k]
                            -= a[index[j]][i]*b[index[i]][k];

        for (int i=0; i<n; ++i) {
            x[n-1][i] = b[index[n-1]][i]/a[index[n-1]][n-1];
            for (int j=n-2; j>=0; --j) {
                x[j][i] = b[index[j]][i];
                for (int k=j+1; k<n; ++k)
                    x[j][i] -= a[index[j]][k]*x[k][i];
                x[j][i] /= a[index[j]][j];
            }
        }
        return x;
    }

    public static void gaussian(double[][] a, int index[]) {
        int n = index.length;
        double c[] = new double[n];

        for (int i=0; i<n; ++i)
            index[i] = i;

        for (int i=0; i<n; ++i) {
            double c1 = 0;
            for (int j=0; j<n; ++j) {
                double c0 = Math.abs(a[i][j]);
                if (c0 > c1) c1 = c0;
            }
            c[i] = c1;
        }

        int k = 0;
        for (int j=0; j<n-1; ++j) {
            double pi1 = 0;
            for (int i=j; i<n; ++i) {
                double pi0 = Math.abs(a[index[i]][j]);
                pi0 /= c[index[i]];
                if (pi0 > pi1) {
                    pi1 = pi0;
                    k = i;
                }
            }

            int itmp = index[j];
            index[j] = index[k];
            index[k] = itmp;
            for (int i=j+1; i<n; ++i) {
                double pj = a[index[i]][j]/a[index[j]][j];

                a[index[i]][j] = pj;

                for (int l=j+1; l<n; ++l)
                    a[index[i]][l] -= pj*a[index[j]][l];
            }
        }
    }

}

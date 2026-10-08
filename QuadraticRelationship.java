public class QuadraticRelationship extends Relationship{
    private double a;
    private double b;
    private double c;

    public QuadraticRelationship(double a, double b, double c){
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public QuadraticRelationship(QuadraticRelationship src){
        this.a = src.a;
        this.b = src.b;
        this.c = src.c;
    }

    public double getA(){
        return a;
    }
    public double getB(){
        return b;
    }
    public double getC(){
        return c;
    }

    public void setA(double a){
        this.a = a;
    }
    public void setB(double b){
        this.b = b;
    }
    public void setC(double c){
        this.c = c;
    }

    public boolean equals(Object comparator){
        if(!super.equals(comparator)){
            return false;
        }
        QuadraticRelationship that = (QuadraticRelationship) comparator;

        return (this.a == that.a) && (this.b == that.b) && (this.c == that.c);
    }

    public QuadraticRelationship clone(){
        return new QuadraticRelationship(this);
    }

    @Override
    public String toString() {
        return "y = " + this.a + "x^2 + " + this.b + "x + " + this.c;
    }

    /**
     * Evaluates the relationship for a given x-value.
     * @param x is the input variable.
     * @return the corresponding y-value as Double.
     */
    public double evaluate(double x){
        return (this.a * Math.pow(x,2) + this.b * x + this.c);
    }
    
}

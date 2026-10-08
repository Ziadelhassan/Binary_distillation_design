public class LinearRelationship extends Relationship{
    private double a;
    private double b;

    
    public LinearRelationship(double a, double b){
        this.a = a;
        this.b = b;
    }

    public LinearRelationship(LinearRelationship src){
        this.a = src.a;
        this.b = src.b;
    }

    public double getA(){
        return a;
    }
    public double getB(){
        return b;
    }

    public void setA(double a){
        this.a = a;
    }
    public void setB(double b){
        this.b = b;
    }


    public boolean equals(Object comparator){
        if(!super.equals(comparator)){
            return false;
        }

        LinearRelationship that = (LinearRelationship) comparator;
        return (this.a == that.a) && (this.b == that.b);
    }

    public LinearRelationship clone(){
        return new LinearRelationship(this);
    }
    
    /**
     * Evaluates the relationship for a given x-value.
     * @param x is the input variable.
     * @return the corresponding y-value as Double.
     */
    public double evaluate(double x){
        return (this.a * x) + this.b;
    }
}

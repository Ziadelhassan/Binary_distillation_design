public abstract class Relationship {
    
    
    public boolean equals(Object comparator){
        if(comparator == null) return false;
        if(this.getClass() != comparator.getClass()) return false;
        return true;
    }

    public abstract Relationship clone();
    
    /**
     * Evaluates the relationship for a given x-value.
     * @param x is the input variable.
     * @return the corresponding y-value as Double.
     */
    public abstract double evaluate(double x);
}

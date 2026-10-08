import java.util.ArrayList;
import java.util.List;

public class DistillationCalculations {
    
    private double q;
    private double zF;
    private double xD;
    private double xB;

    private double rMin;
    private double R;
    private double xPrime;
    private double yPrime;
    private double feedStage;

    private LinearRelationship rectifyingLine;
    private LinearRelationship strippingLine;
    private LinearRelationship qLine;
    
    private List<String> stageData = new ArrayList<>();

    private Relationship vleCurve;

    public DistillationCalculations(double q, double zF, double xD, double xB){
        if(q == 0 || zF == 0 || xD == 0 || xB == 0){
            throw new IllegalArgumentException("Initial Parameters must be greater than 0.");
        }

        this.q = q;
        this.zF = zF;
        this.xD = xD;
        this.xB = xB;
        this.xPrime = 0;
        this.yPrime = 0;
        this.rectifyingLine = null;
        this.strippingLine = null;
        this.qLine = null;
        this.vleCurve = null;
    }

    public DistillationCalculations (DistillationCalculations src){
        if(src == null){
            throw new IllegalArgumentException("Source object cannot be null");
        }
        this.q = src.q;
        this.zF = src.zF;
        this.xD = src.xD;
        this.xB = src.xB;

        this.rMin = src.rMin;
        this.R = src.R;
        this.xPrime = src.xPrime;
        this.yPrime = src.yPrime;
        
        this.rectifyingLine = (src.rectifyingLine != null) ? src.rectifyingLine.clone() : null;
        this.strippingLine = (src.strippingLine != null) ? src.strippingLine.clone() : null;
        this.qLine = (src.qLine != null) ? src.qLine.clone() : null;
        this.vleCurve = (src.vleCurve != null) ? src.vleCurve.clone() : null;
    }

    public double getQ(){
        return q;
    }
    public double getZF(){
        return zF;
    }
    public double getXD(){
        return xD;
    }
    public double getXB(){
        return xB;
    }
    public double getRMin(){
        return rMin;
    }
    public double getR(){
        return R;
    }
    public LinearRelationship getRectifyingLine(){
        return rectifyingLine;
    }
    public LinearRelationship getStrippingLine(){
        return strippingLine;
    }
    public LinearRelationship getQLine(){
        return qLine;
    }
    public List<String> getStageData() {
        return new ArrayList<>(this.stageData);
    }
    public double getxPrime(){
        return xPrime;
    }
    public double getyPrime(){
        return yPrime;
    }
    public double getFeedStage() {
        return this.feedStage;
    }
    public void setQ(double q){ 
        this.q = q; 
    }
    public void setZF(double zF){ 
        this.zF = zF; 
    }
    public void setXD(double xD){ 
        this.xD = xD; 
    }
    public void setXB(double xB){ 
        this.xB = xB; 
    }
    public void setR(double R){ 
        this.R = R; 
    }
    public DistillationCalculations clone(){
        return new DistillationCalculations(this);
    }

    public boolean equals(Object comparator){
        if(comparator == null){
            return false;
        }
        
        if(this.getClass() != comparator.getClass()){
            return false;
        }

        DistillationCalculations that = (DistillationCalculations) comparator;
       
        if (this.q != that.q || this.zF != that.zF || this.xD != that.xD ||
            this.xB != that.xB || this.rMin != that.rMin || this.R != that.R ||
            this.xPrime != that.xPrime || 
            this.yPrime != that.yPrime) {
            return false;
        }

        if (this.rectifyingLine == null) {
            if (that.rectifyingLine != null) return false;
        } else if (!this.rectifyingLine.equals(that.rectifyingLine)) {
            return false;
        }
        
        if (this.strippingLine == null) {
            if (that.strippingLine != null) return false;
        } else if (!this.strippingLine.equals(that.strippingLine)) {
            return false;
        }
        
        if (this.qLine == null) {
            if (that.qLine != null) return false;
        } else if (!this.qLine.equals(that.qLine)) {
            return false;
        }
        
        if (this.vleCurve == null) {
            if (that.vleCurve != null) return false;
        } else if (!this.vleCurve.equals(that.vleCurve)) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Calculates minimum reflux ratio Rmin by determining the intersection (pinch point) of the q-line and the VLE curve.
     * @param vle quadratic relationship representing the VLE curve.
     */
    public void calculateRmin(Relationship vle){
        
        if(vle == null){
            throw new IllegalArgumentException("VLE relationship cannot be null");
        }

        this.vleCurve = vle;

        double qX;
        double qY;

        if (Math.abs(q - 1.0) < 1e-10) {
            // The q-line is vertical: x = zF
            this.qLine = null;
            
            // Intersection is simply where x = zF hits the VLE curve
            qX = zF;
            qY = vle.evaluate(zF);
        }

        else {

        double slope_q = q / (q - 1.0);
        double intercept_q = zF * (1.0 - slope_q);
        this.qLine = new LinearRelationship(slope_q, intercept_q);

        QuadraticRelationship quadraticVLE = (QuadraticRelationship) this.vleCurve;
        double a_v = quadraticVLE.getA();
        double b_v = quadraticVLE.getB();
        double c_v = quadraticVLE.getC();

        //solving for this equation: (a_v)x^2 + (b_v - slope_q)x + (c_v - intercept_q) = 0
        double A = a_v;
        double B = b_v - slope_q;
        double C = c_v - intercept_q;

        double discriminant = B * B - 4 * A * C;
        if(discriminant < 0){
            throw new ArithmeticException("q-line and VLE do not intersect");
        }

        double x_root1 = (-B + Math.sqrt(discriminant)) / (2 * A);
        double x_root2 = (-B - Math.sqrt(discriminant)) / (2 * A);
        

        if(x_root1 >= 0 && x_root1 <= 1.0){
            qX = x_root1;
        } else {
            qX = x_root2;
        }
        
        qY = qLine.evaluate(qX);
        }
        // Rmin = (xD - y') / (y' - x')
        rMin = (xD - qY) / (qY - qX);
    }

        /**
         * Constructs the operating lines: rectifying line and stripping line based on the actual Reflux Ratio and the q-line intersection.
         */
        public void buildOperatingLines(){
            
            double rectifyingSlope = this.R / (this.R + 1.0);
            double rectifyingIntercept = this.xD / (this.R + 1.0);
            this.rectifyingLine = new LinearRelationship(rectifyingSlope, rectifyingIntercept);

            if(Math.abs(q-1) < 1e-10){
                this.xPrime = this.zF;
            // Find y on the rectifying line at that x
            this.yPrime = this.rectifyingLine.evaluate(this.xPrime);
            }

            else{
            double slope_q = this.qLine.getA();
            double intercept_q = this.qLine.getB(); 

            this.xPrime = (intercept_q - rectifyingIntercept) / (rectifyingSlope - slope_q);
            this.yPrime = this.qLine.evaluate(this.xPrime);
            }

            double strippingSlope = (this.yPrime - this.xB) / (this.xPrime - this.xB);
            double strippingIntercept = this.xB * (1.0 - strippingSlope);
            this.strippingLine = new LinearRelationship(strippingSlope, strippingIntercept);
        }
        /**
         * helper method to solve VLE quadratic for x given y by using the quadratic formula
         * @param y vapour composition on the operating line.
         * @return The equilibrium liquid composition of x as a Double.
         */
        private double solveQuadraticIntersection(double y){
            QuadraticRelationship vle = (QuadraticRelationship) this.vleCurve;
            double a = vle.getA();
            double b = vle.getB();
            double c = vle.getC();
        
            double A_quad = a;
            double B_quad = b;
            double C_quad = c - y;

            double discriminant = B_quad * B_quad - 4 * A_quad * C_quad;
            // want the smaller positive root --> x1 = (-B + sqrt(disc)) / (2A) -> (-B + pos) / (neg) = smaller positive root
            if (discriminant < 0) {
            discriminant = 0;
            }   
            return (-B_quad + Math.sqrt(discriminant)) / (2 * A_quad);
        }

        /**
         * Performs the stepping algorithm taught in unit operations from the distillate to bottoms to count the theoretical stages and record composition data for validation.
         * @return the total number of theoretical stages (excluding reboiler) as Double.
         */
        public double mccabeThiele(){

            double stageCount = 0;
            double currX = this.xD;
            double currY = this.xD; //starts at xD, xD on the 45 degree line.

            this.feedStage = -1; //i.e not found.
            boolean feedStageFound = false; //initilize as not found.

            this.stageData.clear();
            this.stageData.add(String.format("%-6s %-10s %-10s %-10s", "Step", "x_Liquid", "y_Vapor", "Section"));
            this.stageData.add(String.format("%-6s %-10.4f %-10.4f %-10s", "Dist", currX, currY, "Start"));

            while(currX > this.xB) {
                
                LinearRelationship opLine;
                String section;

                if(currX > this.xPrime){
                    opLine = this.rectifyingLine;
                    section = "Rectifying";
                }
                else {
                    opLine = this.strippingLine;
                    section = "Stripping";
                    if(!feedStageFound){
                        this.feedStage = stageCount + 1; //+ 1 becauase we are on this stage in this current iteration.
                        feedStageFound = true;
                    }
                }
                
                currY = opLine.evaluate(currX);
                double nextX = solveQuadraticIntersection(currY);
                stageCount++;
                
                this.stageData.add(String.format("%-6.0f %-10.4f %-10.4f %-10s", 
                                             stageCount, nextX, currY, section));

                currX = nextX;
                if(stageCount > 1000){
                    throw new ArithmeticException("Stage count exceeds 1000, check for runaway loop");
                }
                
            }
            
            return stageCount - 1; // subtracting 1 because we are not including the reboiler.
            
    }
}

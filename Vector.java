
public class Vector {
    public double x;
    public double y;
    public double z;

    public Vector(double xx, double yy, double zz) {
         x = xx;
         y = yy;
         z = zz;
    }

    public void DebugPrint(String mystr) {
         System.out.println(mystr + x + "," + y + "," + z);
    }
	
	//This vector is P
	private double CalculateDotProduct(Vector Q) {

        //P dot Q > 0 means P and Q are on the same side of the plane
        //P dot Q < 0 means P and Q are on opposite sides of the plane
        //P dot Q == 0 means P and Q are perpendicular
        //So you take the normal vector of the side do dot product of it with unit-Z vector to see if it
        //is visible to "camera" or not visible

        double answer = (this.x * Q.x) + (this.y * Q.y) + (this.z * Q.z);
        return answer;
   }
}//class Vector


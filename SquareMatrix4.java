
public class SquareMatrix4 {
	
    public double row1col1;
    public double row1col2;
    public double row1col3;
	public double row1col4;
	
	public double row2col1;
	public double row2col2;
	public double row2col3;
	public double row2col4;
	
	public double row3col1;
	public double row3col2;
	public double row3col3;
	public double row3col4;
	
	public double row4col1;
	public double row4col2;
	public double row4col3;
	public double row4col4;

    public SquareMatrix4(double r1c1, double r1c2, double r1c3, double r1c4,
	                     double r2c1, double r2c2, double r2c3, double r2c4,
						 double r3c1, double r3c2, double r3c3, double r3c4,
						 double r4c1, double r4c2, double r4c3, double r4c4) {
         row1col1 = r1c1;
         row1col2 = r1c2;
         row1col3 = r1c3;
		 row1col4 = r1c4;
		 
		 row2col1 = r2c1;
		 row2col2 = r2c2;
		 row2col3 = r2c3;
		 row2col4 = r2c4;
		 
		 row3col1 = r3c1;
		 row3col2 = r3c2;
		 row3col3 = r3c3;
		 row3col4 = r3c4;
		 
		 row4col1 = r4c1;
		 row4col2 = r4c2;
		 row4col3 = r4c3;
		 row4col4 = r4c4;
    }
	
	public Point4 Multiply_Point4(Point4 P)
	{
		double answer_xx;
		double answer_yy;
		double answer_zz;
		double answer_ww;
		
		answer_xx = (row1col1 * P.x) + (row1col2 * P.y) + (row1col3 * P.z) + (row1col4 * P.w);
		answer_yy = (row2col1 * P.x) + (row2col2 * P.y) + (row2col3 * P.z) + (row2col4 * P.w);
		answer_zz = (row3col1 * P.x) + (row3col2 * P.y) + (row3col3 * P.z) + (row3col4 * P.w);
		answer_ww = (row4col1 * P.x) + (row4col2 * P.y) + (row4col3 * P.z) + (row4col4 * P.w);
		
		Point4 answer = new Point4(answer_xx, answer_yy, answer_zz, answer_ww);
		return answer;
	}
	
	public SquareMatrix4 Multiply(SquareMatrix4 G)
	{
		double r1c1 = (this.row1col1 * G.row1col1) + (this.row1col2 * G.row2col1) + (this.row1col3 * G.row3col1) + (this.row1col4 * G.row4col1);
		double r1c2 = (this.row1col1 * G.row1col2) + (this.row1col2 * G.row2col2) + (this.row1col3 * G.row3col2) + (this.row1col4 * G.row4col2);
		double r1c3 = (this.row1col1 * G.row1col3) + (this.row1col2 * G.row2col3) + (this.row1col3 * G.row3col3) + (this.row1col4 * G.row4col3);
		double r1c4 = (this.row1col1 * G.row1col4) + (this.row1col2 * G.row2col4) + (this.row1col3 * G.row3col4) + (this.row1col4 * G.row4col4);
		
		double r2c1 = (this.row2col1 * G.row1col1) + (this.row2col2 * G.row2col1) + (this.row2col3 * G.row3col1) + (this.row2col4 * G.row4col1);
		double r2c2 = (this.row2col1 * G.row1col2) + (this.row2col2 * G.row2col2) + (this.row2col3 * G.row3col2) + (this.row2col4 * G.row4col2);
		double r2c3 = (this.row2col1 * G.row1col3) + (this.row2col2 * G.row2col3) + (this.row2col3 * G.row3col3) + (this.row2col4 * G.row4col3);
		double r2c4 = (this.row2col1 * G.row1col4) + (this.row2col2 * G.row2col4) + (this.row2col3 * G.row3col4) + (this.row2col4 * G.row4col4);

        double r3c1 = (this.row3col1 * G.row1col1) + (this.row3col2 * G.row2col1) + (this.row3col3 * G.row3col1) + (this.row3col4 * G.row4col1);
        double r3c2 = (this.row3col1 * G.row1col2) + (this.row3col2 * G.row2col2) + (this.row3col3 * G.row3col2) + (this.row3col4 * G.row4col2);
        double r3c3 = (this.row3col1 * G.row1col3) + (this.row3col2 * G.row2col3) + (this.row3col3 * G.row3col3) + (this.row3col4 * G.row4col3);
        double r3c4 = (this.row3col1 * G.row1col4) + (this.row3col2 * G.row2col4) + (this.row3col3 * G.row3col4) + (this.row3col4 * G.row4col4);

        double r4c1 = (this.row4col1 * G.row1col1) + (this.row4col2 * G.row2col1) + (this.row4col3 * G.row3col1) + (this.row4col4 * G.row4col1);
        double r4c2 = (this.row4col1 * G.row1col2) + (this.row4col2 * G.row2col2) + (this.row4col3 * G.row3col2) + (this.row4col4 * G.row4col2);
        double r4c3 = (this.row4col1 * G.row1col3) + (this.row4col2 * G.row2col3) + (this.row4col3 * G.row3col3) + (this.row4col4 * G.row4col3);
        double r4c4 = (this.row4col1 * G.row1col4) + (this.row4col2 * G.row2col4) + (this.row4col3 * G.row3col4) + (this.row4col4 * G.row4col4);

        SquareMatrix4 answer = new SquareMatrix4(r1c1, r1c2, r1c3, r1c4,
		                                         r2c1, r2c2, r2c3, r2c4,
												 r3c1, r3c2, r3c3, r3c4,
												 r4c1, r4c2, r4c3, r4c4);
		return answer;
	}
	
}//class


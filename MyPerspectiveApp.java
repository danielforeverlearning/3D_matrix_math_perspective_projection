

// /usr/lib/jvm/jdk-18/bin/javac -cp MyPerspectiveApp.java SortByZ.java Point.java Vector.java
// /usr/lib/jvm/jdk-18/bin/java  -cp MyPerspectiveApp

import java.util.ArrayList;
import java.util.Collections;
import java.lang.Math;
import java.awt.*;
import java.awt.Dimension;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;




public class MyPerspectiveApp extends JFrame implements KeyListener {

   final static Color bg = Color.white;
   final static Color fg = Color.black;
   final static Color side1_color = Color.red;
   final static Color side2_color = Color.pink;
   final static Color side3_color = Color.yellow;
   final static Color side4_color = Color.blue;
   final static Color side5_color = Color.green;
   final static Color side6_color = Color.black;

   //java-swing user-coords is X is positive to right, Y is positive down, 0,0 is top-left, center of window is 300,300
   
   //OpenGL camera-space, origin is (0,0,0), X is positive to right, Y is positive up, focal-length == e == 1 == (1/tan(alpha/2)), alpha==90degrees, camera/eye-ball at (0,0,-1)
   //clockwise , surface visible
   //counter-clockwise, surface hidden (not-visible)
   
   final static double f = 4; //far plane  is z=-4, we know -f <= Pz <= -n, all points inside view-frustrum in camera-space
   final static double n = 1; //near plane is z=-1, we know -f <= Pz <= -n, all points inside view-frustrum in camera-space
   final static double l = -1; //left plane is x=-1
   final static double r = 1;  //right plane is x=1
   final static double t = 1;  //top plane is y=1
   final static double b = -1; //bottom plane is y=-1   
   
   

   Point4 Center_Of_Cube_Point = new Point4(0,0,-2.5, 1);

   //front start
   Point4 side1_vertex1 = new Point4(-0.5,  0.5, -2, 1);
   Point4 side1_vertex2 = new Point4(-0.5, -0.5, -2, 1);
   Point4 side1_vertex3 = new Point4( 0.5, -0.5, -2, 1);
   Point4 side1_vertex4 = new Point4( 0.5,  0.5, -2, 1);
   Vector side1_normal_vector = side1_vertex1.CalculateCrossProduct(side1_vertex2, side1_vertex3);

   //top start 
   Point4 side2_vertex1 = new Point4(-0.5, 0.5, -2, 1);
   Point4 side2_vertex2 = new Point4( 0.5, 0.5, -2, 1);
   Point4 side2_vertex3 = new Point4( 0.5, 0.5, -3, 1);
   Point4 side2_vertex4 = new Point4(-0.5, 0.5, -3, 1);
   Vector side2_normal_vector = side2_vertex1.CalculateCrossProduct(side2_vertex2, side2_vertex3);
   
   //bottom start
   Point4 side3_vertex1 = new Point4(-0.5, -0.5, -2, 1);
   Point4 side3_vertex2 = new Point4(-0.5, -0.5, -3, 1);
   Point4 side3_vertex3 = new Point4( 0.5, -0.5, -3, 1);
   Point4 side3_vertex4 = new Point4( 0.5, -0.5, -2, 1);
   Vector side3_normal_vector = side3_vertex1.CalculateCrossProduct(side3_vertex2, side3_vertex3);

   //right start
   Point4 side4_vertex1 = new Point4(0.5,  -0.5, -2, 1);
   Point4 side4_vertex2 = new Point4(0.5,  -0.5, -3, 1);
   Point4 side4_vertex3 = new Point4(0.5,   0.5, -3, 1);
   Point4 side4_vertex4 = new Point4(0.5,   0.5, -2, 1);
   Vector side4_normal_vector = side4_vertex1.CalculateCrossProduct(side4_vertex2, side4_vertex3);

   //left start
   Point4 side5_vertex1 = new Point4(-0.5, -0.5, -2, 1);
   Point4 side5_vertex2 = new Point4(-0.5,  0.5, -2, 1);
   Point4 side5_vertex3 = new Point4(-0.5,  0.5, -3, 1);
   Point4 side5_vertex4 = new Point4(-0.5, -0.5, -3, 1);
   Vector side5_normal_vector = side5_vertex1.CalculateCrossProduct(side5_vertex2, side5_vertex3);

   //back start
   Point4 side6_vertex1 = new Point4(-0.5,  0.5, -3, 1);
   Point4 side6_vertex2 = new Point4( 0.5,  0.5, -3, 1);
   Point4 side6_vertex3 = new Point4( 0.5, -0.5, -3, 1);
   Point4 side6_vertex4 = new Point4(-0.5, -0.5, -3, 1);
   Vector side6_normal_vector = side6_vertex1.CalculateCrossProduct(side6_vertex2, side6_vertex3);


   public MyPerspectiveApp(String name) {
        super(name);
   }


   private int  Convert_To_UserCoord_X(double dd) {
	    //for now origin of our java-swing-"viewing plane" is 300,300 in java-swing-user-coords
		//for java-swing-user-coords and camera-space X is positive to the right
		
		//after transformation from view-frustrum homogenous-clip-space which is a cube each edge [-1,+1]
		
	    int answer = (int) ((dd * 100) + 300);
        return answer;		
   }
   
   private int  Convert_To_UserCoord_Y(double dd) {
	    //for now origin of our java-swing-"viewing plane" is 300,300 in java-swing-user-coords
		//but camera-space Y is positive going up
		//but java-swing-user-coords Y is positive going down
		
		//after transformation from view-frustrum homogenous-clip-space which is a cube each edge [-1,+1]
		
		int answer = (int) (300 - (dd * 100));
		return answer;
   }

   
   private void DrawObject(Graphics2D g2) {
			
			//matrix math
			SquareMatrix4 Translate = new SquareMatrix4(1, 0, 0, 0 - Center_Of_Cube_Point.x,
			                                            0, 1, 0, 0 - Center_Of_Cube_Point.y,
														0, 0, 1, 0 - Center_Of_Cube_Point.z - 2.5,
														0, 0, 0, 1);
														
			SquareMatrix4 TransformViewFrustrumToClipSpace = new SquareMatrix4( (2 * n)/(r - l),        0       , (r+l)/(r-l)         ,             0     ,
			                                                                          0        , (2 * n)/(t - b), (t+b)/(t-b)         ,             0     ,
																					  0        ,        0       , -1 * ((f+n)/(f-n))  , (-2 * n * f)/(f-n),
																					  0        ,        0       ,      -1             ,             0     );
																					  

			
			//So if we rotated the cube in camera-space
			//If we just want to look at perspective-projection of cube as if it was rotating around its own center
			//translate vertexes to camera-space-origin then
			//translate vertexes to (0,0,-2.5) then
			//transform view-frustrum to clipspace
			
			//Using a normal_threshold like below does not work too good, people may think this is dumb
			//but i think if you sort the normal vectors by z-coordinate, then draw lowest to highest
			//double num = 2;
			//int exp    = -14;
			//double fix_artifact = Math.pow(num, exp);
			
			SortByZ mysort = new SortByZ();
			mysort.add(1, side1_normal_vector.z);
			mysort.add(2, side2_normal_vector.z);
			mysort.add(3, side3_normal_vector.z);
			mysort.add(4, side4_normal_vector.z);
			mysort.add(5, side5_normal_vector.z);
			mysort.add(6, side6_normal_vector.z);
			mysort.DebugPrint();
			
			int count = mysort.GetCount();
			int[] draw_side = mysort.GetDrawSides();
			
			
			for (int ii=0; ii < count; ii++) {
				
				if (draw_side[ii] == 1) {
					
					Point4 translated_side1_vertex1 = Translate.Multiply_Point4(side1_vertex1);
					Point4 translated_side1_vertex2 = Translate.Multiply_Point4(side1_vertex2);
					Point4 translated_side1_vertex3 = Translate.Multiply_Point4(side1_vertex3);
					Point4 translated_side1_vertex4 = Translate.Multiply_Point4(side1_vertex4);
					
					Point4 clipspace_side1_vertex1  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side1_vertex1);
					Point4 clipspace_side1_vertex2  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side1_vertex2);
					Point4 clipspace_side1_vertex3  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side1_vertex3);
					Point4 clipspace_side1_vertex4  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side1_vertex4);
	
					//NOW DIVIDE BY -Pz otherwise it shows as orthogonal not perspective
					clipspace_side1_vertex1.Divide_Each_Axis_Value_By(-1 * translated_side1_vertex1.z);
					clipspace_side1_vertex2.Divide_Each_Axis_Value_By(-1 * translated_side1_vertex2.z);
					clipspace_side1_vertex3.Divide_Each_Axis_Value_By(-1 * translated_side1_vertex3.z);
					clipspace_side1_vertex4.Divide_Each_Axis_Value_By(-1 * translated_side1_vertex4.z);
					
					int [] side1_x = { Convert_To_UserCoord_X(clipspace_side1_vertex1.x), Convert_To_UserCoord_X(clipspace_side1_vertex2.x), Convert_To_UserCoord_X(clipspace_side1_vertex3.x), Convert_To_UserCoord_X(clipspace_side1_vertex4.x) };
					int [] side1_y = { Convert_To_UserCoord_Y(clipspace_side1_vertex1.y), Convert_To_UserCoord_Y(clipspace_side1_vertex2.y), Convert_To_UserCoord_Y(clipspace_side1_vertex3.y), Convert_To_UserCoord_Y(clipspace_side1_vertex4.y) };

					g2.setPaint(fg);
					g2.drawPolygon(side1_x, side1_y, 4);
					g2.setColor(side1_color);
					g2.fillPolygon(side1_x, side1_y, 4);
				}
		
				if (draw_side[ii] == 2) {
					
					Point4 translated_side2_vertex1 = Translate.Multiply_Point4(side2_vertex1);
					Point4 translated_side2_vertex2 = Translate.Multiply_Point4(side2_vertex2);
					Point4 translated_side2_vertex3 = Translate.Multiply_Point4(side2_vertex3);
					Point4 translated_side2_vertex4 = Translate.Multiply_Point4(side2_vertex4);
					
					Point4 clipspace_side2_vertex1  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side2_vertex1);
					Point4 clipspace_side2_vertex2  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side2_vertex2);
					Point4 clipspace_side2_vertex3  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side2_vertex3);
					Point4 clipspace_side2_vertex4  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side2_vertex4);
					
					//NOW DIVIDE BY -Pz otherwise it shows as orthogonal not perspective
					clipspace_side2_vertex1.Divide_Each_Axis_Value_By(-1 * translated_side2_vertex1.z);
					clipspace_side2_vertex2.Divide_Each_Axis_Value_By(-1 * translated_side2_vertex2.z);
					clipspace_side2_vertex3.Divide_Each_Axis_Value_By(-1 * translated_side2_vertex3.z);
					clipspace_side2_vertex4.Divide_Each_Axis_Value_By(-1 * translated_side2_vertex4.z);
					
					int [] side2_x = { Convert_To_UserCoord_X(clipspace_side2_vertex1.x), Convert_To_UserCoord_X(clipspace_side2_vertex2.x), Convert_To_UserCoord_X(clipspace_side2_vertex3.x), Convert_To_UserCoord_X(clipspace_side2_vertex4.x) };
					int [] side2_y = { Convert_To_UserCoord_Y(clipspace_side2_vertex1.y), Convert_To_UserCoord_Y(clipspace_side2_vertex2.y), Convert_To_UserCoord_Y(clipspace_side2_vertex3.y), Convert_To_UserCoord_Y(clipspace_side2_vertex4.y) };
					
					g2.setPaint(fg);
					g2.drawPolygon(side2_x, side2_y, 4);
					g2.setColor(side2_color);
					g2.fillPolygon(side2_x, side2_y, 4);
				}
			
				if (draw_side[ii] == 3) {
					
					Point4 translated_side3_vertex1 = Translate.Multiply_Point4(side3_vertex1);
					Point4 translated_side3_vertex2 = Translate.Multiply_Point4(side3_vertex2);
					Point4 translated_side3_vertex3 = Translate.Multiply_Point4(side3_vertex3);
					Point4 translated_side3_vertex4 = Translate.Multiply_Point4(side3_vertex4);
					
					Point4 clipspace_side3_vertex1  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side3_vertex1);
					Point4 clipspace_side3_vertex2  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side3_vertex2);
					Point4 clipspace_side3_vertex3  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side3_vertex3);
					Point4 clipspace_side3_vertex4  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side3_vertex4);
					
					//NOW DIVIDE BY -Pz otherwise it shows as orthogonal not perspective
					clipspace_side3_vertex1.Divide_Each_Axis_Value_By(-1 * translated_side3_vertex1.z);
					clipspace_side3_vertex2.Divide_Each_Axis_Value_By(-1 * translated_side3_vertex2.z);
					clipspace_side3_vertex3.Divide_Each_Axis_Value_By(-1 * translated_side3_vertex3.z);
					clipspace_side3_vertex4.Divide_Each_Axis_Value_By(-1 * translated_side3_vertex4.z);
					
					int [] side3_x = { Convert_To_UserCoord_X(clipspace_side3_vertex1.x), Convert_To_UserCoord_X(clipspace_side3_vertex2.x), Convert_To_UserCoord_X(clipspace_side3_vertex3.x), Convert_To_UserCoord_X(clipspace_side3_vertex4.x) };
					int [] side3_y = { Convert_To_UserCoord_Y(clipspace_side3_vertex1.y), Convert_To_UserCoord_Y(clipspace_side3_vertex2.y), Convert_To_UserCoord_Y(clipspace_side3_vertex3.y), Convert_To_UserCoord_Y(clipspace_side3_vertex4.y) };

					g2.setPaint(fg);
					g2.drawPolygon(side3_x, side3_y, 4);
					g2.setColor(side3_color);
					g2.fillPolygon(side3_x, side3_y, 4);				
				}
			
				if (draw_side[ii] == 4) {
					
					Point4 translated_side4_vertex1 = Translate.Multiply_Point4(side4_vertex1);
					Point4 translated_side4_vertex2 = Translate.Multiply_Point4(side4_vertex2);
					Point4 translated_side4_vertex3 = Translate.Multiply_Point4(side4_vertex3);
					Point4 translated_side4_vertex4 = Translate.Multiply_Point4(side4_vertex4);
					
					Point4 clipspace_side4_vertex1  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side4_vertex1);
					Point4 clipspace_side4_vertex2  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side4_vertex2);
					Point4 clipspace_side4_vertex3  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side4_vertex3);
					Point4 clipspace_side4_vertex4  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side4_vertex4);
					
					//NOW DIVIDE BY -Pz otherwise it shows as orthogonal not perspective
					clipspace_side4_vertex1.Divide_Each_Axis_Value_By(-1 * translated_side4_vertex1.z);
					clipspace_side4_vertex2.Divide_Each_Axis_Value_By(-1 * translated_side4_vertex2.z);
					clipspace_side4_vertex3.Divide_Each_Axis_Value_By(-1 * translated_side4_vertex3.z);
					clipspace_side4_vertex4.Divide_Each_Axis_Value_By(-1 * translated_side4_vertex4.z);
					
					int [] side4_x = { Convert_To_UserCoord_X(clipspace_side4_vertex1.x), Convert_To_UserCoord_X(clipspace_side4_vertex2.x), Convert_To_UserCoord_X(clipspace_side4_vertex3.x), Convert_To_UserCoord_X(clipspace_side4_vertex4.x) };
					int [] side4_y = { Convert_To_UserCoord_Y(clipspace_side4_vertex1.y), Convert_To_UserCoord_Y(clipspace_side4_vertex2.y), Convert_To_UserCoord_Y(clipspace_side4_vertex3.y), Convert_To_UserCoord_Y(clipspace_side4_vertex4.y) };
					
					g2.setPaint(fg);
					g2.drawPolygon(side4_x, side4_y, 4);
					g2.setColor(side4_color);
					g2.fillPolygon(side4_x, side4_y, 4);
				}
			
				if (draw_side[ii] == 5) {
					
					Point4 translated_side5_vertex1 = Translate.Multiply_Point4(side5_vertex1);
					Point4 translated_side5_vertex2 = Translate.Multiply_Point4(side5_vertex2);
					Point4 translated_side5_vertex3 = Translate.Multiply_Point4(side5_vertex3);
					Point4 translated_side5_vertex4 = Translate.Multiply_Point4(side5_vertex4);
					
					Point4 clipspace_side5_vertex1  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side5_vertex1);
					Point4 clipspace_side5_vertex2  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side5_vertex2);
					Point4 clipspace_side5_vertex3  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side5_vertex3);
					Point4 clipspace_side5_vertex4  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side5_vertex4);
					
					//NOW DIVIDE BY -Pz otherwise it shows as orthogonal not perspective
					clipspace_side5_vertex1.Divide_Each_Axis_Value_By(-1 * translated_side5_vertex1.z);
					clipspace_side5_vertex2.Divide_Each_Axis_Value_By(-1 * translated_side5_vertex2.z);
					clipspace_side5_vertex3.Divide_Each_Axis_Value_By(-1 * translated_side5_vertex3.z);
					clipspace_side5_vertex4.Divide_Each_Axis_Value_By(-1 * translated_side5_vertex4.z);
					
					int [] side5_x = { Convert_To_UserCoord_X(clipspace_side5_vertex1.x), Convert_To_UserCoord_X(clipspace_side5_vertex2.x), Convert_To_UserCoord_X(clipspace_side5_vertex3.x), Convert_To_UserCoord_X(clipspace_side5_vertex4.x) };
					int [] side5_y = { Convert_To_UserCoord_Y(clipspace_side5_vertex1.y), Convert_To_UserCoord_Y(clipspace_side5_vertex2.y), Convert_To_UserCoord_Y(clipspace_side5_vertex3.y), Convert_To_UserCoord_Y(clipspace_side5_vertex4.y) };

					g2.setPaint(fg);
					g2.drawPolygon(side5_x, side5_y, 4);
					g2.setColor(side5_color);
					g2.fillPolygon(side5_x, side5_y, 4);
				}
			
				if (draw_side[ii] == 6) {

					Point4 translated_side6_vertex1 = Translate.Multiply_Point4(side6_vertex1);
					Point4 translated_side6_vertex2 = Translate.Multiply_Point4(side6_vertex2);
					Point4 translated_side6_vertex3 = Translate.Multiply_Point4(side6_vertex3);
					Point4 translated_side6_vertex4 = Translate.Multiply_Point4(side6_vertex4);
					
					Point4 clipspace_side6_vertex1  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side6_vertex1);
					Point4 clipspace_side6_vertex2  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side6_vertex2);
					Point4 clipspace_side6_vertex3  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side6_vertex3);
					Point4 clipspace_side6_vertex4  = TransformViewFrustrumToClipSpace.Multiply_Point4(translated_side6_vertex4);
					
					//NOW DIVIDE BY -Pz otherwise it shows as orthogonal not perspective
					clipspace_side6_vertex1.Divide_Each_Axis_Value_By(-1 * translated_side6_vertex1.z);
					clipspace_side6_vertex2.Divide_Each_Axis_Value_By(-1 * translated_side6_vertex2.z);
					clipspace_side6_vertex3.Divide_Each_Axis_Value_By(-1 * translated_side6_vertex3.z);
					clipspace_side6_vertex4.Divide_Each_Axis_Value_By(-1 * translated_side6_vertex4.z);
					
					int [] side6_x = { Convert_To_UserCoord_X(clipspace_side6_vertex1.x), Convert_To_UserCoord_X(clipspace_side6_vertex2.x), Convert_To_UserCoord_X(clipspace_side6_vertex3.x), Convert_To_UserCoord_X(clipspace_side6_vertex4.x) };
					int [] side6_y = { Convert_To_UserCoord_Y(clipspace_side6_vertex1.y), Convert_To_UserCoord_Y(clipspace_side6_vertex2.y), Convert_To_UserCoord_Y(clipspace_side6_vertex3.y), Convert_To_UserCoord_Y(clipspace_side6_vertex4.y) };
					
					g2.setPaint(fg);
					g2.drawPolygon(side6_x, side6_y, 4);
					g2.setColor(side6_color);
					g2.fillPolygon(side6_x, side6_y, 4);
				}
			}//for
   }//DrawObject

   public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        // g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
		Dimension d = getSize();
		g2.clearRect(0,0,d.width,d.height);
		DrawObject(g2);
    }

    public static void main(String s[]) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

    private static void createAndShowGUI() {
        MyPerspectiveApp frame = new MyPerspectiveApp("PerspectiveProjection Test - only x,y,z for rotate, no scaling, no translation");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
       
        frame.pack();
        frame.setBackground(bg);
        frame.setForeground(fg);
        frame.setSize(new Dimension(600,600));
        frame.addKeyListener(frame);
        frame.setVisible(true);
    }



    public void keyTyped(KeyEvent e) {
        displayInfo(e, "KEY TYPED: ");
    }
     
    public void keyPressed(KeyEvent e) {
        displayInfo(e, "KEY PRESSED: ");
    }
     
    public void keyReleased(KeyEvent e) {
        displayInfo(e, "KEY RELEASED: ");

        HandleKeyPress(e);
    }
	
	
	/******
	private void UniformScale(boolean is_uniform_scale_up) {
		
		//So if we rotated the cube in camera-space
		//If we just want to look at perspective-projection of cube as if it was rotating around its own center
		//translate vertexes to camera-space-origin then
		//translate vertexes to (0,0,-2.5) then
		//transform view-frustrum to clipspace
		//for uniform-scaling need to do this translation first like in DrawObject otherwise cube gets distorted
		//(1) translate to camera-space-origin
		//(2) do scaling math
		//(3) retranslate back
		
		double CurrentScale = 0.9;
		
		if (is_uniform_scale_up)
			CurrentScale = 1.1;
		
		//Center of cube point does not change
		
		//side1
		My3D translated_side1_vertex1 = new My3D(side1_vertex1.x - Center_Of_Cube_Point.x, side1_vertex1.y - Center_Of_Cube_Point.y, side1_vertex1.z - Center_Of_Cube_Point.z);
		My3D translated_side1_vertex2 = new My3D(side1_vertex2.x - Center_Of_Cube_Point.x, side1_vertex2.y - Center_Of_Cube_Point.y, side1_vertex2.z - Center_Of_Cube_Point.z);
		My3D translated_side1_vertex3 = new My3D(side1_vertex3.x - Center_Of_Cube_Point.x, side1_vertex3.y - Center_Of_Cube_Point.y, side1_vertex3.z - Center_Of_Cube_Point.z);
		My3D translated_side1_vertex4 = new My3D(side1_vertex4.x - Center_Of_Cube_Point.x, side1_vertex4.y - Center_Of_Cube_Point.y, side1_vertex4.z - Center_Of_Cube_Point.z);
		
		translated_side1_vertex1.x *= CurrentScale;
		translated_side1_vertex1.y *= CurrentScale;
		translated_side1_vertex1.z *= CurrentScale;
		translated_side1_vertex2.x *= CurrentScale;
		translated_side1_vertex2.y *= CurrentScale;
		translated_side1_vertex2.z *= CurrentScale;
		translated_side1_vertex3.x *= CurrentScale;
		translated_side1_vertex3.y *= CurrentScale;
		translated_side1_vertex3.z *= CurrentScale;
		translated_side1_vertex4.x *= CurrentScale;
		translated_side1_vertex4.y *= CurrentScale;
		translated_side1_vertex4.z *= CurrentScale;
		
		side1_vertex1.x += Center_Of_Cube_Point.x;
		side1_vertex1.y += Center_Of_Cube_Point.y;
		side1_vertex1.z += Center_Of_Cube_Point.z;
		side1_vertex2.x += Center_Of_Cube_Point.x;
		side1_vertex2.y += Center_Of_Cube_Point.y;
		side1_vertex2.z += Center_Of_Cube_Point.z;
		side1_vertex3.x += Center_Of_Cube_Point.x;
		side1_vertex3.y += Center_Of_Cube_Point.y;
		side1_vertex3.z += Center_Of_Cube_Point.z;
		side1_vertex4.x += Center_Of_Cube_Point.x;
		side1_vertex4.y += Center_Of_Cube_Point.y;
		side1_vertex4.z += Center_Of_Cube_Point.z;
		
		//side2
		My3D translated_side2_vertex1 = new My3D(side2_vertex1.x - Center_Of_Cube_Point.x, side2_vertex1.y - Center_Of_Cube_Point.y, side2_vertex1.z - Center_Of_Cube_Point.z);
		My3D translated_side2_vertex2 = new My3D(side2_vertex2.x - Center_Of_Cube_Point.x, side2_vertex2.y - Center_Of_Cube_Point.y, side2_vertex2.z - Center_Of_Cube_Point.z);
		My3D translated_side2_vertex3 = new My3D(side2_vertex3.x - Center_Of_Cube_Point.x, side2_vertex3.y - Center_Of_Cube_Point.y, side2_vertex3.z - Center_Of_Cube_Point.z);
		My3D translated_side2_vertex4 = new My3D(side2_vertex4.x - Center_Of_Cube_Point.x, side2_vertex4.y - Center_Of_Cube_Point.y, side2_vertex4.z - Center_Of_Cube_Point.z);
		
		translated_side2_vertex1.x *= CurrentScale;
		translated_side2_vertex1.y *= CurrentScale;
		translated_side2_vertex1.z *= CurrentScale;
		translated_side2_vertex2.x *= CurrentScale;
		translated_side2_vertex2.y *= CurrentScale;
		translated_side2_vertex2.z *= CurrentScale;
		translated_side2_vertex3.x *= CurrentScale;
		translated_side2_vertex3.y *= CurrentScale;
		translated_side2_vertex3.z *= CurrentScale;
		translated_side2_vertex4.x *= CurrentScale;
		translated_side2_vertex4.y *= CurrentScale;
		translated_side2_vertex4.z *= CurrentScale;
		
		side2_vertex1.x += Center_Of_Cube_Point.x;
		side2_vertex1.y += Center_Of_Cube_Point.y;
		side2_vertex1.z += Center_Of_Cube_Point.z;
		side2_vertex2.x += Center_Of_Cube_Point.x;
		side2_vertex2.y += Center_Of_Cube_Point.y;
		side2_vertex2.z += Center_Of_Cube_Point.z;
		side2_vertex3.x += Center_Of_Cube_Point.x;
		side2_vertex3.y += Center_Of_Cube_Point.y;
		side2_vertex3.z += Center_Of_Cube_Point.z;
		side2_vertex4.x += Center_Of_Cube_Point.x;
		side2_vertex4.y += Center_Of_Cube_Point.y;
		side2_vertex4.z += Center_Of_Cube_Point.z;
		
		//side3
		My3D translated_side3_vertex1 = new My3D(side3_vertex1.x - Center_Of_Cube_Point.x, side3_vertex1.y - Center_Of_Cube_Point.y, side3_vertex1.z - Center_Of_Cube_Point.z);
		My3D translated_side3_vertex2 = new My3D(side3_vertex2.x - Center_Of_Cube_Point.x, side3_vertex2.y - Center_Of_Cube_Point.y, side3_vertex2.z - Center_Of_Cube_Point.z);
		My3D translated_side3_vertex3 = new My3D(side3_vertex3.x - Center_Of_Cube_Point.x, side3_vertex3.y - Center_Of_Cube_Point.y, side3_vertex3.z - Center_Of_Cube_Point.z);
		My3D translated_side3_vertex4 = new My3D(side3_vertex4.x - Center_Of_Cube_Point.x, side3_vertex4.y - Center_Of_Cube_Point.y, side3_vertex4.z - Center_Of_Cube_Point.z);
		
		translated_side3_vertex1.x *= CurrentScale;
		translated_side3_vertex1.y *= CurrentScale;
		translated_side3_vertex1.z *= CurrentScale;
		translated_side3_vertex2.x *= CurrentScale;
		translated_side3_vertex2.y *= CurrentScale;
		translated_side3_vertex2.z *= CurrentScale;
		translated_side3_vertex3.x *= CurrentScale;
		translated_side3_vertex3.y *= CurrentScale;
		translated_side3_vertex3.z *= CurrentScale;
		translated_side3_vertex4.x *= CurrentScale;
		translated_side3_vertex4.y *= CurrentScale;
		translated_side3_vertex4.z *= CurrentScale;
		
		side3_vertex1.x += Center_Of_Cube_Point.x;
		side3_vertex1.y += Center_Of_Cube_Point.y;
		side3_vertex1.z += Center_Of_Cube_Point.z;
		side3_vertex2.x += Center_Of_Cube_Point.x;
		side3_vertex2.y += Center_Of_Cube_Point.y;
		side3_vertex2.z += Center_Of_Cube_Point.z;
		side3_vertex3.x += Center_Of_Cube_Point.x;
		side3_vertex3.y += Center_Of_Cube_Point.y;
		side3_vertex3.z += Center_Of_Cube_Point.z;
		side3_vertex4.x += Center_Of_Cube_Point.x;
		side3_vertex4.y += Center_Of_Cube_Point.y;
		side3_vertex4.z += Center_Of_Cube_Point.z;
		
		
		//side4
		My3D translated_side4_vertex1 = new My3D(side4_vertex1.x - Center_Of_Cube_Point.x, side4_vertex1.y - Center_Of_Cube_Point.y, side4_vertex1.z - Center_Of_Cube_Point.z);
		My3D translated_side4_vertex2 = new My3D(side4_vertex2.x - Center_Of_Cube_Point.x, side4_vertex2.y - Center_Of_Cube_Point.y, side4_vertex2.z - Center_Of_Cube_Point.z);
		My3D translated_side4_vertex3 = new My3D(side4_vertex3.x - Center_Of_Cube_Point.x, side4_vertex3.y - Center_Of_Cube_Point.y, side4_vertex3.z - Center_Of_Cube_Point.z);
		My3D translated_side4_vertex4 = new My3D(side4_vertex4.x - Center_Of_Cube_Point.x, side4_vertex4.y - Center_Of_Cube_Point.y, side4_vertex4.z - Center_Of_Cube_Point.z);
		
		translated_side4_vertex1.x *= CurrentScale;
		translated_side4_vertex1.y *= CurrentScale;
		translated_side4_vertex1.z *= CurrentScale;
		translated_side4_vertex2.x *= CurrentScale;
		translated_side4_vertex2.y *= CurrentScale;
		translated_side4_vertex2.z *= CurrentScale;
		translated_side4_vertex3.x *= CurrentScale;
		translated_side4_vertex3.y *= CurrentScale;
		translated_side4_vertex3.z *= CurrentScale;
		translated_side4_vertex4.x *= CurrentScale;
		translated_side4_vertex4.y *= CurrentScale;
		translated_side4_vertex4.z *= CurrentScale;
		
		side4_vertex1.x += Center_Of_Cube_Point.x;
		side4_vertex1.y += Center_Of_Cube_Point.y;
		side4_vertex1.z += Center_Of_Cube_Point.z;
		side4_vertex2.x += Center_Of_Cube_Point.x;
		side4_vertex2.y += Center_Of_Cube_Point.y;
		side4_vertex2.z += Center_Of_Cube_Point.z;
		side4_vertex3.x += Center_Of_Cube_Point.x;
		side4_vertex3.y += Center_Of_Cube_Point.y;
		side4_vertex3.z += Center_Of_Cube_Point.z;
		side4_vertex4.x += Center_Of_Cube_Point.x;
		side4_vertex4.y += Center_Of_Cube_Point.y;
		side4_vertex4.z += Center_Of_Cube_Point.z;
		
		//side5
		
		My3D translated_side5_vertex1 = new My3D(side5_vertex1.x - Center_Of_Cube_Point.x, side5_vertex1.y - Center_Of_Cube_Point.y, side5_vertex1.z - Center_Of_Cube_Point.z);
		My3D translated_side5_vertex2 = new My3D(side5_vertex2.x - Center_Of_Cube_Point.x, side5_vertex2.y - Center_Of_Cube_Point.y, side5_vertex2.z - Center_Of_Cube_Point.z);
		My3D translated_side5_vertex3 = new My3D(side5_vertex3.x - Center_Of_Cube_Point.x, side5_vertex3.y - Center_Of_Cube_Point.y, side5_vertex3.z - Center_Of_Cube_Point.z);
		My3D translated_side5_vertex4 = new My3D(side5_vertex4.x - Center_Of_Cube_Point.x, side5_vertex4.y - Center_Of_Cube_Point.y, side5_vertex4.z - Center_Of_Cube_Point.z);
		
		translated_side5_vertex1.x *= CurrentScale;
		translated_side5_vertex1.y *= CurrentScale;
		translated_side5_vertex1.z *= CurrentScale;
		translated_side5_vertex2.x *= CurrentScale;
		translated_side5_vertex2.y *= CurrentScale;
		translated_side5_vertex2.z *= CurrentScale;
		translated_side5_vertex3.x *= CurrentScale;
		translated_side5_vertex3.y *= CurrentScale;
		translated_side5_vertex3.z *= CurrentScale;
		translated_side5_vertex4.x *= CurrentScale;
		translated_side5_vertex4.y *= CurrentScale;
		translated_side5_vertex4.z *= CurrentScale;
		
		side5_vertex1.x += Center_Of_Cube_Point.x;
		side5_vertex1.y += Center_Of_Cube_Point.y;
		side5_vertex1.z += Center_Of_Cube_Point.z;
		side5_vertex2.x += Center_Of_Cube_Point.x;
		side5_vertex2.y += Center_Of_Cube_Point.y;
		side5_vertex2.z += Center_Of_Cube_Point.z;
		side5_vertex3.x += Center_Of_Cube_Point.x;
		side5_vertex3.y += Center_Of_Cube_Point.y;
		side5_vertex3.z += Center_Of_Cube_Point.z;
		side5_vertex4.x += Center_Of_Cube_Point.x;
		side5_vertex4.y += Center_Of_Cube_Point.y;
		side5_vertex4.z += Center_Of_Cube_Point.z;
		
		//side6
		My3D translated_side6_vertex1 = new My3D(side6_vertex1.x - Center_Of_Cube_Point.x, side6_vertex1.y - Center_Of_Cube_Point.y, side6_vertex1.z - Center_Of_Cube_Point.z);
		My3D translated_side6_vertex2 = new My3D(side6_vertex2.x - Center_Of_Cube_Point.x, side6_vertex2.y - Center_Of_Cube_Point.y, side6_vertex2.z - Center_Of_Cube_Point.z);
		My3D translated_side6_vertex3 = new My3D(side6_vertex3.x - Center_Of_Cube_Point.x, side6_vertex3.y - Center_Of_Cube_Point.y, side6_vertex3.z - Center_Of_Cube_Point.z);
		My3D translated_side6_vertex4 = new My3D(side6_vertex4.x - Center_Of_Cube_Point.x, side6_vertex4.y - Center_Of_Cube_Point.y, side6_vertex4.z - Center_Of_Cube_Point.z);
		
		translated_side6_vertex1.x *= CurrentScale;
		translated_side6_vertex1.y *= CurrentScale;
		translated_side6_vertex1.z *= CurrentScale;
		translated_side6_vertex2.x *= CurrentScale;
		translated_side6_vertex2.y *= CurrentScale;
		translated_side6_vertex2.z *= CurrentScale;
		translated_side6_vertex3.x *= CurrentScale;
		translated_side6_vertex3.y *= CurrentScale;
		translated_side6_vertex3.z *= CurrentScale;
		translated_side6_vertex4.x *= CurrentScale;
		translated_side6_vertex4.y *= CurrentScale;
		translated_side6_vertex4.z *= CurrentScale;
		
		side6_vertex1.x += Center_Of_Cube_Point.x;
		side6_vertex1.y += Center_Of_Cube_Point.y;
		side6_vertex1.z += Center_Of_Cube_Point.z;
		side6_vertex2.x += Center_Of_Cube_Point.x;
		side6_vertex2.y += Center_Of_Cube_Point.y;
		side6_vertex2.z += Center_Of_Cube_Point.z;
		side6_vertex3.x += Center_Of_Cube_Point.x;
		side6_vertex3.y += Center_Of_Cube_Point.y;
		side6_vertex3.z += Center_Of_Cube_Point.z;
		side6_vertex4.x += Center_Of_Cube_Point.x;
		side6_vertex4.y += Center_Of_Cube_Point.y;
		side6_vertex4.z += Center_Of_Cube_Point.z;
	}//UniformScale
	*****/

    private void HandleKeyPress(KeyEvent e) {
		
       int keyCode = e.getKeyCode();
       if (keyCode == 65) { //a
          System.out.println("a pressed");
          Dimension d = getSize();
          System.out.println("width  = " + d.width);
          System.out.println("height = " + d.height);

          side1_normal_vector.DebugPrint("front red side1_normal_vector = ");
          side2_normal_vector.DebugPrint("top pink side2_normal_vector = ");
          side3_normal_vector.DebugPrint("bottom yellow side3_normal_vector = ");
          side4_normal_vector.DebugPrint("right blue side4_normal_vector = ");
          side5_normal_vector.DebugPrint("left green side5_normal_vector = ");
          side6_normal_vector.DebugPrint("back black side6_normal_vector = ");
       }
	   else if (keyCode == 38) { //up-arrow
	      //scaling broken, wait for refactor in matrix math
	   }
	   else if (keyCode == 40) { //down-arrow
	      //scaling broken, wait for refactor in matrix math
	   }
       else if (keyCode == 88) { //x

          double angle_in_degrees = 15;
          double angle_in_radians = Math.toRadians(angle_in_degrees);	   
		   
		  Center_Of_Cube_Point.Rotate_Around_X_Axis(angle_in_radians);

		  side1_vertex1.Rotate_Around_X_Axis(angle_in_radians);
		  side1_vertex2.Rotate_Around_X_Axis(angle_in_radians);
		  side1_vertex3.Rotate_Around_X_Axis(angle_in_radians);
		  side1_vertex4.Rotate_Around_X_Axis(angle_in_radians);

		  side2_vertex1.Rotate_Around_X_Axis(angle_in_radians);
		  side2_vertex2.Rotate_Around_X_Axis(angle_in_radians);
		  side2_vertex3.Rotate_Around_X_Axis(angle_in_radians);
		  side2_vertex4.Rotate_Around_X_Axis(angle_in_radians);

		  side3_vertex1.Rotate_Around_X_Axis(angle_in_radians);
		  side3_vertex2.Rotate_Around_X_Axis(angle_in_radians);
		  side3_vertex3.Rotate_Around_X_Axis(angle_in_radians);
		  side3_vertex4.Rotate_Around_X_Axis(angle_in_radians);
		  
		  side4_vertex1.Rotate_Around_X_Axis(angle_in_radians);
		  side4_vertex2.Rotate_Around_X_Axis(angle_in_radians);
		  side4_vertex3.Rotate_Around_X_Axis(angle_in_radians);
		  side4_vertex4.Rotate_Around_X_Axis(angle_in_radians);
		  
		  side5_vertex1.Rotate_Around_X_Axis(angle_in_radians);
		  side5_vertex2.Rotate_Around_X_Axis(angle_in_radians);
		  side5_vertex3.Rotate_Around_X_Axis(angle_in_radians);
		  side5_vertex4.Rotate_Around_X_Axis(angle_in_radians);

		  side6_vertex1.Rotate_Around_X_Axis(angle_in_radians);
		  side6_vertex2.Rotate_Around_X_Axis(angle_in_radians);
		  side6_vertex3.Rotate_Around_X_Axis(angle_in_radians);
		  side6_vertex4.Rotate_Around_X_Axis(angle_in_radians);
		  
		  side1_normal_vector = side1_vertex1.CalculateCrossProduct(side1_vertex2, side1_vertex3);
		  side2_normal_vector = side2_vertex1.CalculateCrossProduct(side2_vertex2, side2_vertex3);
		  side3_normal_vector = side3_vertex1.CalculateCrossProduct(side3_vertex2, side3_vertex3);
          side4_normal_vector = side4_vertex1.CalculateCrossProduct(side4_vertex2, side4_vertex3);
		  side5_normal_vector = side5_vertex1.CalculateCrossProduct(side5_vertex2, side5_vertex3);
		  side6_normal_vector = side6_vertex1.CalculateCrossProduct(side6_vertex2, side6_vertex3);

		  this.repaint();
       }//x
	   else if (keyCode == 89) { //y
	      double angle_in_degrees = 15;
          double angle_in_radians = Math.toRadians(angle_in_degrees);	   
		   
		  Center_Of_Cube_Point.Rotate_Around_Y_Axis(angle_in_radians);

		  side1_vertex1.Rotate_Around_Y_Axis(angle_in_radians);
		  side1_vertex2.Rotate_Around_Y_Axis(angle_in_radians);
		  side1_vertex3.Rotate_Around_Y_Axis(angle_in_radians);
		  side1_vertex4.Rotate_Around_Y_Axis(angle_in_radians);

		  side2_vertex1.Rotate_Around_Y_Axis(angle_in_radians);
		  side2_vertex2.Rotate_Around_Y_Axis(angle_in_radians);
		  side2_vertex3.Rotate_Around_Y_Axis(angle_in_radians);
		  side2_vertex4.Rotate_Around_Y_Axis(angle_in_radians);

		  side3_vertex1.Rotate_Around_Y_Axis(angle_in_radians);
		  side3_vertex2.Rotate_Around_Y_Axis(angle_in_radians);
		  side3_vertex3.Rotate_Around_Y_Axis(angle_in_radians);
		  side3_vertex4.Rotate_Around_Y_Axis(angle_in_radians);
		  
		  side4_vertex1.Rotate_Around_Y_Axis(angle_in_radians);
		  side4_vertex2.Rotate_Around_Y_Axis(angle_in_radians);
		  side4_vertex3.Rotate_Around_Y_Axis(angle_in_radians);
		  side4_vertex4.Rotate_Around_Y_Axis(angle_in_radians);
		  
		  side5_vertex1.Rotate_Around_Y_Axis(angle_in_radians);
		  side5_vertex2.Rotate_Around_Y_Axis(angle_in_radians);
		  side5_vertex3.Rotate_Around_Y_Axis(angle_in_radians);
		  side5_vertex4.Rotate_Around_Y_Axis(angle_in_radians);

		  side6_vertex1.Rotate_Around_Y_Axis(angle_in_radians);
		  side6_vertex2.Rotate_Around_Y_Axis(angle_in_radians);
		  side6_vertex3.Rotate_Around_Y_Axis(angle_in_radians);
		  side6_vertex4.Rotate_Around_Y_Axis(angle_in_radians);
		  
		  side1_normal_vector = side1_vertex1.CalculateCrossProduct(side1_vertex2, side1_vertex3);
		  side2_normal_vector = side2_vertex1.CalculateCrossProduct(side2_vertex2, side2_vertex3);
		  side3_normal_vector = side3_vertex1.CalculateCrossProduct(side3_vertex2, side3_vertex3);
          side4_normal_vector = side4_vertex1.CalculateCrossProduct(side4_vertex2, side4_vertex3);
		  side5_normal_vector = side5_vertex1.CalculateCrossProduct(side5_vertex2, side5_vertex3);
		  side6_normal_vector = side6_vertex1.CalculateCrossProduct(side6_vertex2, side6_vertex3);
		  
		  this.repaint();
       }//y
	   else if (keyCode == 90) { //z
	      double angle_in_degrees = 15;
          double angle_in_radians = Math.toRadians(angle_in_degrees);	   
		   
		  Center_Of_Cube_Point.Rotate_Around_Z_Axis(angle_in_radians);

		  side1_vertex1.Rotate_Around_Z_Axis(angle_in_radians);
		  side1_vertex2.Rotate_Around_Z_Axis(angle_in_radians);
		  side1_vertex3.Rotate_Around_Z_Axis(angle_in_radians);
		  side1_vertex4.Rotate_Around_Z_Axis(angle_in_radians);

		  side2_vertex1.Rotate_Around_Z_Axis(angle_in_radians);
		  side2_vertex2.Rotate_Around_Z_Axis(angle_in_radians);
		  side2_vertex3.Rotate_Around_Z_Axis(angle_in_radians);
		  side2_vertex4.Rotate_Around_Z_Axis(angle_in_radians);

		  side3_vertex1.Rotate_Around_Z_Axis(angle_in_radians);
		  side3_vertex2.Rotate_Around_Z_Axis(angle_in_radians);
		  side3_vertex3.Rotate_Around_Z_Axis(angle_in_radians);
		  side3_vertex4.Rotate_Around_Z_Axis(angle_in_radians);
		  
		  side4_vertex1.Rotate_Around_Z_Axis(angle_in_radians);
		  side4_vertex2.Rotate_Around_Z_Axis(angle_in_radians);
		  side4_vertex3.Rotate_Around_Z_Axis(angle_in_radians);
		  side4_vertex4.Rotate_Around_Z_Axis(angle_in_radians);
		  
		  side5_vertex1.Rotate_Around_Z_Axis(angle_in_radians);
		  side5_vertex2.Rotate_Around_Z_Axis(angle_in_radians);
		  side5_vertex3.Rotate_Around_Z_Axis(angle_in_radians);
		  side5_vertex4.Rotate_Around_Z_Axis(angle_in_radians);

		  side6_vertex1.Rotate_Around_Z_Axis(angle_in_radians);
		  side6_vertex2.Rotate_Around_Z_Axis(angle_in_radians);
		  side6_vertex3.Rotate_Around_Z_Axis(angle_in_radians);
		  side6_vertex4.Rotate_Around_Z_Axis(angle_in_radians);
		  
		  side1_normal_vector = side1_vertex1.CalculateCrossProduct(side1_vertex2, side1_vertex3);
		  side2_normal_vector = side2_vertex1.CalculateCrossProduct(side2_vertex2, side2_vertex3);
		  side3_normal_vector = side3_vertex1.CalculateCrossProduct(side3_vertex2, side3_vertex3);
          side4_normal_vector = side4_vertex1.CalculateCrossProduct(side4_vertex2, side4_vertex3);
		  side5_normal_vector = side5_vertex1.CalculateCrossProduct(side5_vertex2, side5_vertex3);
		  side6_normal_vector = side6_vertex1.CalculateCrossProduct(side6_vertex2, side6_vertex3);
		  
		  this.repaint();
       }//z
    }//HandleKeyPress
	
     
    private void displayInfo(KeyEvent e, String keyStatus) {
         
        //You should only rely on the key char if the event
        //is a key typed event.

        int id = e.getID();
        String keyString;
        if (id == KeyEvent.KEY_TYPED) {
            char c = e.getKeyChar();
            keyString = "key character = '" + c + "'";
        } else {
            int keyCode = e.getKeyCode();
            keyString = "key code = " + keyCode
                    + " ("
                    + KeyEvent.getKeyText(keyCode)
                    + ")";
        }
         
        int modifiersEx = e.getModifiersEx();
        String modString = "extended modifiers = " + modifiersEx;
        String tmpString = KeyEvent.getModifiersExText(modifiersEx);
        if (tmpString.length() > 0) {
            modString += " (" + tmpString + ")";
        } else {
            modString += " (no extended modifiers)";
        }
         
        String actionString = "action key? ";
        if (e.isActionKey()) {
            actionString += "YES";
        } else {
            actionString += "NO";
        }
         
        String locationString = "key location: ";
        int location = e.getKeyLocation();
        if (location == KeyEvent.KEY_LOCATION_STANDARD) {
            locationString += "standard";
        } else if (location == KeyEvent.KEY_LOCATION_LEFT) {
            locationString += "left";
        } else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
            locationString += "right";
        } else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
            locationString += "numpad";
        } else { // (location == KeyEvent.KEY_LOCATION_UNKNOWN)
            locationString += "unknown";
        }
         
        System.out.println(keyStatus);
        System.out.println(keyString);
        System.out.println(modString);
        System.out.println(actionString);
        System.out.println(locationString);
        System.out.println();
        System.out.println();
    }
}


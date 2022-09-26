
import java.util.ArrayList;
import java.util.Collections;


public class SortByZ {
	
	private int[] draw_side  = { 0, 0, 0, 0, 0, 0 };
	private double[] z_array = { 0, 0, 0, 0, 0, 0 };
    private int count;	

    public SortByZ() {
		count = 0;
		for (int ii=0; ii < 6; ii++) {
			draw_side[ii] = 0;
			z_array[ii] = 0;
		}
	}//
	
	public void add(int side, double z_coord)
	{
		if (count >= 6) {
			System.out.println("Class SortByZ method add: Can not add anymore i am full at 6!");
			return;
		}
		
		//if z_coord <= 0 do not add it
		if (z_coord > 0) {
			//add it to arrays
			if (count == 0) {
				draw_side[0] = side;
				z_array[0] = z_coord;
				count = 1;
			}
			else {  //Big-O(N) sort now
			    for (int ii=0; ii < count; ii++) {
					if (z_coord < z_array[ii]) {
						squeeze_in(ii, side, z_coord);
						return;
					}
				}
				//if you made it here, then just add to end of arrays
				draw_side[count] = side;
				z_array[count] = z_coord;
				count++;
			}
		}
		 
	}//add
	
	private void squeeze_in(int index, int side, double z_coord) {
		for (int ii=count; ii > index; ii--) {
			//shift them
			draw_side[ii] = draw_side[ii-1];
			z_array[ii]   = z_array[ii-1];
		}
		//squeeze it in
		draw_side[index] = side;
		z_array[index] = z_coord;
		count++;
	}//squeeze_in
	
	public void DebugPrint() {
		System.out.println("count = " + count);
		for (int ii=0; ii < count; ii++) {
			System.out.println("draw_side[" + ii + "] = " + draw_side[ii]);
			System.out.println("z_array["   + ii + "] = " + z_array[ii]);
		}
	}//DebugPrint
	
	public int GetCount() {
		return count;
	}
	
	public int[] GetDrawSides() {
		return draw_side;
	}

}//class
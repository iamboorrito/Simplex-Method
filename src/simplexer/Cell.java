package simplexer;

public class Cell {

	public final int row, col;
	public final Double val;
	
	public Cell(int row, int col, Double val){
		this.row = row;
		this.col = col;
		this.val = val;
	}
	
	public String toString(){
		return String.format("(%d, %d, %.1f)", row, col, val);
	}
	
}

package simplexer;
public class Pivot {
		
		public int row, col;
		
		public Pivot(int row, int col){
			this.row = row;
			this.col = col;
		}
		
		@Override
		public String toString(){
			return "("+row+", "+col+")";
		}
	}
package simplexer;
public class Pivot {
		
		public int row, col;
		
		public Pivot(int row, int col){
			this.row = row;
			this.col = col;
		}
		
		/**
		 * Prints 1-based coordinates
		 */
		@Override
		public String toString(){
			return String.format("(%d, %d)", row+1, col+1);
		}
	}
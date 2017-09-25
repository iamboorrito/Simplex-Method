package simplexer;

import java.util.HashSet;

public class RowColList {

	HashSet<Integer> rows, cols;
	
	
	public RowColList(){
		rows = new HashSet<>();
		cols = new HashSet<>();
	}
	
	public void add(int row, int col){
		rows.add(row);
		cols.add(col);
	}
	
	public boolean containsRow(int row){
		return rows.contains(row);
	}
	
	public boolean containsCol(int col){
		return cols.contains(col);
	}

	public boolean contains(int row, int col) {
		return rows.contains(row) && cols.contains(col);
	}
	
}

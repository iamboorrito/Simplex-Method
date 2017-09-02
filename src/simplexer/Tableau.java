package simplexer;
/**
 * @author Evan Burton
 */

import java.util.LinkedList;

public class Tableau extends LinkedList<LinkedList<Double>> {
	
	public static final long MAX_ITERATIONS = 2000;
	private static final long serialVersionUID = 1902458771229809998L;
	private int rows;
	private int cols;
	
	public enum OUTPUT{SUCCESS, FAILURE};
	
	/**
	 * Default constructor calls parent and creates a Stack<Tableau>.
	 */
	public Tableau(){
		super();
	}
	
	/**
	 * Create a Simplex Tableau with given rows and columns.
	 * @param rows
	 * @param cols
	 */
	public Tableau(int rows, int cols){
		this.rows = 0;
		this.cols = cols;
		
		for(int i = 0; i < rows; i++){
			addRow();
		}
	}
	
	/**
	 * Carries out the simplex algorithm either until it is completed 
	 * or until MAX_ITERATIONS (5000 by default) have passed. 
	 */
	public OUTPUT runSimplexMethod(){
		
		long i = 0;
		
		while(!simplexExit() && i < MAX_ITERATIONS){
			simplexIteration();
			i++;
		}
		
		if(i == MAX_ITERATIONS)
			return Tableau.OUTPUT.FAILURE;
		else
			return Tableau.OUTPUT.SUCCESS;
	}
	
	/**
	 * Performs one step of the Simplex Method. This creates a copy of
	 * the current Tableau in case the undo button is activated.
	 */
	public void simplexIteration(){
		
//////////////////////// Select pivot ////////////////////////
			Pivot pivot = selectPivot();
//////////////////////// Elimination ////////////////////////			
			rowDiv(pivot.row, get(pivot.row, pivot.col));
			
			for(int row = 0; row < rows; row++){
				if(row != pivot.row){
					rowAdd(pivot.row, row, -1*get(row, pivot.col));
				}
			}
////////////////////// End Elimination //////////////////////
			
	}
	
	public boolean simplexExit() {
		for(int i = 0; i < cols; i++)
			if(this.get(rows-1, i) < 0)
				return false;
		return true;
	}

	public Pivot selectPivot(){
		
		int consCol = cols-1;
		int objRow = rows-1;
		
		double min = 1;
		double min2 = Integer.MAX_VALUE;
		int pivCol = 0;
		int pivRow = 0;
		
//////////////////////// Select pivot col ////////////////////////
		for(int j = 0; j < cols; j++){
			if(this.get(objRow, j) < min){
				min = this.get(objRow, j);
				pivCol = j;
			}
		}
//////////////////////// Select pivot row ////////////////////////
		min = this.get(0, consCol)/this.get(0, pivCol);
		// Guard against bad pivot values
		if(min < 0)
			min = Integer.MAX_VALUE;
		
		double colValue = 1;
		
		for(int j = 1; j < rows-1; j++){
			
			colValue = this.get(j, pivCol);
			double rowValue = this.get(j, consCol);
			if(colValue < 0 && rowValue > 0 || colValue == 0)
				continue;
			min2 = rowValue/colValue;
			
			if(min2 < min && min2 > 0){
				min = min2;
				pivRow = j;
			}
		}

		return new Pivot(pivRow, pivCol);
	}
	
	/**
	 * Creates an independent copy of this Tableau.
	 * @return Tableau copy
	 */
	public Tableau copy(){
		Tableau cpy = new Tableau(rows, cols);
		
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				cpy.set(i, j, get(i, j));
		
		return cpy;
		
	}
	
	public int getRows(){
		return rows;
	}
	
	public int getCols(){
		return cols;
	}
	
	/**
	 * Sets a value at given row, column pair.
	 * @param rows
	 * @param cols
	 * @param d
	 */
	public void set(int rows, int cols, double d) {
		this.get(rows).set(cols, d);
	}
	
	/**
	 * Gets the value at (row, column) in the table.
	 * @param row
	 * @param col
	 * @return
	 */
	public double get(int row, int col){
		return this.get(row).get(col).doubleValue();
	}
	
	/**
	 * Reshapes the table while retaining data if possible.
	 * @param r new row size
	 * @param c new column size
	 */
	public void reshape(int r, int c){
		
		while(rows > r){
			deleteRow(0);
		}
		
		while(rows < r){
			addRow();
		}
		
		while(cols > c){
			deleteCol(0);
		}
		
		while(cols < c){
			addCol();
		}
	}
	
	/**
	 * Inserts a row into the table at a given place.
	 * @param place
	 */
	public void insertRow(int place){
		LinkedList<Double> row = new LinkedList<Double>();
		
		for(int i = 0; i < cols; i++){
			row.add(new Double(0));
		}
		
		this.add(place, row);
		rows++;
	}
	
	/**
	 * Inserts a column into the table at a given place.
	 * @param place
	 */
	public void insertCol(int place){
		for(LinkedList<Double> row : this)
			row.add(place, new Double(0));
		cols++;
	}
	
	/**
	 * Inserts a row at the end of the table.
	 * @param place
	 */
	public void addRow(){
		
		LinkedList<Double> row = new LinkedList<Double>();
		
		for(int i = 0; i < cols; i++){
			row.add(new Double(0));
		}
		
		this.add(row);
		rows++;
	}
	
	/**
	 * Inserts a column at the end of the table.
	 * @param place
	 */
	public void addCol(){
		for(LinkedList<Double> row : this)
			row.add(new Double(0));
		cols++;
	}
	
	public void deleteRow(int row){
		this.remove(row);
		rows--;
	}
	
	public void deleteCol(int col){
		for(LinkedList<Double> row : this){
			row.remove(col);
		}
		cols--;
	}
	
	/**
	 * Sets all values in the table to zero.
	 */
	public void reset(){
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				this.set(i, j, 0);
	}

	private void rowAdd(int row1, int row2, double scalar){
		for(int col = 0; col < cols; col++){
			
			double value = this.get(row2, col) + scalar*this.get(row1, col);
			this.set(row2, col, value);
		}
	}
	
	private void rowDiv(int row, double scalar){
		for(int col = 0; col < cols; col++){
			double value = this.get(row, col)/scalar;
			this.set(row, col, value);
		}
	}
	
	/**
	 * Returns true iff the two Tableau have identical entries.
	 */
	@Override
	public boolean equals(Object t){
		Tableau t2 = (Tableau) t;
		
		if(t2.getRows() != this.rows || t2.getCols() != this.cols)
			return false;
		
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				if(t2.get(i, j) != this.get(i, j))
					return false;
					
		return true;
	}
	
	@Override
	public String toString(){
		
		StringBuilder res = new StringBuilder(rows*cols);
		
		int k = 0;
		int i = 1;
		
		res.append(String.format("%7s%d", "X", ++k));
		
		for(; k < cols-rows-1; k++)
			res.append(String.format("%9s%d", "X", k+1));
		
		while(k < cols-1){
			res.append(String.format("%9s%d", "S", i));
			k++;
			i++;
		}
		
		res.append(String.format("%16s\n", "Constraints"));
		
		for(i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				res.append(String.format("%10.3f", this.get(i, j)));
			}
			//res.append(this.get(i));
			res.append('\n');
		}
		
		return res.toString();
		
	}
	
}

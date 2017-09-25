/*
MIT License

Copyright (c) 2017 Evan Burton

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package simplexer;
/**
 * @author Evan Burton
 */

import java.util.LinkedList;

public class Tableau extends LinkedList<LinkedList<Double>> {
	
	public static final int MAX_ITERATIONS = 200;
	private static final long serialVersionUID = 1902458771229809998L;
	private int rows;
	private int cols;
	private boolean fail;
	
	public enum OUTPUT{SUCCESS, FAILURE, NO_SOLUTION};
	
	/**
	 * Default constructor calls parent and creates a Stack<Tableau>.
	 */
	public Tableau(){
		super();
		fail = false;
	}
	
	/**
	 * Create a Simplex Tableau with given rows and columns.
	 * @param rows
	 * @param cols
	 */
	public Tableau(int rows, int cols){
		this.rows = 0;
		this.cols = cols;
		fail = false;
		
		for(int i = 0; i < rows; i++){
			addRow();
		}
	}
	
	public Tableau(double[][] tab) {
		this(tab.length, tab[0].length);
		
		for(int i = 0; i < tab[0].length; i++){
			for(int j = 0; j < tab.length; j++){
				this.set(j, i, tab[j][i]);
			}
		}
		
	}

	/**
	 * Carries out the simplex algorithm either until it is completed 
	 * or until MAX_ITERATIONS (5000 by default) have passed. 
	 */
	public OUTPUT runSimplexMethod(){
		
		RowColList artificialVars = phaseOne();
		
		if(artificialVars != null){
			while(!simplexExit()){
				simplexIteration(1);
			}
			
			// Check if solution exists
			if(this.get(rows-1, cols-1) != 0){
				System.out.println("No feasible solution exists");
				return Tableau.OUTPUT.NO_SOLUTION;
			}else{
				
				// Remove artificial variables and auxillary row
				for(Integer col : artificialVars.cols){
					this.deleteCol(cols-2);
				}
				
				this.deleteRow(rows-1);
			}
		}
		
		long i = 0;
		
		// PHASE 2
		while(!simplexExit() && i < MAX_ITERATIONS && !fail){
			simplexIteration();
			i++;
		}
		
		if(i == MAX_ITERATIONS)
			return Tableau.OUTPUT.FAILURE;
		else
			return Tableau.OUTPUT.SUCCESS;
	}

	public RowColList phaseOne() {
		
		RowColList basicVars = countBasicVariables();
		RowColList artificialVars = null;
		
		// PHASE 1
		if(basicVars.rows.size() < rows-1){
////////////////////////// Add artificial variables /////////////////////
			
			artificialVars = new RowColList();
			
			// Get rows without basic variables
			for(int i = 0; i < rows-1; i++){
				
				if(!basicVars.containsRow(i)){
					artificialVars.add(i, cols-1);
					this.insertCol(cols-1);
					this.set(i, cols-2, 1);
				}
			}
///////////////////////// Add auxillary row /////////////////////////////
			
			this.addRow();
			
			double sum = 0;
			
			for(int j = 0; j < cols; j++){
				
				if(!basicVars.containsCol(j) && !artificialVars.containsCol(j)){
				
					for(int i = 0; i < rows-2; i++){
						sum += this.get(i, j);
					}
					
					this.set(rows-1, j, -sum);
					sum = 0;
				}
			}
			
		}
		
		return artificialVars;
		
	}

	/**
	 * Performs one step of the Simplex Method. This creates a copy of
	 * the current Tableau in case the undo button is activated.
	 */
	public void simplexIteration(int offset){
		
//////////////////////// Select pivot ////////////////////////
			Pivot pivot = selectPivot(offset);
//////////////////////// Elimination ////////////////////////
			
			if(pivot.col >= 0 && pivot.row >= 0){
				rowDiv(pivot.row, get(pivot.row, pivot.col));
			}else{
				System.out.println("Problem is unbounded");
				fail = true;
				return;
			}
			
			for(int row = 0; row < rows; row++){
				if(row != pivot.row){
					rowAdd(pivot.row, row, -get(row, pivot.col));
				}
			}
////////////////////// End Elimination //////////////////////
			
	}
	
	public void simplexIteration(){
		simplexIteration(0);
	}

	
	public boolean simplexExit() {
		for(int i = 0; i < cols-1; i++)
			if(this.get(rows-1, i) < 0)
				return false;
		return true;
	}

	public Pivot selectPivot(){
		return selectPivot(0);
	}
	
	public Pivot selectPivot(int offset){
		
		int consCol = cols-1;
		int objRow = rows-1;
		
		double min = 1;
		double min2 = Integer.MAX_VALUE;
		int pivCol = -1;
		int pivRow = -1;
		
//////////////////////// Select pivot col ////////////////////////
		for(int j = 0; j < cols-1; j++){
			if(this.get(objRow, j) < min){
				min = this.get(objRow, j);
				pivCol = j;
			}
		}
//////////////////////// Select pivot row ////////////////////////
		min = Double.MAX_VALUE;
		
		double colValue = 1;
		
		for(int j = 0; j < rows-1-offset; j++){
			
			colValue = this.get(j, pivCol);
			double rowValue = this.get(j, consCol);
			
			if(colValue < 0 && rowValue > 0 || colValue == 0)
				continue;
			
			min2 = rowValue/colValue;
			
			if(min2 < min && colValue > 0){
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
		++cols;
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
	
	public void rowDiv(int row, double scalar){
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
	
	/**
	 * Creates the dual tableau from LP problem. This method expects no
	 * slack variables to be present.
	 * @return
	 */
	public Tableau getDual(){
		
		Tableau dual = new Tableau(cols, rows+cols);
		
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				if(i == rows-1)
					dual.set(j, rows+cols-1, this.get(i, j));
				else
					dual.set(j, i, this.get(i, j));
			}
		}
		
		dual.rowDiv(cols-1, -1);
		
		int c;
		
		for(int i = 0; i < cols; i++){
			c = dual.getCols()-cols-1+i;
			dual.set(i, c, 1);
		}
		
		return dual;
	}
	
	/**
	 * Checks if a column has a basic variable (All zeroes except for a 1).
	 * @param col
	 * @return
	 */
	public boolean isBasicVariable(int col){
		
		int ones = 0;
		double val;
		
		for(int i = 0; i < rows-1; i++){
			
			val = this.get(i, col);
			
			if(val == 1){
				ones++;
			} else if(val != 0) {
				return false;
			}
			
			if(ones > 1)
				return false;
		}
		
		return true;
	}
	
	/**
	 * Searches for the first basic variable in a row.
	 * @param row The row to search.
	 * @return Returns the column containing the basic variable or -1 if none found.
	 */
	public int getBasicVariable(int row){
		for(int j = 0; j < cols-1; j++){
			if(this.get(row, j) == 1 && isBasicVariable(j)){
				return j;
			}
		}
		
		return -1;
	}
	
	/**
	 * Collects the basic variable positions.
	 * @return A RowColList with the row/column information of the basic variables.
	 */
	private RowColList countBasicVariables() {
		
		RowColList list = new RowColList();
		
		int column = -1;
		
		for(int row = 0; row < rows-1; row++){
			
			column = getBasicVariable(row);
			
			// Found bv
			if(column != -1){
				list.add(row, column);
			}
		}
		
		return list;
	}
	
	public static void main(String[]args){
		
		double[][] tab = {{ 2, 1,  1,  12},
					  	  { 3, 1,  2,  18},
					  	  {-5, -3, -3, 0 }};
		
		Tableau t = new Tableau(tab);
		t.runSimplexMethod();
	}
}

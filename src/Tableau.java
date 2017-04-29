import java.util.LinkedList;
import java.util.Stack;

public class Tableau extends LinkedList<LinkedList<Double>> {
	
	private static final long serialVersionUID = 1902458771229809998L;
	public static final long MAX_ITERATIONS = 5000;
	private int rows;
	private int cols;
	private Stack<Tableau> previousTableau;
	
	public Tableau(){
		super();
		previousTableau = new Stack<Tableau>();
	}
	
	public Tableau(int rows, int cols){
		previousTableau = new Stack<Tableau>();
		this.rows = 0;
		this.cols = cols;
		
		for(int i = 0; i < rows; i++){
			addRow();
		}
	}
	
	public Tableau(double[][] tab){
		previousTableau = new Stack<Tableau>();
		rows = 0;
		cols = tab[0].length;
		
		for(int i = 0; i < tab.length; i++){
			addRow();
		}
		
		for(int i = 0; i < tab.length; i++)
			for(int j = 0; j < cols; j++)
				this.set(i, j, tab[i][j]);
	}
	
	public Tableau copy(){
		Tableau cpy = new Tableau(rows, cols);
		
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				cpy.set(i, j, get(i, j));
		
		return cpy;
		
	}
	
	public Stack<Tableau> getStack(){
		return previousTableau;
	}
	
	public Tableau getLastTableau(){
		return previousTableau.pop();
	}
	
	public int getRows(){
		return rows;
	}
	
	public int getCols(){
		return cols;
	}
	
	public void set(int rows, int cols, double d) {
		this.get(rows).set(cols, d);
	}
	
	public double get(int row, int col){
		return this.get(row).get(col).doubleValue();
	}
	
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
	
	public void insertRow(int place){
		LinkedList<Double> row = new LinkedList<Double>();
		
		for(int i = 0; i < cols; i++){
			row.add(new Double(0));
		}
		
		this.add(place, row);
		rows++;
	}
	
	public void insertCol(int place){
		for(LinkedList<Double> row : this)
			row.add(place, new Double(0));
		cols++;
	}
	
	public void addRow(){
		
		LinkedList<Double> row = new LinkedList<Double>();
		
		for(int i = 0; i < cols; i++){
			row.add(new Double(0));
		}
		
		this.add(row);
		rows++;
	}
	
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
	
	public void reset(){
		for(int i = 0; i < rows; i++)
			for(int j = 0; j < cols; j++)
				this.set(i, j, 0);
	}

	public void runSimplexMethod(){
		
		long i = 0;
		
		while(!simplexExit() && i < MAX_ITERATIONS){
			simplexIteration();
			i++;
		}
		//System.out.println(previousTableau.size());
	}
	
	public void simplexIteration(){
		
		Tableau cpy = copy();
		
//////////////////////// Select pivot ////////////////////////
			Pivot pivot = selectPivot();
//////////////////////// Elimination ////////////////////////			
			rowDiv(pivot.row, get(pivot.row, pivot.col));
			
			for(int row = 0; row < rows; row++){
				if(row != pivot.row){
					rowAdd(pivot.row, row, -1*get(row, pivot.col));
				}
			}
			//System.out.println("With pivot: " +pivot);
			//System.out.println(this);
////////////////////// End Elimination //////////////////////
			
			if(!cpy.equals(this))
				previousTableau.push(cpy);
	}
	
	private boolean simplexExit() {
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
		if(min <= 0)
			min = Integer.MAX_VALUE;
		
		double colValue = 1;
		
		for(int j = 1; j < rows-1; j++){
			
			colValue = this.get(j, pivCol);
			if(colValue <= 0)
				continue;
			min2 = this.get(j, consCol)/colValue;
			
			if(min2 < min && min2 >= 0){
				min = min2;
				pivRow = j;
			}
		}

		return new Pivot(pivRow, pivCol);
	}
	
	/**
	 * Adds scalar*row1 to row2.
	 * @param row1
	 * @param row2
	 * @param scalar
	 */
	public void rowAdd(int row1, int row2, double scalar){
		for(int col = 0; col < cols; col++){
			
			double value = this.get(row2, col) + scalar*this.get(row1, col);
			this.set(row2, col, value);
		}
	}
	
	public void rowScale(int row, double scalar){
		for(int col = 0; col < cols; col++){
			double value = this.get(row, col)*scalar;
			this.set(row, col, value);
		}
	}
	
	public void rowDiv(int row, double scalar){
		for(int col = 0; col < cols; col++){
			double value = this.get(row, col)/scalar;
			this.set(row, col, value);
		}
	}
	
	public String toString(){
		StringBuilder str = new StringBuilder();
		
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				str.append(String.format("%10s", 
						stripTrailingZeros(this.get(i, j))
						));
			}
			str.append('\n');
		}
		return str.toString();
	}
	
	public String toLatex(){
		StringBuilder str = new StringBuilder();
		str.append("\\begin{bmatrix}\n");
		
		for(int i = 0; i < cols; i++){
			str.append(String.format("Label%d&", i));
		}
		
		str.append("\\\\ \n");
		
		for(int i = 0; i < rows; i++){
			for(int j = 0; j < cols; j++){
				
				str.append(String.format("%s", 
						stripTrailingZeros(this.get(i, j))
						));
				str.append('&');
			}
			str.append("\\\\ \n");
		}
		str.append("\\end{bmatrix}");
		return str.toString();
	}
	
	public String stripTrailingZeros(double x){
	
		StringBuilder str = new StringBuilder();
		str.append(String.format("%.3f", x));
		
		int i = str.length()-1;
		
		while(str.charAt(i) == '0' || str.charAt(i) == '.'){
			
			if(str.charAt(i) == '.'){
				str.deleteCharAt(i);
				break;
			}else{
				str.deleteCharAt(i);
			}
			i--;
		}
		
		return str.toString();
		
	}
	
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
	
	private class Pivot {
		
		public int row, col;
		
		public Pivot(int row, int col){
			this.row = row;
			this.col = col;
		}
		
		public String toString(){
			return "("+row+", "+col+")";
		}
	}
	
	public boolean isPreviousEmpty() {
		return previousTableau.isEmpty();
	}

	public void setStack(Stack<Tableau> temp) {
		previousTableau = temp;
		
	}
}

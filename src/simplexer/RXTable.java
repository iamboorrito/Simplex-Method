package simplexer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.table.*;
import javax.swing.text.*;

import org.mariuszgromada.math.mxparser.Expression;

import simplexer.Tableau.OUTPUT;

/** 
 * Original from:
 * 
 * https://github.com/griffon/griffon-javatips-plugin/blob/master/src/main/com/wordpress/tipsforjava/swing/table/RXTable.java
 * 
 * The RXTable provides some extensions to the default JTable
 *
 * 1) Select All editing - when a text related cell is placed in editing mode
 *    the text is selected. Controlled by invoking a "setSelectAll..." method.
 *
 * 2) reorderColumns - static convenience method for reodering table columns
 * 
 *  @author Rob Camick
 *  @author Darryl Burke
 * 
 */
public class RXTable extends JTable
{
	/**
	 * Default serial ID
	 */
	private static final long serialVersionUID = 1L;
	private boolean isSelectAllForMouseEvent = false;
	private boolean isSelectAllForActionEvent = false;
	private boolean isSelectAllForKeyEvent = false;
	private int tableauRows, tableauColumns;
	public int MAX_ITERATIONS = 200;
	private UndoStack undo;

//
// Constructors
//
    /**
     * Constructs a default <code>RXTable</code> that is initialized with a default
     * data model, a default column model, and a default selection
     * model.
     */
    public RXTable()
    {
        this(null, null, null);
    }

    /**
     * Constructs a <code>RXTable</code> that is initialized with
     * <code>dm</code> as the data model, a default column model,
     * and a default selection model.
     *
     * @param dm        the data model for the table
     */
    public RXTable(TableModel dm)
    {
        this(dm, null, null);
    }
    
    /**
     * Constructs a <code>RXTable</code> that is initialized with
     * <code>dm</code> as the data model and tableau size of 
     * tabRows and tabCols;
     *
     * @param dm        the data model for the table
     */
    public RXTable(TableModel dm, int tabRows, int tabCols, UndoStack undo)
    {
        this(dm, null, null);
        setTableauRows(tabRows);
        setTableauColumns(tabCols);
        this.undo = undo;
    }

    /**
     * Constructs a <code>RXTable</code> that is initialized with
     * <code>dm</code> as the data model, <code>cm</code>
     * as the column model, and a default selection model.
     *
     * @param dm        the data model for the table
     * @param cm        the column model for the table
     */
    public RXTable(TableModel dm, TableColumnModel cm)
    {
        this(dm, cm, null);
    }

    /**
     * Constructs a <code>RXTable</code> that is initialized with
     * <code>dm</code> as the data model, <code>cm</code> as the
     * column model, and <code>sm</code> as the selection model.
     * If any of the parameters are <code>null</code> this method
     * will initialize the table with the corresponding default model.
     * The <code>autoCreateColumnsFromModel</code> flag is set to false
     * if <code>cm</code> is non-null, otherwise it is set to true
     * and the column model is populated with suitable
     * <code>TableColumns</code> for the columns in <code>dm</code>.
     *
     * @param dm        the data model for the table
     * @param cm        the column model for the table
     * @param sm        the row selection model for the table
     */
    public RXTable(TableModel dm, TableColumnModel cm, ListSelectionModel sm)
    {
        super(dm, cm, sm);
    }

    /**
     * Constructs a <code>RXTable</code> with <code>numRows</code>
     * and <code>numColumns</code> of empty cells using
     * <code>DefaultTableModel</code>.  The columns will have
     * names of the form "A", "B", "C", etc.
     *
     * @param numRows           the number of rows the table holds
     * @param numColumns        the number of columns the table holds
     */
    public RXTable(int numRows, int numColumns)
    {
        this(new DefaultTableModel(numRows, numColumns));
    }

    /**
     * Constructs a <code>RXTable</code> to display the values in the
     * <code>Vector</code> of <code>Vectors</code>, <code>rowData</code>,
     * with column names, <code>columnNames</code>.  The
     * <code>Vectors</code> contained in <code>rowData</code>
     * should contain the values for that row. In other words,
     * the value of the cell at row 1, column 5 can be obtained
     * with the following code:
     * <p>
     * <pre>((Vector)rowData.elementAt(1)).elementAt(5);</pre>
     * <p>
     * @param rowData           the data for the new table
     * @param columnNames       names of each column
     */
    public RXTable(Vector<?> rowData, Vector<?> columnNames)
    {
        this(new DefaultTableModel(rowData, columnNames));
    }

    /**
     * Constructs a <code>RXTable</code> to display the values in the two dimensional array,
     * <code>rowData</code>, with column names, <code>columnNames</code>.
     * <code>rowData</code> is an array of rows, so the value of the cell at row 1,
     * column 5 can be obtained with the following code:
     * <p>
     * <pre> rowData[1][5]; </pre>
     * <p>
     * All rows must be of the same length as <code>columnNames</code>.
     * <p>
     * @param rowData           the data for the new table
     * @param columnNames       names of each column
     */
    public RXTable(final Object[][] rowData, final Object[] columnNames)
    {
        super(rowData, columnNames);
    }
//
//  Overridden methods
//
	/*
	 *  Override to provide Select All editing functionality
	 */
	@Override
	public boolean editCellAt(int row, int column, EventObject e)
	{
		boolean result = super.editCellAt(row, column, e);

		if (isSelectAllForMouseEvent
		||  isSelectAllForActionEvent
		||  isSelectAllForKeyEvent)
		{
			selectAll(e);
		}

		return result;
	}

	/*
	 * Select the text when editing on a text related cell is started
	 */
	private void selectAll(EventObject e)
	{
		final Component editor = getEditorComponent();

		if (editor == null
		|| ! (editor instanceof JTextComponent))
			return;

		if (e == null)
		{
			((JTextComponent)editor).selectAll();
			return;
		}

		//  Typing in the cell was used to activate the editor

		if (e instanceof KeyEvent && isSelectAllForKeyEvent)
		{
			((JTextComponent)editor).selectAll();
			return;
		}

		//  F2 was used to activate the editor

		if (e instanceof ActionEvent && isSelectAllForActionEvent)
		{
			((JTextComponent)editor).selectAll();
			return;
		}

		//  A mouse click was used to activate the editor.
		//  Generally this is a double click and the second mouse click is
		//  passed to the editor which would remove the text selection unless
		//  we use the invokeLater()

		if (e instanceof MouseEvent && isSelectAllForMouseEvent)
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					((JTextComponent)editor).selectAll();
				}
			});
		}
	}

//
//  Newly added methods
//
	/*
	 *  Sets the Select All property for for all event types
	 */
	public void setSelectAllForEdit(boolean isSelectAllForEdit)
	{
		setSelectAllForMouseEvent( isSelectAllForEdit );
		setSelectAllForActionEvent( isSelectAllForEdit );
		setSelectAllForKeyEvent( isSelectAllForEdit );
	}

	/*
	 *  Set the Select All property when editing is invoked by the mouse
	 */
	public void setSelectAllForMouseEvent(boolean isSelectAllForMouseEvent)
	{
		this.isSelectAllForMouseEvent = isSelectAllForMouseEvent;
	}

	/*
	 *  Set the Select All property when editing is invoked by the "F2" key
	 */
	public void setSelectAllForActionEvent(boolean isSelectAllForActionEvent)
	{
		this.isSelectAllForActionEvent = isSelectAllForActionEvent;
	}

	/*
	 *  Set the Select All property when editing is invoked by
	 *  typing directly into the cell
	 */
	public void setSelectAllForKeyEvent(boolean isSelectAllForKeyEvent)
	{
		this.isSelectAllForKeyEvent = isSelectAllForKeyEvent;
	}
	
//
//  Static, convenience methods
//
	/**
	 *  Convenience method to order the table columns of a table. The columns
	 *  are ordered based on the column names specified in the array. If the
	 *  column name is not found then no column is moved. This means you can
	 *  specify a null value to preserve the current order of a given column.
	 *
     *  @param table        the table containing the columns to be sorted
     *  @param columnNames  an array containing the column names in the
     *                      order they should be displayed
	 */
	public static void reorderColumns(JTable table, Object... columnNames)
	{
		TableColumnModel model = table.getColumnModel();

		for (int newIndex = 0; newIndex < columnNames.length; newIndex++)
		{
			try
			{
				Object columnName = columnNames[newIndex];
				int index = model.getColumnIndex(columnName);
				model.moveColumn(index, newIndex);
			}
			catch(IllegalArgumentException e) {}
		}
	}

	public int getTableauColumns() {
		return tableauColumns;
	}

	public void setTableauColumns(int tableauColumns) {
		this.tableauColumns = tableauColumns;
	}

	public int getTableauRows() {
		return tableauRows;
	}

	public void setTableauRows(int tableauRows) {
		this.tableauRows = tableauRows;
	}
	
	/**
	 * Attempts to retrieve entry as a double. Returns 0 if failure and sets the
	 * outputField accordingly to show error.
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public double getDouble(Object entry) {

		// Tests to see if you can read a double from the cell
		// If that fails try evaluating a JeksExpression as Double
		// Otherwise gives up trying to read input and alerts user.

		if (entry == null || entry.toString().trim().equals("")) {
			return 0;
		}

		try {
			double val = (new Expression(entry.toString())).calculate();
			return val;

		} catch (Exception ex) {
			//outputField.setText("Parse error: possible missing '=' at text field or invalid input");
		}

		return 0;
	}

	/**
	 * Attempts to retrieve entry i,j as a double. Returns 0 if failure and sets
	 * the outputField accordingly to show error.
	 * 
	 * @param row
	 * @param col
	 * @return
	 */
	public double getDouble(int row, int col) {

		// Tests to see if you can read a double from the cell

		if (row < 0 || col < 0)
			return 0;

		Object entry = this.getValueAt(row, col);

		if (entry == null || entry.toString().trim().equals("")) {
			return 0;
		}

		Double val = (new Expression(entry.toString())).calculate();

		if (val == Double.NaN) {
			this.setValueAt("", row, col);
			val = 0.0;
		}

		return val;

	}
	
	/**
	 * Sets the cell at (row, col) with val if val != 0. Otherwise it sets it
	 * to the empty string.
	 * @param val
	 * @param row
	 * @param col
	 */
	public void setDouble(double val, int row, int col){
		if(row < 0 || col < 0 || row > getRowCount() || col > getColumnCount())
			return;
		
		if(val == 0){
			this.setValueAt("", row, col);
		}else{
			this.setValueAt(val, row, col);
		}
		
	}
	
	/**
	 * Divides a row by a scalar.
	 * @param row
	 * @param divisor
	 */
	public void rowDiv(int row, double divisor){
		for(int i = 0; i < tableauColumns; i++)
			this.setDouble(getDouble(row, i)/divisor, row, i);
	}
	
	/**
	 * Adds mul*row1 to row2.
	 * @param row1 Src row
	 * @param row2 Dst row
	 * @param mul
	 */
	public void rowAdd(int row1, int row2, double mul){
		for(int i = 0; i < tableauColumns; i++)
			this.setDouble(getDouble(row1, i)*mul + getDouble(row2, i), row2, i);
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
			rowDiv(pivot.row, getDouble(pivot.row, pivot.col));
			
			for(int row = 0; row < tableauRows; row++){
				if(row != pivot.row){
					rowAdd(pivot.row, row, -getDouble(row, pivot.col));
				}
			}
////////////////////// End Elimination //////////////////////
			
	}
	
	public boolean simplexExit() {
		for(int i = 0; i < tableauColumns-1; i++)
			if(getDouble(tableauRows-1, i) < 0)
				return false;
		return true;
	}

	public Pivot selectPivot(){
		
		int consCol = tableauColumns-1;
		int objRow = tableauRows-1;
		
		double min = 1;
		double min2 = Integer.MAX_VALUE;
		int pivCol = 0;
		int pivRow = 0;
		
//////////////////////// Select pivot col ////////////////////////
		for(int j = 0; j < tableauColumns-1; j++){
			if(getDouble(objRow, j) < min){
				min = getDouble(objRow, j);
				pivCol = j;
			}
		}
//////////////////////// Select pivot row ////////////////////////
			
		min = getDouble(0, consCol)/getDouble(0, pivCol);
		
		// Guard against bad pivot values	
		if(min < 0)
			min = Integer.MAX_VALUE;
		
		double colValue = 1;
		
		for(int j = 1; j < tableauRows-1; j++){
			
			colValue = getDouble(j, pivCol);
			double rowValue = getDouble(j, consCol);
			if(colValue < 0 && rowValue > 0 || colValue == 0)
				continue;
			min2 = rowValue/colValue;
			
			if(min2 < min && min2 > 0){
				min = min2;
				pivRow = j;
			}
		}

		this.selectCell(pivRow, pivCol);
		return new Pivot(pivRow, pivCol);
	}

	public void decTableauColumns() {
		tableauColumns--;
	}

	public void decTableauRows() {
		tableauRows--;
		
	}

	public void incTableauColumns() {
		tableauColumns++;
		
	}
	
	public void incTableauRows() {
		tableauRows++;
		
	}
	
	/**
	 * Selects a cell at (row, col)
	 * @param row
	 * @param col
	 */
	public void selectCell(int row, int col){
		if(row < 0 || col < 0)
			return;
		
		this.setRowSelectionInterval(row, row);
		this.setColumnSelectionInterval(col, col);
	}

	/**
	 * Reshapes the tableau and table to maintain minimum size while
	 * deleting unnecessary rows and columns.
	 * @param rows
	 * @param cols
	 */
	public void reshapeTableau(int rows, int cols) {
		this.setTableauRows(rows);
		this.setTableauColumns(cols);
	}

	/**
	 * Clears the tableau area.
	 */
	public void clear(){
		getSelectionModel().clearSelection();
		undo.push(UndoType.TAB_CHANGE, getTableauState());
		for (int i = 0; i < getRowCount(); i++) {
			for (int j = 0; j < getColumnCount(); j++) {
				setValueAt("", i, j);
			}
		}
	}
	
	/**
	 * Creates the dual tableau from LP problem. This method expects no
	 * slack variables to be present. Assumes LP is of the form
	 * 
	 * 
	 * Minimize f(x)
	 * subject to Ax >= b
	 * 
	 * @return
	 */
	public void convertToDual(){
		
		Tableau dual = new Tableau(tableauColumns, tableauRows+tableauColumns);
		
		for(int i = 0; i < tableauRows; i++){
			for(int j = 0; j < tableauColumns; j++){
				if(i == tableauRows-1)
					dual.set(j, tableauRows+tableauColumns-1, this.getDouble(i, j));
				else
					dual.set(j, i, this.getDouble(i, j));
			}
		}
		
		dual.rowDiv(tableauColumns-1, -1);
		
		int c;
		
		for(int i = 0; i < tableauColumns; i++){
			c = dual.getCols()-tableauColumns-1+i;
			dual.set(i, c, 1);
		}
				
		this.reshapeTableau(tableauColumns, tableauRows+tableauColumns);
		
		HashSet<Cell> changedCells = new HashSet<>(tableauColumns*(tableauRows+tableauColumns));
		
		double oldVal, newVal;
		
		for(int i = 0; i < dual.getRows(); i++){
			for(int j = 0; j < dual.getCols(); j++){
				
				oldVal = getDouble(i, j);
				newVal = dual.get(i, j);
				
				if(oldVal != newVal){
					changedCells.add(new Cell(i, j, oldVal));
					this.setDouble(newVal, i, j);
				}
				
			}
		}
		
		undo.push(UndoType.TAB_CHANGE, changedCells);

	}
	
	/**
	 * Returns the current tableau as a HashSet<Cell>.
	 * @return
	 */
	public HashSet<Cell> getTableauState(){
		// Make group undo
		HashSet<Cell> groupUndo = new HashSet<Cell>();
		for(int i = 0; i < tableauRows; i++)
			for(int j = 0; j < tableauColumns; j++)
				groupUndo.add(new Cell(i, j, getDouble(i, j)));
		return groupUndo;
	}
	
	/**
	 * Gets the currently selected value in the table. Returns "" if nothing
	 * selected.
	 * 
	 * @return Selected cell's value or "" if no selection
	 */
	public Object getSelectedValue() {
		int row = getSelectedRow();
		int col = getSelectedColumn();

		if (row < 0 || col < 0)
			return "";

		Object val = getValueAt(row, col);

		if (val == null)
			return "";

		return val;
	}
	
	/**
	 * Updates the table column headers so that they read in the format X1 ...
	 * XN S0...SM M Constraints with letters after
	 */
	public void updateHeaders() {

		int i = 0;
		int k = 0;
		int numCols = tableauColumns;

		TableColumnModel columnModel = getColumnModel();

		// Renames headers X1, X2, ... XN, Constraints.
		for (; i < tableauColumns; i++) {
			if (i != numCols - 1)
				columnModel.getColumn(i).setHeaderValue(String.format("X%d", i + 1));
			else
				columnModel.getColumn(i).setHeaderValue("Constraints");
		}

		StringBuilder colName = new StringBuilder(3);

		/*
		 * A B C ... i/26 == 0 AA AB AC ... i/26 == 1 BA BB BC ... i/26 == 2 CA
		 * CB CC ...
		 * 
		 */

		k = 0;
		// Need to update the letter headers too
		for (; i < getColumnCount(); i++) {

			char prefix = (char) (i / 26 - 1 + 'A');

			for (int j = 0; j < k; j++) {
				colName.append(prefix);
			}

			colName.append((char) (i % 26 + 'A'));

			if (colName.charAt(0) == 'Z')
				k++;

			columnModel.getColumn(i).setHeaderValue(colName.toString());
			colName.delete(0, colName.length());
		}

		getTableHeader().repaint();
	}

	public void saveRow(int i) {
		
		HashSet<Cell> changedRow = new HashSet<>(tableauColumns);
		for(int k = 0; k < tableauColumns; k++)
			changedRow.add(new Cell(i, k, getDouble(i, k)));
		
		undo.push(UndoType.TAB_CHANGE, changedRow);
		
	}
	
}  // End of Class RXTable 
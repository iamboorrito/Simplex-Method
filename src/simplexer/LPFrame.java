package simplexer;

import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JTable;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.MatteBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import javax.swing.JToolBar;
import java.awt.BorderLayout;
import javax.swing.event.TableColumnModelListener;

/**
 * This class is mostly auto-generated from Eclipse's window builder and my
 * custom action/event listeners and anonymous classes.
 * 
 * @author Evan Burton
 *
 */

public class LPFrame {

	private JFrame frmSimplexer;
	private /* JTable */ RXTable table;
	private JTable rowTable;
	private DefaultTableModel tableModel;
	private UndoStack undo, redo;
	private JTextField outputField;
	private JTextField textField;
	public final int MIN_ROWS = 10;
	public final int MIN_COLUMNS = 10;
	public final int MAX_UNDO = 100;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				try {
					LPFrame window = new LPFrame();
					window.frmSimplexer.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public LPFrame() {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmSimplexer = new JFrame();
		frmSimplexer.setIconImage(Toolkit.getDefaultToolkit().getImage(LPFrame.class.getResource("/Icon.png")));
		frmSimplexer.setTitle("Simplexer");
		frmSimplexer.setBounds(100, 100, 569, 324);
		frmSimplexer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/////////////////////////////////////////////////////
		// Default size is 3 rows and 7 columns
		// doubleInterpreter = new DoubleInterpreter();
		undo = new UndoStack(MAX_UNDO);
		redo = new UndoStack(MAX_UNDO);
		frmSimplexer.getContentPane().setLayout(new BorderLayout(0, 0));

		///////////////////////////////////////////////////////
		JPanel drawingPanel = new JPanel();
		frmSimplexer.getContentPane().add(drawingPanel);

		tableModel = new DefaultTableModel(10, 10);

		drawingPanel.setLayout(new BoxLayout(drawingPanel, BoxLayout.X_AXIS));

		// Constructs JeksTable with objective and constraint columns in gray
		table = new /* JTable */ /* JeksTable */ RXTable(tableModel, 3, 7, undo) {

			private static final long serialVersionUID = 7620981877453025221L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				Component comp = super.prepareRenderer(renderer, row, col);

				if (((row == table.getTableauRows() - 1 && col < table.getTableauColumns()))
						|| (col == table.getTableauColumns() - 1 && row < table.getTableauRows())) {
					comp.setBackground(Color.LIGHT_GRAY);
				} else {
					comp.setBackground(Color.WHITE);
				}

				if(table.isRowSelected(row) && table.isColumnSelected(col))
					comp.setBackground(Color.ORANGE);

				return comp;
			}
		};
		
		// TableModelListener
		table.getColumnModel().addColumnModelListener(new TableColumnModelListener(){
	           public void columnAdded(TableColumnModelEvent e) {
	            }

	            public void columnRemoved(TableColumnModelEvent e) {
	            }

	            public void columnMoved(TableColumnModelEvent e) {
	            	int start = e.getFromIndex();
	            	int end = e.getToIndex();
	            	
	            	if(start != end){
	            		table.updateHeaders();
	            	}
	            }

	            public void columnMarginChanged(ChangeEvent e) {
	            }

	            public void columnSelectionChanged(ListSelectionEvent e) {
	            }
		});

		table.setDefaultEditor(Object.class, new MathEditor(undo));
		table.setColumnSelectionAllowed(true);
		table.setFillsViewportHeight(true);

		// Sets color of text when selected
		table.setSelectionForeground(Color.BLACK);
		// Set column headers appropriately
		table.updateHeaders();
		
		// Add row labels
		rowTable = new RowNumberTable(table);
		rowTable.setFillsViewportHeight(true);

		JScrollPane scrollpane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

		drawingPanel.add(scrollpane);
		/////// Row Header ///////
		scrollpane.setRowHeaderView(rowTable);
		scrollpane.setCorner(JScrollPane.UPPER_LEFT_CORNER, rowTable.getTableHeader());

		Dimension d = table.getPreferredSize();
		scrollpane.setPreferredSize(new Dimension(d.width, table.getRowHeight() * table.getRowCount() * 10));

		// scrollpane.setPreferredSize(new Dimension(454, 50));
		scrollpane.setViewportBorder(new MatteBorder(1, 1, 1, 1, new Color(0, 0, 0)));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		table.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

		table.getColumnModel().getSelectionModel().addListSelectionListener(e -> {
			textField.setText(String.valueOf(table.getSelectedValue()));
		});

		table.getSelectionModel().addListSelectionListener(e -> {
			textField.setText(String.valueOf(table.getSelectedValue()));
		});

		table.setCellSelectionEnabled(true);
		table.putClientProperty("terminateEditOnFocusLost", true);
		table.setGridColor(Color.BLUE);

		JMenuBar menuBar = new JMenuBar();
		frmSimplexer.setJMenuBar(menuBar);

		Action updateTableCell = new AbstractAction("Enter") {

			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				int row = table.getSelectedRow();
				int col = table.getSelectedColumn();

				String text = textField.getText();

				undo.push(UndoType.CELL_VALUE, new Cell(row, col, table.getDouble(row, col)));
				
				table.setDouble(table.getDouble(text), row, col);				

				table.requestFocus();
			}

		};

		JPanel panel = new JPanel();
		menuBar.add(panel);
		panel.setLayout(new GridLayout(0, 2, 0, 0));

		outputField = new JTextField();
		panel.add(outputField);
		outputField.setToolTipText("Displays output information such as pivots.");
		outputField.setEditable(false);
		outputField.setColumns(10);

		textField = new JTextField();
		panel.add(textField);

		textField.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), updateTableCell);
		textField.getActionMap().put("Enter", updateTableCell);
		textField.setColumns(10);

		textField.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2)
					textField.copy();
				else
					textField.selectAll();
			}
		});

		JToolBar toolBar = new JToolBar();
		panel.add(toolBar);
		// Simplex Iterator
		JButton btnSimplex = new JButton("Iterate");
		btnSimplex.setMnemonic('i');
		toolBar.add(btnSimplex);

		///////////////////////// END Simplex Iteration Button
		///////////////////////// ///////////////////////

		///////////////////////////// Simplex Run Button
		///////////////////////////// /////////////////////////////
		JButton btnRun = new JButton("Run");
		btnRun.setMnemonic('r');
		toolBar.add(btnRun);
		/////////////////////////// END Simplex Run Button
		/////////////////////////// ///////////////////////////

		//////////////////////////////// Pivot Button
		//////////////////////////////// ////////////////////////////////
		JButton btnDual = new JButton("Dual");
		btnDual.setToolTipText("Converts tableau to the dual tableau");
		toolBar.add(btnDual);

		///////////////////////////////// Undo Button
		///////////////////////////////// ////////////////////////////////
		JButton btnUndo = new JButton("Undo");
		toolBar.add(btnUndo);

		JButton btnRedo = new JButton("Redo");
		btnRedo.addActionListener(e->{

				if(redo.isEmpty()){
					outputField.setText("Nothing to redo!");
					return;
				}

				//System.out.println(redo.size());
				UndoableAction act = redo.pop();

				performUndoableAction(act, UndoableAction.SRC_REDO);

		});
		
		toolBar.add(btnRedo);

		JButton btnClear = new JButton("Clear");
		toolBar.add(btnClear);

		//////////////////////// Set Size Button ///////////////////////
		JButton btnSet = new JButton("Set Size");
		btnSet.addActionListener(e->{

				String choice[] = JOptionPane.showInputDialog("rows, cols: ").trim().split("[\\s\\t\\n\\,]+");

				if(choice.length != 2){
					outputField.setText(String.format("Invalid input on set size action"));
					return;
				}

				try{
					int rows = Integer.parseInt(choice[0]);
					int cols = Integer.parseInt(choice[1]);
					
					setTabSize(rows, cols);

				}catch(Exception ex){
					outputField.setText(ex.getMessage());
				}

				table.updateHeaders();			
		});
		
		toolBar.add(btnSet);

		btnClear.addActionListener(e->{

				table.clear();
				outputField.setText("");
		});

		btnUndo.addActionListener(e->{

				// Check if history empty or no action performed
				if (undo.isEmpty()) {
					outputField.setText("Nothing to undo!");
					return;
				}

				//System.out.println(undo.size());

				UndoableAction act = undo.pop();

				performUndoableAction(act, UndoableAction.SRC_UNDO);

				outputField.setText("");
				//btnPivot.doClick();
				table.repaint();
		});

		JToolBar toolBar_1 = new JToolBar();
		panel.add(toolBar_1);

		///////////////////////////// Add Row/Col Buttons
		///////////////////////////// ////////////////////////////
		JButton newRowButton = new JButton("Add Row");
		toolBar_1.add(newRowButton);
		JButton newColButton = new JButton("Add Col");
		toolBar_1.add(newColButton);

		JButton deleteRow = new JButton("Delete Row");
		toolBar_1.add(deleteRow);

		////////////////////////////// Delete Col ////////////////////////
		JButton btnDeleteCol = new JButton("Delete Col");
		toolBar_1.add(btnDeleteCol);
		btnDeleteCol.addActionListener(e->{

				if (table.getTableauColumns() > 0) {
					
					undo.push(UndoType.TAB_SIZE, new Pivot(table.getTableauRows(), table.getTableauColumns()));


					table.decTableauColumns();
					
					if(table.getColumnCount() > MIN_COLUMNS)
						tableModel.setColumnCount(table.getTableauColumns());
					// System.out.println(tab);

					table.updateHeaders();
					table.repaint();

				} else {
					outputField.setText("No columns to delete");
				}
		});

		////////////////////////////// Delete Row ////////////////////////
		deleteRow.addActionListener(e->{
				// tableModel.removeRow(tab.getRows() - 1);
				if (table.getTableauRows() > 0) {

					undo.push(UndoType.TAB_SIZE, new Pivot(table.getTableauRows(), 
							table.getTableauColumns()));
					
					//tab.deleteRow(tab.getRows() - 1);

					table.decTableauRows();
					
					if(table.getRowCount() > MIN_ROWS)
						tableModel.setRowCount(table.getTableauRows());

					table.repaint();
					rowTable.repaint();
				} else {
					outputField.setText("No rows to delete");
				}
		});

		//////////////////////// New Column  ////////////////////
		newColButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// Push current size
				undo.push(UndoType.TAB_SIZE, new Pivot(table.getTableauRows(), 
						table.getTableauColumns()));

				if (table.getTableauColumns() >= tableModel.getColumnCount())
					tableModel.setColumnCount(table.getTableauColumns() + 1);

				table.incTableauColumns();

				table.updateHeaders();
				table.repaint();
			}
		});

		//////////////////////// New Row ////////////////////
		newRowButton.addActionListener(e->{

			// Push current size
			undo.push(UndoType.TAB_SIZE, new Pivot(table.getTableauRows(), 
					table.getTableauColumns()));

			while (table.getTableauRows() >= tableModel.getRowCount()) {
				tableModel.addRow((Object[]) null);
			}

			table.incTableauRows();

			table.updateHeaders();
			rowTable.repaint();

			table.repaint();

		});

		/////////// Pivot Button////////////////
		btnDual.addActionListener(e->{

				undo.push(UndoType.TAB_CHANGE, table.getTableauState());
				//TODO: make table compute tab = tab.getDual();
				table.convertToDual();
				table.updateHeaders();
				table.repaint();
				rowTable.repaint();
				//System.out.println(tab);

				outputField.setText("Converted to dual problem");

		});
		
		btnRun.addActionListener(e->{

				//undo.push(UndoableType.TAB_CHANGE, tab.copy());

				undo.push(UndoType.TAB_CHANGE, table.getTableauState());
				
				Tableau.OUTPUT output = table.runSimplexMethod();

				if (output == Tableau.OUTPUT.SUCCESS)
					outputField.setText("Simplex Algorithm Completed");
				else
					outputField.setText("Max iterations exceeded!");

				table.selectCell(table.getTableauRows()-1, table.getTableauColumns()-1);

			
		});
		/////////////////////////// Simplex Iteration Button
		/////////////////////////// /////////////////////////
		btnSimplex.addActionListener(e->{

				table.clearSelection();

				if (!table.simplexExit()) {
					
					undo.push(UndoType.TAB_CHANGE, table.getTableauState());

					Pivot p = table.selectPivot();

					outputField.setText("Pivoting on: " + p);

					// Stops cell editing to allow values to be changed
					// Otherwise, selected cell's value will not be
					// overwritten.
					if (table.getCellEditor() != null)
						table.getCellEditor().cancelCellEditing();

					table.simplexIteration();

					if(table.simplexExit()){
						table.selectCell(table.getTableauRows()-1, table.getTableauColumns()-1);
						outputField.setText(outputField.getText()+" | Completed");
					}else{	
						table.selectCell(p.row, p.col);
					}

				} else {
					outputField.setText("Simplex Algorithm Completed");
				}
		});


		outputField.addMouseListener(new MouseAdapter(){
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					outputField.setText("");
				} else {
					outputField.selectAll();
				}
	
			}
		});
	}
	
	public void manageTableSize(int rows, int cols){
		// Set table size accordingly
		if(rows < MIN_ROWS)
			tableModel.setRowCount(MIN_ROWS);
		if(cols < MIN_COLUMNS)
			tableModel.setColumnCount(MIN_COLUMNS);
		if(rows > table.getRowCount() || rows > MIN_ROWS)
			tableModel.setRowCount(rows);
		if(cols > table.getColumnCount() || cols > MIN_COLUMNS)
			tableModel.setColumnCount(cols);
	}
	
	/**
	 * Sets the tableau and table size. Pushes size onto undo stack.
	 * @param rows
	 * @param cols
	 */
	private void setTabSize(int rows, int cols){
		undo.push(UndoType.TAB_SIZE, new Pivot(table.getTableauRows(), 
				table.getTableauColumns()));
		
		table.reshapeTableau(rows, cols);

		manageTableSize(rows, cols);
	}

	@SuppressWarnings("unchecked")
	private void performUndoableAction(UndoableAction act, int source){
		
		// Undo the action depending on what type it was
		switch(act.type){
		case TAB_SIZE:
			// Push current tab size to redo stack
			if(source == UndoableAction.SRC_UNDO)
				redo.push(UndoType.TAB_SIZE, new Pivot(table.getTableauRows(),
						table.getTableauColumns()));
			else
				undo.push(UndoType.TAB_SIZE, new Pivot(table.getTableauRows(),
						table.getTableauColumns()));
			
			Pivot size = (Pivot)act.data;
			
			table.reshapeTableau(size.row, size.col);
			manageTableSize(size.row, size.col);
			
			table.repaint();
			
			break;
		case CELL_VALUE:
			Cell cell = (Cell) act.data;
			// Push value we are about to overwrite to redo stack
			if(source == UndoableAction.SRC_UNDO)
				redo.push(UndoType.CELL_VALUE, new Cell(cell.row, cell.col, table.getDouble(cell.row, cell.col)));
			else
				undo.push(UndoType.CELL_VALUE, new Cell(cell.row, cell.col, table.getDouble(cell.row, cell.col)));
			

			table.setDouble(cell.val, cell.row, cell.col);
			
			table.selectCell(cell.row, cell.col);

			break;
		case TAB_CHANGE:
						
			HashSet<Cell> groupUndo = table.getTableauState();
			
			if(source == UndoableAction.SRC_UNDO)
				redo.push(UndoType.TAB_CHANGE, groupUndo);
			else
				undo.push(UndoType.TAB_CHANGE, groupUndo);
			
			// update changed cells
			for(Cell item : (HashSet<Cell>) act.data){
				table.setDouble(item.val, item.row, item.col);
			}
			
			table.selectPivot();
			
			break;
		default:
			break;
		}

		table.updateHeaders();
	}
	
}

package simplexer;
import java.awt.EventQueue;
import javax.swing.JFrame;
import javax.swing.JTable;
import java.awt.Color;
import java.awt.Component;
import javax.swing.JPanel;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.util.Stack;
import javax.swing.JButton;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import javax.swing.JMenuBar;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.MatteBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.BoxLayout;

import com.eteks.jeks.JeksExpression;
import com.eteks.jeks.JeksTable;
import com.eteks.parser.DoubleInterpreter;

/**
 * This class is mostly auto-generated from Eclipse's GUI maker and my custom
 * action and event listeners and anonymous classes.
 * 
 * @author Evan Burton
 *
 */

public class LPFrame {

	private JFrame frmSimplexer;
	private JeksTable /*RXTable*/ table;
	private Tableau tab;
	private DefaultTableModel tableModel;
	private JTextField rowField;
	private JTextField colField;
	private Stack<Tableau> history;
	private JTextField outputField;
	private DoubleInterpreter doubleInterpreter;
	
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
		frmSimplexer.setBounds(100, 100, 576, 329);
		frmSimplexer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		/////////////////////////////////////////////////////
		// Default size is 3 rows and 7 columns
		tab = new Tableau(3, 7);
		doubleInterpreter = new DoubleInterpreter();
		history = new Stack<>();
		/////////////////////////////////////////////////////
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 521, 0 };
		gridBagLayout.rowHeights = new int[] { 156, 156, 0 };
		gridBagLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 1.0, 0.0, Double.MIN_VALUE };
		frmSimplexer.getContentPane().setLayout(gridBagLayout);

		JPanel buttonPanel = new JPanel();

		JPanel drawingPanel = new JPanel();
		GridBagConstraints gbc_drawingPanel = new GridBagConstraints();
		gbc_drawingPanel.fill = GridBagConstraints.BOTH;
		gbc_drawingPanel.insets = new Insets(0, 0, 5, 0);
		gbc_drawingPanel.gridx = 0;
		gbc_drawingPanel.gridy = 0;
		frmSimplexer.getContentPane().add(drawingPanel, gbc_drawingPanel);
		
		tableModel = new DefaultTableModel(100, 100);
		
		// Constructs JeksTable with objective and constraint columns in gray
		table = new JeksTable/*RXTable*/(tableModel){
			
			private static final long serialVersionUID = 1L;

			@Override
		    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
		        Component comp = super.prepareRenderer(renderer, row, col);

				if(((row == tab.getRows() - 1 && col < tab.getCols())) || (col == tab.getCols() - 1 && row < tab.getRows())){
				comp.setBackground(Color.LIGHT_GRAY);
				//c.setForeground(Color.BLACK);
			}else{
				comp.setBackground(Color.WHITE);
			}
		        
		        return comp;
		    }
			
		};
		
		// Set column headers appropriately
		updateHeaders();
		
		table.setColumnSelectionAllowed(true);
		table.setFillsViewportHeight(true);

		tableModel.addTableModelListener(e -> {
			
			if(e.getType() == TableModelEvent.UPDATE){
				int row = table.getSelectedRow();
				int col = table.getSelectedColumn();
				
				if(row < 0 || col < 0)
					return;
				
				if(row < tab.getRows() && col < tab.getCols())
					tab.set(row, col, getDouble(row,col));
			}
			
			//System.out.println(tab);
			table.repaint();
			
		});

		drawingPanel.setLayout(new BoxLayout(drawingPanel, BoxLayout.X_AXIS));

		JScrollPane jpane = new JScrollPane(table, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
				ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jpane.setAlignmentY(Component.TOP_ALIGNMENT);
		jpane.setPreferredSize(new Dimension(454, 50));
		jpane.setViewportBorder(new MatteBorder(1, 1, 1, 1, new Color(0, 0, 0)));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		drawingPanel.add(jpane);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setCellSelectionEnabled(true);
		table.putClientProperty("terminateEditOnFocusLost", true);
		table.setGridColor(Color.BLUE);

		GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
		gbc_buttonPanel.fill = GridBagConstraints.BOTH;
		gbc_buttonPanel.gridx = 0;
		gbc_buttonPanel.gridy = 1;
		frmSimplexer.getContentPane().add(buttonPanel, gbc_buttonPanel);
		buttonPanel.setLayout(new GridLayout(0, 2, 0, 0));

		JPanel buttonPanelLeft = new JPanel();
		buttonPanel.add(buttonPanelLeft);
		buttonPanelLeft.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel setSizePanel = new JPanel();
		buttonPanelLeft.add(setSizePanel);
		// Set Size
		JButton setSizeButton = new JButton("Set Size");
		setSizeButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				int rows = Integer.parseInt(rowField.getText());
				int cols = Integer.parseInt(colField.getText());

				if (rows < 0 || cols < 0)
					return;

				if(tableModel.getColumnCount() < cols)
					tableModel.setColumnCount(cols);
				if(tableModel.getRowCount() < rows)
					tableModel.setRowCount(rows);
				
				tab.reshape(rows, cols);

				// Update tab to include new entries
				
				for(int i = 0; i < rows; i++)
					for(int j = 0; j < cols; j++)
						tab.set(i, j, getDouble(i, j));
				
				updateHeaders();

				history.clear();
			}
		});
		setSizePanel.add(setSizeButton);

		rowField = new JTextField();
		setSizePanel.add(rowField);
		rowField.setColumns(3);

		colField = new JTextField();
		setSizePanel.add(colField);
		colField.setColumns(3);

		JPanel runButtonPanel = new JPanel();
		buttonPanelLeft.add(runButtonPanel);
		// Simplex Iterator
		JButton btnSimplex = new JButton("Iterate");
		/////////////////////// Simplex Iteration Button //////////////////
		btnSimplex.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if (!tab.simplexExit()) {

					history.push(tab.copy());

					Pivot p = tab.selectPivot();
					// Make pivot indices 1-based for math familiarity
					p.row++;
					p.col++;

					outputField.setText("Pivoting on: " + p);

					tab.simplexIteration();

					updateTable();
				} else {
					outputField.setText("Simplex Algorithm Completed");
				}
			}

		});
		runButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		runButtonPanel.add(btnSimplex);
		// Simplex
		JButton btnSimplex_1 = new JButton("Run");
		//////////////////////// Simplex Method Button ////////////////////
		btnSimplex_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				history.push(tab.copy());
				Tableau.OUTPUT output = tab.runSimplexMethod();

				if (output == Tableau.OUTPUT.SUCCESS)
					outputField.setText("Simplex Algorithm Completed");
				else
					outputField.setText("Max iterations exceeded!");

				updateTable();
			}
		});
		runButtonPanel.add(btnSimplex_1);

		JButton btnPivot = new JButton("Pivot");
		/////////// Pivot Button////////////////
		btnPivot.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				Pivot p = tab.selectPivot();
				// Make pivot indices 1-based for math familiarity
				p.row++;
				p.col++;

				outputField.setText("Pivot: " + p);

			}
		});
		runButtonPanel.add(btnPivot);

		JPanel editPanel = new JPanel();
		buttonPanelLeft.add(editPanel);
		JButton btnUndoSimplexIteration = new JButton("Undo");
		editPanel.add(btnUndoSimplexIteration);
		btnUndoSimplexIteration.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				// Check if history empty or no action performed
				if (history.isEmpty() || tab.equals(history.peek())) {
					outputField.setText("Nothing to undo!");
					return;
				}

				tab = history.pop();

				updateTable();
				outputField.setText("");
				table.repaint();
			}
		});

		JPanel buttonPanelRight = new JPanel();
		buttonPanel.add(buttonPanelRight);
		buttonPanelRight.setLayout(new GridLayout(0, 1, 0, 0));

		JPanel addButtonPanel = new JPanel();
		buttonPanelRight.add(addButtonPanel);
		addButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		// Add Row Button
		JButton newRowButton = new JButton("Add Row");
		addButtonPanel.add(newRowButton);

		////////////////////////////// Add Col ////////////////////////
		// Add Column Button
		JButton newColButton = new JButton("Add Col");
		addButtonPanel.add(newColButton);

		JPanel deleteButtonPanel = new JPanel();
		buttonPanelRight.add(deleteButtonPanel);
		deleteButtonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		JButton deleteRow = new JButton("Delete Row");
		deleteButtonPanel.add(deleteRow);
		////////////////////////////// Delete Col ////////////////////////
		JButton btnDeleteCol = new JButton("Delete Col");
		btnDeleteCol.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				//tableModel.setColumnCount(tab.getCols() - 1);
				if(tab.getCols() > 0){
					tab.deleteCol(tab.getCols() - 1);
					table.repaint();
					
					updateHeaders();
					
					//System.out.println(tab);
					
					updateHeaders();
					table.repaint();
					
				}else{
					outputField.setText("No columns to delete");
				}
			}
		});
		deleteButtonPanel.add(btnDeleteCol);

		JPanel clearButtonPanel = new JPanel();
		buttonPanelRight.add(clearButtonPanel);

		JButton btnClear = new JButton("Clear");
		clearButtonPanel.add(btnClear);

		JMenuBar menuBar = new JMenuBar();
		frmSimplexer.setJMenuBar(menuBar);

		outputField = new JTextField();
		outputField.setToolTipText("Displays output information such as pivots.");
		outputField.setEditable(false);
		menuBar.add(outputField);
		outputField.setColumns(10);
		
		outputField.addMouseListener(new MouseListener(){

			@Override
			public void mouseClicked(MouseEvent e) {
				outputField.setText("");
				
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		btnClear.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (int i = 0; i < tab.getRows(); i++) {
					for (int j = 0; j < tab.getCols(); j++) {
						tableModel.setValueAt("", i, j);
						tab.set(i, j, 0);
					}
				}
				outputField.setText("");
			}
		});
		////////////////////////////// Delete Row ////////////////////////
		deleteRow.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//tableModel.removeRow(tab.getRows() - 1);
				if(tab.getRows() > 0){
					tab.deleteRow(tab.getRows() - 1);
					table.repaint();
				} else {
					outputField.setText("No rows to delete");
				}
			}
		});
		
		newColButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {

				if(tab.getCols() > tableModel.getColumnCount())
					tableModel.setColumnCount(tab.getCols() + 1);
				
				tab.addCol();
				
				int lastCol = tab.getCols()-1;
				
				// Auto populates tab with current values
				for(int i = 0; i < tab.getRows(); i++)
					tab.set(i, lastCol, getDouble(i, lastCol));
				
				
				updateHeaders();
				
				table.repaint();
			}
		});

		newRowButton.addActionListener(e -> {
			if(tab.getRows() > tableModel.getRowCount())
				tableModel.addRow(new Double[tab.getRows() + 1]);
			tab.addRow();
			
			int lastRow = tab.getRows()-1;
			
			// Auto populates tab with current values
			for(int i = 0; i < tab.getCols(); i++)
				tab.set(lastRow, i, getDouble(lastRow, i));
			
			updateHeaders();
			table.repaint();
			//tableModel.fireTableDataChanged();
		});
	}

	private void updateHeaders() {
		
		int i = 0;
		int k = 0;
		int numCols = tab.getCols();
		
		TableColumnModel columnModel = table.getColumnModel();
		
		for(; i < tab.getCols(); i++){
			if(i >= numCols-tab.getRows()-1 && i != numCols-1){
				columnModel.getColumn(i).setHeaderValue(String.format("S%d", k+1));
				k++;
			}else{
				
				
				if(i != numCols - 1)
					columnModel.getColumn(i).setHeaderValue(String.format("X%d", i+1));
				else
					columnModel.getColumn(i).setHeaderValue("Constraints");
			}
		}
		
		StringBuilder colName = new StringBuilder(3);
		
		/*
		 * A B C ...    i/26 == 0
		 * AA AB AC ... i/26 == 1
		 * BA BB BC ... i/26 == 2
		 * CA CB CC ...
		 * 
		 */
		
		k = 0;
		// Need to update the letter headers too
		for(; i < table.getColumnCount(); i++){
			
			char prefix = (char) (i/26-1 + 'A');
			
			for(int j = 0; j < k; j++){
				colName.append(prefix);
			}
			
			colName.append((char)(i%26+'A'));
			
			if(colName.charAt(0) == 'Z')
				k++;
			
			columnModel.getColumn(i).setHeaderValue(colName.toString());
			colName.delete(0, colName.length());
		}
		
		frmSimplexer.repaint();
	}

	/**
	 * Goes through the tableModel and sets internal representation accordingly.
	 */
	public void updateTable() {
		for (int i = 0; i < tab.getRows(); i++) {
			for (int j = 0; j < tab.getCols(); j++) {
				tableModel.setValueAt(tab.get(i, j), i, j);
			}
		}
		table.invalidate();
	}

	/**
	 * Gets the JTable object which is used to manage spreadsheet activities.
	 * 
	 * @return JTable object
	 */
	public JTable getTable() {
		return table;
	}
	

	/**
	 * Attempts to retrieve entry i,j as a double. Returns 0 if failure.
	 * @param row
	 * @param col
	 * @return
	 */
	public double getDouble(int row, int col) {
		
		// Tests to see if you can read a double from the cell
		// If that fails try evaluating a JeksExpression as Double
		// Otherwise gives up trying to read input and alerts user.
		
		Object entry = table.getValueAt(row, col);
		
		if(entry == null || toString().equals("")){
			return 0;
		}
		
		try{
			
			if(table.getValueAt(row, col) instanceof JeksExpression){
				double val = (double) ((JeksExpression)entry).getValue(doubleInterpreter);
				return val;
			}else{
				double val = Double.parseDouble(entry.toString());
				return val;
			}
			
		}catch(Exception ex){
			outputField.setText(String.format("Parse error: possible missing '=' at (%d, %d) or invalid input", row+1 ,col+1));
		}
		
		return 0;
	}

}

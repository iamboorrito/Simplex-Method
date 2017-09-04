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
import javax.swing.table.TableColumnModel;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
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
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import java.awt.event.KeyEvent;
import org.mariuszgromada.math.mxparser.Expression;
import javax.swing.JToolBar;
import java.awt.BorderLayout;

/**
 * This class is mostly auto-generated from Eclipse's window builder and my
 * custom action/event listeners and anonymous classes.
 * 
 * @author Evan Burton
 *
 */

public class LPFrame {

	private JFrame frmSimplexer;
	private /* JTable */ /* JeksTable */ RXTable table;
	private Tableau tab;
	private DefaultTableModel tableModel;
	private HistoryStack<Tableau> history;
	private JTextField outputField;
	// private DoubleInterpreter doubleInterpreter;
	private JTextField textField;
	public final int MIN_ROWS = 10;
	public final int MIN_COLUMNS = 10;
	
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
		// doubleInterpreter = new DoubleInterpreter();
		history = new HistoryStack<Tableau>(Tableau.MAX_ITERATIONS+100);
		frmSimplexer.getContentPane().setLayout(new BorderLayout(0, 0));
		
		///////////////////////////////////////////////////////
		JPanel drawingPanel = new JPanel();
		frmSimplexer.getContentPane().add(drawingPanel);

		tableModel = new DefaultTableModel(10, 10);

		// TableModelListener
		tableModel.addTableModelListener(e -> {

			if (e.getType() == TableModelEvent.UPDATE) {

				int row = table.getSelectedRow();
				int col = table.getSelectedColumn();

				if (row < 0 || col < 0)
					return;

				double val = getDouble(row, col);

				if (row < tab.getRows() && col < tab.getCols())
					tab.set(row, col, val);

			}

			// System.out.println(tab);
			table.repaint();

		});

		drawingPanel.setLayout(new BoxLayout(drawingPanel, BoxLayout.X_AXIS));

		// Constructs JeksTable with objective and constraint columns in gray
		table = new /* JTable */ /* JeksTable */ RXTable(tableModel) {

			private static final long serialVersionUID = 1L;

			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
				Component comp = super.prepareRenderer(renderer, row, col);

				if (((row == tab.getRows() - 1 && col < tab.getCols()))
						|| (col == tab.getCols() - 1 && row < tab.getRows())) {
					comp.setBackground(Color.LIGHT_GRAY);
				} else {
					comp.setBackground(Color.WHITE);
				}
				
				if(table.isRowSelected(row) && table.isColumnSelected(col))
					comp.setBackground(Color.ORANGE);

				return comp;
			}
		};

		table.setDefaultEditor(Object.class, new MathEditor());
		table.setColumnSelectionAllowed(true);
		table.setFillsViewportHeight(true);

		// Sets color of text when selected
		table.setSelectionForeground(Color.BLACK);
		// Set column headers appropriately
		updateHeaders();

		// Add rows?
		JTable rowTable = new RowNumberTable(table, tab);
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

		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		table.getColumnModel().getSelectionModel().addListSelectionListener(e -> {
			textField.setText(String.valueOf(getSelectedValue()));
		});

		table.getSelectionModel().addListSelectionListener(e -> {
			textField.setText(String.valueOf(getSelectedValue()));
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

				double val;

				val = getDouble(text);
				tableModel.setValueAt(val, row, col);

				// System.out.printf("text = %f\n", val);
				tab.set(row, col, val);

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
												
														textField.addMouseListener(new MouseListener() {
												
															@Override
															public void mouseClicked(MouseEvent e) {
																if (e.getClickCount() == 2)
																	textField.copy();
																else
																	textField.selectAll();
															}
												
															@Override
															public void mousePressed(MouseEvent e) {
															}
												
															@Override
															public void mouseReleased(MouseEvent e) {
															}
												
															@Override
															public void mouseEntered(MouseEvent e) {
															}
												
															@Override
															public void mouseExited(MouseEvent e) {
															}
												
														});
								
								JToolBar toolBar = new JToolBar();
								panel.add(toolBar);
								// Simplex Iterator
								JButton btnSimplex = new JButton("Iterate");
								toolBar.add(btnSimplex);
								
								
								
								///////////////////////// END Simplex Iteration Button
								///////////////////////// ///////////////////////

								///////////////////////////// Simplex Run Button
								///////////////////////////// /////////////////////////////
								JButton btnRun = new JButton("Run");
								toolBar.add(btnRun);
								/////////////////////////// END Simplex Run Button
								/////////////////////////// ///////////////////////////

								//////////////////////////////// Pivot Button
								//////////////////////////////// ////////////////////////////////
								JButton btnPivot = new JButton("Pivot");
								toolBar.add(btnPivot);
								
										///////////////////////////////// Undo Button
										///////////////////////////////// ////////////////////////////////
										JButton btnUndoSimplexIteration = new JButton("Undo");
										toolBar.add(btnUndoSimplexIteration);
										
												JButton btnClear = new JButton("Clear");
												toolBar.add(btnClear);
												
														btnClear.addActionListener(new ActionListener() {
												
															@Override
															public void actionPerformed(ActionEvent e) {
												
																// Save tableau state on clear()
																history.push(tab.copy());
												
																table.getSelectionModel().clearSelection();
																
																for (int i = 0; i < table.getRowCount(); i++) {
																	for (int j = 0; j < table.getColumnCount(); j++) {
																		tableModel.setValueAt("", i, j);
																		if(i < tab.getRows() && j < tab.getCols())
																			tab.set(i, j, 0);
																	}
																}
																outputField.setText("");
															}
														});
										
												btnUndoSimplexIteration.addActionListener(new ActionListener() {
													@Override
													public void actionPerformed(ActionEvent e) {
										
														// Check if history empty or no action performed
														if (history.isEmpty()) {
															outputField.setText("Nothing to undo!");
															return;
														}
										
														tab = history.pop();
										
														updateTable();
														outputField.setText("");
														btnPivot.doClick();
														table.repaint();
													}
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
														btnDeleteCol.addActionListener(new ActionListener() {
															@Override
															public void actionPerformed(ActionEvent e) {

																// tableModel.setColumnCount(tab.getCols() - 1);
																if (tab.getCols() > 0) {
																	
																	// Save in case of undo
																	history.push(tab.copy());
																	
																	tab.deleteCol(tab.getCols() - 1);
																	
																	if(table.getColumnCount() > MIN_COLUMNS)
																		tableModel.setColumnCount(tab.getCols());
																	// System.out.println(tab);

																	updateHeaders();
																	table.repaint();

																} else {
																	outputField.setText("No columns to delete");
																}
															}
														});
												
														////////////////////////////// Delete Row ////////////////////////
														deleteRow.addActionListener(new ActionListener() {
															@Override
															public void actionPerformed(ActionEvent e) {
																// tableModel.removeRow(tab.getRows() - 1);
																if (tab.getRows() > 0) {
																	
																	history.push(tab.copy());
																	
																	tab.deleteRow(tab.getRows() - 1);
																	
																	if(table.getRowCount() > MIN_ROWS)
																		tableModel.setRowCount(tab.getRows());
																	
																	table.repaint();
																} else {
																	outputField.setText("No rows to delete");
																}
															}
														});
										
												//////////////////////// Col Act. Listener ////////////////////
												newColButton.addActionListener(new ActionListener() {
													@Override
													public void actionPerformed(ActionEvent e) {
										
														history.push(tab.copy());
														
														if (tab.getCols() >= tableModel.getColumnCount())
															tableModel.setColumnCount(tab.getCols() + 1);
										
														tab.addCol();
										
														int lastCol = tab.getCols() - 1;
										
														// Auto populates tab with current values
														for (int i = 0; i < tab.getRows(); i++)
															tab.set(i, lastCol, getDouble(i, lastCol));
										
														updateHeaders();
														table.repaint();
													}
												});
										
												//////////////////////// Row Act. Listener ////////////////////
												newRowButton.addActionListener(e -> {
										
													history.push(tab.copy());
													
													while (tab.getRows() >= tableModel.getRowCount()) {
														tableModel.addRow((Object[]) null);
													}
										
													tab.addRow();
										
													int lastRow = tab.getRows() - 1;
										
													// Auto populates tab with current values
													for (int i = 0; i < tab.getCols(); i++)
														tab.set(lastRow, i, getDouble(lastRow, i));
										
													updateHeaders();
										
													table.repaint();
										
												});
												
								/////////// Pivot Button////////////////
								btnPivot.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {

										Pivot p = tab.selectPivot();
										// Make pivot indices 1-based for math familiarity

										table.setRowSelectionInterval(p.row, p.row);
										table.setColumnSelectionInterval(p.col, p.col);

										outputField.setText("Pivot: " + p);

									}
								});
								btnRun.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {

										history.push(tab.copy());
										
										Tableau.OUTPUT output = tab.runSimplexMethod();

										if (output == Tableau.OUTPUT.SUCCESS)
											outputField.setText("Simplex Algorithm Completed");
										else
											outputField.setText("Max iterations exceeded!");

										updateTable();
										
										table.setRowSelectionInterval(tab.getRows()-1, tab.getRows()-1);
										table.setColumnSelectionInterval(tab.getCols()-1, tab.getCols()-1);
										
									}
								});
								/////////////////////////// Simplex Iteration Button
								/////////////////////////// /////////////////////////
								btnSimplex.addActionListener(new ActionListener() {
									@Override
									public void actionPerformed(ActionEvent e) {

										table.clearSelection();

										if (!tab.simplexExit()) {

											history.push(tab.copy());

											Pivot p = tab.selectPivot();

											outputField.setText("Pivoting on: " + p);

											// Stops cell editing to allow values to be changed
											// Otherwise, selected cell's value will not be
											// overwritten.
											if (table.getCellEditor() != null)
												table.getCellEditor().cancelCellEditing();

											tab.simplexIteration();

											updateTable();

											if(tab.simplexExit()){
												table.setRowSelectionInterval(tab.getRows()-1, tab.getRows()-1);
												table.setColumnSelectionInterval(tab.getCols()-1, tab.getCols()-1);
												outputField.setText(outputField.getText()+" | Completed");
											}else{	
												table.setRowSelectionInterval(p.row, p.row);
												table.setColumnSelectionInterval(p.col, p.col);
											}

										} else {
											outputField.setText("Simplex Algorithm Completed");
										}
									}

								});
								
								
										outputField.addMouseListener(new MouseListener() {
								
											@Override
											public void mouseClicked(MouseEvent e) {
												if (e.getClickCount() == 2) {
													outputField.setText("");
												} else {
													outputField.selectAll();
												}
								
											}
								
											@Override
											public void mousePressed(MouseEvent e) {
											}
								
											@Override
											public void mouseReleased(MouseEvent e) {
											}
								
											@Override
											public void mouseEntered(MouseEvent e) {
											}
								
											@Override
											public void mouseExited(MouseEvent e) {
											}
								
										});
	}

	/**
	 * Updates the table column headers so that they read in the format X1 ...
	 * XN S0...SM M Constraints with letters after
	 */
	private void updateHeaders() {

		int i = 0;
		int k = 0;
		int numCols = tab.getCols();

		if (table == null)
			return;

		TableColumnModel columnModel = table.getColumnModel();

		// This code renames the headers with slack variables
//		for (; i < tab.getCols(); i++) {
//			if (i >= numCols - tab.getRows() - 1 && i != numCols - 1) {
//				if (i != numCols - 2) {
//					columnModel.getColumn(i).setHeaderValue(String.format("S%d", k + 1));
//				} else {
//					columnModel.getColumn(i).setHeaderValue("M");
//				}
//				k++;
//			} else {
//
//				if (i != numCols - 1)
//					columnModel.getColumn(i).setHeaderValue(String.format("X%d", i + 1));
//				else
//					columnModel.getColumn(i).setHeaderValue("Constraints");
//			}
//		}
		
		// Renames headers X1, X2, ... XN, Constraints.
		for (; i < tab.getCols(); i++) {
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
		for (; i < table.getColumnCount(); i++) {

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

		frmSimplexer.repaint();
	}

	/**
	 * Goes through the tableModel and sets external representation accordingly.
	 */
	public void updateTable() {
		
		table.getSelectionModel().clearSelection();
		
		for (int i = 0; i < tab.getRows(); i++) {
			for (int j = 0; j < tab.getCols(); j++) {
				table.setValueAt(tab.get(i, j), i, j);
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
			outputField.setText("Parse error: possible missing '=' at text field or invalid input");
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
		// If that fails try evaluating a JeksExpression as Double
		// Otherwise gives up trying to read input and alerts user.

		if (row < 0 || col < 0)
			return 0;

		Object entry = table.getValueAt(row, col);

		if (entry == null || entry.toString().trim().equals("")) {
			return 0;
		}

		Double val = (new Expression(entry.toString())).calculate();

		if (val == Double.NaN) {
			outputField.setText(
					String.format("Parse error: possible missing '=' at (%d, %d) or invalid input", row + 1, col + 1));
			table.setValueAt(0, row, col);

			val = 0.0;
		}

		return val;

	}

	/**
	 * Gets the currently selected value in the table. Returns "" if nothing
	 * selected.
	 * 
	 * @return Selected cell's value or "" if no selection
	 */
	public Object getSelectedValue() {
		int row = table.getSelectedRow();
		int col = table.getSelectedColumn();

		if (row < 0 || col < 0)
			return "";

		Object val = table.getValueAt(row, col);

		if (val == null)
			return "";

		return val;
	}
}

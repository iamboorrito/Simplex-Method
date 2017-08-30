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
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextField;
import java.awt.FlowLayout;
import javax.swing.ListSelectionModel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JScrollPane;
import java.awt.Dimension;
import javax.swing.border.SoftBevelBorder;
import javax.swing.border.BevelBorder;
import java.awt.GridLayout;
import java.awt.BorderLayout;
import java.awt.Toolkit;

/**
 *  This class is mostly auto-generated from Eclipse's GUI maker.
 *  
 * @author Evan Burton
 *
 */

public class LPFrame {

	private JFrame frmSimplexer;
	private RXTable table;
	private Tableau tab;
	private DefaultTableModel tableModel;
	private JTextField rowField;
	private JTextField colField;
	private Stack<Tableau> history;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
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
		frmSimplexer.setBounds(100, 100, 521, 335);
		frmSimplexer.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		tab = new Tableau(3,6);
		history = new Stack<>();
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{521, 0};
		gridBagLayout.rowHeights = new int[]{156, 156, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		frmSimplexer.getContentPane().setLayout(gridBagLayout);
		
		JPanel buttonPanel = new JPanel();
								
										JPanel drawingPanel = new JPanel();
										GridBagConstraints gbc_drawingPanel = new GridBagConstraints();
										gbc_drawingPanel.fill = GridBagConstraints.BOTH;
										gbc_drawingPanel.insets = new Insets(0, 0, 5, 0);
										gbc_drawingPanel.gridx = 0;
										gbc_drawingPanel.gridy = 0;
										frmSimplexer.getContentPane().add(drawingPanel, gbc_drawingPanel);
										
												table = new RXTable(3,7);
												table.setSelectAllForEdit(true);
												
												table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer()
												{
												    /**
													 * Default serial ID
													 */
													private static final long serialVersionUID = 1L;

													@Override
												    public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int col)
												    {
												        final Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
												        c.setBackground((row == tableModel.getRowCount()-1)||(col == tableModel.getColumnCount()-1) ? Color.LIGHT_GRAY : Color.WHITE);
												        
												        return c;
												    }
												});
												drawingPanel.setLayout(new BorderLayout(0, 0));
												
												JScrollPane jpane = new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
												jpane.setPreferredSize(new Dimension(454, 204));
												jpane.setViewportBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
												table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
												drawingPanel.add(jpane);
												table.setFillsViewportHeight(true);
												table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
												table.setCellSelectionEnabled(true);
												table.putClientProperty("terminateEditOnFocusLost", true);
												table.setGridColor(Color.BLUE);
												tableModel = (DefaultTableModel)table.getModel();
												table.getModel().addTableModelListener(e -> {
													if (e.getType() == TableModelEvent.UPDATE) {

														int row = e.getFirstRow();
														int col = e.getColumn();

														if(row < 0 || col < 0)
															return;

														String cellValue = (String)(""+table.getModel().getValueAt(row, col));

														if(cellValue == "" || cellValue == null)
															tab.set(row, col, 0);
														else{
															// It's okay for bad values, don't crash program, make them 0.
															// This way you do not need to specify blanks as zeros.
															try{
																double value = Double.parseDouble(cellValue);
																tab.set(row, col, value);
															}catch(Exception Exc){
																table.getModel().setValueAt(0, row, col);
															}

														}

													}
												});
								GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
								gbc_buttonPanel.fill = GridBagConstraints.BOTH;
								gbc_buttonPanel.gridx = 0;
								gbc_buttonPanel.gridy = 1;
								frmSimplexer.getContentPane().add(buttonPanel, gbc_buttonPanel);
														buttonPanel.setLayout(new GridLayout(0, 2, 0, 0));
														
														JPanel panel_4 = new JPanel();
														buttonPanel.add(panel_4);
														
																JPanel panel_3 = new JPanel();
																panel_4.add(panel_3);
																// Set Size		
																JButton setSizeButton = new JButton("Set Size");
																setSizeButton.addActionListener(new ActionListener() {
																	public void actionPerformed(ActionEvent e) {

																		int rows = Integer.parseInt(rowField.getText());
																		int cols = Integer.parseInt(colField.getText());

																		if(rows < 0 || cols < 0)
																			return;

																		tableModel.setColumnCount(cols);
																		tableModel.setRowCount(rows);
																		tab.reshape(rows, cols);
																		history.clear();
																	}
																});
																panel_3.add(setSizeButton);
																
																		rowField = new JTextField();
																		panel_3.add(rowField);
																		rowField.setColumns(3);
																		
																				colField = new JTextField();
																				panel_3.add(colField);
																				colField.setColumns(3);
																				
																						JPanel panel_2 = new JPanel();
																						panel_4.add(panel_2);
																						// Simplex Iterator		
																						JButton btnSimplex = new JButton("Simplex Iteration");
																						/////////////////////// Simplex Iteration Button //////////////////
																						btnSimplex.addActionListener(new ActionListener() {
																							public void actionPerformed(ActionEvent e) {

																								history.push(tab.copy());
																								tab.simplexIteration();

																								updateTable();
																							}

																						});
																						panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
																						panel_2.add(btnSimplex);
																						// Simplex		
																						JButton btnSimplex_1 = new JButton("Simplex");
																						//////////////////////// Simplex Method Button ////////////////////
																						btnSimplex_1.addActionListener(new ActionListener() {
																							public void actionPerformed(ActionEvent e) {
																								
																								while(!tab.simplexExit()){
																									history.push(tab.copy());
																									tab.simplexIteration();
																								}
																								
																								updateTable();
																							}
																						});
																						panel_2.add(btnSimplex_1);
																						JButton btnUndoSimplexIteration = new JButton("Undo Simplex Iteration");
																						panel_4.add(btnUndoSimplexIteration);
																						btnUndoSimplexIteration.addActionListener(new ActionListener() {
																							public void actionPerformed(ActionEvent e) {
																								
																								if(history.isEmpty()){
																									return;
																								}
																								
																								tab = history.pop();
																								
																								updateTable();
																								
																							}
																						});
																
																JPanel buttonPanelLeft = new JPanel();
																buttonPanel.add(buttonPanelLeft);
																
																		JPanel panel = new JPanel();
																		buttonPanelLeft.add(panel);
																		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
																		
																				//Add Row Button
																				JButton newRowButton = new JButton("Add Row");
																				panel.add(newRowButton);
																				
																						////////////////////////////// Add Col ////////////////////////
																						//Add Column Button
																						JButton newColButton = new JButton("Add Col");
																						panel.add(newColButton);
																						
																								JPanel panel_1 = new JPanel();
																								buttonPanelLeft.add(panel_1);
																								panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
																								
																										JButton deleteRow = new JButton("Delete Row");
																										panel_1.add(deleteRow);
																										////////////////////////////// Delete Col ////////////////////////
																										JButton btnDeleteCol = new JButton("Delete Col");
																										btnDeleteCol.addActionListener(new ActionListener() {
																											public void actionPerformed(ActionEvent e) {
																												tableModel.setColumnCount(tab.getCols()-1);
																												tab.deleteCol(tab.getCols()-1);
																											}
																										});
																										panel_1.add(btnDeleteCol);
																										
																												JButton btnClear = new JButton("Clear");
																												buttonPanelLeft.add(btnClear);
																												btnClear.addActionListener(new ActionListener() {
																													public void actionPerformed(ActionEvent e) {
																														for(int i = 0; i < tab.getRows(); i++){
																															for(int j = 0; j < tab.getCols(); j++){
																																tableModel.setValueAt("", i, j);
																																tab.set(i, j, 0);
																															}
																														}
																													}
																												});
																										////////////////////////////// Delete Row ////////////////////////
																										deleteRow.addActionListener(new ActionListener() {
																											public void actionPerformed(ActionEvent e) {
																												tableModel.removeRow(tab.getRows()-1);
																												tab.deleteRow(tab.getRows()-1);
																											}
																										});
																						newColButton.addActionListener(new ActionListener() {
																							public void actionPerformed(ActionEvent e) {

																								//tableModel.addColumn("");
																								tableModel.setColumnCount(tab.getCols()+1);
																								tab.addCol();	
																							}
																						});
																						newRowButton.addActionListener(e->{
																								DefaultTableModel model = (DefaultTableModel) table.getModel();
																								model.addRow(new Double[tab.getRows()+1]);
																								tab.addRow();
																								tableModel.fireTableDataChanged();
																						});
		
		/*
		 * Adds TableModelListener to watch for changes made and
		 * update internal table.
		 */
		/*
		 * Clears the table's values by setting them to 0.
		 */
		
		/*
		 * Use the Tableau object's stack to store and load previous
		 * instances.
		 */
	}

	/**
	 * Goes through the tableModel and sets internal representation accordingly.
	 */
	public void updateTable(){
		for(int i = 0; i < tab.getRows(); i++){
			for(int j = 0; j < tab.getCols(); j++){
				tableModel.setValueAt(tab.get(i,j),i, j);
			}
		}
	}
	
	/**
	 * Gets the JTable object which is used to manage spreadsheet activities.
	 * @return JTable object
	 */
	public JTable getTable(){
		return table;
	}

}

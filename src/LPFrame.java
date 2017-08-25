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

/**
 *  This class is mostly auto-generated from Eclipse's GUI maker.
 *  
 * @author Evan Burton
 *
 */

public class LPFrame {

	private JFrame frame;
	private RXTable table;
	private Tableau tab;
	private DefaultTableModel tableModel;
	private JTextField rowField;
	private JTextField colField;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					LPFrame window = new LPFrame();
					window.frame.setVisible(true);
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
		frame = new JFrame();
		frame.setBounds(100, 100, 521, 460);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		tab = new Tableau(3,6);

		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{550, 0};
		gridBagLayout.rowHeights = new int[]{122, 204, 0};
		gridBagLayout.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 1.0, Double.MIN_VALUE};
		frame.getContentPane().setLayout(gridBagLayout);

		JPanel buttonPanel = new JPanel();
		GridBagLayout gbl_buttonPanel = new GridBagLayout();
		gbl_buttonPanel.columnWidths = new int[]{260, 208, 0};
		gbl_buttonPanel.rowHeights = new int[]{39, 39, 0, 0};
		gbl_buttonPanel.columnWeights = new double[]{1.0, 0.0, Double.MIN_VALUE};
		gbl_buttonPanel.rowWeights = new double[]{0.0, 0.0, 1.0, Double.MIN_VALUE};
		buttonPanel.setLayout(gbl_buttonPanel);

		JPanel panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.anchor = GridBagConstraints.NORTH;
		gbc_panel.insets = new Insets(0, 0, 5, 5);
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 0;
		buttonPanel.add(panel, gbc_panel);
		panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

		//Add Row Button
		JButton newRowButton = new JButton("Add Row");
		panel.add(newRowButton);

		////////////////////////////// Add Col ////////////////////////
		//Add Column Button
		JButton newColButton = new JButton("Add Col");
		panel.add(newColButton);
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
		GridBagConstraints gbc_buttonPanel = new GridBagConstraints();
		gbc_buttonPanel.anchor = GridBagConstraints.NORTHWEST;
		gbc_buttonPanel.insets = new Insets(0, 0, 5, 0);
		gbc_buttonPanel.gridx = 0;
		gbc_buttonPanel.gridy = 0;
		frame.getContentPane().add(buttonPanel, gbc_buttonPanel);

		JPanel panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 0, 5, 0);
		gbc_panel_1.anchor = GridBagConstraints.NORTH;
		gbc_panel_1.gridwidth = 2;
		gbc_panel_1.gridx = 1;
		gbc_panel_1.gridy = 0;
		buttonPanel.add(panel_1, gbc_panel_1);
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
		////////////////////////////// Delete Row ////////////////////////
		deleteRow.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tableModel.removeRow(tab.getRows()-1);
				tab.deleteRow(tab.getRows()-1);
			}
		});

		JButton btnClear = new JButton("Clear");
		GridBagConstraints gbc_btnClear = new GridBagConstraints();
		gbc_btnClear.insets = new Insets(0, 0, 5, 5);
		gbc_btnClear.gridx = 0;
		gbc_btnClear.gridy = 1;
		buttonPanel.add(btnClear, gbc_btnClear);
		/*
		 * Clears the table's values by setting them to 0.
		 */
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
		
		/*
		 * Use the Tableau object's stack to store and load previous
		 * instances.
		 */
		JButton btnUndoSimplexIteration = new JButton("Undo Simplex Iteration");
		btnUndoSimplexIteration.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				if(tab.isPreviousEmpty()){
					return;
				}
				
				Tableau t = tab.getLastTableau();
				Stack<Tableau> temp = tab.getStack();
				tab = t;
				t.setStack(temp);
				
				updateTable();
				
			}
		});
		GridBagConstraints gbc_btnUndoSimplexIteration = new GridBagConstraints();
		gbc_btnUndoSimplexIteration.insets = new Insets(0, 0, 5, 5);
		gbc_btnUndoSimplexIteration.gridx = 1;
		gbc_btnUndoSimplexIteration.gridy = 1;
		buttonPanel.add(btnUndoSimplexIteration, gbc_btnUndoSimplexIteration);

		JPanel panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(0, 0, 0, 5);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 0;
		gbc_panel_3.gridy = 2;
		buttonPanel.add(panel_3, gbc_panel_3);
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
				tab.getStack().clear();
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
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.insets = new Insets(0, 0, 0, 5);
		gbc_panel_2.anchor = GridBagConstraints.NORTHWEST;
		gbc_panel_2.gridx = 1;
		gbc_panel_2.gridy = 2;
		buttonPanel.add(panel_2, gbc_panel_2);
		// Simplex Iterator		
		JButton btnSimplex = new JButton("Simplex Iteration");
		btnSimplex.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				tab.simplexIteration();

				updateTable();
			}

		});
		panel_2.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		panel_2.add(btnSimplex);
		// Simplex		
		JButton btnSimplex_1 = new JButton("Simplex");
		btnSimplex_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				tab.runSimplexMethod();
				updateTable();
			}
		});
		panel_2.add(btnSimplex_1);

		JPanel drawingPanel = new JPanel();
		GridBagLayout gbl_drawingPanel = new GridBagLayout();
		gbl_drawingPanel.columnWidths = new int[]{454, 0};
		gbl_drawingPanel.rowHeights = new int[]{204, 0};
		gbl_drawingPanel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_drawingPanel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		drawingPanel.setLayout(gbl_drawingPanel);
		GridBagConstraints gbc_drawingPanel = new GridBagConstraints();
		gbc_drawingPanel.fill = GridBagConstraints.BOTH;
		gbc_drawingPanel.gridx = 0;
		gbc_drawingPanel.gridy = 1;
		frame.getContentPane().add(drawingPanel, gbc_drawingPanel);

		table = new RXTable(3,6);
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
		
		JScrollPane jpane = new JScrollPane(table,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		jpane.setPreferredSize(new Dimension(454, 204));
		jpane.setViewportBorder(new SoftBevelBorder(BevelBorder.LOWERED, null, null, null, null));
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		GridBagConstraints gbc_jpane = new GridBagConstraints();
		gbc_jpane.fill = GridBagConstraints.BOTH;
		gbc_jpane.gridx = 0;
		gbc_jpane.gridy = 0;
		drawingPanel.add(jpane, gbc_jpane);
		table.setFillsViewportHeight(true);
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setRowSelectionAllowed(false);
		table.setCellSelectionEnabled(true);
		table.putClientProperty("terminateEditOnFocusLost", true);
		table.setGridColor(Color.BLUE);
		tableModel = (DefaultTableModel)table.getModel();
		
		/*
		 * Adds TableModelListener to watch for changes made and
		 * update internal table.
		 */
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

					try{
						double value = Double.parseDouble(cellValue);
						tab.set(row, col, value);
					}catch(Exception Exc){
						table.getModel().setValueAt(0, row, col);
					}

				}

			}
		});
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

package simplexer;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.Component;
import java.awt.Toolkit;
import java.text.NumberFormat;
import java.text.ParseException;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;

import org.mariuszgromada.math.mxparser.Expression;

/**
 * Implements a cell editor that uses a formatted text field to edit Integer
 * values.
 */
public class MathEditor extends DefaultCellEditor {
	JFormattedTextField ftf;
	NumberFormat floatrFormat;
	private boolean DEBUG = false;

	public MathEditor() {
		super(new JFormattedTextField());
		ftf = (JFormattedTextField) getComponent();

		ftf.setValue("");
		ftf.setHorizontalAlignment(JTextField.TRAILING);
		ftf.setFocusLostBehavior(JFormattedTextField.PERSIST);

		// React when the user presses Enter while the editor is
		// active. (Tab is handled as specified by
		// JFormattedTextField's focusLostBehavior property.)
		ftf.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "check");
		ftf.getActionMap().put("check", new AbstractAction() {
			public void actionPerformed(ActionEvent e) {
				if (!ftf.isEditValid())
					try { // The text is valid,
						ftf.commitEdit(); // so use it.
						ftf.postActionEvent(); // stop editing
					} catch (java.text.ParseException exc) {}
			}

		});

		ftf.addFocusListener(new FocusAdapter() {
			public void focusGained(final FocusEvent e) {
				ftf.selectAll();
			}
		});

	}

	// Override to invoke setValue on the formatted text field.
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		JFormattedTextField ftf = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row,
				column);
		
		ftf.setValue(value);
		ftf.selectAll();
		return ftf;
	}

	// Override to ensure that the value remains a double
	public Object getCellEditorValue() {
		JFormattedTextField ftf = (JFormattedTextField) getComponent();
		Object o = ftf.getValue();

		if (o == null)
			return "";

		Double val = (new Expression(String.valueOf(o))).calculate();

		if (o.toString().trim().equals("") || val.equals(Double.NaN))
			return 0.0;

		return val;

	}

	// Override to check whether the edit is valid,
	// setting the value if it is and complaining if
	// it isn't. If it's OK for the editor to go
	// away, we need to invoke the superclass's version
	// of this method so that everything gets cleaned up.
	public boolean stopCellEditing() {
		JFormattedTextField ftf = (JFormattedTextField) getComponent();
		if (ftf.isEditValid()) {
			try {
				ftf.commitEdit();
			} catch (java.text.ParseException exc) {
			}

		}
		return super.stopCellEditing();
	}


}
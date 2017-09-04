package simplexer;

import javax.swing.AbstractAction;
import javax.swing.DefaultCellEditor;
import javax.swing.JFormattedTextField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyEvent;
import java.awt.Color;
import java.awt.Component;
import java.text.NumberFormat;
import java.text.ParseException;

import org.mariuszgromada.math.mxparser.Expression;

/**
 * Implements a cell editor that uses a formatted text field to edit Integer
 * values.
 */
public class MathEditor extends DefaultCellEditor {

	private static final long serialVersionUID = 1L;
	JFormattedTextField ftf;
	NumberFormat floatrFormat;

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

	// Call setValue on the formatted text field and selectAll for easy editing
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
		JFormattedTextField ftf = (JFormattedTextField) super.getTableCellEditorComponent(table, value, isSelected, row,
				column);
		
		ftf.setValue(value);
		ftf.selectAll();

		return ftf;
	}

	// Ensure that the value remains a double
	@Override
	public Object getCellEditorValue() {
		JFormattedTextField ftf = (JFormattedTextField) getComponent();

		String text = ftf.getText().trim();
		
		if (text == null)
			return 0.0;

		Double val = (new Expression(text)).calculate();

		if (text.trim().equals("") || val.equals(Double.NaN))
			return 0;

		return val;

	}

	// Check if edit is valid
	@Override
	public boolean stopCellEditing() {
		JFormattedTextField ftf = (JFormattedTextField) getComponent();
		if (ftf.isEditValid()) {
			try {
				ftf.commitEdit();
			} catch (java.text.ParseException exc) {}

		}
		return super.stopCellEditing();
	}

}
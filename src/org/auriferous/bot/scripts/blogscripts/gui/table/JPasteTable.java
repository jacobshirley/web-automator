package org.auriferous.bot.scripts.blogscripts.gui.table;

import java.awt.Toolkit;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.IOException;

import javax.swing.JComponent;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.table.DefaultTableModel;

public class JPasteTable extends JTable implements ActionListener{
	public JPasteTable() {
		this(new DefaultTableModel());
	}
	
	public JPasteTable(DefaultTableModel model) {
		super(model);
		init();
	}
	
	private void init() {
		final KeyStroke stroke = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);

		registerKeyboardAction(this, "Paste", stroke, JComponent.WHEN_FOCUSED);
	}
	
	public void pasteIn() {
		Transferable t = Toolkit.getDefaultToolkit().getSystemClipboard().getContents(this);
		try {
			String data = t.getTransferData(DataFlavor.stringFlavor).toString();
			
			String[] elements = data.split("\n");
			for (String s : elements) {
				String[] parts = s.split("\t");
			
				pasteIn(parts);
			}
		} catch (UnsupportedFlavorException e) {
			System.out.println("Non pastable object.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void pasteIn(String[] parts) throws UnsupportedFlavorException {
		if (parts.length <= getColumnCount()) {
			String[] dest = new String[6];

			System.arraycopy(parts, 0, dest, 0, parts.length);
			
			DefaultTableModel model = (DefaultTableModel) getModel();
			
	        model.addRow(dest);
		} else throw new UnsupportedFlavorException(DataFlavor.stringFlavor);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		pasteIn();
	}
}

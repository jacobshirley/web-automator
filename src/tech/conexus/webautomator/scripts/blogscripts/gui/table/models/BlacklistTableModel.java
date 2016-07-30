package tech.conexus.webautomator.scripts.blogscripts.gui.table.models;

import java.util.List;

import javax.swing.table.DefaultTableModel;

public class BlacklistTableModel extends DefaultTableModel{
	private List<String> blacklist;

	public BlacklistTableModel(List<String> blacklist) {
		super(new Object[] {"URL Regex"}, blacklist.size());
		this.blacklist = blacklist;
	}
	
	@Override
	public void addRow(Object[] rowData) {
		blacklist.add(""+rowData[0]);
		this.fireTableDataChanged();
	}
	
	@Override
	public int getRowCount() {
		if (blacklist == null)
			return super.getRowCount();
		return blacklist.size();
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Object getValueAt(int row, int column) {
		switch (column) {
			case 0: return blacklist.get(row);
		}
		return null;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		switch (columnIndex) {
			case 0: blacklist.set(rowIndex, ""+aValue);
		}
	}
}

package org.auriferous.bot.scripts.blogscripts.gui.table.models;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.DefaultTableModel;

import org.auriferous.bot.scripts.blogscripts.task.Task;

public class TaskTableModel extends DefaultTableModel{
	private List<Task> tasks = new ArrayList<Task>();
	private boolean justShuffler = false;
	public TaskTableModel(List<Task> tasks, boolean justShuffler, boolean previousTasks) {
		super(new String[] {"URL", "Shuffles", "Shuffle Time Interval (seconds)"}, tasks.size());
		this.justShuffler = justShuffler;
		if (!justShuffler) {
			addColumn("Time On Ad (seconds)");
			addColumn("Clicks In Ad");
			addColumn("Facebook Link");
		}
		
		if (previousTasks) {
			if (!justShuffler)
				addColumn("Ad Clicked");
			
			addColumn("Status");
			addColumn("Info");
		}
		
		this.tasks = tasks;
	}
	
	@Override
	public void addRow(Object[] rowData) {
		int shuffles = parseInt(rowData[1]);
		int interval = parseInt(rowData[2]);
		int timeOnAd = parseInt(rowData[3]);
		int clicksInAd = parseInt(rowData[4]);
		
		tasks.add(new Task((String)rowData[0], shuffles, interval, timeOnAd, clicksInAd, (String)rowData[5]));
		this.fireTableDataChanged();
	}

	private int parseInt(Object o) {
		try {
			return Integer.parseInt(o.toString());
		} catch (Exception e) {
			return 0;
		}
	}
	
	@Override
	public int getRowCount() {
		if (tasks == null)
			return super.getRowCount();
		return tasks.size();
	}
	
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return true;
	}

	@Override
	public Object getValueAt(int row, int column) {
		Task task = tasks.get(row);
		switch (column) {
			case 0: return task.url;
			case 1: return task.shuffles;
			case 2: return task.timeInterval;
			case 3: return task.timeOnAd;
			case 4: return task.subClicks;
			case 5: return task.fbLink;
			case 6: if (!justShuffler)
						return task.adClicked;
			case 7: return task.status;
			case 8: return task.info;
		}
		return null;
	}
	
	@Override
	public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
		Task task = tasks.get(rowIndex);
		switch (columnIndex) {
			case 0: task.url = ((String) aValue).replaceAll(" ", "").replaceAll(",", ".").replaceAll("\\(", "").replaceAll("\\)", "").toLowerCase();
					break;
			case 1: task.shuffles = Integer.parseInt((String)aValue);
					break;
			case 2: task.timeInterval = Integer.parseInt((String)aValue);
					break;
			case 3: task.timeOnAd = Integer.parseInt((String)aValue);
					break;
			case 4: task.subClicks = Integer.parseInt((String)aValue);
					break;
			case 5: task.fbLink = (String)aValue;
					break;
			case 6: if (!justShuffler) {
						task.adClicked = (String)aValue;
						break;
					}
			case 7: task.status = Integer.parseInt((String)aValue);
					break;
			case 8: task.info = (String)aValue;
					break;
		}
	}
}

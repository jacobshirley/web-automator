package org.auriferous.bot.scripts.adclicker.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.auriferous.bot.script.library.ScriptManifest;
import org.auriferous.bot.scripts.adclicker.Task;

public class TaskManager extends JFrame{
	private JTable taskTable;
	private List<Task> tasks;
	
	public TaskManager(JFrame parent, List<Task> tasks) {
		super("Task Manager");
		
		this.tasks = tasks;
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.setBorder(new EmptyBorder(10, 10, 10, 10));

		MyTableModel model = new MyTableModel(tasks);
		
        //Create the list and put it in a scroll pane.
		taskTable = new JTable(model);
		taskTable.setMinimumSize(new Dimension(400, 500));

		JScrollPane listScrollPane = new JScrollPane(taskTable);
		
		content.add(listScrollPane, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		
		JButton addTask = new JButton(new ButtonAction("Add", 0));
		JButton removeTask = new JButton(new ButtonAction("Remove", 1));
		
		buttonPanel.add(addTask);
		buttonPanel.add(removeTask);
		content.add(buttonPanel, BorderLayout.SOUTH);
		
		setContentPane(content);
		
		setSize(1000, 500);
		setLocationRelativeTo(parent);
		setVisible(true);
		
		//setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	class ButtonAction extends AbstractAction {
		private int actionID;
		
		public ButtonAction(String name, int actionID) {
			super(name);
			this.actionID = actionID;
		}
		
		@Override
		public void actionPerformed(ActionEvent arg0) {
			switch (actionID) {
				case 0: tasks.add(new Task("http://", -1, -1, -1, -1));
						break;
				case 1: tasks.remove(taskTable.getSelectedRow());
						break;
			}
			taskTable.revalidate();
		}
		
	}
	
	class MyTableModel extends AbstractTableModel {
		private List<Task> tasks;
		private String[] columns = new String[] {"URL", "Shuffles", "Shuffle Time Interval", "Time On Ad", "Clicks In Ad"};

		public MyTableModel(List<Task> tasks) {
			this.tasks = tasks;
		}
		
		@Override
		public int getColumnCount() {
			return columns.length;
		}
		
		@Override
		public String getColumnName(int column) {
			return columns[column];
		}

		@Override
		public int getRowCount() {
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
			}
			return null;
		}
		
		@Override
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			super.setValueAt(aValue, rowIndex, columnIndex);
			
			Task task = tasks.get(rowIndex);
			switch (columnIndex) {
				case 0: task.url = (String) aValue;
						break;
				case 1: task.shuffles = Integer.parseInt((String)aValue);
						break;
				case 2: task.timeInterval = Integer.parseInt((String)aValue);
						break;
				case 3: task.timeOnAd = Integer.parseInt((String)aValue);
						break;
				case 4: task.subClicks = Integer.parseInt((String)aValue);
						break;
			}
		}
	}
}

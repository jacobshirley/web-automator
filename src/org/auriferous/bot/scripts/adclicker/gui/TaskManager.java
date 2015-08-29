package org.auriferous.bot.scripts.adclicker.gui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;

import org.auriferous.bot.config.ConfigurableEntry;
import org.auriferous.bot.scripts.adclicker.Task;
import org.auriferous.bot.scripts.adclicker.TaskConfigEntry;

public class TaskManager extends JFrame{
	private JTable taskTable;
	private List<Task> tasks;
	private ConfigurableEntry taskConfig;
	
	public TaskManager(List<Task> tasks, ConfigurableEntry taskConfig) {
		super("Task Manager");
		
		this.tasks = tasks;
		this.taskConfig = taskConfig;
		
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
		JButton copyTask = new JButton(new ButtonAction("Copy", 2));
		JButton removeTask = new JButton(new ButtonAction("Remove", 1));
		JButton clearTasks = new JButton(new ButtonAction("Clear", 3));
		
		buttonPanel.add(addTask);
		buttonPanel.add(copyTask);
		buttonPanel.add(removeTask);
		buttonPanel.add(clearTasks);
		
		content.add(buttonPanel, BorderLayout.SOUTH);
		
		setContentPane(content);
		
		setSize(1000, 500);
		setLocationRelativeTo(null);
		setVisible(true);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				super.windowClosing(e);
			}
		});
		
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
				case 0: Task t = new Task("http://tk-eaziness.tk/", 1, 0, 0, 2, "");
						tasks.add(t);
						
						taskConfig.getChildren().add(new TaskConfigEntry(t));
						
						break;
				case 1: tasks.remove(taskTable.getSelectedRow());
						break;
				case 2: int selected = taskTable.getSelectedRow();
						if (selected >= 0) {
							Task task = tasks.get(selected).copy();
							tasks.add(task);
						}
						break;
				case 3: tasks.clear();
						break;
			}
			taskTable.revalidate();
		}
		
	}
	
	class MyTableModel extends AbstractTableModel {
		private List<Task> tasks;
		private String[] columns = new String[] {"URL", "Shuffles", "Shuffle Time Interval (seconds)", "Time On Ad (seconds)", "Clicks In Ad", "Facebook Link"};

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
				case 5: return task.fbLink;
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
				case 5: task.fbLink = (String)aValue;
						break;
			}
		}
	}
}

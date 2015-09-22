package org.auriferous.bot.scripts.adclicker.gui;

import java.awt.BorderLayout;
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
import javax.swing.border.EmptyBorder;

import org.auriferous.bot.data.DataEntry;
import org.auriferous.bot.scripts.adclicker.Task;
import org.auriferous.bot.scripts.adclicker.TaskConfigEntry;
import org.auriferous.bot.scripts.adclicker.gui.table.JPasteTable;
import org.auriferous.bot.scripts.adclicker.gui.table.TaskTableModel;

public class TaskManager extends JFrame{
	//http://sadiebrookes.com
	private static final Task DEFAULT_TASK = new Task("http://", 10, 12, 20, 2, "");
	
	private JTable taskTable;
	private List<Task> tasks;
	private DataEntry taskConfig;
	
	public TaskManager(List<Task> tasks, DataEntry taskConfig) {
		super("Task Manager");
		
		this.tasks = tasks;
		this.taskConfig = taskConfig;
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.setBorder(new EmptyBorder(10, 10, 10, 10));
		
        //Create the list and put it in a scroll pane.
		taskTable = new JPasteTable(new TaskTableModel(tasks));
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
				case 0: Task t = DEFAULT_TASK.copy();
						tasks.add(t);

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
}

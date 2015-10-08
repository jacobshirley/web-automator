package org.auriferous.bot.scripts.blogscripts.gui;

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
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.auriferous.bot.scripts.blogscripts.gui.table.JPasteTable;
import org.auriferous.bot.scripts.blogscripts.gui.table.models.TaskTableModel;
import org.auriferous.bot.scripts.blogscripts.task.Task;

public class JTaskManagerFrame extends JFrame{
	//http://sadiebrookes.com
	private static final Task DEFAULT_TASK = new Task("http://", 10, 12, 20, 4, "");
	//private static final Task DEFAULT_TASK = new Task("http://rxquiehm.cf/", 0, 0, 0, 1, "");
	//private static final Task DEFAULT_TASK = new Task("http://interjet.tk", 0, 0, 0, 1, "");
	
	private JTable curTasksTable;
	private JTable prevTasksTable;
	private List<Task> tasks;
	
	private JTabbedPane tabPane;
	
	private List<Task> currentTasks;
	private List<Task> previousTasks;
	
	public JTaskManagerFrame(List<Task> currentTasks, boolean justShufflr, List<Task> previousTasks) {
		super("Task Manager");
		
		this.tasks = currentTasks;
		this.currentTasks = currentTasks;
		this.previousTasks = previousTasks;
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		curTasksTable = new JPasteTable(new TaskTableModel(currentTasks, justShufflr, false));
		curTasksTable.setMinimumSize(new Dimension(400, 500));
		
		prevTasksTable = new JPasteTable(new TaskTableModel(previousTasks, justShufflr, true));
		prevTasksTable.setMinimumSize(new Dimension(400, 500));
		
		tabPane = new JTabbedPane();
		tabPane.addTab("Current Tasks", new JScrollPane(curTasksTable));
		tabPane.addTab("Previous Tasks", new JScrollPane(prevTasksTable));
		
		tabPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				int selectedTab = tabPane.getSelectedIndex();
				switch (selectedTab) {
					case 0:	JTaskManagerFrame.this.tasks = JTaskManagerFrame.this.currentTasks;
							break;
					case 1:	JTaskManagerFrame.this.tasks = JTaskManagerFrame.this.previousTasks;
							break;
				}
				tabPane.revalidate();
				tabPane.repaint();
			}
		});
		
		content.add(tabPane, BorderLayout.CENTER);
		
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
				case 1: int selected = curTasksTable.getSelectedRow();
						if (selected >= 0) {
							tasks.remove(selected);
						}
						break;
				case 2: selected = curTasksTable.getSelectedRow();
						if (selected >= 0) {
							Task task = tasks.get(selected).copy();
							tasks.add(task);
						}
						break;
				case 3: tasks.clear();
						break;
			}
			curTasksTable.revalidate();
			prevTasksTable.revalidate();
		}
	}
}

package tech.conexus.webautomator.scripts.blogscripts.gui;

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
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import tech.conexus.webautomator.scripts.blogscripts.gui.JTaskManagerFrame.ButtonAction;
import tech.conexus.webautomator.scripts.blogscripts.gui.table.JPasteTable;
import tech.conexus.webautomator.scripts.blogscripts.gui.table.models.BlacklistTableModel;
import tech.conexus.webautomator.scripts.blogscripts.gui.table.models.TaskTableModel;
import tech.conexus.webautomator.scripts.blogscripts.task.Task;

public class JBlackListFrame extends JFrame {
	private List<String> blacklist;
	private JPasteTable blacklistTable;

	public JBlackListFrame(List<String> blacklist) {
		super("Blacklist");
		
		this.blacklist = blacklist;
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.setBorder(new EmptyBorder(10, 10, 10, 10));
		
		blacklistTable = new JPasteTable(new BlacklistTableModel(blacklist));
		blacklistTable.setMinimumSize(new Dimension(400, 500));

		content.add(new JScrollPane(blacklistTable), BorderLayout.CENTER);
		
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
		
		setSize(400, 500);
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
				case 0: blacklist.add("");

						break;
				case 1: int selected = blacklistTable.getSelectedRow();
						if (selected >= 0) {
							blacklist.remove(selected);
						}
						break;
				case 2: selected = blacklistTable.getSelectedRow();
						if (selected >= 0) {
							String regex = blacklist.get(selected);
							blacklist.add(regex);
						}
						break;
				case 3: blacklist.clear();
						break;
			}
			blacklistTable.revalidate();
		}
	}
}

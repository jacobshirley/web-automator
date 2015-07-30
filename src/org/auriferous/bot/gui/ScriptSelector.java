package org.auriferous.bot.gui;

import java.awt.BorderLayout;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

public class ScriptSelector extends JFrame{
	public ScriptSelector() {
		super("Script Selector");
		
		JPanel content = new JPanel();
		
		DefaultListModel listModel = new DefaultListModel();
        listModel.addElement("Jane Doe");
        listModel.addElement("John Smith");
        listModel.addElement("Kathy Green");
 
        //Create the list and put it in a scroll pane.
        JList list = new JList(listModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        //list.addListSelectionListener(this);
        list.setVisibleRowCount(5);

		JScrollPane listScrollPane = new JScrollPane(list);
		
		content.add(listScrollPane, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		
		JButton runScript = new JButton("Run");
		
		buttonPanel.add(runScript);
		content.add(buttonPanel);
		
		setContentPane(content);
		
		setSize(500, 500);
		setVisible(true);
	}
}

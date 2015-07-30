package org.auriferous.bot.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;

public class ScriptSelector extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	private DefaultListModel listModel = new DefaultListModel();
	private JList list = null;
	private LinkedList<ScriptSelectorListener> scriptSelListeners = new LinkedList<ScriptSelectorListener>();
	
	public ScriptSelector(Frame parent) {
		super("Script Selector");
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.setBorder(new EmptyBorder(10, 10, 10, 10));
		
        listModel.addElement("Click ads");
 
        //Create the list and put it in a scroll pane.
        list = new JList(listModel);
        list.setMinimumSize(new Dimension(400, 500));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        //list.addListSelectionListener(this);
        //list.setVisibleRowCount(5);

		JScrollPane listScrollPane = new JScrollPane(list);
		
		content.add(listScrollPane, BorderLayout.CENTER);
		
		JPanel buttonPanel = new JPanel();
		
		JButton runScript = new JButton("Run");
		
		runScript.addActionListener(this);
		
		buttonPanel.add(runScript);
		content.add(buttonPanel, BorderLayout.SOUTH);
		
		setContentPane(content);
		
		setSize(500, 500);
		setLocationRelativeTo(parent);
		setVisible(true);
		//pack();
		//setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		for (ScriptSelectorListener listener : scriptSelListeners)
			listener.onScriptSelected((String)list.getSelectedValue());
	}
	
	public void addScriptSelectorListener(ScriptSelectorListener listener) {
		this.scriptSelListeners.add(listener);
	}
	
	public void removeScriptSelectorListener(ScriptSelectorListener listener) {
		this.scriptSelListeners.remove(listener);
	}
}

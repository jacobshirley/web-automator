package org.auriferous.bot.gui.scriptselector;

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
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;

import org.auriferous.bot.Bot;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.ScriptExecutor;
import org.auriferous.bot.script.library.ScriptLibrary;
import org.auriferous.bot.script.library.ScriptManifest;
import org.auriferous.bot.script.loader.ScriptLoader;
import org.auriferous.bot.scripts.OnAdTask;
import org.auriferous.bot.tabs.TabPaintListener;

public class ScriptSelector extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;
	
	private DefaultListModel listModel = new DefaultListModel();
	private JList list = null;
	private LinkedList<ScriptSelectorListener> scriptSelListeners = new LinkedList<ScriptSelectorListener>();

	private Bot bot;
	
	public ScriptSelector(Frame parent, Bot bot) {
		super("Script Selector");
		
		this.bot = bot;
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.setBorder(new EmptyBorder(10, 10, 10, 10));

        //Create the list and put it in a scroll pane.
        list = new JList(listModel);
        list.setMinimumSize(new Dimension(400, 500));
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0);
        
        for (ScriptManifest script : bot.getScriptLibrary().getScripts()) {
        	listModel.addElement(script.getMainClass() + " | "+script.getVersion());
        }
        
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
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		this.dispose();
		
		ScriptContext context = new ScriptContext(bot);
		ScriptLoader loader = bot.getScriptLoader();
		
		Script linkClicker;
		try {
			linkClicker = loader.loadScript("1");
			ScriptExecutor bundle = new ScriptExecutor(context, new Script[] {linkClicker});
			
			bundle.processScripts();//*/
			
			for (ScriptSelectorListener listener : scriptSelListeners)
				listener.onScriptSelected((String)list.getSelectedValue());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void addScriptSelectorListener(ScriptSelectorListener listener) {
		this.scriptSelListeners.add(listener);
	}
	
	public void removeScriptSelectorListener(ScriptSelectorListener listener) {
		this.scriptSelListeners.remove(listener);
	}
}

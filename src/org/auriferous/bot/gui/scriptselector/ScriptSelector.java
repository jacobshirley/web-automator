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
import javax.swing.JTree;
import javax.swing.ListSelectionModel;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import org.auriferous.bot.Bot;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.script.executor.ScriptExecutor;
import org.auriferous.bot.script.library.ScriptLibrary;
import org.auriferous.bot.script.library.ScriptManifest;
import org.auriferous.bot.script.loader.ScriptLoader;
import org.auriferous.bot.scripts.OnAdTask;
import org.auriferous.bot.tabs.TabPaintListener;

public class ScriptSelector extends JFrame implements ActionListener, TreeSelectionListener{
	private static final long serialVersionUID = 1L;
	
	private JTree tree;
	private LinkedList<ScriptSelectorListener> scriptSelListeners = new LinkedList<ScriptSelectorListener>();

	private Bot bot;
	
	private ScriptLibrary library;
	
	public ScriptSelector(Frame parent, Bot bot) {
		super("Script Selector");
		
		this.bot = bot;
		this.library = bot.getScriptLibrary();
		
		JPanel content = new JPanel();
		content.setLayout(new BorderLayout());
		content.setBorder(new EmptyBorder(10, 10, 10, 10));

		DefaultMutableTreeNode top = new DefaultMutableTreeNode(this.library.getName());
		
		DefaultTreeModel model = new DefaultTreeModel(top);
		
        //Create the list and put it in a scroll pane.
		tree = new JTree(model);
        tree.setMinimumSize(new Dimension(400, 500));
        
        for (ScriptManifest script : bot.getScriptLibrary().getScripts()) {
        	top.add(new ScriptTreeNode(script));
        }
        
        for (int i = 0; i < tree.getRowCount(); i++) {
            tree.expandRow(i);
        }
        
        tree.addTreeSelectionListener(this);
        //list.addListSelectionListener(this);
        //list.setVisibleRowCount(5);

		JScrollPane listScrollPane = new JScrollPane(tree);
		
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
		
		ScriptLoader loader = bot.getScriptLoader();
		
		Script script;
		try {
			script = loader.loadScript(lastSelected.manifest);
			ScriptExecutor executor = bot.getScriptExecutor();
			executor.addScript(script);
			
			for (ScriptSelectorListener listener : scriptSelListeners) {
				listener.onScriptSelected(script);
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
	}
	
	public void addScriptSelectorListener(ScriptSelectorListener listener) {
		this.scriptSelListeners.add(listener);
	}
	
	public void removeScriptSelectorListener(ScriptSelectorListener listener) {
		this.scriptSelListeners.remove(listener);
	}
	
	private ScriptTreeNode lastSelected = null;
	
	@Override
	public void valueChanged(TreeSelectionEvent e) {
		Object o = tree.getLastSelectedPathComponent();
		if (o instanceof ScriptTreeNode)
			lastSelected = (ScriptTreeNode)o;
	}
	
	class ScriptTreeNode extends DefaultMutableTreeNode {
		private ScriptManifest manifest;
		public ScriptTreeNode(ScriptManifest manifest) {
			super(manifest.getName() + " | " + manifest.getVersion());
			
			this.manifest = manifest;
		}
	}
}

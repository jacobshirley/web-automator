package tech.conexus.webautomator.gui.swing.script;

import javax.swing.JMenu;

public interface JScriptGui {
	public boolean shouldCreateMenu();
	public void onJMenuCreated(JMenu menu);
}

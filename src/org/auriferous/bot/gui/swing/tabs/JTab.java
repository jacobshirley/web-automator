package org.auriferous.bot.gui.swing.tabs;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import org.auriferous.bot.Utils;
import org.auriferous.bot.gui.swing.JOverlayComponent;
import org.auriferous.bot.tabs.Tab;
import org.auriferous.bot.tabs.TabListener;
import com.teamdev.jxbrowser.chromium.DialogParams;
import com.teamdev.jxbrowser.chromium.swing.DefaultDialogHandler;

public class JTab extends JPanel implements TabListener {
	private JTabComponent tabComponent;
	private Tab tab;
	private JTabBar tabBar;
	private JTabView tabView;
	
	public JTab(JOverlayComponent paintComp, JTabBar tabBar, Tab tab) {
		super(new BorderLayout());
		
		this.tabBar = tabBar;
		this.tabComponent = new JTabComponent(tabBar);
		
		this.tab = tab;
		this.tab.addTabListener(this);
		
		this.tabView = new JTabView(paintComp, this.tab);
		this.tab.setTabView(tabView);
		
		this.tab.getBrowserWindow().setDialogHandler(new DefaultDialogHandler(tabView) {
            @Override
            public void onAlert(DialogParams params) {
                String title = "From: "+params.getURL();
                String message = params.getMessage();
                Utils.alert(title, message);
            }
        });
		
		this.add(tabView);
	}
	
	public JTabComponent getTabComponent() {
		return tabComponent;
	}
	
	public Tab getTab() {
		return tab;
	}
	
	public JTabView getJTabView() {
		return tabView;
	}

	@Override
	public void onTitleChange(String newTitle) {
		int id = tabBar.indexOfComponent(this);
		
		tabBar.setTitleAt(id, newTitle);
		Component comp = tabBar.getTabComponentAt(id);
		if (comp != null)
			comp.revalidate();
	}

	@Override
	public void onTabUpdating() {
	}

	@Override
	public void onTabReloaded() {
	}
}

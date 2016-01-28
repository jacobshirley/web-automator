package org.auriferous.webautomator.gui.swing.tabs;

import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.JPanel;

import org.auriferous.webautomator.Utils;
import org.auriferous.webautomator.gui.swing.JConstants;
import org.auriferous.webautomator.gui.swing.JOverlayComponent;
import org.auriferous.webautomator.shared.tabs.Tab;
import org.auriferous.webautomator.shared.tabs.TabListener;
import org.auriferous.webautomator.shared.tabs.view.PaintListener;

import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.DialogParams;
import com.teamdev.jxbrowser.chromium.swing.DefaultDialogHandler;

public class JTab extends JPanel implements TabListener {
	
	private JTabComponent tabComponent;
	private Tab tab;
	private JTabBar tabBar;
	private JTabView tabView;
	private JOverlayComponent overlayComp;
	
	public JTab(JOverlayComponent overlayComp, JTabBar tabBar, Tab tab) {
		super(new BorderLayout());
		
		this.overlayComp = overlayComp;
		this.tabBar = tabBar;
		this.tabComponent = new JTabComponent(tabBar);
		
		this.tab = tab;
		this.tab.addTabListener(this);
		
		this.tabView = new JTabView(overlayComp, this.tab);
		this.tab.setTabView(tabView);
		
		/*this.tab.getBrowserInstance().setDialogHandler(new DefaultDialogHandler(tabView) {
            @Override
            public void onAlert(DialogParams params) {
                String title = "From: "+params.getURL();
                String message = params.getMessage();
                Utils.alert(title, message);
            }
        });*/
		
		add(tabView, BorderLayout.CENTER);
	}
	
	public JTabComponent getTabComponent() {
		return tabComponent;
	}
	
	public Tab getTabInstance() {
		return tab;
	}
	
	public JTabView getJTabView() {
		return tabView;
	}

	@Override
	public void onTitleChange(String newTitle) {
		int id = tabBar.indexOfComponent(this);
		
		if (newTitle.length() > JConstants.MAX_TAB_TITLE_LENGTH)
			newTitle = newTitle.substring(0, JConstants.MAX_TAB_TITLE_LENGTH).trim()+"...";
		
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

	@Override
	public void onTabBrowserChanged(Browser browser) {
		JTabView old = this.tabView;
		
		this.tabView = new JTabView(overlayComp, this.tab);
		
		for (PaintListener l : old.getPaintListeners())
			this.tabView.addPaintListener(l);

		this.tab.setTabView(tabView);
		
		this.remove(old);
		this.add(tabView);
	}
}

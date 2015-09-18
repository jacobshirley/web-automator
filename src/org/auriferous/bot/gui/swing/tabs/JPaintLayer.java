package org.auriferous.bot.gui.swing.tabs;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

public class JPaintLayer extends JComponent{
	@Override
	public void paintComponent(Graphics arg0) {
		
		System.out.println("painting");
		super.paintComponent(arg0);
		
		arg0.setColor(Color.red);
		arg0.drawRect(100, 100, 100, 100);
		
	}
}

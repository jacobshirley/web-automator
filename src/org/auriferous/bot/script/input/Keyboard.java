package org.auriferous.bot.script.input;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import org.auriferous.bot.Utils;

public class Keyboard extends Input {
	
	public Keyboard(Component target) {
		super(target);
	}
	
	public void type(int c, int time, int mods) {
		pressKey(c);
		
		KeyEvent event = new KeyEvent(target, KeyEvent.KEY_TYPED, System.currentTimeMillis(), mods, KeyEvent.VK_UNDEFINED, (char)c, KeyEvent.KEY_LOCATION_UNKNOWN);
		target.dispatchEvent(event);
	
		Utils.wait(time+Utils.random(20));
	
		releaseKey(c);
	}
	
	public void pressKey(int c) {
		KeyEvent event = new KeyEvent(target, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, c, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD);
		target.dispatchEvent(event);
	}
	
	public void releaseKey(int c) {
		KeyEvent event = new KeyEvent(target, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), 0, c, KeyEvent.CHAR_UNDEFINED, KeyEvent.KEY_LOCATION_STANDARD);
		target.dispatchEvent(event);
	}
}

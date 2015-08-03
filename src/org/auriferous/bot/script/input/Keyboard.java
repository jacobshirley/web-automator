package org.auriferous.bot.script.input;

import java.awt.Component;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;

import org.auriferous.bot.Utils;

public class Keyboard extends Input {
	private static final String SHIFT_KEYS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ¬!\"£$%^&*()_+{}:@~<>?|€";
	private static final int DEFAULT_MOUSE_TIME = 100;
	private static final int DEFAULT_KEY_TIME = 200;
	
	public Keyboard(Component target) {
		super(target);
	}
	
	public final void type(String message) {
		for (char c : message.toCharArray())
			type(c);
	}
	
	public void type(int c, int time) {
		type(c, time, 0);
	}
	
	public final void type(int c) {
		int mods = 0;
		if (SHIFT_KEYS.contains(""+(char)c)) {
			type(KeyEvent.VK_SHIFT, DEFAULT_KEY_TIME, InputEvent.SHIFT_DOWN_MASK);
			mods |= InputEvent.SHIFT_DOWN_MASK;
		}
		type(c, DEFAULT_KEY_TIME, mods);
	}
	
	public void type(int c, int time, int mods) {
		int key = KeyEvent.getExtendedKeyCodeForChar(c);

		KeyEvent event = new KeyEvent(target, KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, key, (char)c, KeyEvent.KEY_LOCATION_STANDARD);
		target.dispatchEvent(event);
		
		event = new KeyEvent(target, KeyEvent.KEY_TYPED, System.currentTimeMillis(), mods, KeyEvent.VK_UNDEFINED, (char)c, KeyEvent.KEY_LOCATION_UNKNOWN);
		target.dispatchEvent(event);
	
		Utils.wait(time+Utils.random(20));
	
		event = new KeyEvent(target, KeyEvent.KEY_RELEASED, System.currentTimeMillis(), mods, key, (char)c, KeyEvent.KEY_LOCATION_STANDARD);
		target.dispatchEvent(event);
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

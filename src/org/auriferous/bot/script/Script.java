package org.auriferous.bot.script;

public abstract class Script extends ScriptMethods{
	public static final int STATE_RUNNING = 0;
	public static final int STATE_EXIT_SUCCESS = 1;
	
	public Script(ScriptContext context) {
		super(context);
	}
	
	public abstract int tick();
	
	public abstract void onStart();
	public abstract void onPause();
	public abstract void onTerminate();
}

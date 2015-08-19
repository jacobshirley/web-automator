package org.auriferous.bot.script.executor;

import org.auriferous.bot.script.Script;

public interface ScriptExecutionListener {
	public void onRunScript(Script script);
	
	public void onScriptFinished(Script script);
	
	public void onTerminateScript(Script script);

	public void onPauseScript(Script script);
	
	public void onResumeScript(Script script);
}

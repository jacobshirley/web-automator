package org.auriferous.webautomator.internal.executor;

import org.auriferous.webautomator.script.Script;

public interface ScriptExecutionListener {
	public void onRunScript(Script script);
	
	public void onScriptFinished(Script script);
	
	public void onTerminateScript(Script script);

	public void onPauseScript(Script script);
	
	public void onResumeScript(Script script);
}

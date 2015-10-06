package org.auriferous.bot.scripts.shufflr;

import java.util.ArrayList;
import java.util.List;

import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.scripts.adclicker.gui.JTaskManagerFrame;
import org.auriferous.bot.scripts.adclicker.task.Task;
import org.auriferous.bot.shared.data.library.ScriptManifest;

public class Shufflr extends Script {
	private List<Task> tasks = new ArrayList<Task>();
	private List<Task> previousTasks = new ArrayList<Task>();
	public Shufflr(ScriptManifest manifest, ScriptContext context) {
		super(manifest, context);
	}
	
	@Override
	public void onStart() {
		new JTaskManagerFrame(tasks, previousTasks);
	}
}

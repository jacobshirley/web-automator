package org.auriferous.bot.internal.loader;

import java.util.HashMap;
import java.util.Map;

import org.auriferous.bot.Bot;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;
import org.auriferous.bot.shared.data.library.ScriptManifest;

public class CachedScriptLoader extends ScriptLoaderImpl {
	private static final Map<ScriptManifest, Script> CACHED_SCRIPTS = new HashMap<ScriptManifest, Script>();
	
	public CachedScriptLoader(Bot bot, ScriptContext context) {
		super(bot, context);
	}

	@Override
	public Script loadScript(ScriptManifest manifest) throws ClassNotFoundException {
		Script script = CACHED_SCRIPTS.get(manifest);
		if (script != null) {
			return script;
		} else {
			script = super.loadScript(manifest);
			CACHED_SCRIPTS.put(manifest, script);
			return script;
		}
	}
}

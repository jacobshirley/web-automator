package tech.conexus.webautomator.internal.loader;

import java.util.HashMap;
import java.util.Map;

import tech.conexus.webautomator.Bot;
import tech.conexus.webautomator.script.Script;
import tech.conexus.webautomator.script.ScriptContext;
import tech.conexus.webautomator.shared.data.library.ScriptManifest;

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

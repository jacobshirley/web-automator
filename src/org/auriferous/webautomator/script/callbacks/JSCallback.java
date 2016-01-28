package org.auriferous.webautomator.script.callbacks;

import com.teamdev.jxbrowser.chromium.JSValue;

public interface JSCallback {
	public boolean onResult(JSValue value);
}

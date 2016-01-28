package org.auriferous.webautomator.shared.data.config;

import org.auriferous.webautomator.shared.data.DataEntry;

public interface Configurable {
	public void load(DataEntry configuration);
	public void save(DataEntry configuration);
}

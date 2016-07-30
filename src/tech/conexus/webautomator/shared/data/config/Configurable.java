package tech.conexus.webautomator.shared.data.config;

import tech.conexus.webautomator.shared.data.DataEntry;

public interface Configurable {
	public void load(DataEntry configuration);
	public void save(DataEntry configuration);
}

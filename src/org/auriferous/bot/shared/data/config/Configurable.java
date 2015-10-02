package org.auriferous.bot.shared.data.config;

import org.auriferous.bot.shared.data.DataEntry;

public interface Configurable {
	public void load(DataEntry configuration);
	public void save(DataEntry configuration);
}

package org.auriferous.bot.data.config;

import org.auriferous.bot.data.DataEntry;

public interface Configurable {
	public void load(DataEntry configuration);
	public void save(DataEntry configuration);
}

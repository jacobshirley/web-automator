package org.auriferous.bot.shared.data;

public class RootEntry extends DataEntry {

	public RootEntry(Object key) {
		super(key);
	}

	@Override
	public void setKey(Object key) {
		throw new UnsupportedOperationException("Do not use this for root elements");
	}
	
	@Override
	public void setValue(Object value) {
		throw new UnsupportedOperationException("Do not use this for root elements");
	}
}

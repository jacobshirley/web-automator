package org.adclicker.bot.tasks;

public interface BotTask {
	public static final int EXIT_FAILURE = 0;
	public static final int EXIT_SUCCESS = 1;
	
	public int getPriority();
	
	public double getPercentageComplete();
	
	public int perform();
}

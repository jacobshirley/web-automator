package org.auriferous.bot;

import org.auriferous.bot.scripts.blogscripts.states.adclicker.ClickAdState;
import org.auriferous.bot.shared.data.history.HistoryConfig;

public class Main {
    public static void main(String[] args) {
    	new Bot(args, true);
    	//new HistoryConfig();
    }
}
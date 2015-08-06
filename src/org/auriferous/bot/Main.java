package org.auriferous.bot;

import java.util.ArrayList;

import org.auriferous.bot.scripts.adclicker.Task;
import org.auriferous.bot.scripts.adclicker.gui.TaskManager;

public class Main {
    public static void main(String[] args) {
    	Bot bot = new Bot(args, true);
    	//new TaskManager(null, new ArrayList<Task>());
    }
}
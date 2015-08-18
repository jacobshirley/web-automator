package org.auriferous.bot;

import java.io.File;

import org.auriferous.bot.config.Configurable;
import org.auriferous.bot.config.xml.XMLConfigurableFile;

public class Main {
    public static void main(String[] args) {
    	new Bot(args, true);
    }
}
package org.auriferous.bot;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class Utils {
	public static final void wait(int ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public static String getText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                    connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) 
            response.append(inputLine);

        in.close();

        return response.toString();
    }
	
	public static double random(double min, double max) {
		return min+(Math.random()*(max-min));
	}
	
	public static int random(double n) {
		return (int)(n*Math.random());
	}
	
	public static boolean inRange(double x, double min, double max) {
		return x >= min && x <= max;
	}
	
	public static Object getRandomObject(Object[] arr) {
		return arr[(int) Math.floor(Math.random()*arr.length)];
	}
}

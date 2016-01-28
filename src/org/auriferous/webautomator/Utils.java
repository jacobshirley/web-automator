package org.auriferous.webautomator;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import javax.swing.JOptionPane;

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
	
	public static String getBaseURL(String urlString) {
		try {
			URL url = new URL(urlString);

			String base = url.getProtocol() + "://" + url.getHost();
			
			return base;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static int random(int min, int max) {
		return (int)(min+(Math.random()*(max-min)));
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
	
	public static <T> T getRandomObject(T[] arr) {
		if (arr == null || arr.length == 0)
			return null;
		return arr[(int) Math.floor(Math.random()*arr.length)];
	}
	
	public static void alert(String message) {
		alert("ALERT", message);
	}
	
	public static void alert(String title, String message) {
		JOptionPane.showMessageDialog(null, message, title,
                JOptionPane.PLAIN_MESSAGE);
	}
}

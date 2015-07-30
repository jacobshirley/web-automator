package org.auriferous.bot.scripts;

import java.awt.Point;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import org.auriferous.bot.Utils;
import org.auriferous.bot.script.ElementRect;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;

import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;

public class ClickAdTask extends Script {
	private static final int STAGE_SHUFFLES = 0;
	private static final int STAGE_URL = 1;
	private static final int STAGE_WAIT_ON_AD = 2;
	private static final int STAGE_DONE = 3;
	
	private String url;
	private double timeOnAd;	
	private int adClicks;
	private int shuffles;
	private int timeInterval;
	private int subClicks;
	
	private int curAdClick = 0;
	private int curShuffles = 0;
	private int curSubClick = 0;
	
	private int successCode = -1;
	private int taskStage = 1;
	
	private ScriptContext ctx;
	
	public ClickAdTask(ScriptContext ctx) {
		super(ctx);
		
		createAddTaskDialog();
	}

	private void createAddTaskDialog() {
		JTextField url = new JTextField();
		JTextField timeOnAd = new JTextField();
		JTextField shuffles = new JTextField();
		JTextField shuffleInterval = new JTextField();
		JTextField adClicks = new JTextField();
		JTextField adShuffles = new JTextField();
		JTextField subClicks = new JTextField();
		
		Object[] message = {
		    "URL:", url,
		    "Time on ad:", timeOnAd,
		    "Shuffles:", shuffles,
		    "Shuffle Interval:", shuffleInterval,
		    "Ad Clicks:", adClicks,
		    "Ad Shuffles: ", adShuffles,
		    "Sub Clicks:", subClicks,
		};		

		int option = JOptionPane.showConfirmDialog(null, message, "Add Task", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			this.url = url.getText();
			this.timeOnAd = Double.parseDouble(timeOnAd.getText());
			this.adClicks = Integer.parseInt(adClicks.getText());
			this.shuffles = Integer.parseInt(shuffles.getText());
			this.timeInterval = Integer.parseInt(shuffleInterval.getText());
			this.subClicks = Integer.parseInt(subClicks.getText());
		} else {
		    System.out.println("Cancelled");
		}
	}
	
	@Override
    public void onFinishLoadingFrame(FinishLoadingEvent event) {
		long frameID = event.getFrameId();
		
		ElementRect adElement = null;
		
		if (taskStage == STAGE_SHUFFLES) {
			if (event.isMainFrame()) {
				if (curShuffles < shuffles) {
					Utils.wait(this.timeInterval*1000);
					curShuffles++;
					if (this.url.endsWith("/"))
						this.browser.loadURL(this.url+"random");
					else
						this.browser.loadURL(this.url+"/random");
				} else
					taskStage++;
			}
		} 
		if (taskStage == STAGE_URL) {
	        if (event.isMainFrame()) {
	        	injectJQuery(frameID);
	
	        	adElement = getElementRect(frameID, "$('.adsbygoogle').first()");
	        	
	        	Point p = adElement.getRandomPointInRect();
	        	
	        	System.out.println("Clicking at "+p.x+", "+p.y);
	        	
	        	mouse(p.x, p.y);
	        	
	        	taskStage++;
	        	curAdClick++;
	        }
		} 
		if (taskStage == STAGE_WAIT_ON_AD) {
			System.out.println("Now waiting on ad");
			Utils.wait((int) timeOnAd);
			if (curAdClick < adClicks) {
				taskStage = 0;
			}
			taskStage++;
		} 
		if (taskStage == STAGE_DONE) {
			successCode = STATE_EXIT_SUCCESS;
			this.browser.removeLoadListener(this);
		}
    }

	@Override
	public int tick() {
		return STATE_RUNNING;
	}

	@Override
	public void onStart() {
		this.browser.addLoadListener(this);
		this.browser.loadURL(this.url);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		
	}
}
package org.auriferous.bot.tasks;

import java.awt.Point;
import org.auriferous.bot.Utils;
import org.auriferous.bot.script.ElementRect;
import org.auriferous.bot.script.Script;
import org.auriferous.bot.script.ScriptContext;

import com.teamdev.jxbrowser.chromium.events.FinishLoadingEvent;

public class ClickAdTask extends Script implements BotTask {
	
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
	
	public ClickAdTask(ScriptContext ctx, String url, double timeOnAd, int adClicks, int shuffles, int timeInterval, int subClicks) {
		super(ctx);
		
		this.url = url;
		this.timeOnAd = timeOnAd;
		this.adClicks = adClicks;
		this.shuffles = shuffles;
		this.timeInterval = timeInterval;
		this.subClicks = subClicks;
	}
	
	@Override
	public int getPriority() {
		return 0;
	}

	@Override
	public int perform() {
		this.browser.addLoadListener(this);
		this.browser.loadURL(this.url);
		
		while (successCode == -1) {
			Thread.yield();
		}
		
		return successCode;
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
			successCode = BotTask.EXIT_SUCCESS;
			this.browser.removeLoadListener(this);
		}
    }

	@Override
	public double getPercentageComplete() {
		return (curAdClick/adClicks)*100.0;
	}
	
	
}
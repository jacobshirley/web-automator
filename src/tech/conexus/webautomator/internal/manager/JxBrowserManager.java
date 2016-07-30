package tech.conexus.webautomator.internal.manager;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Observable;
import java.util.Observer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class JxBrowserManager {
	private static final String JX_VERSION = "6.6";
	private static final String JX_URL = "http://cloud.teamdev.com/downloads/jxbrowser/jxbrowser-"+JX_VERSION+"-cross-desktop-win_mac_linux.zip";
	private static final String TEMP_FILE = "temp/jxbrowser.zip";
	
	private ZipFile zip;
	
	public JxBrowserManager() {
	}
	
	public boolean downloadLatest(FinishedHandler finishedHandler) {
		File tempFile = new File(TEMP_FILE);

		if (!new File("lib/jxbrowser.jar").exists()) {
			if (!tempFile.getParentFile().exists())
				tempFile.getParentFile().mkdirs();
			
			System.out.println("Starting JxBrowser download.");
			try {
				final Download dl = new Download(new URL(JX_URL), 
												 new RandomAccessFile(TEMP_FILE, "rw"));
	
				//System.out.println(dl.getDownloaded());
				dl.addObserver(new Observer() {
					@Override
					public void update(Observable o, Object arg) {
						if (dl.getStatus() == Download.DOWNLOADING) {
							System.out.println(dl.STATUSES[dl.getStatus()]+": "+dl.getProgress()+"%");
						}
						
						if (dl.getStatus() == Download.COMPLETE) {
							System.out.println("Finished download. Extracting...");
							
							extractZipFile("lib/jxbrowser-6.6.jar", "lib/jxbrowser.jar");
							extractZipFile("lib/jxbrowser-win-6.6.jar", "lib/jxbrowser-win.jar");
							
							System.out.println("Extraction complete. Please restart the bot.");
							
							finishedHandler.onFinish();
						}
					}
				});
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			finishedHandler.onFinish();
		}
		
		return false;
	}
	
	private void extractZipFile(String file, String output) {
		try {
			if (zip == null)
				zip = new ZipFile(TEMP_FILE);
			
			InputStream zis = zip.getInputStream(zip.getEntry(file));
				
			File newFile = new File(output);

			FileOutputStream fos = new FileOutputStream(newFile);             
			
			int len;
			byte[] buffer = new byte[1024];
			while ((len = zis.read(buffer)) > 0) {
				fos.write(buffer, 0, len);
			}
				
			fos.close(); 
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public interface FinishedHandler {
		public void onFinish();
	}
}

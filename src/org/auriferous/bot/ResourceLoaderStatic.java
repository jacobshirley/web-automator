package org.auriferous.bot;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class ResourceLoaderStatic {
	private static final Map<String, String> STRING_RESOURCES = new HashMap<String, String>();
	public static String loadResourceAsString(String path, boolean cache) throws IOException {
		String str = STRING_RESOURCES.get(path);
		if (str == null) {
			ClassLoader classloader = Thread.currentThread().getContextClassLoader();
			InputStream in = classloader.getResourceAsStream(path);
	
			DataInputStream dis = new DataInputStream(in);
			byte[] bytes = new byte[dis.available()];
			
			dis.readFully(bytes, 0, bytes.length);
			
			str = new String(bytes);
			
			if (cache) {
				STRING_RESOURCES.put(path, str);
			}
		}
		
		return str;
	}
}

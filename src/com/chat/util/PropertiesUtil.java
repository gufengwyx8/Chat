package com.chat.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {
	public static Map<String, String> getPropertiesMap(String path) {
		Properties p = new Properties();
		Map<String, String> map = new HashMap<String, String>();
		try {
			p.load(ClassLoader.getSystemResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		} catch (NullPointerException e) {
			try {
				p.load(new FileInputStream(path));
			} catch (FileNotFoundException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		for (Object key : p.keySet()) {
			map.put(key.toString(), p.getProperty(key.toString()));
		}
		return map;
	}
}

package uk.ac.glasgow.scclippy.uicomponents.main;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

public class PersistentProperties extends Properties {
	
	private String path;
	
	public PersistentProperties(String path){
		super();
		this.path = path;
		
	}
	
	@Override
	public Object setProperty(String key, String value){
		Object result = super.setProperty(key, value);
		try {
			this.store(new FileOutputStream(path), "Source code clippy configuration.");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}
}
package uk.ac.glasgow.scclippy.plugin.search;

public class SearchException extends Exception {
	public SearchException(Throwable cause){
		super(cause);
	}
	
	public SearchException(String message){
		super(message);
	}
	
	public SearchException(String message, Throwable cause){
		super(message, cause);
	}


}

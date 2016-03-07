package uk.ac.glasgow.scclippy.lucene;

import static java.lang.String.format;

import org.json.simple.JSONObject;

public class StackoverflowEntry {
	
	private static final  String HTML_TEXT_TEMPLATE = 
		"%s <br/>> %s (score: %d + ; <a href=\"http://stackoverflow.com/questions/%s\">link</a>)";

	public final String id;
	public final String parentId;
	public final Integer score;
	public final String body;
	
	public StackoverflowEntry(String id, String parentId, int score, String body){
		this.id = id;
		this.parentId = parentId;
		this.score = score;
		this.body = body;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject toJSONObject () {
		JSONObject record = new JSONObject ();
		record.put("id", id);
		record.put("body", body);
		record.put("score",score);
		return record;
	}

	public String toHTMLText() {
		String entryTypeText = parentId != null?"Answer":"Question";
				
		String htmlFormattedText = 
			format(
				HTML_TEXT_TEMPLATE, body, entryTypeText, score, id);
		return htmlFormattedText;
	}
}
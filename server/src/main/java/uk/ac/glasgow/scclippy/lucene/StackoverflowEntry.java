package uk.ac.glasgow.scclippy.lucene;

import org.json.simple.JSONObject;

public class StackoverflowEntry {

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
		record.put("parentid", parentId);
		record.put("body", body);
		record.put("score",score);
		return record;
	}
}
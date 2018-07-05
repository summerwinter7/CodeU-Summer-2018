package codeu.model.data;

import java.time.Instant;

public interface Activity extends Comparable<Activity>{
	
	public Instant getCreationTime();
	
	public String getDisplayText();
	
	public int compareTo(Activity a);
}
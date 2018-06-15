package codeu.model.data;

import java.time.Instant;

public interface Activity extends Comparable<Activity>{
	
	public Instant getCreationTime();
	
	public String getMessage();
	
	public int compareTo(Activity a);
}
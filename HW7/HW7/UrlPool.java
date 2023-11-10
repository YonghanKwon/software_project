import java.util.*;
import java.util.concurrent.*;

public class UrlPool {
    // List of pending urls to be crawled
    BlockingQueue<UrlDepthPair> pending_urls;
    // List of all the urls we've seen -- this forms the result
    List<UrlDepthPair> seen_urls;
    // Maximum crawl depth
    int maxDepth;
    // Count of waiting threads
    int waits;
    // Constructor
    
	Boolean foundUrl=Boolean.valueOf(false);
	
    public UrlPool(int maxDepth) {
        this.maxDepth = maxDepth;
        pending_urls = new LinkedBlockingQueue<>();
        seen_urls = new LinkedList<>();
        waits = 0;
    }
    
    // Get the next UrlDepthPair to crawl
    public UrlDepthPair getNextPair() {
        waitsPlus();
        UrlDepthPair pair;
        try {
            pair = pending_urls.take(); 
        } catch (InterruptedException e) {
            pair = null;
        }
        waitsMinus();
        return pair;
    }

    public synchronized void waitsPlus() {
    	waits++;
    }
    public synchronized void waitsMinus() {
    	waits--;
    }
    
    // Add a new pair to the pool if the depth is
    // less than the maximum depth to be considered.
    public synchronized void addPair(UrlDepthPair pair) {
    	
    	containCheck(pair);
    	
    	if(!foundUrl) {
    	seen_urls.add(pair);
    	if (pair.getDepth() < maxDepth)
            try {
            		pending_urls.put(pair); 
            	} catch (InterruptedException e) {}
    	}
    	else
        	foundUrl=Boolean.valueOf(false);
    }
    
    public synchronized void containCheck(UrlDepthPair pair){
    	for(UrlDepthPair i:seen_urls) {
    		if(i.url.equals(pair.url))
    			foundUrl=Boolean.valueOf(true);
    	}
    }
    
    // Get the number of waiting threads
    public synchronized int getWaitCount() {
        return waits;
    }
    
    // Get all the urls seen
    public List<UrlDepthPair> getSeenUrls() {
        return seen_urls;
    }
}

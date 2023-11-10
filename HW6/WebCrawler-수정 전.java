import java.net.*;
import java.util.*;
import java.io.*;
import javax.net.ssl.*;
import java.security.cert.X509Certificate;
import java.security.SecureRandom;

public class WebCrawler {

	public static void main(String[] args) {
		
		int depth = 0;

        if (args.length != 2) {
            System.out.println("usage: java Crawler <URL> <depth>");
            System.exit(1);
        }

        else {
            try {
                depth = Integer.parseInt(args[1]);
            }
            catch (NumberFormatException nfe) {
                System.out.println("usage: java Crawler <URL> <depth>");
                System.exit(1);
            }
        }
        
        List<UrlDepthPair> pendingUrls = new LinkedList<>();
        
        List<UrlDepthPair> processedUrls= new LinkedList<>();
        
        List<String> seenUrls = new ArrayList<>();
        
        UrlDepthPair presentUrlDP;
        try {
        presentUrlDP= new UrlDepthPair(args[0],0);
        pendingUrls.add(presentUrlDP);
       
        }catch (MalformedURLException e) {
            System.err.println("MalformedURLException: " + e.getMessage());
            System.exit(1);
        }

        
        while(pendingUrls.size() != 0) {
        	UrlDepthPair repUrlDP = ((LinkedList<UrlDepthPair>) pendingUrls).removeFirst();
            seenUrls.add(repUrlDP.getURLString());
        	processedUrls.add(repUrlDP);
        	String line;
        	try {
        	URL ur1 = new URL(repUrlDP.getURLString());
        	HttpURLConnection conn=(HttpURLConnection) ur1.openConnection();
        	
        	try {
        		TrustManager[] trustAllCerts = 
        		new TrustManager[]{ new X509TrustManager() {

        		public X509Certificate[] getAcceptedIssuers() { return null; }

        		public void checkClientTrusted(X509Certificate[] certs, String authType) {}
        		public void checkServerTrusted(X509Certificate[] certs, String authType) {} 
        		}};
        		 
        		SSLContext sc = SSLContext.getInstance("SSL");
        		 
        		sc.init(null, trustAllCerts, new SecureRandom());
        		 
        		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        		 
        		} catch (Exception e) { System.exit(1); }
        	conn.setConnectTimeout(1000);
        	conn.setReadTimeout(3000);
        	
        	BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        	
        	while((line=in.readLine())!=null) {
            	int beginindex = 0;
            	int endindex = 0;        	
            	int index = 0;
            	int presentDepth=repUrlDP.getDepth();
            	

            	
            	while(true) {
                	String Urlbegin = "<a href=\"";
                	String Urlend = "\"";
                	
            		index=line.indexOf(Urlbegin,index);
            		if(index == -1)
            			break;
            		index+=Urlbegin.length();
            		beginindex=index;
            		
            		endindex=line.indexOf(Urlend,index);
            		index=endindex;
            		
		if(endindex<beginindex){
			break;
		}

            		String newUrl = line.substring(beginindex,endindex);
            		if(seenUrls.contains(newUrl)) {
            			break;
            			}
            		else {
            		try {
            			if(presentDepth < depth) {
            				UrlDepthPair newUrlDP = new UrlDepthPair(newUrl,presentDepth+1);
            				pendingUrls.add(newUrlDP);
            				}
            			}catch (MalformedURLException e) {
            				break;
            			}
            		}
            	}

            
        	}
        	in.close();
        	conn.disconnect();
        	}catch  (IOException except) {
                break;
        	}
        }
        
        for(UrlDepthPair i : processedUrls) {
        	System.out.println(i.toString());
        }

	}
}

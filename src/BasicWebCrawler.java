import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;

public class BasicWebCrawler {
	private static final String POST = "post";
	private static final String COMMENT = "comment";
	
    public static void main(String[] args) throws IOException {
    		Logger logger = Logger.getLogger(BasicWebCrawler.class);
    		int cntPost = 0;
    		int cntCommon = 0;
    		
    		BufferedWriter bwPost = null;
    		FileWriter fwPost = null;
    		BufferedWriter bwComment = null;
    		FileWriter fwComment = null;
    		
    		File filePost = new File(POST);
    		File fileComment = new File(COMMENT);

    		// if file doesnt exists, then create it
    		if (!filePost.exists()) {
    			filePost.createNewFile();
    		}
    		if (!fileComment.exists()) {
    			fileComment.createNewFile();
    		}

    		for(int i = 26000000; i < 27000000; i++) {
    			String URL = "http://www.discuss.com.hk/viewthread.php?tid=" + i;
	    		try {
	    			Document document = Jsoup.connect(URL).get();

	    			if(document.title() != "") {
	    				int firstIndexOf = 0;
	    				firstIndexOf = document.title().indexOf(" - ");	
	    				if(firstIndexOf > 0) {
		    				String title = document.title().substring(0, firstIndexOf).trim();
			    			if(!title.equals("香港討論區 Discuss.com.hk")) {					
			    				Elements elements = document.select("span[id~=^postorig_[0-9]+$]:not(:has(div))");
				    			
			    	    			fwComment = new FileWriter(fileComment.getAbsoluteFile(), true);
			    	    			bwComment = new BufferedWriter(fwComment);
			    	    			
			    	    			int cntElement = 0;
			    	    			LinkedHashSet hashSetComment = new LinkedHashSet();
			    	    			for(Element element : elements) {
			    	    				if(element.text().length() < 50 && element.text().length() > 0) {
			    	    					hashSetComment.add(element.text());
			    	    				}
			    	    			}
			    	    			
			    	    			for(Object h : hashSetComment) {
			    	    				cntElement++;
			    	    				bwComment.write(i + "-" + cntElement + " " + h.toString());
			    	    				bwComment.newLine();	
			    	    			}
				    			
				    			if(cntElement > 0) {
					    			// true = append file
				    				fwPost = new FileWriter(filePost.getAbsoluteFile(), true);
				    	    			bwPost = new BufferedWriter(fwPost);
				    	    			
				    	    			bwPost.write(i + " " + title);
				    	    			bwPost.newLine();
						    		cntPost++;
						    		cntCommon = cntCommon + cntElement;
					    			logger.info("post = " + i + ", cntPost = " + cntPost + ", cntComment = " + cntCommon);				    			
				    			}

				    			if(bwPost != null)
				    				bwPost.close();
				    			if(fwPost != null)
				    				fwPost.close();
				    			if(bwComment != null)
				    				bwComment.close();
				    			if(fwComment != null)
				    				fwComment.close();
			    			}
		    			}
	    			}
	    		}catch(IOException e) {
	    			System.err.println("For '" + URL + "': " + e.getMessage());
	    			logger.error(e);
	    		}
    		}
    }
}
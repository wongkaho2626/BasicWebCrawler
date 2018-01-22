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

public class BasicWebCrawler {
	
    public static void main(String[] args) {
    		Logger logger = Logger.getLogger(BasicWebCrawler.class);
    		int cntPost = 0;
    		int cntCommon = 0;
    		
    		JSONArray posts = new JSONArray();
    		JSONArray comments = new JSONArray();
    		
    		for(int i = 27000000; i < 27100000; i++) {
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
				    			
				    			JSONObject post = new JSONObject();
				    			post.put("id", i);
				    			post.put("title", title);
				    			
				    			JSONObject comment = new JSONObject();
				    			comment.put("id", i);
				    			JSONArray content = new JSONArray();
				    			for(Element element : elements) {
				    				if(element.text().length() < 50) {
				    					content.put(element.text());
				    				}
				    			}
				    			comment.put("content", content);
				    			
				    			if(content.length() != 0) {
				    				posts.put(post);
				    				comments.put(comment);
				    				
				    				cntPost++;
				    				cntCommon = cntCommon + content.length();
				    				logger.info("post = " + i + ", cntPost = " + cntPost + ", cntComment = " + cntCommon);
				    			}
			    			}
		    			}
	    			}
	    			writerToJson(posts, comments);
	    		}catch(IOException e) {
	    			System.err.println("For '" + URL + "': " + e.getMessage());
	    			logger.error(e);
	    		}
    		}
    }
    
    public static void writerToJson (JSONArray post, JSONArray common) throws IOException {
    		File filePost = new File("post.json");
    		File fileCommon = new File("comment.json");
    		if (!filePost.exists()) {
    			filePost.createNewFile();
    		}
    		if (!fileCommon.exists()) {
    			fileCommon.createNewFile();
    		}
    		FileWriter fwp = new FileWriter(filePost);
    		FileWriter fwc = new FileWriter(fileCommon);
    		
    		BufferedWriter writerPost = new BufferedWriter(fwp);
    		BufferedWriter writerCommon = new BufferedWriter(fwc);

    		writerPost.write(post.toString());
    		writerPost.close();
		
    		writerCommon.write(common.toString());
    		writerCommon.close();
	}
}
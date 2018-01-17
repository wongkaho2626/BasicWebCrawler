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
    		Elements elements;
    		String title, category;
    		int firstIndexOf, secondIndexOf;
    		Document document;    		
    		String URL;
    		int cntPost = 0;
    		int cntCommon = 0;
    		
    		JSONArray posts = new JSONArray();
    		JSONArray commons = new JSONArray();
    		
    		for(int i = 27000000; i < 28000000; i++) {
    			URL = "http://www.discuss.com.hk/viewthread.php?tid=" + i;
    			System.out.println(URL);
	    		try {
	    			document = Jsoup.connect(URL).get();
	    			if(document.title() != "") {
		    			firstIndexOf = document.title().indexOf("-");
		    			secondIndexOf = document.title().indexOf('-', document.title().indexOf('-')+1);
		    			
		    			title = document.title().substring(0, firstIndexOf).trim();
		    			System.out.println(title);
		    			if(!title.equals("香港討論區 Discuss.com.hk")) {
		    				category = document.title().substring(firstIndexOf+1, secondIndexOf).trim();
		    			
		
			    			elements = document.select("span[id~=^postorig_[0-9]+$]:not(:has(div))");
			    			
			    			JSONObject post = new JSONObject();
			    			post.put("id", i);
			    			post.put("title", title);
			    			post.put("category", category);
			    			
			    			JSONObject common = new JSONObject();
			    			common.put("id", i);
			    			JSONArray content = new JSONArray();
			    			for(Element element : elements) {
			    				if(element.text().length() < 50) {
			    					content.put(element.text());
			    				}
			    			}
			    			common.put("content", content);
			    			
			    			if(content.length() != 0) {
			    				posts.put(post);
			    				commons.put(common);
			    				
			    				cntPost++;
			    				cntCommon = cntCommon + content.length();
				    			logger.info("The total number of post:" + cntPost);
				    			logger.info("The total number of common:" + cntCommon);
				    			logger.info("The number of common " + content.length() + " in post " + i);
			    			}
		    			}
	    			}
	    			writerToJson(posts, commons);
	    		}catch(IOException e) {
	    			System.err.println("For '" + URL + "': " + e.getMessage());
	    			logger.error(e);
	    		}
    		}
    }
    
    public static void writerToJson (JSONArray post, JSONArray common) throws IOException {
    		File filePost = new File("post.json");
    		File fileCommon = new File("common.json");
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
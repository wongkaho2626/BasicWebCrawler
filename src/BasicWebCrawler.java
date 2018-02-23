import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedHashSet;

public class BasicWebCrawler extends Thread{
	private static final String POST = "post";
	private static final String COMMENT = "comment";
	int NUMBER;
	
	public BasicWebCrawler (int NUMBER) {
		this.NUMBER = NUMBER;
	}
	
	public void run() {
    		System.setProperty("my.log", "C:\\BasicWebCrawler2\\log4j-application" + NUMBER + ".log");
    		Logger logger = Logger.getLogger(BasicWebCrawler.class);
    		int cntPost = 0;
    		int cntCommon = 0;
    		
    		BufferedWriter bwPost = null;
    		FileWriter fwPost = null;
    		BufferedWriter bwComment = null;
    		FileWriter fwComment = null;
    		
    		File filePost = new File(POST + NUMBER/10000);
    		File fileComment = new File(COMMENT + NUMBER/10000);

    		// if file doesnt exists, then create it
    		if (!filePost.exists()) {
    			try {
					filePost.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		if (!fileComment.exists()) {
    			try {
					fileComment.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
    		}
    		
    		for(int i = NUMBER; i < NUMBER + 1000000; i++) {
    			String URL = "http://www.discuss.com.hk/viewthread.php?tid=" + i;
	    		try {
	    			Document document = Jsoup.connect(URL).get();

	    			if(document.title() != "") {
	    				int firstIndexOf = 0;
	    				firstIndexOf = document.title().indexOf(" - ");	
	    				if(firstIndexOf > 0) {
		    				String title = document.title().substring(0, firstIndexOf).trim();
			    			if(!title.equals("香港討論區 Discuss.com.hk")) {
			    				Elements elements = document.select("span[id~=^postorig_[0-9]+$]");
			    				Elements filterElements = document.select("span[id~=^postorig_[0-9]+$] > div");
			    	    			fwComment = new FileWriter(fileComment.getAbsoluteFile(), true);
			    	    			bwComment = new BufferedWriter(fwComment);
			    	    			
			    	    			int cntElement = 0;
			    	    			LinkedHashSet hashSetComment = new LinkedHashSet();
			    	    			for(Element element : elements) {
			    	    				String txt = filterWord(element.text(), filterElements);
			    	    				if(txt.length() < 500 && txt.length() > 0) {
			    	    					hashSetComment.add(txt);
			    	    				}
			    	    			}
			    	    			
			    	    			for(Object h : hashSetComment) {
			    	    				cntElement++;
			    	    				String num = null;
			    	    				for(int j = 0; j < elements.size(); j++) {
			    	    					if(filterWord(elements.get(j).text(), filterElements).equals(h.toString())) {
				    	    					Elements postnumber = document.select("strong[id~=^" + elements.get(j).id().replace("postorig_", "postnum_") + "]:not(:has(div))");
			    	    						num = postnumber.get(0).text().replace("#", "");
			    	    					}
			    	    				}
			    	    				bwComment.write(i + "-" + num + " " + h.toString());
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
    
    private static String filterWord(String input, Elements filterElements) {
		String output = input;
		for(Element e : filterElements) {
			output = output.replace(e.text(), "");
		}
		if(input.contains(" [ 本帖最後由")) {
			int firstindexof = input.indexOf(" [ 本帖最後由");
			output = input.substring(0, firstindexof);
		}
		return output.trim();
	}
}
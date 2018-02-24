import java.util.ArrayList;
import java.util.List;

public class Main {
	public static void main(String [] args) throws InterruptedException {
		int cnt = 10000000;
		for(int j = 0; j < 5; j++) {
			
			List <BasicWebCrawler> BasicWebCrawlerList = new ArrayList<BasicWebCrawler>();
			for(int i = cnt; i < cnt + 1000000; i+=10000) {
				BasicWebCrawler basicWebCrawler = new BasicWebCrawler(i);
				BasicWebCrawlerList.add(basicWebCrawler);
				basicWebCrawler.start();
			}

			for(BasicWebCrawler w : BasicWebCrawlerList)
				w.join();
			
			cnt+= 1000000;
			System.out.println("Done for " + cnt);
		}
	}
}

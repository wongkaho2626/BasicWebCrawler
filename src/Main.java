public class Main {
	public static void main(String [] args) {
		 for(int i = 15000000; i < 15500000; i+=10000) {
			 BasicWebCrawler basicWebCrawler = new BasicWebCrawler(i);
			 basicWebCrawler.start();
		 }
	}
}

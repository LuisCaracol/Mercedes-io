package biz.netcentric.util;

import javax.script.Bindings;

import org.jsoup.nodes.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GenericUtils {

	private static Logger log = LoggerFactory.getLogger(GenericUtils.class);
	
	public static void printBindings(Bindings binding, String type)  {
		log.info("=====================================================================================");
		log.info(type + " BINDINGS");
		log.info("=====================================================================================");
		binding.forEach((a, b) -> log.info("Key: " + a + " Value : " + b));
	}
	
	public static void printHtml(Document doc, String type){
		log.info("=====================================================================================");
		log.info(type);
		log.info("=====================================================================================");
		log.info(doc.html());
	}
}

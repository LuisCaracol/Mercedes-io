package biz.netcentric.handlers;

import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TailHandler implements BaseHandler {

	private Logger log = LoggerFactory.getLogger(TailHandler.class);
	
	public void handle(Node node, int depth) {
		log.info("Exiting tag: " + node.nodeName() + " Depth: " + depth);
	}
	
}

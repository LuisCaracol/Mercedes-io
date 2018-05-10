package biz.netcentric.handlers.nodes;

import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptEngine;

import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.netcentric.handlers.BaseHandler;

public class TextHandler implements NodeHandler, BaseHandler{

	private Logger log = LoggerFactory.getLogger(ElementHandler.class);
	private ScriptEngine engine; 
	
	public TextHandler(ScriptEngine engine){
        this.engine = engine;
    }
	
	@Override
	public void execute(Node node, int depth, List<Node> trash, Bindings session) {
		TextNode textNode = (TextNode)node;
        log.info("Entering tag: " + textNode.nodeName() + " Depth: " + depth);
        log.info("TextNode: " + textNode.text());
        //Replace this textNodes text with the result of the expression evaluation from the Nashorn engine
        textNode.text(expression(textNode.text(), engine, log));
	}

}

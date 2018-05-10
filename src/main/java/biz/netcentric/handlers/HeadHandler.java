package biz.netcentric.handlers;

import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptEngine;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import biz.netcentric.handlers.nodes.ElementHandler;
import biz.netcentric.handlers.nodes.TextHandler;

public class HeadHandler implements BaseHandler
{

    private ElementHandler elementHandler;
    private TextHandler textHandler;
    
    public HeadHandler(ScriptEngine engine){
        elementHandler = new ElementHandler(engine);
        textHandler = new TextHandler(engine);
    }
    
    public void handle(Node node, int depth, List<Node> trash, Bindings session) {
    	//Can do with Reflection to avoid using if /else if code!!!
        if(node instanceof Element){
            elementHandler.execute(node, depth, trash, session);
        }
        else if(node instanceof TextNode){
        	textHandler.execute(node, depth, trash, session);
        }
        else{
            //Unsupported Types, DO NOTHING
        }
    }
}

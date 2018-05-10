package biz.netcentric;

import java.util.ArrayList;
import java.util.List;

import javax.script.Bindings;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.http.HttpServletRequest;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.NodeVisitor;

import biz.netcentric.handlers.HeadHandler;
import biz.netcentric.handlers.TailHandler;
import biz.netcentric.util.GenericUtils;

public class SlightlyDispatcher {

	private ScriptEngine engine;

	public SlightlyDispatcher() {
		engine = new ScriptEngineManager().getEngineByName("nashorn");
	}

	public String parse(String html, HttpServletRequest request) throws ScriptException {

		// parse html
		Document doc = Jsoup.parse(html);
		// Needed for importClass definitions
		engine.eval("load(\"nashorn:mozilla_compat.js\");");
		engine.getContext();
		// Get and print the global scoped bindings in this ScriptEngine
		Bindings global = engine.getBindings(ScriptContext.GLOBAL_SCOPE);
		Bindings session = engine.getBindings(ScriptContext.ENGINE_SCOPE);
		session.put("request", request);

		GenericUtils.printHtml(doc, "ORIGINAL");
		
		List<Node> trash = new ArrayList<Node>();
		HeadHandler headHandler = new HeadHandler(engine);
		TailHandler tailHandler = new TailHandler();
		// Navigate over the doc
		doc.traverse(new NodeVisitor() {
			public void head(Node node, int depth) {
				headHandler.handle(node, depth, trash, session);
			}

			public void tail(Node node, int depth) {
				tailHandler.handle(node, depth);
			}
		});

		// Remove all nodes that are in the trash
		trash.forEach(node -> {
			node.remove();
		});
		
		GenericUtils.printHtml(doc, "TRANSFORMED");
		// Print engine scopes
		engine.getContext().getScopes().forEach(System.out::println);
		// Print global bindings
		GenericUtils.printBindings(global, "GLOBAL");
		// Print session bindings
		GenericUtils.printBindings(session, "SESSION");
		session.forEach((k, v) -> System.out.println("Key: " + k + " Value : " + v));
		return doc.html();
	}
}

package biz.netcentric.handlers.nodes;

import java.util.List;
import java.util.Map;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptException;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import biz.netcentric.handlers.BaseHandler;
import biz.netcentric.util.GenericUtils;

public class ElementHandler implements NodeHandler, BaseHandler {

	private Logger log = LoggerFactory.getLogger(ElementHandler.class);
	private ScriptEngine engine;

	public ElementHandler(ScriptEngine engine) {
		this.engine = engine;
	}

	@Override
	public void execute(Node node, int depth, List<Node> trash, Bindings session) {
		Element element = (Element) node;
		String tagName = element.tagName();
		log.info("Entering tag: " + tagName + " Depth: " + depth);
		Attributes attributes = element.attributes();
		log.info("Attributes: " + attributes);
		String data = element.data();
		Map<String, String> dataset = element.dataset();
		log.info("Data: " + data + " Dataset: " + dataset);

		// look for script elements execute & evaluate against ScriptEngine
		if (tagName.equalsIgnoreCase("script")) {
			try {
				engine.eval(data);
			} catch (ScriptException e) {
				e.printStackTrace();
			}
		}
		// transform all attributes according to el-expressions
		attributes.forEach(attr -> {
			attr.setValue(expression(attr.getValue(), engine, log));
		});

		// check for data-if attribute, if it doesn't resolve to true,
		// then trash element and its children
		try {
			String condition = dataset.get("if");
			if (condition != null && !condition.isEmpty()) {
				// What does this if resolve to? evaluate in nashorn
				Boolean dataIf = (Boolean) engine.eval(condition);
				// can not remove while traversing, so we must trash and remove
				// later
				// this also allows for data-if and data-for to coexist
				if (!dataIf) {
					trash.add(element);
				}
			}
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		// check for data-for attribute
		// ala the example data-for-child="person.children"
		// the child variable is implicitly mapped to the items in the
		// collection
		// referenced in the data for value, i.e. for child in children
		// notice the code below, neither child nor children is mention,
		// implicit mapping is taking place
		dataset.forEach((k, v) -> {
			if (k.startsWith("for-")) {
				String implicitItem = k.substring(4, k.length()); // get the
																	// suffix of
																	// for-*
				log.info("Implicit item: " + implicitItem + " Collection: " + v);
				try {
					@SuppressWarnings("rawtypes")
					List items = (List) engine.eval(v);
					// Create the dummy element from a clone and remove its data
					// specific properties
					// to prevent subsequent calls on the traverse
					Element dummy = element.clone();
					dummy.attributes().dataset().clear();
					element.html("");
					// For each item in items, set the implicitItem variable and
					// then evaluate the expression
					items.forEach(item -> {
						Element temp = dummy.clone();
						session.put(implicitItem, item);
						temp.html(expression(temp.text(), engine, log)); // Evaluate
																			// the
																			// expression
						element.appendChild(temp);// Add another element after
													// this one
					});
				} catch (ScriptException e) {
					e.printStackTrace();
				}
			}
			// Local Vars
			else if (k.startsWith("local-")) {
				String implicitItem = k.substring(6, k.length());
				log.info("Implicit item: " + implicitItem + " Collection: " + v);
				try {
					Element dummy = element.clone();
					dummy.attributes().dataset().clear();
					element.html("");
					Element temp = dummy.clone();
					Object item = engine.eval(v);
					GenericUtils.printBindings(session, "SESSION");
					//Retrieve the current variable value if any
					Object storedVal = session.get(implicitItem);
					//Set new value for eval in expression
					session.put(implicitItem, item);
					GenericUtils.printBindings(session, "SESSION");
					temp.html(expression(temp.text(), engine, log));
					element.appendChild(temp);
					//Put back the original value to be used on other nodes
					session.put(implicitItem, storedVal);
					GenericUtils.printBindings(session, "SESSION");
				} catch (ScriptException e) {
					e.printStackTrace();
				}
			}
		});

	}

}

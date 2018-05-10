package biz.netcentric.handlers.nodes;

import java.util.List;

import javax.script.Bindings;

import org.jsoup.nodes.Node;

public interface NodeHandler {

	void execute(Node node,int depth, List<Node> trash, Bindings session);
}

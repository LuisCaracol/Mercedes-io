package biz.netcentric;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/")
public class SlightlyServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html");
		response.setBufferSize(8192);
		PrintWriter out = response.getWriter();
		String html = "";
		// Get the file contents
		URI uri = null;
		String uriRequest = request.getRequestURI();
		try {
			uri = getServletContext().getResource(uriRequest).toURI();
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (NullPointerException npException) {
			out.println("Unknown Template");
		}

		try (Stream<String> stream = Files.lines(Paths.get(uri))) {
			html = stream.reduce("", (a, b) -> a + b);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// Create instance, parse and write result to OutputStream
		try {
			out.println(new SlightlyDispatcher().parse(html, request));
		} catch (ScriptException e) {
			e.printStackTrace();
		}
		// Close OutputStream
		out.close();

	}

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}

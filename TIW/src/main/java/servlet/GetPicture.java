package servlet;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@WebServlet("/GetPicture/*")
public class GetPicture extends HttpServlet {
	private static final long serialVersionUID = 1L;
	String folderPath = "";

	public void init() throws ServletException {
		folderPath = getServletContext().getInitParameter("imgFolder");
	}


	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String name = request.getParameter("image");

		if (name == null || name.equals("/")) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing file name!");
			return;
		}

		String filename = URLDecoder.decode(name.substring(0), "UTF-8");
		File file = new File(folderPath+ filename);
		System.out.println(filename);

		if (!file.exists() || file.isDirectory()) {
			file = new File(folderPath + "placeholder.jpg");
		}

		response.setHeader("Content-Type", getServletContext().getMimeType(filename));
		response.setHeader("Content-Length", String.valueOf(file.length()));
				response.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");

		Files.copy(file.toPath(), response.getOutputStream());
	}
}
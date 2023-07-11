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



@WebServlet(urlPatterns = "/GetPicture/*")
public class GetPicture extends HttpServlet {
    /**
     * Took inspiration from <a href="https://stackoverflow.com/a/1812356">Stackoverflow</a>.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if(req.getPathInfo() == null){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "You should specify a path");
            return;
        }

        
        String folderPath = getServletContext().getInitParameter("imgFolder");
        // Get the path of the file from the request
        String filename = URLDecoder.decode(req.getPathInfo().substring(1), "UTF-8");

        // Get the file from the filesystem
       // File file = FileManager.getFile(getServletContext(), filename);
        File file = new File(folderPath + filename);

        System.out.println(filename);
        /*
         * String filename = URLDecoder.decode(name.substring(0), "UTF-8");
		File file = new File(folderPath+ filename);
		

         * */
        
        
        // Check if the file exists
        if(!file.exists() || !file.isFile()){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Image not found");
            return;
        }

        // Set the response headers
        resp.setHeader("Content-Type", getServletContext().getMimeType(filename));
        resp.setHeader("Content-Length", String.valueOf(file.length()));
        resp.setHeader("Content-Disposition", "inline; filename=\"" + file.getName() + "\"");
        

        // Copy the file to the response
        Files.copy(file.toPath(), resp.getOutputStream());
    }
}
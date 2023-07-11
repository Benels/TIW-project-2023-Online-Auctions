package servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import org.apache.commons.lang.StringEscapeUtils;


import beans.User;
import dao.ItemDAO;
import other.ConnectionHandler;

@MultipartConfig
@WebServlet("/NewItem")
public class NewItem extends HttpServlet{
    private static final long serialVersionUID = 1L;
    private Connection connection;

    
    public NewItem() {
    	super();	
    }
    
    
    
    @Override
    public void init() throws UnavailableException {
		connection = ConnectionHandler.getConnection(getServletContext());
    }
    
    

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }
    

    @Override
    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
    }
    
    


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    
    	String name;
    	String description;
    	String priceString;
    	
    	
		
		try {
			name = StringEscapeUtils.escapeJava(request.getParameter("name"));
			description = request.getParameter("description");
			priceString = request.getParameter("price");
			
		} catch(NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Valori parametri mancanti o incorretti");
			return;
		}
		
    	System.out.print(name + description + priceString);

    	float price;
    	String imgName;

        ServletContext servletContext = getServletContext();
        
        try {

    		Part filePart = request.getPart("file"); 

    		System.out.println(filePart);
    		if (filePart == null || filePart.getSize() <= 0) {
    			System.out.println("null img");
    			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while saving file -1");
    			return;
    		}
    		
    		System.out.println("not null img");

    		String contentType = filePart.getContentType();
    		System.out.println("Type " + contentType);

    		if (!contentType.startsWith("image")) {
    			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while saving file -2");

    			return;
    		}

    		imgName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
    		System.out.println("Filename: " + imgName);

    		String folderPath = "/Users/frabe/Desktop/imgstiw/";

    		String outputPath = folderPath + imgName; //folderPath inizialized in init
    		System.out.println("Output path: " + outputPath);

    		File file = new File(outputPath);

    		try (InputStream fileContent = filePart.getInputStream()) {
    			
    			Files.copy(fileContent, file.toPath());
    			System.out.println("File saved correctly!");

    		} catch (Exception f) {
    			f.printStackTrace();
    			return;
    		}
        	
        	if(imgName==null) {
    			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error while saving file -3");
    			return;
        	}
        }catch(Exception g) {
        	System.out.println("error with img upload");
        	response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Error with img upload");
        	return;
        }
        
        
        //image finished
        
    	try {
    	
	        if (name == null || name.isEmpty() 
	        		|| description == null || description.isEmpty()
	        		|| imgName == null || imgName.isEmpty()
	                || priceString == null || priceString.isEmpty()) 
	        {
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing Request Parameters");
				return;
	        }
	        price=Float.parseFloat(priceString);

	        if(price<=0) {
	            throw new NumberFormatException();
	        }
	       
    	}catch(NullPointerException x) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parsing problems");
			return;
    		
    	}catch(NumberFormatException f) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Parsing problems -2");
			return;    	
		}
    	
    	
        User user = (User) request.getSession().getAttribute("user");
    	int userid = user.getUserID();
    	ItemDAO itemDAO = new ItemDAO(connection);
    	
    	try{
    		itemDAO.newItem(userid, name, description, imgName, price);
    	}catch (SQLException k) {
    		response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			response.getWriter().println("Couldn't submit the item");
			return;
    	}
    	
    	System.out.println("item submitted");
    	
		//Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();

		String json = "";// = gson.toJson("");
		response.setStatus(HttpServletResponse.SC_OK);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);


    }


    
}
    
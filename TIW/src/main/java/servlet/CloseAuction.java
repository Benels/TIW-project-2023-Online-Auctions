package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import beans.Auction;
import beans.Item;
import beans.User;
import dao.AuctionDAO;
import dao.ItemDAO;
import other.ConnectionHandler;

@WebServlet("/CloseAuction")
public class CloseAuction extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public CloseAuction(){super();}

    
    
    @Override
    public void init() throws UnavailableException {
		connection = ConnectionHandler.getConnection(getServletContext());
		ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(getServletContext());
		templateResolver.setTemplateMode(TemplateMode.HTML);
		this.templateEngine = new TemplateEngine();
		this.templateEngine.setTemplateResolver(templateResolver);
		templateResolver.setSuffix(".html");
    }
    

    @Override
    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    
    	
        
        ServletContext servletContext = getServletContext();
        final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
     
        boolean resp;
        
        String auctionID = request.getParameter("auctionid");
        try {
        	int auctionid = Integer.parseInt(auctionID);
        	User user = (User) request.getSession().getAttribute("user");
            int userid = user.getUserID();
            
            AuctionDAO auctionDAO = new AuctionDAO(connection);
            ItemDAO itemDAO = new ItemDAO(connection);
            
            
            
            Auction check = new Auction();
            try {
            	check = auctionDAO.getAuction(auctionid);
            }catch(SQLException sql){
	            sql.printStackTrace();
	            throw new UnavailableException("Error executing query");
	        }
            
            List<Item> soldItems = new ArrayList<>();
            
            
            LocalDateTime now = LocalDateTime.now();
            if(check != null) {
            	if(check.getCreatorID()==userid && check.getAuctionID()==auctionid && check.getIsOpen()==true && check.getDeadlineDate().isBefore(now)){
            		//ok
            		
                    try {
                    	soldItems = itemDAO.findItems(auctionid);
                    }catch(SQLException sql){
        	            sql.printStackTrace();
        	            throw new UnavailableException("Error executing query");
        	        }
            		
                    try{
                    	auctionDAO.closeAuction(auctionid);
                    }catch (SQLException sqle){
                    	sqle.printStackTrace();
                    	throw new UnavailableException("Issue from database");
                    }
                    
                    for (Item it : soldItems) {
        	        	try {
        	        		itemDAO.setToSold(it.itemID());
        	            }catch (SQLException sqle){
        	                sqle.printStackTrace();
        	                throw new UnavailableException("Issue from database");
        	            }
                    }
                    System.out.println("closed");
            		
            	} else {
            		//error
            		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "This auction can't be closed yet");
            		return;
            	}
            }else {
            	//error
        		response.sendError(HttpServletResponse.SC_BAD_REQUEST, "This auction can't be closed yet - 2");
        		return;
            }
                  
	        
  	       
        }catch(NumberFormatException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Incorrect param values");
			return;
        }
        
        /**
         * Check:
         * 
         * 	che la current timestamp sia successiva alla data di chiusura
         * 	che auctionid sia un numero valido
         *  che l'auction sia effettivamente dell'utente
         *  
         */    
        
        /**
         * Se Check vanno a buon fine:
         * 
         * 	chiudi l'asta con un dao
         *  metti gli item a sold se c'è stata almeno una bid
         *  se non c'è stata nessuna bid metto gli item a non in auction
         *  
         */
        
        response.sendRedirect(getServletContext().getContextPath() + "/SellServlet");
    
    }
    
}
    
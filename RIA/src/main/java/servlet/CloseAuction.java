package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

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

    public CloseAuction(){super();}

    
    
    @Override
    public void init() throws UnavailableException {
		connection = ConnectionHandler.getConnection(getServletContext());
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
     
        boolean resp;
        
        String auctionID = request.getParameter("auctionId");
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
            	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            	response.setContentType("application/json");
            	response.getWriter().write(new Gson().toJson(new Error("Can't close this auction.")));
            	return;
	        }
            
            List<Item> soldItems = new ArrayList<>();
            
            
            //LocalDateTime now = LocalDateTime.now();
            Date currentDate = new Date(); //il costruttore la inizializza già a questo istante
            if(check != null) {
            	
				if(check.getCreatorID()==userid && check.getAuctionID()==auctionid && check.getIsOpen()==true && check.getDeadlineDate().before(currentDate)){
            		//ok
            		
                    try {
                    	soldItems = itemDAO.findItems(auctionid);
                    }catch(SQLException sql){
                    	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    	response.setContentType("application/json");
                    	response.getWriter().write(new Gson().toJson(new Error("Can't close this auction.")));
                    	return;
        	        }
            		
                    try{
                    	auctionDAO.closeAuction(auctionid);
                    }catch (SQLException sqle){
                    	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    	response.setContentType("application/json");
                    	response.getWriter().write(new Gson().toJson(new Error("Can't close this auction.")));
                    	return;
                    }
                    
                    for (Item it : soldItems) {
        	        	try {
        	        		itemDAO.setToSold(it.itemID());
        	            }catch (SQLException sqle){
        	            	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        	            	response.setContentType("application/json");
        	            	response.getWriter().write(new Gson().toJson(new Error("Can't close this auction.")));
        	            	return;
        	            }
                    }
                    System.out.println("closed");
                    response.setStatus(HttpServletResponse.SC_OK);
                	response.setContentType("application/json");
                	response.getWriter().write(new Gson().toJson(new Error("Auction Closed Correctly.")));
                	return;

            		
            	} else {
                	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                	response.setContentType("application/json");
                	response.getWriter().write(new Gson().toJson(new Error("Can't close this auction.")));
                	return;
            	}
            }else {
            	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            	response.setContentType("application/json");
            	response.getWriter().write(new Gson().toJson(new Error("Can't close this auction.")));
            	return;
            }
                  
	        
  	       
        }catch(NumberFormatException e) {
        	response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        	response.setContentType("application/json");
        	response.getWriter().write(new Gson().toJson(new Error("Can't close this auction.")));
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
        
        //response.sendRedirect(getServletContext().getContextPath() + "/SellServlet");
    
    }
    
}
    
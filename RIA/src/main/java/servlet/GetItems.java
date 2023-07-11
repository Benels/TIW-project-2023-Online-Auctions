package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.Auction;
import beans.Bid;
import beans.Item;
import beans.User;
import dao.AuctionDAO;
import dao.BidDAO;
import dao.ItemDAO;
import other.ConnectionHandler;

@WebServlet("/GetItems")
public class GetItems extends HttpServlet{
    private static final long serialVersionUID = 1L;
    private Connection connection;

    
    public GetItems() {
    	super();	
    }
    
    
    
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	/**
    	 * Get Items
    	 * 
    	 */
    	
        ItemDAO itemDAO = new ItemDAO(connection);

        int auctionid;
    	try {
    		auctionid = Integer.parseInt(request.getParameter("auctionId"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Valori parametri incorretti");
			return;
		}
        
        
        List<Item> items = null;
        
        try{
        	items = itemDAO.findItems(auctionid);
        }catch(SQLException sql2){
            sql2.printStackTrace();
            throw new UnavailableException("Error executing query");
        }
            
        		
		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(items);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
    }
    
}
package servlet;

import java.io.IOException;
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


import beans.User;
import dao.AuctionDAO;
import dao.BidDAO;
import other.ConnectionHandler;

@WebServlet("/NewBid")
@MultipartConfig

public class NewBid extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;

    public NewBid(){super();}

    
    
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
    	
    	
    	System.out.println("REQUESTED NEW BID SUBMIT -- AUCTIONID: " + request.getParameter("auctionID_bid") + " -- PRICE: " + request.getParameter("bid"));
    	
        float currMaxPrice = 0;
        float minRaise = 0;
        float min=0;

        User user = (User) request.getSession().getAttribute("user");
        int userid = user.getUserID();

        ServletContext servletContext = getServletContext();
        
        String auctionID = request.getParameter("auctionID_bid");
        int auctionid = Integer.parseInt(auctionID);
        
        String priceProposed = (String) request.getParameter("bid");
        System.out.println("bid: " +priceProposed);
        Float price=Float.parseFloat(priceProposed);

        float priceCheck =0;
        
        System.out.println("bid: " +price + "; auctionID: "+auctionid);
        
        if(auctionID==null || auctionID.isEmpty() || priceProposed==null || priceProposed.isEmpty() 
        		||auctionid<=0 || price<=0) {
            //context.setVariable("bidErrorMessage", "Missing credentials!");
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        	return;
        	
        }else {
        	BidDAO bidDAO = new BidDAO(connection);
	        try{
	        	currMaxPrice = bidDAO.findBidPrice(auctionid);
	        }catch(SQLException sql){
	            sql.printStackTrace();
	            throw new UnavailableException("Error executing query");
	        }         
	        AuctionDAO auctionDAO = new AuctionDAO(connection);
	        try{
	        	minRaise = auctionDAO.findBidMinRaise(auctionid);
	        }catch(SQLException sql){
	            sql.printStackTrace();
	            throw new UnavailableException("Error executing query");
	        } 	        
	        
	        try{
	        	min = auctionDAO.findStartingBid(auctionid);
	        }catch(SQLException sql){
	            sql.printStackTrace();
	            throw new UnavailableException("Error executing query");
	        }          
	        
	        priceCheck = minRaise + currMaxPrice;
	        
	        if(price < priceCheck || price < min) {
	            //context.setVariable("bidErrorMessage", "Respect the min price...");
	        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        	return;
	        }else{
                try {
                    BidDAO bidDAO2 = new BidDAO(connection);
                    bidDAO2.insertNewBid(price, userid, auctionid);
                } catch (SQLException sqle) {
                	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    return;
                }
                priceCheck=price;
	        }        	
        }
        response.setStatus(HttpServletResponse.SC_OK);
        
    }
}
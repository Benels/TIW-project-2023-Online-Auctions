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

@WebServlet("/BidServlet")
public class BidServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    private Connection connection;

    
    public BidServlet() {
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
    
    
	class Jbuild {
		private List<Item> i;
		private List<Bid> bids;
		Auction a;
		Float nextBidMin;
		
		public Jbuild(List<Item> articoli, List<Bid> offerte, Auction a, Float nextBidMin) {
			this.i = articoli;
			this.bids = offerte;
			this.a = a;
			this.nextBidMin = nextBidMin;
		}
	}
    
    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	/**
    	 * Get di Offerte e Items
    	 * 
    	 */
    	
        User user = (User) request.getSession().getAttribute("user");

        AuctionDAO auctionDAO = new AuctionDAO(connection);
        BidDAO bidDAO = new BidDAO(connection);
        ItemDAO itemDAO = new ItemDAO(connection);


        
        int auctionid;
    	try {
    		auctionid = Integer.parseInt(request.getParameter("auctionid"));
		} catch (NumberFormatException | NullPointerException e) {
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Valori parametri incorretti");
			return;
		}
        
        
        List<Bid> Bids = null;
        List<Item> Items = null;
        Auction a = null;
        
        try{
        	Bids = bidDAO.findBids(auctionid);
        }catch(SQLException sql){
            sql.printStackTrace();
            throw new UnavailableException("Error executing query");
        }          
        
        try{
        	 a = auctionDAO.searchDatas(auctionid);
        }catch(SQLException sql){
            sql.printStackTrace();
            throw new UnavailableException("Error executing query");
        }        
        
        try{
        	Items = itemDAO.findItems(auctionid);
        }catch(SQLException sql2){
            sql2.printStackTrace();
            throw new UnavailableException("Error executing query");
        }
        
        
        
        Float nextBidMin = a.getCurrentBestBid() + a.getMinimumRise();
        
        
		Jbuild risposta = new Jbuild(Items, Bids, a, nextBidMin);
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(risposta);
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
    }
    
}
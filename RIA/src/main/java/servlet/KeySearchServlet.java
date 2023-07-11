
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


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import beans.Auction;
import beans.Item;
import beans.User;
import dao.AuctionDAO;
import dao.ItemDAO;
import other.ConnectionHandler;

@WebServlet("/KeySearchServlet")
public class KeySearchServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    private Connection connection;

    
    public KeySearchServlet() {
    	super();	
    }
    
    
    
    @Override
    public void init() throws UnavailableException {
		connection = ConnectionHandler.getConnection(getServletContext());
    }
    
    
    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
		System.out.println("Cerco aste by keyword  \n");

    	
    	
        User user = (User) request.getSession().getAttribute("user");
        
		System.out.println("UserID : " + user.getUserID() + "\n");

        
        AuctionDAO auctionDAO = new AuctionDAO(connection);
        //AuctionDAO auction2DAO = new AuctionDAO(connection);

        
        String key = request.getParameter("keyword");
        
		System.out.println("key : " + key + "\n");

        
        List<Auction> keyAuctionList =  new ArrayList<Auction>();
        //List<Auction> wonAuctionList =  new ArrayList<Auction>();
        
        /*
        try{
        	wonAuctionList = auctionDAO.findWonAuctions(user.getUserID());
        }catch(SQLException sql){
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        	response.getWriter().println("DB error");
        }        
        */
        try{
        	keyAuctionList = auctionDAO.findAuctionsByKey(user.getUserID(), key);
        }catch(SQLException sql2){
            sql2.printStackTrace();
            throw new UnavailableException("Error executing query");
        }
        
        /*
        context.setVariable("keyAuctionList", keyAuctionList);
        context.setVariable("wonAuctionList", wonAuctionList);*/
        
        
		System.out.println(keyAuctionList);
		for(Auction a : keyAuctionList) {
			System.out.println(a.getAuctionID());
			for(Item i : a.getItemList()) {
				System.out.println(i.getName());
			}

		}
		
		
		Gson gson = new GsonBuilder().setDateFormat("yyyy MMM dd").create();
		String json = gson.toJson(keyAuctionList);
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(json);
        

    }
    
    
    

    @Override
    public void destroy() {
        try {
            ConnectionHandler.closeConnection(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
}


















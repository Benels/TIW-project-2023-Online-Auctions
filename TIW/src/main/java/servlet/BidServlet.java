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

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

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
    private TemplateEngine templateEngine;

    
    public BidServlet() {
    	super();	
    }
    
    
    
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
        ServletContext servletContext = getServletContext();
        final WebContext context = new WebContext(request,response,servletContext,request.getLocale());

        

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
        
        
        context.setVariable("Bids", Bids);
        context.setVariable("Items", Items);
        context.setVariable("Auction", a);
        
        Float nextBidMin = a.getCurrentBestBid() + a.getMinimumRise();
        context.setVariable("nextBidMin", nextBidMin);
        
        String path = "/WEB-INF/Bid.html";
        templateEngine.process(path, context, response.getWriter());

    }
    
}
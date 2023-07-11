package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

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

import beans.User;
import dao.AuctionDAO;
import dao.BidDAO;
import other.ConnectionHandler;

@WebServlet("/NewBid")
public class NewBid extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private Connection connection = null;
    private TemplateEngine templateEngine;

    public NewBid(){super();}

    
    
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
    	
        float currMaxPrice = 0;
        float minRaise = 0;

        User user = (User) request.getSession().getAttribute("user");
        int userid = user.getUserID();

        ServletContext servletContext = getServletContext();
        final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
        
        String priceProposed = request.getParameter("bid");
        Float price=Float.parseFloat(priceProposed);
        String auctionID = request.getParameter("auctionid");
        int auctionid = Integer.parseInt(auctionID);
        float priceCheck =0;
        float min =0;
        
        if(auctionID==null || auctionID.isEmpty() || priceProposed==null || priceProposed.isEmpty() 
        		||auctionid<=0 || price<=0) {
            context.setVariable("bidErrorMessage", "Missing credentials!");
        	
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
	            context.setVariable("bidErrorMessage", "Respect the min price...");
	        }else{
                try {
                    BidDAO bidDAO2 = new BidDAO(connection);
                    bidDAO2.insertNewBid(price, userid, auctionid);
                } catch (SQLException sqle) {
                    response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "database error");
                    return;
                }
                priceCheck=price;
	        }        	
        }
        response.sendRedirect(getServletContext().getContextPath() + "/BidServlet?auctionid="+auctionid);
        
    }
}
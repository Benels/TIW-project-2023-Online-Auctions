package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
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
import dao.BidDAO;
import dao.ItemDAO;
import other.ConnectionHandler;

@WebServlet("/ClosedAuctionDetails")
public class ClosedAuctionDetails extends HttpServlet{
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

    
    public ClosedAuctionDetails() {
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
    protected void doGet (HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        User user = (User) request.getSession().getAttribute("user");
        int userid = user.getUserID();
        
        BidDAO bidDAO= new BidDAO(connection);
        AuctionDAO auctionDAO= new AuctionDAO(connection);
        ServletContext servletContext = getServletContext();
        final WebContext context = new WebContext(request,response,servletContext,request.getLocale());


        
        String auctionID = request.getParameter("closedAuctionID");
        int auctionid;
        Auction auction;
        User winner;
        
        try {
        	auctionid = Integer.parseInt(auctionID);
        	
        	if(auctionid <= 0) {
        		response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Closed Auction Error -3");
    			return;
        	}
        	
        	if(checkClosedUserAuction(userid, auctionid)) {
                
        		try{
	        		winner = bidDAO.searchWinner(auctionid);
	            }catch(SQLException sql){
	                sql.printStackTrace();
	                throw new UnavailableException("Error executing query");
	            }        
        		if(winner!=null) {
        			if(winner.getUserID()!=userid) {
        				
                        try{
                			auction = auctionDAO.findClosedInfos(auctionid);
        		        }catch(SQLException sql){
        		            sql.printStackTrace();
        		            throw new UnavailableException("Error executing query");
        		        }
                        
                        
                        context.setVariable("winner", winner);
                        context.setVariable("auction", auction);
                        System.out.println("variables winner and auction set");
                        
        			}else {
        				//è un problema che chi ha vinto l'asta sia anche chi ha creato l'asta 
            			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Closed Auction Error -3");
            			return;
        			}
        		}else {// La query ha ritornato null
        			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Closed Auction Error -3");
        			return;
        		}
        	}else {//l'asta non è dell'utenete oppure non è chiusa
    			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Closed Auction Error -3");
    			return;
        	}
        }catch(NumberFormatException e){
        	System.out.println("parsing errors - colsedauctionDetails");
        	response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Closed Auction Error -3");
			return;
        }
        String path = "/WEB-INF/ClosedAuction.html";
        templateEngine.process(path, context, response.getWriter());
    }
    
    
    
    
    
    private boolean checkClosedUserAuction(int userid, int auctionid) throws UnavailableException {
		// TODO Auto-generated method stub
    	
    	AuctionDAO auctionDAO = new AuctionDAO(connection);
    	int num=0;
        try{
			num = auctionDAO.checkClosedUserAuction(auctionid, auctionid);
        }catch(SQLException sql){
            sql.printStackTrace();
            throw new UnavailableException("Error executing query");
        }
		return true;
	}
    

	@Override
    public void destroy(){
        try{
            ConnectionHandler.closeConnection(connection);
        }catch (SQLException sql){
            sql.printStackTrace();
        }
    }
    
}
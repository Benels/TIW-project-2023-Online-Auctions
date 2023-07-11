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
import beans.Bid;
import beans.Item;
import beans.User;
import dao.AuctionDAO;
import dao.BidDAO;
import dao.ItemDAO;
import other.ConnectionHandler;

@WebServlet("/OpenAuctionDetails")
public class OpenAuctionDetails extends HttpServlet{
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

    
    public OpenAuctionDetails() {
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
    public void destroy(){
        try{
            ConnectionHandler.closeConnection(connection);
        }catch (SQLException sql){
            sql.printStackTrace();
        }
    }
    
    
    
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
      
        ServletContext servletContext = getServletContext();
        final WebContext context = new WebContext(request, response, servletContext, request.getLocale());
               
        //@{/OpenAuctionDetails(auctionID=${auction.auctionID})}
        String auctionID = request.getParameter("auctionID");
        int auctionid = Integer.parseInt(auctionID);
        AuctionDAO auctionDAO = new AuctionDAO(connection);
        BidDAO bidDAO = new BidDAO(connection);
        ItemDAO itemDAO = new ItemDAO(connection);
        
        if(auctionID==null || auctionID.isEmpty() ||auctionid<=0) {
        	//error branch
            context.setVariable("ErrorMessage", "Wrong selection!");
        }else {          
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
            String countDown = calculatePeriod(a.getDeadlineDate());
            
            if(countDown!=null) {
            	String pre ="You will be able to close this auction in: ";
            	countDown = pre + countDown;
            }
            context.setVariable("countdown", countDown);
        	
        }
        
        String path = "/WEB-INF/OpenAuction.html";
        templateEngine.process(path, context, response.getWriter());
        
    }
    
    

	private String calculatePeriod(LocalDateTime sqlTimestamp) {
		// TODO Auto-generated method stub
		LocalDateTime currentDateTime = LocalDateTime.now();
				
		int sec = sqlTimestamp.getSecond() - currentDateTime.getSecond();
		int mins = sqlTimestamp.getMinute() - currentDateTime.getMinute();
		if(sec<0) {
			sec = -sec;
			mins=(mins-1)%60;
		}
		int hours = sqlTimestamp.getHour() - currentDateTime.getHour();
		if(mins<0) {
			mins = -mins;
			hours=(hours-1)%60;
		}
		int days = sqlTimestamp.getDayOfMonth() - currentDateTime.getDayOfMonth();
		if(hours<0) {
			hours = -hours;
			days--;
		}
		int months = sqlTimestamp.getMonthValue() - currentDateTime.getMonthValue();
		days=days + months*30;

		int years = sqlTimestamp.getYear() - currentDateTime.getYear();
		days=days + years*30*12;

		if(days<0 || hours<0 || sec<0 || mins<0) {
			return null;
		}
		return (days + "Days; " + hours + "h " + mins + "m " + sec + "s ");
	}
    
}
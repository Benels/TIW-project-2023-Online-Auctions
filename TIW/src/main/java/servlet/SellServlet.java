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
import dao.ItemDAO;
import other.ConnectionHandler;

@WebServlet("/SellServlet")
public class SellServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

    
    public SellServlet() {
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
        AuctionDAO auctionDAO= new AuctionDAO(connection);
        ServletContext servletContext = getServletContext();
        final WebContext context = new WebContext(request,response,servletContext,request.getLocale());

        
        /**
         * Trovare tutte le aste dell'utente, sia aperte che chiuse
         */
        
        List<Auction> openAuctionList;
        List<Auction> closedAuctionList;
        
        try{
        	openAuctionList = auctionDAO.findSellerOpenAuctions(user.getUserID());
        }catch(SQLException sql){
            sql.printStackTrace();
            throw new UnavailableException("Error executing query");
        }

        try{
        	closedAuctionList = auctionDAO.findSellerClosedAuctions(user.getUserID());
        }catch(SQLException sql){
            sql.printStackTrace();
            throw new UnavailableException("Error executing query");
        }
        
        context.setVariable("openAuctionList", openAuctionList);
        context.setVariable("closedAuctionList", closedAuctionList);
        
        /**
         * Trovo gli item per metterli nel form[NewAuction] dell'utente 
         *
         */
        
        List<Item> userItemsList; 
        ItemDAO itemDAO = new ItemDAO(connection);

        try{
        	userItemsList = itemDAO.findUserItems(user.getUserID());
        }catch(SQLException sql){
            sql.printStackTrace();
            throw new UnavailableException("Error executing query");
        }
        
        
        context.setVariable("userItemsList", userItemsList);
        
        LocalDateTime today = LocalDateTime.now().plusDays(1);
        context.setVariable("dateMin", today);


        
        /**
         * redirect
         */
        String path = "/WEB-INF/Sell.html";
        templateEngine.process(path, context, response.getWriter());
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
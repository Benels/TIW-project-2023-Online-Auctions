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

@WebServlet("/BuyServlet")
public class BuyServlet extends HttpServlet{
    private static final long serialVersionUID = 1L;
    private Connection connection;
    private TemplateEngine templateEngine;

    
    public BuyServlet() {
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
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
    	
        User user = (User) request.getSession().getAttribute("user");
        AuctionDAO auctionDAO = new AuctionDAO(connection);
        AuctionDAO auction2DAO = new AuctionDAO(connection);
        ServletContext servletContext = getServletContext();
        final WebContext context = new WebContext(request,response,servletContext,request.getLocale());

        
        String key = request.getParameter("keyword");
        
        List<Auction> keyAuctionList = null;
        List<Auction> wonAuctionList = null;
        
        try{
        	wonAuctionList = auctionDAO.findWonAuctions(user.getUserID());
        }catch(SQLException sql){
            sql.printStackTrace();
            throw new UnavailableException("Error executing query");
        }        
        
        if(key!=null) {
		    try{
		    	keyAuctionList = auction2DAO.findAuctionsByKey(user.getUserID(), key);
		    }catch(SQLException sql2){
		        sql2.printStackTrace();
		        throw new UnavailableException("Error executing query");
		    }    	
        }

        
        context.setVariable("keyAuctionList", keyAuctionList);
        context.setVariable("wonAuctionList", wonAuctionList);
        
        String path = "/WEB-INF/Buy.html";
        templateEngine.process(path, context, response.getWriter());

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
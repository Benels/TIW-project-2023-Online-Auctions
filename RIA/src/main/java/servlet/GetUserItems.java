package servlet;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import beans.User;
import dao.ItemDAO;
import other.ConnectionHandler;

@WebServlet("/GetUserItems")
public class GetUserItems extends HttpServlet{
    private static final long serialVersionUID = 1L;
    private Connection connection;

    
    public GetUserItems() {
    	super();	
    }
    
    @Override
    public void init() throws UnavailableException {
		connection = ConnectionHandler.getConnection(getServletContext());

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User user = (User) req.getSession().getAttribute("user");

    	int userID = user.getUserID();

        ItemDAO itemDAO = new ItemDAO(connection);
        try {
            resp.setContentType("application/json");
            resp.getWriter().write(new Gson().toJson(itemDAO.getUserItems(userID)));
        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.setContentType("application/json");
            resp.getWriter().write(new Gson().toJson(new Error("Cannot retrieve items")));
            return;
        }
    }
}
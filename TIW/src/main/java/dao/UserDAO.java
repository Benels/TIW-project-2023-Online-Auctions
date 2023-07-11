package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import beans.User;


public class UserDAO{
	
	
	private Connection connection;
	
	
	/**
	 * Constructor method
	 */
    public UserDAO(Connection connection) {
        this.connection = connection;
    }

    
	public User checkUserPassword(String username, String password) throws SQLException {

        String query = "SELECT * FROM user  WHERE username = ? AND password =?";


		try (PreparedStatement statement = connection.prepareStatement(query);) {
            statement.setString(1, (String) username);
            statement.setString(2, password);
            
            System.out.println("query: " + query);
			try (ResultSet lines = statement.executeQuery();) {
				if (!lines.isBeforeFirst()) { 
					System.out.println("nessun risultato query"); 
					return null;
				}else {                
					lines.next();
			        User user = new User();

					user.setUserID(lines.getInt("userid"));
					user.setUsername(lines.getString("username"));
					user.setName(lines.getString("name"));
					user.setSurname(lines.getString("surname"));
					user.setEmail(lines.getString("email"));
					user.setCity(lines.getString("city"));
					user.setAddress(lines.getString("street"));
					user.setCivic(lines.getInt("number"));
					System.out.println(user.getName());
					return user;
				}
			}
		}
	}
}
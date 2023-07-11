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


	public User findWinner(int auctionid) throws SQLException {
		
		String query = "SELECT userid as id, name as na, surname as su, username as us, email as em, city as ci, street as st, number as nu FROM bid JOIN user ON bid.bidderid = user.userid WHERE bid.auctionid = ? ORDER BY bid.price DESC LIMIT 1;";
		
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        User user = new User();
		
        try {
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, auctionid);
	        resultSet = preparedStatement.executeQuery();
	        
			if (!resultSet.isBeforeFirst()) { // no results, check failed
				System.out.println("nessun risultato query"); 
				return null;
			}else {                
				resultSet.next();
				user.setUserID(resultSet.getInt("id"));
				user.setUsername(resultSet.getString("us"));
				user.setName(resultSet.getString("na"));
				user.setSurname(resultSet.getString("su"));
				user.setEmail(resultSet.getString("em"));
				user.setCity(resultSet.getString("ci"));
				user.setAddress(resultSet.getString("st"));
				user.setCivic(resultSet.getInt("nu"));
			}
	        
        }catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Query error");
        }
		
		
		return user;
	}
}
package dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Bid;
import beans.Item;
import beans.User;

public class BidDAO{
	
	
	private Connection connection;
	
	
	/**
	 * Constructor method
	 */
    public BidDAO(Connection connection) {
        this.connection = connection;
    }


	public List<Bid> findBids(int auctionid) throws SQLException{
		// TODO Auto-generated method stub
		String query = "SELECT b.bidid AS id, b.bidderid AS bbid, b.date AS date, b.price AS price, u.username AS name FROM bid b JOIN user u ON b.bidderid = u.userid WHERE b.auctionid =? ORDER BY b.date DESC, b.price DESC";

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
		List<Bid> bidList = new ArrayList<>();
		
        try {
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, auctionid);
	        resultSet = preparedStatement.executeQuery();
	
	        while(resultSet.next()) {
	        	Bid bid = new Bid();
	        	
	        	
	        	bid.setBidID(resultSet.getInt("id"));
	        	bid.setBidderID(resultSet.getInt("bbid"));
	        	bid.setCost(resultSet.getFloat("price"));
	        	bid.setBidderUsername(resultSet.getString("name"));
	        	java.time.LocalDateTime sqlTimestamp = resultSet.getTimestamp("date").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
	        	bid.setBidDate(sqlTimestamp);


	        	bidList.add(bid);
	        	bid=null;
	        }
        }catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Query error");
        }
		
		return bidList;
	}

    
	
	
	public Float findBidPrice(int auctionid) throws SQLException{
		// TODO Auto-generated method stub
		String query = "select max(price) as imp from bid where auctionid =?";

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;	
        
        float price =0;
        
        try {
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, auctionid);
	        resultSet = preparedStatement.executeQuery();
	        
			if (!resultSet.isBeforeFirst()) { // no results, check failed
				System.out.println("nessun risultato query"); 
				return null;
			}else {                
				resultSet.next();
				price = resultSet.getFloat("imp");
			}
	        
        }catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Query error");
        }
        
        return price;
	
	}


	public void insertNewBid(Float price, int userid, int auctionid) throws SQLException{
		// TODO Auto-generated method stub
		String query = "INSERT INTO bid (bidderid, auctionid, date, price) VALUES (?,?,current_timestamp(),?)";
        PreparedStatement preparedStatement = null;
        connection.setAutoCommit(false);

        try {
        	preparedStatement= connection.prepareStatement(query);
	        preparedStatement.setInt(1, userid);
	        preparedStatement.setInt(2, auctionid);
	        preparedStatement.setFloat(3, price);
	        preparedStatement.executeUpdate();     
	        connection.commit();
        } catch (SQLException sql) {
        	connection.rollback();
            sql.printStackTrace();
        }
        connection.setAutoCommit(true);
	}


	public User searchWinner(int auctionid) throws SQLException{

		String query = "SELECT u.userid as uid, u.username as unick, u.name as uname, u.surname as usurnamne, u.email as uemail, u.city as ucity, u.street as ustreet, u.number as unum FROM bid b INNER JOIN user u ON b.bidderid = u.userid  WHERE b.auctionid = ? ORDER BY b.price DESC LIMIT 1";
		
		
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;	

        User user = new User();

        try {
        	preparedStatement= connection.prepareStatement(query);
	        preparedStatement.setInt(1, auctionid);
	        resultSet = preparedStatement.executeQuery();
	        
			if (!resultSet.isBeforeFirst()) { // no results, check failed
				System.out.println("nessun risultato query"); 
				return null;
			}else {                
				resultSet.next();
				user.setUserID(resultSet.getInt("uid"));
				user.setUsername(resultSet.getString("unick"));
				user.setName(resultSet.getString("uname"));
				user.setSurname(resultSet.getString("usurnamne"));
				user.setEmail(resultSet.getString("uemail"));
				user.setCity(resultSet.getString("ucity"));
				user.setAddress(resultSet.getString("ustreet"));
				user.setCivic(resultSet.getInt("unum"));

			}
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
		

		return user;
	}
}


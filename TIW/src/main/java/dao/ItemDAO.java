package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import beans.Auction;
import beans.Bid;
import beans.Item;

public class ItemDAO{
	
	
	private Connection connection;
	
	
	/**
	 * Constructor method
	 */
    public ItemDAO(Connection connection) {
        this.connection = connection;
    }


    

	public List<Item> findUserItems(int userID)  throws SQLException {
		// TODO Auto-generated method stub
        String query = "SELECT itemid, name, price FROM item WHERE auctionerid = ? AND isonauction=0 AND isalreadysold=0 AND auctionidin IS NULL;";

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
		List<Item> itemsList = new ArrayList<>();
		
        
        
        try {
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, userID);
	        resultSet = preparedStatement.executeQuery();
	
	        while(resultSet.next()) {
	        	Item item = new Item();
	        	
	        	item.setItemID(resultSet.getInt("itemid"));
	        	item.setName(resultSet.getString("name"));
	        	item.setPrice(resultSet.getFloat("price"));

	        	itemsList.add(item);
	        	item=null;
	        }
        }catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Query error");
        }

		
		return itemsList;
	}



	public List<Item> findItems(int auctionid) throws SQLException{
		// TODO Auto-generated method stub
		String query = "select itemid, name, description, picture FROM item where auctionidin = ?";

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
		List<Item> itemList = new ArrayList<>();
		
        
        
        try {
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, auctionid);
	        resultSet = preparedStatement.executeQuery();
	
	        while(resultSet.next()) {
	        	Item item=new Item();	        	
	        	
	        	item.setItemID(resultSet.getInt("itemid"));
	        	item.setName(resultSet.getString("name"));
	        	item.setDescription(resultSet.getString("description"));
	        	item.setItemPicture(resultSet.getString("picture"));

	        	itemList.add(item);
	        	item=null;
	        }
        }catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Query error");
        }
		
		return itemList;	}


	public void setToSold(int itemid) throws SQLException{
		//String query = "UPDATE item SET item.isonauction = 0, item.isalreadysold=1 WHERE item.auctionidin = ?;";
		String query = "UPDATE ITEM SET item.isalreadysold = 1, item.isonauction=0 where item.itemid= ?";
        PreparedStatement preparedStatement = null;		
        connection.setAutoCommit(false);

        try {
        	preparedStatement= connection.prepareStatement(query);
	        preparedStatement.setInt(1, itemid);
	        preparedStatement.executeUpdate();  
	        connection.commit();

        } catch (SQLException sql) {
        	connection.rollback();
            sql.printStackTrace();
        }       
        connection.setAutoCommit(true);

	}


	public void newItem(int userid, String name, String description, String imgName, float price) throws SQLException{
		
		String query = "INSERT INTO item (auctionerid, name, price, description, picture) VALUES (?,?,?,?,?);";
        PreparedStatement preparedStatement = null;
        connection.setAutoCommit(false);

        try {
        	preparedStatement= connection.prepareStatement(query);
	        preparedStatement.setInt(1, userid);
	        preparedStatement.setString(2, name);
	        preparedStatement.setFloat(3, price);
	        preparedStatement.setString(4, description);
	        preparedStatement.setString(5, imgName);

	        preparedStatement.executeUpdate();  	        
	        connection.commit();
    	
        } catch (SQLException sql) {
        	connection.rollback();
        	sql.printStackTrace();
        }
        connection.setAutoCommit(true);

	}

	


	public void setToInAuction(int itemid, int auctionid) throws SQLException{
		String query = "UPDATE item SET item.isonauction = 1, item.auctionidin = ? WHERE item.itemid = ?";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        connection.setAutoCommit(false);
		
        try {
        	preparedStatement= connection.prepareStatement(query);
	        preparedStatement.setInt(1, auctionid);
	        preparedStatement.setInt(2, itemid);


	        preparedStatement.executeUpdate();
	        connection.commit();
        } catch (SQLException sql) {
        	connection.rollback();
            sql.printStackTrace();
        }
        connection.setAutoCommit(true);
	}



	
	
	
	
	//TO REMOVE 
	

	public float findItemPrice(int item) throws SQLException{

        String query = "SELECT price FROM item WHERE itemid = ?";
        PreparedStatement statement = null;
        ResultSet res = null;
        float price;
        
        try {
        	statement = connection.prepareStatement(query);
            statement.setInt(1, item);
            res = statement.executeQuery(query);
            price = res.getFloat("price");
        }
        finally {
        	if(res != null) {
        		res.close();
        	}
        }
        return price;
	}


	public boolean checkItem(int userid, String itemid) throws SQLException {

        String query = "SELECT auctionerid, isonauction FROM item WHERE itemid = ?";
        PreparedStatement statement = null;
        ResultSet res = null;
        boolean risp = true;
		
        try {
        	statement = connection.prepareStatement(query);
            statement.setString(1, itemid);
            res = statement.executeQuery(query);
            
            if(res==null) {
            	risp = false;
            }else {
            	res.getInt("price");
            	if(res.getInt("auctionerid")!=userid) {
            		risp=false;
            	}
            	if(res.getBoolean("isonauction")==true) {
            		risp=false;
            	}
            }
        }        
        finally {
        	if(res != null) {
        		res.close();
        	}
        }
        
		return risp;
	}
	
	
	

	public int checkItems(int userid, String payload) throws SQLException{
		// TODO Auto-generated method stub

		String query = "SELECT COUNT(*) AS itemcount FROM item WHERE itemid IN (" + payload + ") AND auctionerid = ? AND isonauction = 0 AND isalreadysold = 0";
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, userid);
	        resultSet = preparedStatement.executeQuery();
	        if(resultSet.next()) {
	        	return resultSet.getInt("itemcount");
	        }
        } catch (SQLException sql) {
            sql.printStackTrace();
        }
		return 0;
	}


}
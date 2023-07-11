package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import beans.Auction;
import beans.Item;


public class AuctionDAO{
	
	
	private Connection connection;
	
	
	/**
	 * Constructor method
	 */
    public AuctionDAO(Connection connection) {
        this.connection = connection;
    }




	public List<Auction> findSellerOpenAuctions(int userID) throws SQLException{
		
		List<Auction> openAuctions = new ArrayList<>();
		
		String query = "SELECT auction.auctionid, auction.startingbid, auction.minraise, auction.deadline,  COALESCE(MAX(bid.price), 0) AS bestbid FROM auction LEFT JOIN bid ON auction.auctionid=bid.auctionid WHERE auction.creatorid=? AND auction.isopen=? GROUP BY auction.auctionid, auction.startingbid, auction.minraise, auction.deadline ORDER BY auction.deadline ASC";

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        
        try {
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, userID);
	        preparedStatement.setInt(2, 1);
	        resultSet = preparedStatement.executeQuery();
	
	        while(resultSet.next()) {
	        	Auction auction = new Auction();
	        	auction.setAuctionID(resultSet.getInt("auctionid"));
	        	auction.setStartingBid(resultSet.getFloat("startingbid"));
	        	auction.setMinimumRise(resultSet.getFloat("minraise"));
	        	java.time.LocalDateTime sqlTimestamp = resultSet.getTimestamp("deadline").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
	        	auction.setDeadlineDate(sqlTimestamp);
	        	
	        	auction.setPeriod(calculatePeriod(sqlTimestamp));
	        	auction.setCurrentBestBid(resultSet.getFloat("bestbid"));
	        	openAuctions.add(auction);
	        	auction=null;
	        }
        }catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Query error");
        }

		return openAuctions;
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
 	
		return (days + " Days; " + hours + " h, " + mins + " m, " + sec + " s ");
	}




	public List<Auction> findSellerClosedAuctions(int userID) throws SQLException{

		List<Auction> closedAuctions = new ArrayList<>();
		
		String query = "SELECT auction.auctionid, auction.startingbid, auction.minraise, auction.deadline, max(bid.price) AS bestbid FROM auction JOIN bid ON auction.auctionid=bid.auctionid WHERE auction.creatorid=? AND auction.isopen=? GROUP BY auction.auctionid, auction.startingbid, auction.minraise, auction.deadline ORDER BY auction.deadline ASC";

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        
        try {
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, userID);
	        preparedStatement.setInt(2, 0);
	        resultSet = preparedStatement.executeQuery();
	
	        while(resultSet.next()) {
	        	Auction auction = new Auction();
	        	auction.setAuctionID(resultSet.getInt("auctionid"));
	        	auction.setStartingBid(resultSet.getFloat("startingbid"));
	        	auction.setMinimumRise(resultSet.getFloat("minraise"));
	        	java.time.LocalDateTime sqlTimestamp = resultSet.getTimestamp("deadline").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
	        	auction.setDeadlineDate(sqlTimestamp);
	        	auction.setCurrentBestBid(resultSet.getFloat("bestbid"));
	        	closedAuctions.add(auction);
	        	auction=null;
	        }
        }catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Query error");
        } 

		return closedAuctions;
	}




	public List<Auction> findWonAuctions(int userID) throws SQLException{
		// TODO Auto-generated method stub
		
		List<Auction> wonAuction = new ArrayList<>();

		String query = "SELECT a.auctionid AS aid, b.price AS bprice, i.itemid AS iid, i.name AS iname, i.description AS ides, i.picture AS ipic FROM auction a JOIN bid b ON a.auctionid = b.auctionid JOIN item i ON a.auctionid =  i.auctionidin WHERE b.bidderid = ? AND a.isopen = 0 AND b.price = (   SELECT MAX(price)   FROM bid   WHERE auctionid = a.auctionid)";
		
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        Map<Integer, Auction> won = new HashMap<>();

        System.out.println("cerco aste vinte");
        
        try {
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, userID);
	        resultSet = preparedStatement.executeQuery();
	
	        while(resultSet.next()) {
	        	Auction auction;	
	        	int auctionID = resultSet.getInt("aid");
	        	
	        	if(won.containsKey(auctionID)) {
	        		auction=won.get(auctionID);
	        	}else {
	        		auction =  new Auction();
	        		auction.setAuctionID(auctionID);
	        		won.put(auctionID, auction);
	        	}
	        	auction.setCurrentBestBid(resultSet.getFloat("bprice"));
	        	
	        	Item item = new Item();
	        	item.setItemID(resultSet.getInt("iid"));
	        	item.setName(resultSet.getString("iname"));
	        	item.setDescription(resultSet.getString("ides"));
	        	item.setItemPicture(resultSet.getString("ipic"));

	        	auction.setItem(item);	  
	        }
	        
	        wonAuction = new ArrayList<>(won.values());
	        for(Auction a: wonAuction) {
	        	System.out.print(a.getAuctionID());
	        }

        }catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Query error");
        }
        return wonAuction;
	}




	public List<Auction> findAuctionsByKey(int userID, String key) throws SQLException{
		// TODO Auto-generated method stub

		
		List<Auction> foundAuction = new ArrayList<>();
        Map<Integer, Auction> found = new HashMap<>();
        
        String query = "SELECT a.auctionid AS aid, a.deadline AS adead, a.minraise AS araise, COALESCE(MAX(b.price), 0) AS maxbidimport,  i.itemid AS iid, i.name AS iname, i.description AS ides, i.picture AS ipic FROM auction a LEFT JOIN bid b ON a.auctionid = b.auctionid  JOIN item i ON a.auctionid = i.auctionidin  WHERE a.creatorid != ? AND a.deadline > CURRENT_TIMESTAMP() AND a.isopen = 1 AND EXISTS (     SELECT 1     FROM item     WHERE auctionidin = a.auctionid AND (name LIKE ? OR description LIKE ?) ) GROUP BY a.auctionid, a.deadline, i.itemid, i.name, i.description, i.picture ORDER BY a.deadline DESC";
        String keyword ="%" + key + "%";
		
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
                
        
        
        try {
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, userID);
	        preparedStatement.setString(2, keyword);
	        preparedStatement.setString(3, keyword);
	        resultSet = preparedStatement.executeQuery();
	        
	        
	        while(resultSet.next()) {
	        	Auction auction;	
	        	int auctionID = resultSet.getInt("aid");
	        	
	        	if(found.containsKey(auctionID)) {
	        		auction=found.get(auctionID);
	        	}else {
	        		auction =  new Auction();
	        		auction.setAuctionID(auctionID);
	        		found.put(auctionID, auction);
	        	}
	        	
	        	java.time.LocalDateTime sqlTimestamp = resultSet.getTimestamp("adead").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
	        	auction.setDeadlineDate(sqlTimestamp);
	        	auction.setMinimumRise(resultSet.getFloat("araise"));
	        	auction.setCurrentBestBid(resultSet.getFloat("maxbidimport"));
	        	
	        	Item item = new Item();
	        	item.setItemID(resultSet.getInt("iid"));
	        	item.setName(resultSet.getString("iname"));
	        	item.setDescription(resultSet.getString("ides"));
	        	item.setItemPicture(resultSet.getString("ipic"));

	        	auction.setItem(item);	  
	        }
	        
	        foundAuction = new ArrayList<>(found.values());
	        for(Auction a: foundAuction) {
	        	System.out.print(a.getAuctionID());
	        }
	        
	        
        }catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Query error");
        }
        return foundAuction;
        
	}




	public Auction searchDatas(int auctionid) throws SQLException{
		// TODO Auto-generated method stub
		
		Auction a = new Auction();
		String query = "SELECT deadline, minraise, max(bid.price) as p FROM auction JOIN bid ON auction.auctionid = bid.auctionid WHERE auction.auctionid=?";
		
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, auctionid);

	        resultSet = preparedStatement.executeQuery();
	        
	        
	        if (!resultSet.isBeforeFirst()) { // no results, credential check failed
				System.out.println("nessun risultato query Auction searchDatas"); 
				return null;
			}else {                
				resultSet.next();
				
				a.setAuctionID(auctionid);
				a.setCurrentBestBid(resultSet.getFloat("p"));
				a.setMinimumRise(resultSet.getFloat("minraise"));
	        	java.time.LocalDateTime sqlTimestamp = resultSet.getTimestamp("deadline").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
	        	a.setDeadlineDate(sqlTimestamp);

	        }  
        }catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Query error");
        }
	
		return a;
	}




	public float findBidMinRaise(int auctionid)  throws SQLException{

		float minr =0;
		String query = "select minraise from auction where auctionid =?";
		
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, auctionid);

	        resultSet = preparedStatement.executeQuery();
	        
	        if (!resultSet.isBeforeFirst()) { // no results, credential check failed
				System.out.println("nessun risultato query Auction searchDatas"); 
				return 0;
			}else { 
				resultSet.next();
				minr = resultSet.getFloat("minraise");
			}
        }catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Query error");
        }
        
		return minr;
	}


	public float findStartingBid(int auctionid)  throws SQLException{

		float minr =0;
		String query = "select auction.startingbid as sta from auction where auction.auctionid =?";
		
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, auctionid);

	        resultSet = preparedStatement.executeQuery();
	        
	        if (!resultSet.isBeforeFirst()) { // no results, credential check failed
				System.out.println("nessun risultato query Auction searchDatas"); 
				return 0;
			}else { 
				resultSet.next();
				minr = resultSet.getFloat("sta");
			}
        }catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Query error");
        }
        
		return minr;
	}



	public void closeAuction(int auctionid) throws SQLException{
		//String query = "UPDATE auction SET isopen = 0 WHERE auctionid = ?;";
		String query = "UPDATE auction SET auction.isopen=false WHERE auction.auctionid = ?";
        PreparedStatement preparedStatement = null;	
		connection.setAutoCommit(false);

        try {
        	preparedStatement= connection.prepareStatement(query);
	        preparedStatement.setInt(1, auctionid);
	        preparedStatement.executeUpdate(); 
	        connection.commit();

        } catch (SQLException sql) {
        	connection.rollback();
            sql.printStackTrace();
        }
        connection.setAutoCommit(true);

	}




	public int createAuction(int userid, float total, LocalDateTime deadline, float minRaise) throws SQLException{
		String query = "INSERT INTO auction (creatorid, isopen, startingbid, deadline, minraise) VALUES (?,1,?,?,?)";
		java.sql.Timestamp timestamp = java.sql.Timestamp.valueOf(deadline);
		PreparedStatement preparedStatement = null;
	    ResultSet resultSet = null;
		connection.setAutoCommit(false);
	        try {
	        	preparedStatement= connection.prepareStatement(query);
		        preparedStatement.setInt(1, userid);
		        preparedStatement.setFloat(2, total);
		        preparedStatement.setTimestamp(3, timestamp);
		        preparedStatement.setFloat(4, minRaise);


		        preparedStatement.executeUpdate();  
		        connection.commit();
	        } catch (SQLException sql) {
	        	connection.rollback();
	            sql.printStackTrace();
	        }
	        
	        connection.setAutoCommit(true);
	        
	        String query2 = "SELECT LAST_INSERT_ID() AS auctionid;";
	        try {
		        preparedStatement = connection.prepareStatement(query2);
		        resultSet = preparedStatement.executeQuery();
		        if(resultSet.next()) {
		        	return resultSet.getInt("auctionid");
		        }
	        } catch (SQLException sql) {
	            sql.printStackTrace();
	        }
		return 0;
	}









	public List<Auction> getCookieAuctions(int[] ids)  throws SQLException{

		String query = "SELECT auction.auctionid AS aid, auction.startingbid AS ast, auction.minraise AS amin, auction.deadline AS adead, COALESCE(MAX(bid.price), -1) AS bestbid FROM auction LEFT JOIN bid ON auction.auctionid = bid.auctionid  WHERE auction.isopen = 1 AND auction.auctionid IN (";

			for (int i = 0; i < ids.length; i++) {
			  query += "?";
			  if (i < ids.length - 1) {
			    query += ",";
			  }
			}

			query += ") GROUP BY auction.auctionid, auction.startingbid, auction.minraise, auction.deadline ORDER BY auction.deadline ASC";
			
		List<Auction> aucts = new ArrayList<>();

        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
		
        try {
	        preparedStatement = connection.prepareStatement(query);
	        for (int i = 0; i < ids.length; i++) {
	        	preparedStatement.setInt(i + 1, ids[i]);
	          }
	        resultSet = preparedStatement.executeQuery();
	
	        while(resultSet.next()) {
	        	Auction auction = new Auction();
	        	auction.setAuctionID(resultSet.getInt("aid"));
	        	auction.setStartingBid(resultSet.getFloat("ast"));
	        	auction.setMinimumRise(resultSet.getFloat("amin"));
	        	java.time.LocalDateTime sqlTimestamp = resultSet.getTimestamp("adead").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
	        	auction.setDeadlineDate(sqlTimestamp);
	        	
	        	auction.setPeriod(calculatePeriod(sqlTimestamp));
	        	auction.setCurrentBestBid(resultSet.getFloat("bestbid"));
	        	aucts.add(auction);
	        	auction=null;
	        }
        }catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Query error");
        }

		return aucts;	
	}


	public Auction getAuction(int auctionid) throws SQLException { String query = "select auctionid, creatorid, isopen, deadline from auction where auctionid = ? ";

		
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        Auction auction = new Auction();
        
        try {
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, auctionid);

	        resultSet = preparedStatement.executeQuery();
	        
	        if (resultSet.next()) {
				
	        	auction.setAuctionID(resultSet.getInt("auctionid"));
	        	auction.setCreatorID(resultSet.getInt("creatorid"));
	        	auction.setIsOpen(resultSet.getBoolean("isopen"));
	        	java.time.LocalDateTime sqlTimestamp = resultSet.getTimestamp("deadline").toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
	        	auction.setDeadlineDate(sqlTimestamp);
			    
			} 

        }catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Query error");
        }
		return auction;
	}    
	
	
	
	
	//TO REMOVE 
	


	public boolean checkAuctionUserDate(int auctionid, int userid) throws SQLException{
		
		String query = "SELECT * FROM auction WHERE auctionid = ? AND creatorid = ? AND isopen = 1 AND deadline < CURRENT_TIMESTAMP();";
		
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        boolean returnResponse = false;
        
        try {
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, auctionid);
	        preparedStatement.setInt(1, userid);

	        resultSet = preparedStatement.executeQuery();
	        		
	        resultSet.last();

	        int rowCount = resultSet.getRow();
	        
	        if(rowCount == 1) {
	        	returnResponse = true;
	        }

        }catch(SQLException e) {
            e.printStackTrace();
            System.out.println("Query error");
        }
		return returnResponse;
	}



	public int checkClosedUserAuction(int userid, int auctionid) throws SQLException {
		// TODO Auto-generated method stub
        String query = "SELECT COUNT(*) AS count FROM auction WHERE auctionid = ? AND creatorid = ?";
        
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        
        try {
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, auctionid);	
	        preparedStatement.setInt(2, userid);	
	        resultSet = preparedStatement.executeQuery();
	        if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count;
            }
				        
        }catch(SQLException e) {
	         e.printStackTrace();
	         System.out.println("Query error");
	    }

		return 0;
	}


	public Auction findClosedInfos(int auctionid) throws SQLException{
	
		Auction auction = new Auction();
		
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
		
		String query = "SELECT b.price AS bestbidprice, i.itemid, i.name, i.description, i.picture FROM bid b JOIN auction a ON b.auctionid = a.auctionid JOIN item i ON a.auctionid = i.auctionidin WHERE a.auctionid = ? ORDER BY b.price DESC";
		
		
		try {
	        preparedStatement = connection.prepareStatement(query);
	        preparedStatement.setInt(1, auctionid);	
	        resultSet = preparedStatement.executeQuery();
				        
			
			if (resultSet.next()) {
				
				auction.setCurrentBestBid(resultSet.getFloat("bestbidprice"));
			    
			    Item item = new Item();
			    
			    item.setItemID(resultSet.getInt("itemid"));
			    item.setName(resultSet.getString("name"));
			    item.setDescription(resultSet.getString("description"));
			    item.setItemPicture(resultSet.getString("picture"));
			    
			    auction.setItem(item);
			    item=null;
			
			    while (resultSet.next()) {
			
			        item = new Item();
			        
			        item.setItemID(resultSet.getInt("itemid"));
			        item.setName(resultSet.getString("name"));
			        item.setDescription(resultSet.getString("description"));
			        item.setItemPicture(resultSet.getString("picture"));
			        
			        auction.setItem(item);
			        item=null;
			    } 
			} 
	        
		}catch(SQLException e) {
	         e.printStackTrace();
	         System.out.println("Query error");
	    }
		
		return auction;
	}




}






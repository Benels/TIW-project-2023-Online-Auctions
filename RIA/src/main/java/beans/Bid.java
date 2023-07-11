//Bid Bean 
package beans;


import java.time.LocalDateTime;
import java.util.Date;
//import java.sql.Timestamp

public class Bid{
	private int bidID;
	private int bidderID;
	private int auctionID;
	private java.util.Date bidDate;
	private float cost;
	private String bidderUsername;


	public int getBidID(){
		return bidID;
	}

	public void setBidID(int bidID){
		this.bidID=bidID;
	}

	public int getBidderID(){
		return bidderID;
	}

	public void setBidderID(int bidderID){
		this.bidderID=bidderID;
	}

	public int getAuctionID(){
		return auctionID;
	}

	public void seAuctionID(int auctionID){
		this.auctionID=auctionID;
	}

	public java.util.Date getBidDate(){
		return bidDate;
	}

	public void setBidDate(LocalDateTime date){
		//this.bidDate=date;
		this.bidDate= new Date (date.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
		//		this.deadlineDate= new Date (deadlineDate.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());

	}

	public float getCost(){
		return cost;
	}

	public void setCost(float cost){
		this.cost=cost;
	}

	
	public String getBidderUsername() {
		return bidderUsername;
	}
	
	public void setBidderUsername(String username) {
		this.bidderUsername=username;
	}
}
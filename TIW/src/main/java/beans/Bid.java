//Bid Bean 
package beans;


import java.time.LocalDateTime;
import java.util.Date;
//import java.sql.Timestamp

public class Bid{
	private int bidID;
	private int bidderID;
	private int auctionID;
	private LocalDateTime bidDate;
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

	public LocalDateTime getBidDate(){
		return bidDate;
	}

	public void setBidDate(LocalDateTime date){
		this.bidDate=date;
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
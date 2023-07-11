package beans;

import java.util.ArrayList;

/*Auction Bean*/

import java.util.Date;
import java.util.List;
import java.time.LocalDateTime;
import java.time.Period;


public class Auction{ //l'asta deve avere uno o più articoli in vendita

	private int auctionID;
	private int creatorID;
	private boolean isOpen; //true se è aperta, false se è chiusa
	private float startingBid;
	private float currentBestBid;
	private float minimumRise;
	private java.util.Date deadlineDate;	
	private String period;
	private ArrayList<Item> itemList = new ArrayList<Item>();
		//	https://docs.oracle.com/javase/8/docs/api/java/util/Date.html


	public int getAuctionID(){
		return auctionID;
	}

	public void setAuctionID(int auctionID){
		this.auctionID=auctionID;
	}

	public int getCreatorID(){
		return creatorID;
	}

	public void setCreatorID(int creatorID){
		this.creatorID=creatorID;
	}


	public boolean getIsOpen(){
		return isOpen;
	}

	public void setIsOpen(boolean isOpen){
		this.isOpen=isOpen;
	}

	public float getStartingBid(){
		return startingBid;
	}

	public void setStartingBid(float startingBid){
		this.startingBid=startingBid;
	}
	
	public float getCurrentBestBid(){
		return currentBestBid;
	}

	public void setCurrentBestBid(float bid){
		this.currentBestBid=bid;
	}


	public float getMinimumRise(){
		return minimumRise;
	}

	public void setMinimumRise(float minimumRise){
		this.minimumRise=minimumRise;
	}


	public java.util.Date getDeadlineDate(){
		return deadlineDate;
	}

	public void setDeadlineDate(java.time.LocalDateTime deadlineDate){
		this.deadlineDate= new Date (deadlineDate.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli());
	}
	
	public String getPeriod(){
		return period;
	}

	public void setPeriod(String diff){
		this.period=diff;
	}
	
	
	public ArrayList<Item> getItemList(){
		return itemList;
	}
	
	public void setItem(Item item) {
		itemList.add(item);
	}


}
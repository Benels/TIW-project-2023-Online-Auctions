//Bean Item
package beans;


public class Item{

	private int itemID;
	private int auctionerID;
	private String name;
	private float price;
	private String description;
    private String itemPicture;
    private boolean alreadySold; 	//true if the item is already sold
    								//false if it can be put on auction


	public int itemID(){
		return itemID;
	}

	public void setItemID(int itemID){
		this.itemID=itemID;
	}	

	public int auctionerID(){
		return auctionerID;
	}

	public void setAuctionerID(int auctionerID){
		this.auctionerID=auctionerID;
	}

	public String getName(){ 
		return name; 
	}

	public void setName(String name){
		this.name=name;
	}

	public float getPrice(){
		return price;
	}

	public void setPrice(float price){
		this.price=price;
	}

	public String getDescription(){
		return description;
	}

	public void setDescription(String description){
		this.description=description;
	}


	public String getItemPicture() {
        return itemPicture;
    }

    public void setItemPicture(String itemPicture) {
        this.itemPicture=itemPicture;
    }
    
    public boolean getAlreadySold() {
    	return alreadySold;
    }
    
    public void setAlreadySold(boolean alreadySold) {
    	this.alreadySold=alreadySold;
    }

}
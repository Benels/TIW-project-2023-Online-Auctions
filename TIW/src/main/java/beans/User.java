//Bean User
package beans;


public class User{
	private int userID;
	private String name;
	private String surname;
	private String email;
	private String username;

	//per il salvataggio dell'indirizzo uso 3 variabili differenti: città, via, numero
	private String city;
	private String address;
	private int civic;

	public int getUserID(){
		return userID;
	}

	public void setUserID(int userID){
		this.userID=userID;
	}

	public String getName(){ 
		return name; 
	}

	public void setName(String name){
		this.name=name;
	}
	
	public String getUsername(){ 
		return username; 
	}

	public void setUsername(String username){
		this.username=username;
	}

	public String getSurname(){
		return surname;
	}

	public void setSurname(String surname){
		this.surname=surname;
	}

	public String getEmail(){
		return email;
	}

	public void setEmail(String email){
		this.email=email;
	}


	
	
	
	
	public String getAddress() {
		return address;
	}
	
	public void setAddress(String address) {
		this.address=address;
	}
	
	public String getCity() {
		return city;
	}
	
	public void setCity(String city) {
		this.city=city;
	}
	
	public int getCivic() {
		return civic;
	}

	public void setCivic(int civic) {
		this.civic=civic;
	}
}
package db;

public class Contact {
	public String ID;
	public String name;
	public String phoneNumber;
	public String email;
	
	public Contact (String name, String phoneNumber, String email){
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.email = email;
	}
	
	public Contact() {
		// TODO Auto-generated constructor stub
		this.name = "";
		this.phoneNumber = "";
		this.email = "";
	}

	@Override
	public String toString() {
		return "Contact [ID=" + ID + ", name=" + name + ", phoneNumber=" + phoneNumber + ", email=" + email + "]";
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPhoneNumber() {
		return phoneNumber;
	}
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getID() {
		return ID;
	}

	public void setID(String iD) {
		ID = iD;
	}
	
}

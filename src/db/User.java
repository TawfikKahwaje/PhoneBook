package db;

import java.util.ArrayList;

public class User {
	public String userID;


	public String username;
	public String password;
	public ArrayList<String> contacts = new ArrayList<String>();
	
	
	public User() {
		// TODO Auto-generated constructor stub
	}
	public User(String username, String password){
		this.username = username;
		this.password = password;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public ArrayList<String> getContacts() {
		return contacts;
	}
	public void setContacts(ArrayList<String> contacts) {
		this.contacts = contacts;
	}
	@Override
	public String toString() {
		return "User [username=" + username + ", password=" + password + ", contacts=" + contacts + "]";
	}
}

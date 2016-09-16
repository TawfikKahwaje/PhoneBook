package db;
import com.mongodb.*;

import java.net.UnknownHostException;
import java.util.*;

import org.bson.types.ObjectId;

public class UserDB {
	private static final String DBNAME = "phonebook";
	private static final String COLLECTION_NAME_USER = "user";
	private static final String key = "Bar12345Bar12345"; // 128 bit key
	private static final String initVector = "RandomInitVector"; // 16 bytes IV
	
	public static User saveUser(User user) throws UnknownHostException{
		// TODO Auto-generated method stub
		DB db = DbManager.getDb(DBNAME);
		DBCollection dbCollection = db.getCollection(COLLECTION_NAME_USER);
		DBObject query = new BasicDBObject("username", user.getUsername());
		if(dbCollection.findOne(query) != null ){
			System.out.println("the user name existe");
			return null;
		}
		System.out.println("user name not exist");
			
		DBObject dbObject = BasicDBObjectBuilder.start()
				.add("username", user.getUsername())
				.add("password", Encryptor.encrypt(key, initVector, user.getPassword()))
				.get();
		
		dbCollection.save(dbObject);
		User newUser = new User(dbObject.get("username").toString(),dbObject.get("password").toString());
		newUser.setUserID(dbObject.get("_id").toString());
		return newUser;
	}
	
	public static DBObject getUser(User user) throws UnknownHostException {
		DB db = DbManager.getDb(DBNAME);
		DBCollection dbCollection = db.getCollection(COLLECTION_NAME_USER);
		DBObject query = new BasicDBObject("username", user.getUsername());
		DBObject dbObject = dbCollection.findOne(query);
		if( dbObject != null){
			if(Objects.equals(dbObject.get("password"),(Encryptor.encrypt(key, initVector, user.getPassword()))))
				return dbObject;
			else
				System.out.println("Wrong Password");
		}
		else
			System.out.println("Wrong UserName");
		return null ;
	}
	
	public static User getUserById(String UserID) throws UnknownHostException {
		DB db = DbManager.getDb(DBNAME);
		DBCollection dbCollection = db.getCollection(COLLECTION_NAME_USER);
		DBObject query = new BasicDBObject("_id", new ObjectId(UserID));
		DBObject dbObject = dbCollection.findOne(query);
		User user = new User(dbObject.get("username").toString(),dbObject.get("password").toString());
		user.setUserID(UserID);
		return user;
	}
	
	public static void deleteUserById(String userID) throws UnknownHostException{
		DB db = DbManager.getDb(DBNAME);
		DBCollection dbCollection = db.getCollection(COLLECTION_NAME_USER);
		ObjectId objectId = new ObjectId(userID);
		DBObject query= new BasicDBObject("_id", objectId);
		DBObject userObject = dbCollection.findAndRemove(query);
		ArrayList<String> contacts = new ArrayList<>();
		contacts = (ArrayList<String>) userObject.get("cotacts");
		System.out.println("contacts : "+contacts);
		if(contacts != null){
			for (String contactID : contacts) {
				ContactDB.deleteContact(contactID);
			}
		}
		
	}
	
	@SuppressWarnings("unchecked")
	public static String addContact(Contact contact, String userID) throws UnknownHostException{
		DB db = DbManager.getDb(DBNAME);		
		DBObject contactObject = ContactDB.saveContact(contact);
		String contactID = contactObject.get("_id").toString();
		System.out.println(contactID);
		
		DBCollection dbCollectionUser = db.getCollection(COLLECTION_NAME_USER);
		ObjectId id= new ObjectId(userID);    
		DBObject query = new BasicDBObject("_id", id);
		DBObject update = new BasicDBObject();
		update.put("$push", new BasicDBObject("cotacts", contactID));
		//DBObject newUser = dbCollectionUser.findAndModify(query, update,true);
		DBObject newUser = dbCollectionUser.findAndModify(query, null, null, false,update, true, false);
		User user = new User(newUser.get("username").toString(), newUser.get("password").toString());
		ArrayList<String> contacts = new ArrayList<String>();
		contacts = (ArrayList<String>) newUser.get("cotacts");
		user.setContacts(contacts);
		return contactID;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Contact> getAllContactsByUserId(String userID) throws UnknownHostException{
		ObjectId objectID = new ObjectId(userID);
		DBObject query = new BasicDBObject("_id", objectID);
		
		DB db = DbManager.getDb(DBNAME);
		DBCollection dbCollectionUser = db.getCollection(COLLECTION_NAME_USER);
		DBObject userObject = dbCollectionUser.findOne(query);
		ArrayList<String> contactsIds = new ArrayList<>();
		contactsIds = (ArrayList<String>) userObject.get("cotacts");

		ArrayList<Contact> contactList = new ArrayList<Contact>();
		if(contactsIds != null) {	
			for (int i = 0; i < contactsIds.size(); i++) {
				Contact contact = ContactDB.getOneContactById(contactsIds.get(i));
				contactList.add(contact);
			}
		}
		if(contactList != null){
			return contactList;
		}
		System.out.println("No contacts List");
		return null;
		
	}
}

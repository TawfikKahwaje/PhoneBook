package db;
import java.net.UnknownHostException;
import org.bson.types.ObjectId;

import com.mongodb.BasicDBObject;
import com.mongodb.BasicDBObjectBuilder;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

public class ContactDB {
	private static final String DBNAME = "phonebook";
	private static final String COLLECTION_NAME_COTACT = "contact";
	private static final String  COLLECTION_NAME_USER = "user";
	
	public static DBObject saveContact(Contact contact) throws UnknownHostException{
		// TODO Auto-generated method stub
		DBObject dbObject = BasicDBObjectBuilder.start()
				.add("name", contact.getName())
				.add("phoneNumber", contact.getPhoneNumber())
				.add("email", contact.getEmail())
				.get();
		DB db = DbManager.getDb(DBNAME);
		DBCollection dbCollection = db.getCollection(COLLECTION_NAME_COTACT);
		dbCollection.save(dbObject);
		return dbObject;
	}

	
	
	public static Contact getOneContactById(String contactID) throws UnknownHostException{
		ObjectId objectId = new ObjectId(contactID);
		DB db = DbManager.getDb(DBNAME);
		DBCollection dbCollectionContact = db.getCollection(COLLECTION_NAME_COTACT);
		DBObject query = new BasicDBObject("_id",objectId);
		DBObject contactObject = dbCollectionContact.findOne(query);
		Contact newContact = new Contact();
		newContact.setID(contactObject.get("_id").toString());
		newContact.setName(contactObject.get("name").toString());
		newContact.setPhoneNumber(contactObject.get("phoneNumber").toString());
		newContact.setEmail(contactObject.get("email").toString());
		return newContact;
	}
	
	public static void editContactById(Contact newContact) throws UnknownHostException{
		DB db = DbManager.getDb(DBNAME);
		DBCollection dbCollection = db.getCollection(COLLECTION_NAME_COTACT);
		ObjectId objectId = new ObjectId(newContact.getID());
		DBObject query= new BasicDBObject("_id", objectId);
		DBObject update = new BasicDBObject().append("$set",
				new BasicDBObject()
				.append("name", newContact.getName())
				.append("email", newContact.getEmail())
				.append("phoneNumber", newContact.getPhoneNumber()));
		DBObject newContactObject = dbCollection.findAndModify(query, null, null, false,update, true, false);		
	}
	
	public static void deleteContact (String contactID) throws UnknownHostException{
		DB db = DbManager.getDb(DBNAME);
		DBCollection dbCollectionContact = db.getCollection(COLLECTION_NAME_COTACT);
		ObjectId objectId = new ObjectId(contactID);
		DBObject query= new BasicDBObject("_id", objectId);
		dbCollectionContact.findAndRemove(query);
	}
	
	public static void deleteContactById(String ContactID,String UserID) throws UnknownHostException{
		deleteContact(ContactID);
		DB db = DbManager.getDb(DBNAME);
		DBCollection dbCollectionUser = db.getCollection(COLLECTION_NAME_USER);
		BasicDBObject match = new BasicDBObject("_id", new ObjectId(UserID));
		System.out.println("match "+ match);
	    BasicDBObject update2 = new BasicDBObject("cotacts", ContactID);
	    System.out.println("update2 : "+update2);
	    dbCollectionUser.update(match, new BasicDBObject("$pull", update2));
	    
	    
	}

}
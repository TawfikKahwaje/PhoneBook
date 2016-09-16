package db;
import java.net.UnknownHostException;

import com.mongodb.DB;
import com.mongodb.Mongo;
public class DbManager {
	
	private static DB db;
	public static DB getDb (String name)
	    throws UnknownHostException {
	    Mongo mongo = new Mongo();
	    if ( db == null){
	      db = mongo.getDB(name);
	    }
	    return db;
	  }
}
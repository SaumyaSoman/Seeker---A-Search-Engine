package edu.rutgers.cloud.util;

import java.net.UnknownHostException;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

/**
 * Class to establish connection with mongoDB database
 *
 */
public class MorphiaUtil {

    private static Mongo mongo;
    private static Datastore datastore;

    static {
        try {
            // Create the database connection
            mongo =  new Mongo("localhost");
            datastore = new Morphia().createDatastore(mongo,"search");
        } catch (UnknownHostException e) {
            System.err.println("Caught Unknown host exception:"+e);
            e.printStackTrace();
        } catch (MongoException e) {
            System.err.println("Initial Datastore creation failed:"+e);
            e.printStackTrace();
        }
    }

    public static Datastore getDatastore() {
        return datastore;
    }
} 
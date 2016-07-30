package inventory.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.DriverManager;

public class DB {

    private static final Logger logger = LogManager.getLogger(DB.class);

    public static String dbURL = "jdbc:derby:InventoryDB;create=true;";

    public static void loadDriver(){
        try {
            Class.forName("org.apache.derby.jdbc.EmbeddedDriver").newInstance();
            logger.info("DRIVER LOADED");
        } catch (Exception except) {
            logger.error("PROBLEM LOADING DRIVER");
        }
    }

    public static void disconnect(){
        try {
            DriverManager.getConnection("jdbc:derby:;shutdown=true");
        }catch (Exception e){
            logger.error("DISCONNECTED FROM DERBY: "+e);
        }
    }

}

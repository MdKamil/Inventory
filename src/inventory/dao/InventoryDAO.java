package inventory.dao;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;

public class InventoryDAO {

    private static final Logger logger = LogManager.getLogger(InventoryDAO.class);

    public static boolean checkTableExists(String tableName){
        try {
            Connection connection = DB.getConnection();
            if(connection!=null) {
                DatabaseMetaData dbm = connection.getMetaData();
                ResultSet tables = dbm.getTables(null, null, tableName, null);
                if (tables.next()) {
                    
                } else {
                    createTable(connection,tableName);
                }
            }else {
                return false;
            }
        }catch (Exception e){
            logger.error("COULDN'T ESTABLISH CONNECTION");
        }
        return false;
    }

    private static boolean createTable(Connection connection, String tableName) {
        try {
            String sql = "CREATE TABLE PRODUCT_TYPE(TYPE_ID INT NOT NULL GENERATED ALWAYS AS IDENTITY,TYPE_NAME VARCHAR(100) NOT NULL,PRIMARY KEY(TYPE_NAME)";
            connection.createStatement();
        }   catch (Exception e){
            logger.
        }
        return false;
    }


}

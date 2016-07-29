package inventory.dao;

import inventory.model.DaySale;
import inventory.model.Product;
import inventory.model.SaleReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InventoryDAO {

    private static final Logger logger = LogManager.getLogger(InventoryDAO.class);

    private static boolean checkTableExists(Connection connection,String tableName) throws SQLException{
        boolean result = false;
        DatabaseMetaData dbm = connection.getMetaData();
        ResultSet tables = dbm.getTables(null, null, tableName, null);
        if (tables.next()) {
            // Table exists.
            logger.info("TABLE: "+tableName+" EXISTS IN DB");
            result = true;
        } else {
            // Table does not exist.
            logger.info("TABLE DOESN'T EXISTS");
        }
        return result;
    }

    public static List<String> retrieveProductType(){
        List<String> list = null;
        try(Connection connection = DriverManager.getConnection(DB.dbURL);Statement statement = connection.createStatement()){
            if(checkTableExists(connection,"PRODUCT_TYPE")){
               list = getProductType(statement);
            }else {
                if(createProductTypeTable(connection)) {
                    list = getProductType(statement);
                }else {
                    list = Collections.emptyList();
                }
            }
        }catch (Exception e){
            logger.error("ERROR RETRIEVING PRODUCT_TYPE: "+e);
        }finally {
            list = Collections.emptyList();
        }
        return list;
    }

    private static boolean createProductTypeTable(Connection connection) {
        boolean result = false;
        String sql = "CREATE TABLE PRODUCT_TYPE(type_id INT NOT NULL GENERATED ALWAYS AS IDENTITY, type_name VARCHAR(100) NOT NULL, PRIMARY KEY(type_name))";
        try(Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            logger.info("PRODUCT_TYPE TABLE CREATED SUCCESSFULLY");
            result = true;
        } catch (Exception e){
            logger.error("COULDN'T CREATE PRODUCT_TYPE TABLE: "+e);
        }
        return result;
    }

    private static List<String> getProductType(Statement statement) throws SQLException{
        List<String> list = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCT_TYPE";
        ResultSet resultSet = statement.executeQuery(sql);
        while(resultSet.next()){
            String name = resultSet.getString("type_name");
            list.add(name);
        }
        resultSet.close();
        return list;
    }

    public static boolean updateProductTypeTable(String typeName){
        boolean result = false;
        try(Connection connection = DriverManager.getConnection(DB.dbURL);PreparedStatement ps = getPSProductType(connection,typeName);Statement statement = connection.createStatement()) {
            ps.executeUpdate();
            String sql = "SELECT type_name FROM PRODUCT_TYPE WHERE type_id = (SELECT MAX(type_id) FROM PRODUCT_TYPE)";
            ResultSet rs = statement.executeQuery(sql);
            if(!rs.next()){
                // NO DATA;
            }else {
                result = true;
            }
            rs.close();
        }catch (Exception e){
            logger.error("ERROR WHILE UPDATING PRODUCT TYPE: "+e);
        }
        return result;
    }

    private static PreparedStatement getPSProductType(Connection connection,String typeName) throws  SQLException{
        String sql = "INSERT INTO PRODUCT_TYPE(type_name) VALUES(?)";
        PreparedStatement  stmt = connection.prepareStatement(sql);
        stmt.setString(1, typeName);
        return stmt;
    }

    public static List<Product> retrieveProduct() {
        List<Product> list = null;
        try(Connection connection = DriverManager.getConnection(DB.dbURL);Statement statement = connection.createStatement()){
            if(checkTableExists(connection,"PRODUCT")){
                list = getProduct(statement);
            }else {
                if(createProductTable(connection)) {
                    list = getProduct(statement);
                }else {
                    list = Collections.emptyList();
                }
            }
        }catch (Exception e){
            logger.error(e);
        }finally {
            list = Collections.emptyList();
        }
        return list;
    }

    private static boolean createProductTable(Connection connection) {
        boolean result = false;
        String sql = "CREATE TABLE PRODUCT(product_id INT NOT NULL GENERATED ALWAYS AS IDENTITY, product_type VARCHAR(100) NOT NULL, product_name VARCHAR(100) NOT NULL, stock INT NOT NULL, rate INT NOT NULL, netWeight INT NOT NULL, PRIMARY KEY(product_id), FOREIGN KEY(product_type) REFERENCES PRODUCT_TYPE(type_id))";
        try(Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
            logger.info("PRODUCT TABLE CREATED SUCCESSFULLY");
            result = true;
        } catch (Exception e){
            logger.error("COULDN'T CREATE PRODUCT TABLE: "+e);
        }
        return result;
    }

    private static List<Product> getProduct(Statement statement) throws SQLException{
        List<Product> list = new ArrayList<>();
        String sql = "SELECT * FROM PRODUCT";
        ResultSet resultSet = statement.executeQuery(sql);
        while(resultSet.next()){
            int id = resultSet.getInt("product_id");
            String type = resultSet.getString("product_type");
            String name = resultSet.getString("product_name");
            int inStock = resultSet.getInt("stock");
            int rate = resultSet.getInt("rate");
            int newWeight = resultSet.getInt("netWeight");
            Product product = new Product(id,type,name,inStock,rate,newWeight);
            list.add(product);
        }
        resultSet.close();
        return list;
    }

    public static boolean updateProductTable(Product product){
        boolean result = false;
        try(Connection connection = DriverManager.getConnection(DB.dbURL);PreparedStatement ps = getPSProduct(connection,product)){
            int count = ps.executeUpdate();
            if(count > 0) {
                result = true;
            }
        }catch (Exception e){
            logger.error("ERROR WHILE CREATING A NEW PRODUCT: "+e);
        }
        return result;
    }

    private static PreparedStatement getPSProduct(Connection connection,Product product) throws SQLException{
        String sql = "INSERT INTO PRODUCT(product_type,product_name,stock,rate,netWeight) values(?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,product.getProductType());
        preparedStatement.setString(2,product.getProductName());
        preparedStatement.setInt(3,product.getLeftInStock());
        preparedStatement.setInt(4,product.getProductRate());
        preparedStatement.setInt(5,product.getNetWeight());
        return preparedStatement;
    }

    public static boolean updateStock(int id,int quantity){
        boolean result = false;
        try (Connection connection = DriverManager.getConnection(DB.dbURL);PreparedStatement ps = getProductQtyUpdateStmt(connection,id,quantity,"INC")){
            int count = ps.executeUpdate();
            if(count > 0){
                result = true;
            }
        }catch (Exception e){
            logger.error("COULDN'T UPDATE STOCK: "+e);
        }
        return result;
    }

    public static boolean reduceFromStock(int id,int quantity){
        boolean result = false;
        try (Connection connection = DriverManager.getConnection(DB.dbURL);PreparedStatement ps = getProductQtyUpdateStmt(connection,id,quantity,"DEC")){
            int count = ps.executeUpdate();
            if(count > 0){
                result = true;
            }
        }catch (Exception e){
            logger.error("COULDN'T REDUCE FROM STOCK: "+e);
        }
        return result;
    }

    private static PreparedStatement getProductQtyUpdateStmt(Connection connection,int id, int quantity,String type) throws SQLException{
        String sql;
        if(type.equals("INC")){
            sql = "UPDATE PRODUCT SET stock = (SELECT stock FROM PRODUCT WHERE product_id = ?) + ? WHERE product_id = ?";
        }else {
            sql = "UPDATE PRODUCT SET stock = (SELECT stock FROM PRODUCT WHERE product_id = ?) - ? WHERE product_id = ?";
        }
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,id);
        preparedStatement.setInt(2,quantity);
        preparedStatement.setInt(3,id);
        return preparedStatement;
    }

    public static boolean deleteProduct(int id){
        boolean result = false;
        try(Connection connection = DriverManager.getConnection(DB.dbURL);PreparedStatement ps = getDeletePS(connection,id)){
            int count = ps.executeUpdate();
            if(count > 0){
                result = true;
            }
        }catch (Exception e){
            logger.error("ERROR WHILE DELETING PRODUCT: "+e);
        }
        return result;
    }

    private static PreparedStatement getDeletePS(Connection connection, int id) throws SQLException{
        String sql = "DELETE FROM PRODUCT WHERE product_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,id);
        return preparedStatement;
    }

    public static boolean createSaleTable(){
        boolean result = false;
        try(Connection connection = DriverManager.getConnection(DB.dbURL);Statement statement = connection.createStatement()){
            String sql = "CREATE TABLE SALE_REPORT(sale_id INT NOT NULL GENERATED ALWAYS AS IDENTITY, product_id INT NOT NULL, sale_day DATE NOT NULL, sale_time TIME NOT NULL, quantity_sold INT NOT NULL, sale_amt INT NOT NULL, PRIMARY KEY(sale_id), FOREIGN KEY(product_id) REFERENCES PRODUCT(product_id)";
            int count = statement.executeUpdate(sql);
            result = true;
            logger.error("SUCCESSFULLY CREATED SALE_REPORT TABLE    ");
        }catch (Exception e){
            logger.error("ERROR WHILE CREATING SALE TABLE: "+e);
        }
        return result;
    }

    public static boolean updateSaleReport(Product product,int quantitySold){
        boolean result = false;
        try(Connection connection = DriverManager.getConnection(DB.dbURL)){
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();
            try(PreparedStatement psUpdate = getProductQtyUpdateStmt(connection,product.getProductID(),quantitySold,"DEC");PreparedStatement psSaleReportUpdate = getPSSaleReport(connection,product,quantitySold)){
                psUpdate.executeUpdate();
                psSaleReportUpdate.executeUpdate();
                result = true;
                logger.error("REDUCE FROM STOCK OPERATION SUCCESSFUL");
            }catch (SQLException e){
                connection.rollback();
                logger.error("ROLLBACK CAUSED DURING REDUCE FROM STOCK OPERATION: "+e);
            }finally {
                connection.releaseSavepoint(savepoint);
                connection.setAutoCommit(true);
            }
        }catch (Exception e){
            logger.error("REDUCE FROM STOCK OPERATION FAILED: "+e);
        }
        return result;
    }

    private static PreparedStatement getPSSaleReport(Connection connection,Product product,int quantitySold) throws SQLException{
        String sql = "INSERT INTO SALE_REPORT(product_id,sale_day,sale_time,quantity_sold,sale_amt) VALUES(?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,product.getProductID());
        preparedStatement.setDate(2,Date.valueOf(LocalDate.now()));
        preparedStatement.setTime(3,Time.valueOf(LocalTime.now()));
        preparedStatement.setInt(4,quantitySold);
        preparedStatement.setInt(5,product.getProductRate()*quantitySold);
        return preparedStatement;
    }


    public static List<SaleReport> getSaleRecord(LocalDate date){
        List<SaleReport> saleList = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(DB.dbURL);PreparedStatement statement = getSaleRecordPS(connection,date)){
            ResultSet rs = statement.executeQuery();
            while(rs.next()){
                Time saleTime = rs.getTime("sale_time");
                String productType = rs.getString("product_type");
                String productName = rs.getString("product_name");
                Integer productRate = rs.getInt("product_rate");
                Integer quantitySold = rs.getInt("quantity_sold");
                Integer saleAmt = rs.getInt("sale_amt");

                SaleReport sale = new SaleReport(saleTime.toLocalTime(),productType,productName,productRate,quantitySold,saleAmt);
                saleList.add(sale);
            }
            rs.close();
        }catch (Exception e){
            logger.error("COULDN'T RETRIEVE SALE REPORT: "+e);
        }
        return saleList;
    }

    private static PreparedStatement getSaleRecordPS(Connection connection,LocalDate date) throws SQLException{
        String sql = "SELECT product_type,product_name,product_rate,sale_time,quantity_sold,sale_amt FROM PRODUCT JOIN SALE_REPORT ON PRODUCT.product_id = SALE_REPORT.product_id WHERE SALE_REPORT.sale_day = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setDate(1,Date.valueOf(date));
        return preparedStatement;
    }

    public static DaySale getTodaySaleReport(LocalDate date) {
        DaySale daySale = null;
        try(Connection connection = DriverManager.getConnection(DB.dbURL);PreparedStatement ps = getTodaySalePS(connection,date)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Integer quantitySold = rs.getInt("quantity_sold");
                Integer saleAmt = rs.getInt("sale_amt");
                daySale = new DaySale(quantitySold,saleAmt);
            }
            rs.close();
        }catch (Exception e){
            daySale = new DaySale(0,0);
            logger.error("ERROR WHILE RETRIEVING TODAY'S SALE: "+e);
        }
        return daySale;
    }

    private static PreparedStatement getTodaySalePS(Connection connection, LocalDate date) throws SQLException{
        String sql = "SELECT SUM(quantity_sold), SUM(sale_amt) FROM SALE_REPORT WHERE sale_month = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setDate(1,Date.valueOf(date));
        return preparedStatement;
    }

    public static boolean checkSaleReportTable() {
        boolean result = false;
        try(Connection connection = DriverManager.getConnection(DB.dbURL)){
            if(checkTableExists(connection,"SALE_REPORT")){
                result = true;
            }else {
                if(createSaleTable()){
                    result = true;
                }else {
                    result = false;
                }
            }
        }catch (Exception e){
            logger.error("ERROR WHILE CHECKING EXISTENCE OF SALE_REPORT TABLE: "+e);
        }
        return result;
    }
}

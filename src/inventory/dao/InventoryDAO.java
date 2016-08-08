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
import java.util.List;

public class InventoryDAO {

    private static final Logger logger = LogManager.getLogger(InventoryDAO.class);

    public static boolean checkTableExists(String tableName) {
        boolean result = false;
        try (Connection connection = DriverManager.getConnection(DB.dbURL)){
            DatabaseMetaData dbm = connection.getMetaData();
            ResultSet tables = dbm.getTables(null, null, tableName, null);
            if (tables.next()) {
                // Table exists.
                //logger.info("TABLE " + tableName + " EXISTS IN DB");
                result = true;
            } else {
                // Table does not exist.
                logger.info("TABLE "+tableName+" DOESN'T EXISTS");
            }
        }catch(Exception e){
            logger.error("ERROR WHILE INSPECTING PRESENCE OF TABLE: "+e);
        }
        return result;
    }

    public static boolean createProductTypeTable() {
        boolean result = false;
        try(Connection connection = DriverManager.getConnection(DB.dbURL);Statement statement = connection.createStatement()) {
            String sql = "CREATE TABLE PRODUCT_TYPE(type_id INT NOT NULL GENERATED ALWAYS AS IDENTITY, type_name VARCHAR(100) NOT NULL, CONSTRAINT type_pk PRIMARY KEY(type_name))";
            statement.executeUpdate(sql);
            logger.info("PRODUCT_TYPE TABLE CREATED SUCCESSFULLY");
            result = true;
        } catch (Exception e){
            logger.error("COULDN'T CREATE PRODUCT_TYPE TABLE: "+e);
        }
        return result;
    }

    public static List<String> retrieveProductType(){
        List<String> list = list = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(DB.dbURL);Statement statement = connection.createStatement()){
            String sql = "SELECT * FROM PRODUCT_TYPE";
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                String name = resultSet.getString("type_name");
                list.add(name);
            }
            resultSet.close();
        }catch (Exception e){
            logger.error("ERROR RETRIEVING PRODUCT_TYPE: "+e);
        }
        return list;
    }

    public static boolean updateProductType(String typeName){
        boolean result = false;
        try(Connection connection = DriverManager.getConnection(DB.dbURL);PreparedStatement ps = updateProductTypePS(connection,typeName)) {
            int count = ps.executeUpdate();
            result = true;
        }catch (Exception e){
            logger.error("ERROR WHILE UPDATING PRODUCT TYPE: "+e);
        }
        return result;
    }

    private static PreparedStatement updateProductTypePS(Connection connection,String typeName) throws  SQLException{
        String sql = "INSERT INTO PRODUCT_TYPE(type_name) VALUES(?)";
        PreparedStatement  stmt = connection.prepareStatement(sql);
        stmt.setString(1, typeName);
        return stmt;
    }

    public static boolean createProductTable() {
        boolean result = false;
        try(Connection connection = DriverManager.getConnection(DB.dbURL)) {
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();
            try(Statement statement = connection.createStatement();Statement indexStmt = connection.createStatement()){
                String sql = "CREATE TABLE PRODUCT(product_id INT NOT NULL GENERATED ALWAYS AS IDENTITY, product_type VARCHAR(100) NOT NULL, product_name VARCHAR(100) NOT NULL, in_stock INT NOT NULL, product_rate INT NOT NULL, net_weight INT, CONSTRAINT product_pk PRIMARY KEY (product_id), CONSTRAINT product_fk FOREIGN KEY (product_type) REFERENCES product_type(type_name) ON DELETE CASCADE)";
                statement.executeUpdate(sql);
                logger.info("PRODUCT TABLE CREATED SUCCESSFULLY");

                String indexSql = "CREATE INDEX product_index ON product(product_type)";
                indexStmt.executeUpdate(indexSql);
                logger.info("INDEX CREATED FOR PRODUCT TABLE");

                result = true;
            }catch (SQLException e){
                connection.rollback(savepoint);
                logger.error("ERROR: "+e);
            }finally {
                connection.releaseSavepoint(savepoint);
                connection.setAutoCommit(true);
            }
        } catch (Exception e){
            logger.error("COULDN'T CREATE PRODUCT TABLE: "+e);
        }
        return result;
    }

    public static List<Product> retrieveProduct() {
        List<Product> list = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(DB.dbURL);Statement statement = connection.createStatement()){
            String sql = "SELECT * FROM PRODUCT";
            ResultSet resultSet = statement.executeQuery(sql);
            while(resultSet.next()){
                int id = resultSet.getInt("product_id");
                String type = resultSet.getString("product_type");
                String name = resultSet.getString("product_name");
                int inStock = resultSet.getInt("in_stock");
                int rate = resultSet.getInt("product_rate");
                int newWeight = resultSet.getInt("net_weight");
                Product product = new Product(id,type,name,inStock,rate,newWeight);
                list.add(product);
            }
            resultSet.close();
        }catch (Exception e){
            logger.error("ERROR WHILE RETRIEVING PRODUCT: "+e);
        }
        return list;
    }

    public static Product createProduct(String productType,String productName,int stock,int rate,int netWeight){
        Product product = null;
        String sql = "SELECT * FROM PRODUCT WHERE product_id = (SELECT MAX(product_id) from PRODUCT)";
        try(Connection connection = DriverManager.getConnection(DB.dbURL)){
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();
            try(PreparedStatement ps = createProductPS(connection,productType,productName,stock,rate,netWeight);Statement statement = connection.createStatement()) {
                ps.executeUpdate();
                ResultSet resultSet = statement.executeQuery(sql);
                while(resultSet.next()){
                    Integer pId = resultSet.getInt("product_id");
                    String pType = resultSet.getString("product_type");
                    String pName = resultSet.getString("product_name");
                    Integer pStock = resultSet.getInt("in_stock");
                    Integer pRate = resultSet.getInt("product_rate");
                    Integer pNetWt = resultSet.getInt("net_weight");
                    product  = new Product(pId,pType,pName,pStock,pRate,pNetWt);
                }
            }catch (Exception e){
                connection.rollback(savepoint);
                logger.error("ERROR: "+e);
            }finally {
                connection.releaseSavepoint(savepoint);
                connection.setAutoCommit(true);
            }
        }catch (Exception e){
            logger.error("ERROR WHILE CREATING A NEW PRODUCT: "+e);
        }
        return product;
    }

    private static PreparedStatement createProductPS(Connection connection,String productType,String productName,int stock,int rate,int netWeight) throws SQLException{
        String sql = "INSERT INTO PRODUCT(product_type,product_name,in_stock,product_rate,net_weight) values(?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,productType);
        preparedStatement.setString(2,productName);
        preparedStatement.setInt(3,stock);
        preparedStatement.setInt(4,rate);
        preparedStatement.setInt(5,netWeight);
        return preparedStatement;
    }

    public static boolean editProduct(String pType,String pName,int pStock,int pRate,int pNetWt,int productID) {
        boolean result = false;
        try(Connection connection = DriverManager.getConnection(DB.dbURL)){
            connection.setAutoCommit(false);
            Savepoint savepoint = connection.setSavepoint();
            try(PreparedStatement ps = getEditProductPS(connection,pType,pName,pStock,pRate,pNetWt,productID);PreparedStatement ps2 = getEditSalePS(connection,pType,pName,pRate,productID)){
                ps.executeUpdate();
                ps2.executeUpdate();
                result = true;
            }catch (SQLException e){
                connection.rollback();
                logger.error("ERROR: "+e);
            }finally {
                connection.releaseSavepoint(savepoint);
                connection.setAutoCommit(true);
            }
        }catch (Exception e){
            logger.error("ERROR WHILE EDITING EXISTING PRODUCT:"+e);
        }
        return result;
    }

    private static PreparedStatement getEditProductPS(Connection connection,String pType,String pName,int pStock,int pRate,int pNetWt,int productID) throws SQLException{
        String sql = "UPDATE PRODUCT SET product_type = ?, product_name = ?, in_stock = ?, product_rate = ?, net_weight = ? WHERE product_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,pType);
        preparedStatement.setString(2,pName);
        preparedStatement.setInt(3,pStock);
        preparedStatement.setInt(4,pRate);
        preparedStatement.setInt(5,pNetWt);
        preparedStatement.setInt(6,productID);
        return preparedStatement;
    }


    private static PreparedStatement getEditSalePS(Connection connection,String pType,String pName,int pRate,int productID) throws SQLException{
        String sql = "UPDATE SALE_REPORT SET product_type = ?, product_name = ?, product_rate = ? WHERE product_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,pType);
        preparedStatement.setString(2,pName);
        preparedStatement.setInt(3,pRate);
        preparedStatement.setInt(4,productID);
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

    private static PreparedStatement getDeletePS(Connection connection,int id) throws SQLException{
        String sql = "DELETE FROM PRODUCT WHERE product_id = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,id);
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

    private static PreparedStatement getProductQtyUpdateStmt(Connection connection,int id,int quantity,String type) throws SQLException{
        String sql;
        if(type.equals("INC")){
            sql = "UPDATE PRODUCT SET in_stock = (SELECT in_stock FROM PRODUCT WHERE product_id = ?) + ? WHERE product_id = ?";
        }else {
            sql = "UPDATE PRODUCT SET in_stock = (SELECT in_stock FROM PRODUCT WHERE product_id = ?) - ? WHERE product_id = ?";
        }
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setInt(1,id);
        preparedStatement.setInt(2,quantity);
        preparedStatement.setInt(3,id);
        return preparedStatement;
    }

    public static boolean createSaleReportTable(){
        boolean result = false;
        try(Connection connection = DriverManager.getConnection(DB.dbURL);Statement statement = connection.createStatement()){
            String sql = "CREATE TABLE SALE_REPORT(sale_id INT NOT NULL GENERATED ALWAYS AS IDENTITY, sale_day DATE NOT NULL, sale_time TIME NOT NULL, product_id INT, product_type VARCHAR(100) NOT NULL, product_name VARCHAR(100) NOT NULL, product_rate INT NOT NULL, quantity_sold INT NOT NULL, sale_amt INT NOT NULL, CONSTRAINT sale_pk PRIMARY KEY (sale_id), CONSTRAINT sale_fk FOREIGN KEY (product_id) REFERENCES product(product_id) ON DELETE SET NULL)";
            int count = statement.executeUpdate(sql);
            result = true;
            logger.error("SUCCESSFULLY CREATED SALE_REPORT TABLE    ");
        }catch (Exception e){
            logger.error("ERROR WHILE CREATING SALE TABLE: "+e);
        }
        return result;
    }

    public static boolean reduceFromStock(Product product,int quantitySold){
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
        String sql = "INSERT INTO SALE_REPORT(sale_day,sale_time,product_id,product_type,product_name,product_rate,quantity_sold,sale_amt) VALUES(?,?,?,?,?,?,?,?)";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setDate(1,Date.valueOf(LocalDate.now()));
        preparedStatement.setTime(2,Time.valueOf(LocalTime.now()));
        preparedStatement.setInt(3,product.getProductID());
        preparedStatement.setString(4,product.getProductType());
        preparedStatement.setString(5,product.getProductName());
        preparedStatement.setInt(6,product.getProductRate());
        preparedStatement.setInt(7,quantitySold);
        preparedStatement.setInt(8,product.getProductRate()*quantitySold);
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
        String sql = "SELECT sale_time,product_type,product_name,product_rate,quantity_sold,sale_amt FROM SALE_REPORT WHERE sale_day = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setDate(1,Date.valueOf(date));
        return preparedStatement;
    }

    public static DaySale getTodaySaleReport(LocalDate date,String type) {
        DaySale daySale = null;
        try(Connection connection = DriverManager.getConnection(DB.dbURL);PreparedStatement ps = getTodaySalePS(connection,date,type)){
            ResultSet rs = ps.executeQuery();
            while (rs.next()){
                Integer quantitySold = rs.getInt(1);
                Integer saleAmt = rs.getInt(2);
                daySale = new DaySale(quantitySold,saleAmt);
            }
            rs.close();
        }catch (Exception e){
            daySale = new DaySale(0,0);
            logger.error("ERROR WHILE RETRIEVING TODAY'S SALE: "+e);
        }
        return daySale;
    }

    private static PreparedStatement getTodaySalePS(Connection connection,LocalDate date,String type) throws SQLException{
        String sql = "SELECT SUM(quantity_sold), SUM(sale_amt) FROM SALE_REPORT WHERE sale_day = ? and product_type = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setDate(1,Date.valueOf(date));
        preparedStatement.setString(2,type);
        return preparedStatement;
    }

    public static List<Product> getProductOfType(String type) {
        List<Product> list = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(DB.dbURL);PreparedStatement ps = getProductOfTypePS(connection,type)){
            ResultSet resultSet = ps.executeQuery();
            while(resultSet.next()){
                int id = resultSet.getInt("product_id");
                String pType = resultSet.getString("product_type");
                String name = resultSet.getString("product_name");
                int inStock = resultSet.getInt("in_stock");
                int rate = resultSet.getInt("product_rate");
                int newWeight = resultSet.getInt("net_weight");
                Product product = new Product(id,pType,name,inStock,rate,newWeight);
                list.add(product);
            }
            resultSet.close();
        }catch (Exception e){
            logger.error("ERROR WHILE RETRIEVING PARTICULAR PRODUCT_TYPE LIST: "+e);
        }
        return list;
    }

    private static PreparedStatement getProductOfTypePS(Connection connection,String type) throws SQLException{
        String sql = "SELECT * FROM PRODUCT WHERE product_type = ?";
        PreparedStatement preparedStatement = connection.prepareStatement(sql);
        preparedStatement.setString(1,type);
        return preparedStatement;
    }

}
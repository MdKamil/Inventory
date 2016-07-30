package inventory.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Product {

    private IntegerProperty productID;

    private StringProperty productType;

    private StringProperty productName;

    private IntegerProperty leftInStock;

    private IntegerProperty productRate;

    private IntegerProperty netWeight;

    public Product(int productID,String productType,String productName,int leftInStock,int productRate,int netWeight){
        this.productID = new SimpleIntegerProperty(productID);
        this.productType = new SimpleStringProperty(productType);
        this.productName = new SimpleStringProperty(productName);
        this.leftInStock = new SimpleIntegerProperty(leftInStock);
        this.productRate = new SimpleIntegerProperty(productRate);
        this.netWeight = new SimpleIntegerProperty(netWeight);
    }

    public int getLeftInStock() {
        return leftInStock.get();
    }

    public void setLeftInStock(int leftInStock) {
        this.leftInStock.set(leftInStock);
    }

    public String getProductName() {
        return productName.get();
    }

    public void setProductName(String productName) {
        this.productName.set(productName);
    }

    public int getProductRate() {
        return productRate.get();
    }

    public void setProductRate(int productRate) {
        this.productRate.set(productRate);
    }

    public String getProductType() {
        return productType.get();
    }

    public void setProductType(String productType) {
        this.productType.set(productType);
    }

    public int getNetWeight() {
        return netWeight.get();
    }

    public void setNetWeight(int netWeight) {
        this.netWeight.set(netWeight);
    }

    public int getProductID() {
        return productID.get();
    }
}

package inventory.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public abstract class Product {

    private StringProperty productName;

    private IntegerProperty leftInStock;

    private IntegerProperty productRate;

    public Product(String productName,int leftInStock,int productRate){
        this.productName = new SimpleStringProperty(productName);
        this.leftInStock = new SimpleIntegerProperty(leftInStock);
        this.productRate = new SimpleIntegerProperty(productRate);
    }

    public int getLeftInStock() {
        return leftInStock.get();
    }

    public String getProductName() {
        return productName.get();
    }

    public int getProductRate() {
        return productRate.get();
    }
}

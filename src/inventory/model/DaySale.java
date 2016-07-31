package inventory.model;


import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class DaySale {

    private IntegerProperty quantitySold;

    private IntegerProperty saleAmt;

    public DaySale(Integer quantitySold,Integer saleAmt){
        this.quantitySold = new SimpleIntegerProperty(quantitySold);
        this.saleAmt = new SimpleIntegerProperty(saleAmt);
    }

    public int getQuantitySold() {
        return quantitySold.get();
    }

    public void setQuantitySold(int quantitySold) {
        this.quantitySold.set(quantitySold);
    }

    public int getSaleAmt() {
        return saleAmt.get();
    }

    public void setSaleAmt(int saleAmt) {
        this.saleAmt.set(saleAmt);
    }
}

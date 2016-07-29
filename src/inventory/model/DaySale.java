package inventory.model;


public class DaySale {

    private Integer quantitySold;

    private Integer saleAmt;

    public DaySale(Integer quantitySold,Integer saleAmt){
        this.quantitySold = quantitySold;
        this.saleAmt = saleAmt;
    }

    public Integer getQuantitySold() {
        return quantitySold;
    }

    public Integer getSaleAmt() {
        return saleAmt;
    }

}

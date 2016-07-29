package inventory.model;

import java.time.LocalTime;

public class SaleReport {

    private LocalTime saleTime;

    private String productType;

    private String productName;

    private Integer productRate;

    private Integer quantitySold;

    private Integer saleAmt;

    public SaleReport(LocalTime saleTime, String productType, String productName, Integer productRate, Integer quantitySold, Integer saleAmt){
        this.saleTime = saleTime;
        this.productType = productType;
        this.productName = productName;
        this.productRate = productRate;
        this.quantitySold = quantitySold;
        this.saleAmt = saleAmt;
    }


    public Integer getSaleAmt() {
        return saleAmt;
    }

    public String getProductName() {
        return productName;
    }

    public Integer getProductRate() {
        return productRate;
    }

    public Integer getQuantitySold() {
        return quantitySold;
    }

    public LocalTime getSaleTime() {
        return saleTime;
    }

    public String getProductType() {
        return productType;
    }

}

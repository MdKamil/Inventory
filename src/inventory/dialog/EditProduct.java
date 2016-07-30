package inventory.dialog;


import inventory.dao.InventoryDAO;
import inventory.model.Product;
import inventory.util.Validate;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class EditProduct {

    public static void show(ObservableList<String> productTypeList, Product product){
        // Custom dialog.
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Edit Product");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);

        VBox vBox = new VBox();
        vBox.setSpacing(10);

        ComboBox<String> productTypeComboBox = new ComboBox<>();
        productTypeComboBox.setPromptText("Select ProductType");
        productTypeComboBox.setValue(product.getProductType());
        productTypeComboBox.setItems(productTypeList);

        TextField productName = new TextField();
        productName.setPrefWidth(350);
        productName.setPromptText("Product Name");
        productName.setText(product.getProductName());

        TextField totalStock = new TextField();
        totalStock.setPrefWidth(350);
        totalStock.setPromptText("Stock");
        totalStock.setText(Integer.toString(product.getLeftInStock()));

        TextField rate = new TextField();
        rate.setPrefWidth(350);
        rate.setPromptText("Rate");
        rate.setText(Integer.toString(product.getProductRate()));

        TextField netWeight = new TextField();
        netWeight.setPrefWidth(350);
        netWeight.setPromptText("Net Weight");
        netWeight.setText(Integer.toString(product.getNetWeight()));

        vBox.getChildren().addAll(productTypeComboBox,productName,totalStock,rate,netWeight);

        dialog.getDialogPane().setContent(vBox);

        Node finishBtn =  dialog.getDialogPane().lookupButton(ButtonType.FINISH);
        finishBtn.setDisable(true);

        productName.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!productTypeComboBox.getValue().isEmpty() && !newValue.trim().isEmpty() && Validate.checkInt(totalStock.getText().trim()) && Validate.checkInt(rate.getText().trim()) && Validate.checkInt(netWeight.getText().trim())){
                finishBtn.setDisable(false);
            }else {
                finishBtn.setDisable(true);
            }
        });

        totalStock.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!productTypeComboBox.getValue().isEmpty() && !productName.getText().isEmpty() && Validate.checkInt(newValue.trim()) && Validate.checkInt(rate.getText().trim()) && Validate.checkInt(netWeight.getText().trim())){
                finishBtn.setDisable(false);
            }else {
                finishBtn.setDisable(true);
            }
        });

        rate.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!productTypeComboBox.getValue().isEmpty() && !productName.getText().isEmpty() && Validate.checkInt(totalStock.getText().trim()) && Validate.checkInt(newValue.trim()) && Validate.checkInt(netWeight.getText().trim())){
                finishBtn.setDisable(false);
            }else {
                finishBtn.setDisable(true);
            }
        });

        netWeight.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!productTypeComboBox.getValue().isEmpty() && !productName.getText().isEmpty() && Validate.checkInt(totalStock.getText().trim()) && Validate.checkInt(newValue.trim()) && Validate.checkInt(newValue.trim())){
                finishBtn.setDisable(false);
            }else {
                finishBtn.setDisable(true);
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.FINISH){
            String pType = productTypeComboBox.getValue().trim();
            String pName = productName.getText().trim();
            int pStock = Integer.parseInt(totalStock.getText().trim());
            int pRate = Integer.parseInt(rate.getText().trim());
            int pNetWt = Integer.parseInt(netWeight.getText().trim());
            //InventoryDAO.updateProduct(pType,pName,pStock,pRate,pNetWt);
            boolean rs = InventoryDAO.editProduct(pType,pName,pStock,pRate,pNetWt,product.getProductID());
            if(rs){
                // UPDATE TO NEW VALUES.
                product.setProductType(pType);
                product.setProductName(pName);
                product.setLeftInStock(pStock);
                product.setProductRate(pRate);
                product.setNetWeight(pNetWt);
            } else {
                ErrorDialog.show("Couldn't edit product");
            }
        }
    }

}

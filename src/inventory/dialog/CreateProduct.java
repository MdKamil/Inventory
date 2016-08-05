package inventory.dialog;

import inventory.dao.InventoryDAO;
import inventory.model.Product;
import inventory.util.Validate;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class CreateProduct {

    public static Product show(ObservableList<String> productTypeList){

        Product product = null;

        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Add Product");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.FINISH, ButtonType.CANCEL);

        VBox vBox = new VBox();
        vBox.setSpacing(10);

        ComboBox<String> productTypeComboBox = new ComboBox<>();
        productTypeComboBox.setPromptText("Select ProductType");
        productTypeComboBox.setItems(productTypeList);

        TextField productName = new TextField();
        productName.setPrefWidth(350);
        productName.setPromptText("Product Name");

        TextField totalStock = new TextField();
        totalStock.setPrefWidth(350);
        totalStock.setPromptText("Stock");

        TextField rate = new TextField();
        rate.setPrefWidth(350);
        rate.setPromptText("Rate");

        TextField netWeight = new TextField();
        netWeight.setPrefWidth(350);
        netWeight.setPromptText("Net Weight");

        vBox.getChildren().addAll(productTypeComboBox,productName,totalStock,rate,netWeight);

        dialog.getDialogPane().setContent(vBox);

        Node finishBtn =  dialog.getDialogPane().lookupButton(ButtonType.FINISH);
        finishBtn.setDisable(true);

        productTypeComboBox.setOnAction(event -> {
            if(productTypeComboBox.getValue() != null) {
                if (!productTypeComboBox.getValue().isEmpty() && !productName.getText().trim().isEmpty() && Validate.checkInt(totalStock.getText().trim()) && Validate.checkInt(rate.getText().trim()) && Validate.checkInt(netWeight.getText().trim())) {
                    finishBtn.setDisable(false);
                } else {
                    finishBtn.setDisable(true);
                }
            }
        });

        productName.textProperty().addListener((observable, oldValue, newValue) -> {
            if(productTypeComboBox.getValue() != null) {
                if (!productTypeComboBox.getValue().isEmpty() && !newValue.trim().isEmpty() && Validate.checkInt(totalStock.getText().trim()) && Validate.checkInt(rate.getText().trim()) && Validate.checkInt(netWeight.getText().trim())) {
                    finishBtn.setDisable(false);
                } else {
                    finishBtn.setDisable(true);
                }
            }
        });

        totalStock.textProperty().addListener((observable, oldValue, newValue) -> {
            if(productTypeComboBox.getValue() != null) {
                if (!productTypeComboBox.getValue().isEmpty() && !productName.getText().isEmpty() && Validate.checkInt(newValue.trim()) && Validate.checkInt(rate.getText().trim()) && Validate.checkInt(netWeight.getText().trim())) {
                    finishBtn.setDisable(false);
                } else {
                    finishBtn.setDisable(true);
                }
            }
        });

        rate.textProperty().addListener((observable, oldValue, newValue) -> {
            if(productTypeComboBox.getValue() != null) {
                if (!productTypeComboBox.getValue().isEmpty() && !productName.getText().isEmpty() && Validate.checkInt(totalStock.getText().trim()) && Validate.checkInt(newValue.trim()) && Validate.checkInt(netWeight.getText().trim())) {
                    finishBtn.setDisable(false);
                } else {
                    finishBtn.setDisable(true);
                }
            }
        });

        netWeight.textProperty().addListener((observable, oldValue, newValue) -> {
            if(productTypeComboBox.getValue() != null) {
                if (!productTypeComboBox.getValue().isEmpty() && !productName.getText().isEmpty() && Validate.checkInt(totalStock.getText().trim()) && Validate.checkInt(rate.getText().trim()) && Validate.checkInt(newValue.trim())) {
                    finishBtn.setDisable(false);
                } else {
                    finishBtn.setDisable(true);
                }
            }
        });

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.FINISH){
            String pType = productTypeComboBox.getValue().trim();
            String pName = productName.getText().trim();
            int pStock = Integer.parseInt(totalStock.getText().trim());
            int pRate = Integer.parseInt(rate.getText().trim());
            int pNetWt = Integer.parseInt(netWeight.getText().trim());
            product = InventoryDAO.createProduct(pType,pName,pStock,pRate,pNetWt);
            if(product == null){
                ErrorDialog.show("Couldn't create product !!!");
            }
        }
        return product;
    }

}

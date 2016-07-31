package inventory.dialog;

import inventory.dao.InventoryDAO;
import inventory.model.Product;
import inventory.util.Validate;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class UpdateProduct {

    public static int show(Product product, String title, String updateType) {
        TextInputDialog inputDialog = new TextInputDialog();
        inputDialog.setContentText("Enter no of units:");
        inputDialog.setTitle(title);
        inputDialog.setHeaderText(null);
        inputDialog.setGraphic(null);

        Node button = inputDialog.getDialogPane().lookupButton(ButtonType.OK);
        button.setDisable(true);

        TextField textField = inputDialog.getEditor();
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            String value = newValue.trim();
            if(Validate.checkInt(value)){
                button.setDisable(false);
            }else {
                button.setDisable(true);
            }
        });

        Optional<String> result = inputDialog.showAndWait();
        if(result.isPresent()){
            String quantity = result.get().trim();
            int noOfUnit = Integer.parseInt(quantity);
            if(updateType.equals("ADD")){
                boolean rs = InventoryDAO.updateStock(product.getProductID(),noOfUnit);
                if(rs){
                    return noOfUnit;
                }else {
                    ErrorDialog.show("Couldn't update product !!!");
                    return 0;
                }
            }else if(updateType.equals("DEDUCT")){
                if(noOfUnit > product.getLeftInStock()){
                    ErrorDialog.show("Please enter correct no of units !!!");
                }else {
                    boolean rs = InventoryDAO.reduceFromStock(product, noOfUnit);
                    if (rs) {
                        return noOfUnit;
                    } else {
                        ErrorDialog.show("Couldn't update product !!!");
                        return 0;
                    }
                }
            }
        }
        return 0;
    }

}

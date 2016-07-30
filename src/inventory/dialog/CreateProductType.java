package inventory.dialog;

import inventory.dao.InventoryDAO;
import javafx.scene.Node;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public class CreateProductType {

    public static String show(){
        String type = null;
        TextInputDialog newProductTypeDialog = new TextInputDialog();
        newProductTypeDialog.setTitle("Create new ProductType");
        newProductTypeDialog.setContentText("Enter ProductType:");
        newProductTypeDialog.setHeaderText(null);
        newProductTypeDialog.setGraphic(null);

        Node button = newProductTypeDialog.getDialogPane().lookupButton(ButtonType.OK);
        button.setDisable(true);

        TextField textField = newProductTypeDialog.getEditor();
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            String value = newValue.trim();
            if(!value.isEmpty()){
                button.setDisable(false);
            }else {
                button.setDisable(true);
            }
        });

        Optional<String> optional = newProductTypeDialog.showAndWait();
        if(optional.isPresent()){
            String typeName = optional.get().trim();
            boolean result = InventoryDAO.updateProductType(typeName);
            if(result){
                type = typeName;
            }else {
                ErrorDialog.show("Couldn't create new product type !!!");
            }
            textField.clear();
        }
        return type;
    }

}

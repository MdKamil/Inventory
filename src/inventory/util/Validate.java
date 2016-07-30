package inventory.util;

public class Validate {

    public static boolean checkInt(String text) {
        if(text.matches("^[0-9]*$")){
            return true;
        }else {
            return false;
        }
    }

}

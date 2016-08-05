package inventory.util;

public class Validate {

    public static boolean checkInt(String text) {
        if(text.matches("\\d+")){
            return true;
        }else {
            return false;
        }
    }

}

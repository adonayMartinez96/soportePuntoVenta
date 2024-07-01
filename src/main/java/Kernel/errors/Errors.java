package Kernel.errors;

import java.util.ArrayList;
import java.util.List;

/* 
 * Esto parece no funcionar xd
 */

public class Errors {

    public static List<String> errors = new ArrayList<>();

    public static void add(String err){
        Errors.errors.add(err);
    }

    public static List<String> get(){
        return errors;
    }
}
